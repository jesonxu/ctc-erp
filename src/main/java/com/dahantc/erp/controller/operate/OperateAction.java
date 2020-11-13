package com.dahantc.erp.controller.operate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jodconverter.DocumentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.modifyPrice.ToChargeRecordRespDto;
import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.dto.operate.AuditProcessReqDto;
import com.dahantc.erp.dto.operate.FlowEntReqDto;
import com.dahantc.erp.dto.operate.FlowEntRespDto;
import com.dahantc.erp.dto.operate.ProductBillsDto;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.util.UAParserUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntWithOpt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.operate.service.OperateService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;

@Controller
@RequestMapping("/operate")
public class OperateAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(OperateAction.class);
	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private OperateService operateService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private DocumentConverter converter;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private ICustomerService customerService;

	private String uploadFile = "upFile/cedentials";

	/**
	 * 根据产品id查询运营流程数量和时间标题 有哪些年、月
	 *
	 * @param type
	 * @param productId
	 * @return
	 */
	@RequestMapping("/queryOperate")
	public String queryOperate(int type, String productId) {
		logger.info("查询供应商运营时间标题和未处理流程数开始，productId:" + productId);
		// 从产品创建时间到现在的年月集合
		Map<String, List<ToQueryMonthRespDto>> timeMap = new LinkedHashMap<>();
		// 是否没有流程要展示
		boolean empty = true;
		// 是否展示发起按钮
		boolean buttonBody = false;
		try {
			if (StringUtils.isNotBlank(productId)) {
				// 查询当前用户在每个供应商的每个产品等待处理的运营/结算流程数（结果不会为null）
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				// 过滤出指定供应商产品的流程
				flowCount = flowCount.stream().filter(c -> c.getEntityType() == EntityType.SUPPLIER.ordinal() && c.getFlowType() == FlowType.OPERATE.ordinal()
						&& StringUtils.equals(productId, c.getProductId())).collect(Collectors.toList());
				// 年未处理流程数
				Map<String, Long> yearFlowCount = new HashMap<>();
				Timestamp chargeStart = null;
				buttonBody = true;
				Product product = productService.read(productId);
				if (product != null) {
					chargeStart = product.getWtime();
				} else {
					logger.info("产品id为" + productId + "的产品不存在");
				}
				if (chargeStart != null) {
					// 获取运营月份
					timeMap = DateUtil.getMonthBetweenDate(chargeStart, new Date());
					if (flowCount != null && !flowCount.isEmpty() && timeMap != null && !timeMap.isEmpty()) {
						// 获取每年的流程数统计数据 {年 -> 数据统计对象}
						Map<Integer, IntSummaryStatistics> map = flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
						// 将 每年数据统计数据中的总数 放到 年未处理流程数Map，得到 {年 -> 当年未处理流程总数}
						map.entrySet().stream().forEachOrdered(entry -> yearFlowCount.put(entry.getKey() + "", entry.getValue().getSum()));
						// 将未处理流程按年分组，得到 {年 -> 当年有未处理流程的月份}
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream().collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 每年的集合 [年 -> [每个月]]
						Set<Entry<String, List<ToQueryMonthRespDto>>> set = timeMap.entrySet();
						// 遍历每年
						for (Entry<String, List<ToQueryMonthRespDto>> entry : set) {
							// 当年有未处理流程，当年有月份有未处理流程
							if (yearFlowCount.get(entry.getKey()) != null && yearFlowCount.get(entry.getKey()) > 0 && StringUtils.isNumeric(entry.getKey())
									&& monthCountMap.get(Integer.valueOf(entry.getKey())) != null) {
								// 获得每个月的未处理流程数 {每个月 -> 当月未处理流程数}
								Map<Integer, Integer> monthFlowCount = monthCountMap.get(Integer.valueOf(entry.getKey())).stream()
										.collect(Collectors.toMap(FlowEntDealCount::getMonth, FlowEntDealCount::getFlowEntCount));
								// 把 每个月的未处理流程数 放到 每月里
								List<ToQueryMonthRespDto> dtoList = entry.getValue().stream().map(dto -> {
									if (dto == null) {
										return null;
									}
									Integer value = monthFlowCount.get(dto.getMonth());
									if (value != null) {
										dto.setFlowEntCount(value.longValue());
									} else {
										dto.setFlowEntCount(0l);
									}
									return dto;
								}).collect(Collectors.toList());
								timeMap.replace(entry.getKey(), dtoList);
							}
						}
					}
				}
				if (timeMap != null && !timeMap.isEmpty()) {
					empty = false;
					request.setAttribute("timeMap", timeMap);
				}
				request.setAttribute("yearFlowCount", yearFlowCount);
			}
			if (99 != type) {
				buttonBody = true;
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("empty", empty);
			request.setAttribute("buttonBody", buttonBody);
		} catch (Exception e) {
			logger.info("查询供应商运营时间标题和未处理流程数异常", e);
			return "";
		}
		return "/views/operate/operateInfo";
	}

	// 跳转选择账单页面
	@RequestMapping("/toQueryAccount")
	public String toQueryAccount(@RequestParam String productId, @RequestParam String flowClass) {
		// 1、根据productId查产品
		try {
			if (StringUtils.isBlank(productId)) {
				logger.error("产品id不能为空");
				return "";
			}
			Product product = productService.read(productId);
			if (product == null) {
				logger.error("id为" + productId + "产品不存在");
				return "";
			}
			ToChargeRecordRespDto dto = new ToChargeRecordRespDto();
			String supplierId = product.getSupplierId();
			// 根据supplierId查询供应商
			Supplier supplier = supplierService.read(supplierId);
			if (supplier != null) {
				dto.setSupplierId(supplier.getSupplierId());
				dto.setSupplierName(supplier.getCompanyName());
			}
			dto.setProductId(product.getProductId());
			dto.setProductName(product.getProductName());
			dto.setProductTypeInt(product.getProductType());
			request.setAttribute("dto", dto);
			request.setAttribute("flowClass", flowClass);
		} catch (Exception e) {
			logger.error("", e);
			return "";
		}
		return "/views/operate/queryAccount";

	}

	// 查询供应商下所有产品的账单记录
	@PostMapping("/readProductBills")
	@ResponseBody
	public BaseResponse<List<ProductBillsDto>> readProductBills(@RequestParam String productId, @RequestParam String flowClass) {
		List<ProductBillsDto> dtos = new ArrayList<ProductBillsDto>();
		try {
			Product product = productService.read(productId);

			if (product == null) {
				logger.error("id为" + productId + "产品不存在");
				return BaseResponse.success(new ArrayList<ProductBillsDto>());
			}

			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, product.getSupplierId()));
			Supplier supplier = supplierService.queryAllBySearchFilter(filter).get(0);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
			String hql = "select t From ProductBills t  WHERE t.entityId = :entityId";
			if (Constants.BILL_PAYMENT_FLOW_CLASS.equals(flowClass)) { // 账单流程，只显示未付清的账单
				hql += " and t.payables > t.actualPayables";
			} else if (Constants.REMUNERATION_FLOW_CLASS.equals(flowClass)) { // 酬金流程，只显示未收完的账单
				hql += " and t.receivables > t.actualReceivables";
			}

			Map<String, Object> params = new HashMap<>();
			params.put("entityId", product.getSupplierId());
			List<ProductBills> bills = productBillsService.findByhql(hql, params, 0);

			for (ProductBills bill : bills) {
				ProductBillsDto dto = new ProductBillsDto();
				dto.setId(bill.getId());
				dto.setPayables(bill.getPayables());
				dto.setActualpayables(bill.getActualPayables());
				dto.setPayables(bill.getPayables());
				dto.setActualReceivables(bill.getActualReceivables());
				dto.setReceivables(bill.getReceivables());

				Date date = bill.getWtime();
				String dateString = formatter.format(date);
				Product temp = productService.read(bill.getProductId());

				dto.setTitle("账单-" + dateString + "-" + supplier.getCompanyName() + "-" + temp.getProductName());
				dtos.add(dto);
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}

		return BaseResponse.success(dtos);
	}

	// 申请流程方法
	@PostMapping("/applyProcess")
	@ResponseBody
	public BaseResponse<String> applyProcess(@RequestBody @Valid ApplyProcessReqDto reqDto) {
		return operateService.applyProcess(reqDto, getOnlineUserAndOnther());
	}

	// 审核流程（通过、驳回、保存）
	@PostMapping("/auditProcess")
	@ResponseBody
	public BaseResponse<String> auditProcess(@Valid AuditProcessReqDto reqDto) {
		return operateService.auditProcess(reqDto, getOnlineUser().getOssUserId());
	}

	// 撤销
	@PostMapping("/revokeProcess")
	@ResponseBody
	public BaseResponse<String> revokeProcess(@RequestParam(required = true) String flowEntId, @RequestParam(required = false) String revokeReson, @RequestParam int platform) {
		return operateService.revokeProcess(flowEntId, revokeReson, getOnlineUser().getOssUserId(), platform);
	}

	// 上传文件
	@PostMapping("/upLoadFile")
	@ResponseBody
	public BaseResponse<List<UploadFileRespDto>> uploadFile(@RequestParam("files") MultipartFile[] files) {
		List<UploadFileRespDto> dtos = new ArrayList<UploadFileRespDto>();
		try {
			if (files != null && files.length > 0) {
				for (MultipartFile multipartFile : files) {
					String docFileName = multipartFile.getOriginalFilename();
					if (StringUtils.isNotBlank(docFileName)) {
						String ext = docFileName.substring(docFileName.lastIndexOf(".") + 1, docFileName.length());
						String reName = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "") + "." + ext;
						String resource = Constants.RESOURCE;
						String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
						String resourceDir = resource + File.separator + uploadFile + File.separator + datePath;
						File dir = new File(resourceDir);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						String disPath = resourceDir + File.separator + reName;
						File disFile = new File(disPath);
						multipartFile.transferTo(disFile);
						UploadFileRespDto dto = new UploadFileRespDto();
						dto.setFileName(docFileName);
						dto.setFilePath(disPath);
						dtos.add(dto);
					}
				}
			}
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
			return BaseResponse.error("上传异常");
		}
		return BaseResponse.success(dtos);
	}

	@PostMapping("/readFlowEnt")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> readFlowEnt(@Valid FlowEntReqDto req) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		try {
			String roleId = onlineUser.getRoleId();
			List<FlowEntRespDto> resultList = new ArrayList<>();
			// 查询点击产品的流程
			List<FlowEnt> flowList = flowEntService.queryFlowEntByDate(onlineUser, Integer.parseInt(req.getEntityType()), FlowType.OPERATE.ordinal(),
					req.getDate(), "", req.getProductId());
			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success(resultList);
			}
			flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));

			for (FlowEnt ent : flowList) {
				FlowEntRespDto rsp = new FlowEntRespDto();
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setId(ent.getId());
				rsp.setProductId(ent.getProductId());
				FlowNode flowNode = flowNodeService.read(ent.getNodeId());
				if (null != flowNode) {
					rsp.setNodeName(flowNode.getNodeName());
					if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
							&& (flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
						rsp.setCanOperat(true);
					}
				}
				resultList.add(rsp);
			}
			resultList.sort((o1, o2) -> {
				int temp1 = o1.isCanOperat() ? 1 : 0;
				int temp2 = o2.isCanOperat() ? 1 : 0;
				return temp2 - temp1;
			});
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}

	// 获取可以发起的流程信息
	@PostMapping("/getFlowTab")
	@ResponseBody
	public BaseResponse<JSONArray> getFlowTab() {
		// 流程选项框以及默认流程的信息
		JSONArray flows = new JSONArray();
		try {
			String roleId = getOnlineUserAndOnther().getRoleId();
			String hql = "select f From ErpFlow f LEFT JOIN FlowNode n on f.startNodeId = n.nodeId WHERE n.roleId like :roleId and f.status = :status and f.flowClass in (:flowClass)";
			Map<String, Object> params = new HashMap<>();
			List<String> flowClass = new ArrayList<String>();
			flowClass.add(Constants.COMMON_FLOW_CLASS);
			flowClass.add(Constants.PAYMENT_FLOW_CLASS);
			flowClass.add(Constants.BILL_PAYMENT_FLOW_CLASS);
			flowClass.add(Constants.REMUNERATION_FLOW_CLASS);
			flowClass.add(Constants.BILL_FLOW_CLASS);
			flowClass.add(Constants.ADJUST_PRICE_FLOW_CLASS);
			flowClass.add(Constants.INTER_ADJUST_PRICE_FLOW_CLASS);
			flowClass.add(Constants.CUSTOMER_BILL_FLOW_CLASS);
			flowClass.add(Constants.BILL_RECEIVABLES_FLOW_CLASS);
			flowClass.add(Constants.BILL_WRITE_OFF_FLOW_CLASS);
			params.put("roleId", "%" + roleId + "%");
			params.put("status", EntityStatus.NORMAL.ordinal());
			params.put("flowClass", flowClass);
			List<ErpFlow> flowList = erpFlowService.findByhql(hql, params, 0);
			if (flowList != null && !flowList.isEmpty()) {
				String defaultFlowId = flowList.get(0).getFlowId(); // 第一个为默认流程

				SearchFilter labelFilter = new SearchFilter(); // 获取所有lebel
				labelFilter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, defaultFlowId));
				labelFilter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
				List<FlowLabel> flowLabels = flowLabelService.queryAllBySearchFilter(labelFilter);

				String startNode = flowList.get(0).getStartNodeId();
				FlowNode flowNode = flowNodeService.read(startNode);

				JSONArray labelList = new JSONArray();

				for (FlowLabel label : flowLabels) {
					JSONObject labelobj = new JSONObject();
					labelobj.put("id", label.getId());
					labelobj.put("name", label.getName());
					labelobj.put("defaultValue", label.getDefaultValue());
					labelobj.put("type", label.getType());
					labelList.add(labelobj);
				}

				for (ErpFlow flow : flowList) {
					JSONObject flowobj = new JSONObject();
					flowobj.put("flowName", flow.getFlowName());
					flowobj.put("flowId", flow.getFlowId());
					if (flow.getFlowId().equals(defaultFlowId)) {
						flowobj.put("flowLabels", labelList);
					}
					flowobj.put("editLabelIds", flowNode.getEditLabelIds());
					flowobj.put("mustLabelIds", flowNode.getMustLabelIds());
					flowobj.put("flowClass", flow.getFlowClass());
					flows.add(flowobj);
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return BaseResponse.success(flows);
	}

	// 获取可以发起的流程信息
	@PostMapping("/getFlowTabLabel")
	@ResponseBody
	public BaseResponse<JSONObject> getFlowTabLabel() {
		// 流程选项框以及默认流程的信息

		String flowId = request.getParameter("flowId");

		JSONObject flowobj = new JSONObject();
		try {
			ErpFlow flow = erpFlowService.read(flowId);

			if (flow != null) {

				SearchFilter labelFilter = new SearchFilter();
				labelFilter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				labelFilter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
				List<FlowLabel> flowLabels = flowLabelService.queryAllBySearchFilter(labelFilter);

				JSONArray labelList = new JSONArray();
				flowobj.put("flowName", flow.getFlowName());
				flowobj.put("flowId", flow.getFlowId());
				for (FlowLabel label : flowLabels) {
					JSONObject labelobj = new JSONObject();
					labelobj.put("id", label.getId());
					labelobj.put("name", label.getName());
					labelobj.put("defaultValue", label.getDefaultValue());
					labelobj.put("type", label.getType());
					labelList.add(labelobj);
				}

				String startNode = flow.getStartNodeId();
				FlowNode flowNode = flowNodeService.read(startNode);
				flowobj.put("editLabelIds", flowNode.getEditLabelIds());
				flowobj.put("mustLabelIds", flowNode.getMustLabelIds());
				flowobj.put("flowLabels", labelList);
				flowobj.put("flowClass", flow.getFlowClass());
			} else {
				logger.error("未找到该流程的基础信息");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return BaseResponse.success(flowobj);
	}

	/**
	 * 根据路径下载文件
	 */
	@ResponseBody
	@GetMapping("/downloadFile")
	public void downloadFile(@RequestParam String filePath, @RequestParam String fileName) {
		try {
			if (StringUtils.isNotBlank(filePath)) {
				if (filePath.startsWith("http")) {
					download(filePath, fileName, response);
				} else {
					File file = new File(filePath);
					download(file, fileName, response);
				}
			} else {
				logger.error("下载失败，资源不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据路径下载文件
	 */
	@ResponseBody
	@GetMapping("/viewFile")
	public String viewFile(@RequestParam String filePath, @RequestParam String fileName) {
		try {
			if (StringUtils.isNotBlank(filePath)) {
				if (filePath.startsWith("http")) {
					download(filePath, fileName, response);
				} else {
					InputStream inputStream = null;
					ServletOutputStream outputStream = null;
					try {
						String viewFiledir = Constants.RESOURCE + File.separator + "viewFileDir";
						File newFile = new File(viewFiledir);// 转换之后文件生成的地址
						if (!newFile.exists()) {
							newFile.mkdirs();
						}
						File file = new File(filePath);
						// 文件转化
						// 使用response,将pdf文件以流的方式发送的前段
						outputStream = response.getOutputStream();

						if (filePath.toLowerCase().endsWith(".pdf")) {
							inputStream = new FileInputStream(file);// 读取文件
						} else {
							String viewFilePath = viewFiledir + File.separator + UUID.randomUUID().toString();
							if (filePath.toLowerCase().endsWith(".xls") || filePath.toLowerCase().endsWith(".xlsx")) {
								viewFilePath += ".html";
							} else {
								viewFilePath += ".pdf";
							}
							converter.convert(file).to(new File(viewFilePath)).execute();
							inputStream = new FileInputStream(new File(viewFilePath));// 读取文件
						}
						// copy文件
						IOUtils.copy(inputStream, outputStream);
					} catch (Exception e) {
						logger.error("预览文件异常", e);
					} finally {
						if (null != inputStream)
							inputStream.close();
						if (null != outputStream)
							outputStream.close();
					}
				}
			} else {
				logger.error("下载失败，资源不存在");
			}
		} catch (Exception e) {
			logger.error("预览文件异常", e);
		}
		return "This is to pdf";
	}

	// 下载文件方法
	private void download(File file, String fileName, HttpServletResponse response) {
		ServletOutputStream outputStream = null;
		FileInputStream fileInputStream = null;
		try {
			outputStream = response.getOutputStream();
			fileInputStream = new FileInputStream(file);
			response.setHeader("content-type", "application/octet-stream");
			response.setContentType("application/octet-stream");
			// 下载文件能正常显示中文
			response.setCharacterEncoding("UTF-8");
			String browser = getBrowser(request);
			if ("Edge".equals(browser) || "IE".equals(browser)) {
				response.setHeader("Content-Disposition", "attachment;fileName=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
			} else {
				response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(fileName.getBytes("UTF-8"), "iso-8859-1") + "\"");
			}
			IOUtils.copy(fileInputStream, outputStream);
			outputStream.flush();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	// 下载网络文件方法
	private void download(String filePath, String fileName, HttpServletResponse response) {
		ServletOutputStream outputStream = null;
		URL url = null;
		InputStream inputStream = null;
		try {
			url = new URL(filePath);
			inputStream = new BufferedInputStream(url.openStream());
			outputStream = response.getOutputStream();
			response.setHeader("content-type", "application/octet-stream");
			response.setContentType("application/octet-stream");
			// 下载文件能正常显示中文
			response.setCharacterEncoding("UTF-8");
			String browser = getBrowser(request);
			if ("Edge".equals(browser) || "IE".equals(browser)) {
				response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
			} else {
				response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
			}
			IOUtils.copy(inputStream, outputStream);
			outputStream.flush();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	// 判断浏览器种类的方法
	private String getBrowser(HttpServletRequest request) throws Exception {
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		String reqMsg = UAParserUtil.getBrower(userAgent);
		return reqMsg;
	}

	/**
	 * 根据供应商ID查询所有运营流程数量和时间标题
	 *
	 * @param supplierId
	 *            供应商id
	 * @return 运营记录
	 */
	@RequestMapping("/getAllOperate")
	public String getAllOperate(String supplierId) {
		logger.info("查询供应商运营的时间和数量开始，供应商id：" + supplierId);
		long _start = System.currentTimeMillis();
		Map<String, List<ToQueryMonthRespDto>> timeMap = new LinkedHashMap<>();
		boolean empty = true;
		boolean buttonBody = false;
		String entityType = request.getParameter("entityType");
		int entitytype = EntityType.SUPPLIER.ordinal();
		try {
			// 如果是电商的供应商，点击供应商就要显示流程发起按钮
			if (StringUtil.isNotBlank(entityType)) {
				if (StringUtils.isNumeric(entityType)) {
					Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entityType));
					if (entityTypeOpt.isPresent()) {
						entitytype = entityTypeOpt.get().ordinal();
					} else {
						logger.info("错误的实体类型：" + entityType);
						return "";
					}
				} else {
					logger.info("错误的实体类型：" + entityType);
					return "";
				}
			}
			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 运营的最早时间
			Timestamp startTime = null;
			// 有供应商id，查询供应商，获取供应商的产品的最早开始时间
			if (StringUtils.isNotBlank(supplierId)) {
				Supplier supplier = supplierService.read(supplierId);
				if (supplier != null) {
					buttonBody = EntityType.SUPPLIER_DS.ordinal() == entitytype;
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
					searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
					List<Product> resultList = productService.queryAllBySearchFilter(searchFilter);
					if (resultList != null && resultList.size() > 0) {
						startTime = resultList.get(0).getWtime();
					} else {
						startTime = supplier.getWtime();
					}
				}
			} else { // 没有供应商id，获取流程的最早时间
				// List<FlowEnt> flowEnts =
				// flowEntService.queryFlowEntByDate(onlineUser, entitytype,
				// FlowType.OPERATE.ordinal(), "", "", "");
				Role role = roleService.read(onlineUser.getRoleId());
				FlowEnt flowEnt = flowEntService.queryEarliestFlowEntByRole(onlineUser.getUser(), role, entitytype, FlowType.OPERATE.ordinal());
				if (flowEnt != null) {
					startTime = flowEnt.getWtime();
				}
			}

			if (startTime != null) {
				// 获取从开始时间到现在的年每个月集合
				timeMap = DateUtil.getMonthBetweenDate(startTime, new Date());
				// 获取当前用户角色的所有未处理流程
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				// 获取当前用户的所有供应商
				List<Supplier> suppliers = supplierService.readSuppliers(onlineUser, "", supplierId, "", "", SearchType.FLOW.ordinal());
				List<String> supplierIds = new ArrayList<String>();
				if (suppliers != null && !suppliers.isEmpty()) {
					supplierIds = suppliers.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
				}
				if (flowCount != null && !flowCount.isEmpty()) {
					if (StringUtils.isNotBlank(supplierId)) {
						// 过滤出指定供应商的运营流程
						flowCount = flowCount.stream()
								.filter(flow -> flow.getFlowType() == FlowType.OPERATE.ordinal() && StringUtils.equals(supplierId, flow.getSupplierId()))
								.collect(Collectors.toList());
					} else {
						// 过滤出用户所有供应商的流程
						List<String> finalSupplierIds = supplierIds;
						int finalEntitytype = entitytype;
						flowCount = flowCount.stream().filter(flow -> flow.getFlowType() == FlowType.OPERATE.ordinal()
								&& flow.getEntityType() == finalEntitytype && finalSupplierIds.contains(flow.getSupplierId())).collect(Collectors.toList());
					}
					// 年-年未处理流程数
					Map<String, Long> yearFlowCount = new HashMap<>();
					if (timeMap != null && !timeMap.isEmpty()) {
						// 统计年-流程数量
						flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)))
								.forEach((year, yearCount) -> yearFlowCount.put(year + "", yearCount.getSum()));
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream().collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 统计时间（月份-流程数量）
						Set<Map.Entry<String, List<ToQueryMonthRespDto>>> yearMonthTime = timeMap.entrySet();
						for (Map.Entry<String, List<ToQueryMonthRespDto>> yearMonthInfo : yearMonthTime) {
							// 年份
							String year = yearMonthInfo.getKey();
							// 年未处理流程数
							Long yearCount = yearFlowCount.get(year);
							if (yearCount != null && yearCount > 0 && StringUtils.isNumeric(year) && monthCountMap.get(Integer.valueOf(year)) != null) {
								Map<Integer, Integer> monthFlowCount = monthCountMap.get(Integer.valueOf(year)).stream()
										.collect(Collectors.groupingBy(FlowEntDealCount::getMonth, Collectors.summingInt(FlowEntDealCount::getFlowEntCount)));
								List<ToQueryMonthRespDto> dtoList = yearMonthInfo.getValue().stream().map(dto -> {
									if (dto == null) {
										return null;
									}
									// 月流程数
									Integer value = monthFlowCount.get(dto.getMonth());
									if (value != null) {
										dto.setFlowEntCount(value.longValue());
									} else {
										dto.setFlowEntCount(0l);
									}
									return dto;
								}).collect(Collectors.toList());
								timeMap.replace(yearMonthInfo.getKey(), dtoList);
							}
						}
					}
					request.setAttribute("yearFlowCount", yearFlowCount);
				}
				if (timeMap != null && !timeMap.isEmpty()) {
					empty = false;
					request.setAttribute("timeMap", timeMap);
				}
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("empty", empty);
			request.setAttribute("buttonBody", buttonBody);
		} catch (ServiceException e) {
			logger.error("查询供应商运营的时间和数量异常", e);
			return "";
		}
		logger.info("查询供应商运营的时间和数量结束，耗时：" + (System.currentTimeMillis() - _start));
		if (entitytype == EntityType.SUPPLIER_DS.ordinal()) {
			return "/views/operateDs/operateInfo";
		}
		return "/views/operate/operateInfo";
	}

	/**
	 * 按月份查询指定供应商/客户的所有运营记录
	 *
	 * @param date
	 *            月份
	 * @param supplierId
	 *            供应商id
	 * @return
	 */
	@RequestMapping("/getAllOperateByDate")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> getAllOperateByDate(@RequestParam String date, @RequestParam String supplierId, @RequestParam String entityType) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份查询所有运营记录开始，月份：" + date);
		long _start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<FlowEntRespDto> resultList = new ArrayList<>(); // 最终的结果
			List<FlowEnt> flowList = flowEntService.queryFlowEntByDate(onlineUser, Integer.parseInt(entityType), FlowType.OPERATE.ordinal(), date, supplierId,
					"");
			if (null == flowList || flowList.isEmpty()) {
				logger.info("未查询到流程：" + date);
				return BaseResponse.success(resultList);
			}
			logger.info("按月份查询到运营流程数：" + flowList.size());
			flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));
			// 封装返回结果
			for (FlowEnt ent : flowList) {
				FlowEntRespDto rsp = new FlowEntRespDto();
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setId(ent.getId());
				rsp.setProductId(ent.getProductId());
				FlowNode flowNode = flowNodeService.read(ent.getNodeId());
				if (null != flowNode) {
					rsp.setNodeName(flowNode.getNodeName());
					// 当前角色可处理
					if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
							&& (flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
						rsp.setCanOperat(true);
					}
				}
				resultList.add(rsp);
			}
			resultList.sort((o1, o2) -> {
				int temp1 = o1.isCanOperat() ? 1 : 0;
				int temp2 = o2.isCanOperat() ? 1 : 0;
				return temp2 - temp1;
			});
			logger.info("按月份查询所有运营记录结束，耗时:" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.info("按月份查询所有运营记录异常", e);
		}
		return BaseResponse.error("按月份查询所有运营记录失败");
	}

	/**
	 * 获取开票信息或银行信息
	 * 
	 * @param type
	 *            开票信息或银行信息
	 * @param supplierId
	 *            供应商或客户id
	 * @return
	 */
	@RequestMapping("/getInvoice")
	@ResponseBody
	public BaseResponse<JSONArray> getInvoice(@RequestParam int type, @RequestParam String supplierId) {
		JSONArray jsonArray = new JSONArray();
		try {
			SearchFilter filter = new SearchFilter();
			// 类型过滤条件：我司开票、对方开票、我司银行、对方银行
			if (InvoiceType.getEnumsByCode(type).isPresent()) {
				filter.getRules().add(new SearchRule("invoiceType", Constants.ROP_EQ, type));
				// 我司开票、我司银行信息不需要basicsId
				if (StringUtils.isNotBlank(supplierId) && type != InvoiceType.SelfInvoice.ordinal() && type != InvoiceType.SelfBank.ordinal()) {
					filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, supplierId));
				}
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			} else {
				return BaseResponse.error("开票信息或银行信息类型错误");
			}
			List<Object> jsonList = null;
			if (InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.SelfInvoice.ordinal()
					|| InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.OtherInvoice.ordinal()) {
				List<InvoiceInformation> list = invoiceInformationService.queryAllBySearchFilter(filter);
				jsonList = list.stream().map(info -> {
					JSONObject json = new JSONObject();
					json.put("value",
							"basicsId:" + info.getInvoiceId() + "####companyName:" + info.getCompanyName() + "####taxNumber:" + info.getTaxNumber()
									+ "####companyAddress:" + info.getCompanyAddress() + "####phone:" + info.getPhone() + "####accountBank:"
									+ info.getAccountBank() + "####bankAccount:" + info.getBankAccount());
					json.put("text", info.getCompanyName() + "【" + info.getAccountBank() + "：" + info.getBankAccount() + "】");
					json.put("title", "公司名称：" + info.getCompanyName() + "\r\n税务号：" + info.getTaxNumber() + "\r\n公司地址：" + info.getCompanyAddress() + "\r\n联系电话："
							+ info.getPhone() + "\r\n开户银行：" + info.getAccountBank() + "\r\n银行账号：" + info.getBankAccount());
					return json;
				}).collect(Collectors.toList());
			} else if (InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.SelfBank.ordinal()
					|| InvoiceType.getEnumsByCode(type).get().ordinal() == InvoiceType.OtherBank.ordinal()) {
				List<BankAccount> list = bankAccountService.queryAllBySearchFilter(filter);
				jsonList = list.stream().map(account -> {
					JSONObject json = new JSONObject();
					String accountBank = account.getAccountBank();
					String bankAccount = account.getBankAccount();
					json.put("value",
							"basicsId:" + account.getBankAccountId() + "####accountName:" + (account.getAccountName() == null ? "" : account.getAccountName())
									+ "####accountBank:" + accountBank + "####bankAccount:" + bankAccount);
					json.put("text", accountBank + "【" + bankAccount + "】");
					json.put("title", "名称：" + account.getAccountName() + "\r\n开户银行：" + account.getAccountBank() + "\r\n银行账号：" + account.getBankAccount());
					return json;
				}).collect(Collectors.toList());
			}
			if (jsonList != null && !jsonList.isEmpty()) {
				return BaseResponse.success(new JSONArray(jsonList));
			}
		} catch (Exception e) {
			logger.error("查询开票信息异常", e);
		}
		return BaseResponse.success(jsonArray);
	}

	/**
	 * 获取未审核通过的账单金额
	 */
	@RequestMapping("/queryApplying")
	@ResponseBody
	public BaseResponse<String> queryApplyPay(@RequestParam String flowId, @RequestParam String billId, @RequestParam String type,
			@RequestParam String flowEntId) {
		return operateService.queryApplying(flowId, billId, type, flowEntId);
	}

	/**
	 * 分页查询流程标题
	 *
	 * @param date
	 *            展开的月份
	 * @param supplierId
	 *            供应商，客户id
	 * @param entityType
	 *            0供应商，1客户
	 * @param page
	 *            第几页
	 * @param pageSize
	 *            每页大小
	 * @return 流程信息
	 */
	@RequestMapping("/getAllOperateByPage")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> getAllOperateByPage(String date, String supplierId, String entityType, int page, int pageSize) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份分页查询运营记录开始，月份：" + date + "，page：" + page + "，pageSize：" + pageSize);
		long startTime = System.currentTimeMillis();
		boolean isSale = isSale();
		try {
			String roleId = onlineUser.getRoleId();
			List<FlowEntRespDto> resultList = new ArrayList<>(); // 最终的结果
			// List<FlowEnt> flowList =
			// flowEntService.queryFlowEntByDate(onlineUser,
			// Integer.parseInt(entityType), FlowType.OPERATE.ordinal(), date,
			// supplierId,
			// "");
			PageResult<FlowEntWithOpt> flowPageInfo = flowEntService.queryFlowEntByPageSql(onlineUser, Integer.parseInt(entityType), FlowType.OPERATE.ordinal(),
					date, supplierId, "", pageSize, page, "");
			if (null == flowPageInfo || flowPageInfo.getData() == null || flowPageInfo.getData().isEmpty()) {
				logger.info("未查询到流程：" + date);
				return BaseResponse.success("0", resultList);
			}
			List<FlowEntWithOpt> flowList = flowPageInfo.getData();
			if (isSale) {
				// 查询公共池客户
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(
						new SearchRule("customerTypeId", Constants.ROP_EQ, customerTypeService.getCustomerTypeIdByValue(CustomerTypeValue.PUBLIC.getCode())));
				List<Customer> list = customerService.queryAllBySearchFilter(searchFilter);
				if (!CollectionUtils.isEmpty(list)) {
					List<String> customerIds = list.stream().map(Customer::getCustomerId).collect(Collectors.toList());
					List<String> flowIds = null;
					searchFilter.getRules().clear();
					searchFilter.getRules().add(new SearchRule("flowClass", Constants.ROP_EQ, Constants.APPLY_CUSTOMER_FLOW_CLASS));
					List<ErpFlow> flows = erpFlowService.queryAllBySearchFilter(searchFilter);
					if (!CollectionUtils.isEmpty(flows)) {
						flowIds = flows.stream().map(ErpFlow::getFlowId).collect(Collectors.toList());
					}
					if (flowIds == null) {
						flowIds = new ArrayList<>();
					}
					if (!CollectionUtils.isEmpty(customerIds)) {
						for (Iterator<FlowEntWithOpt> iterator = flowList.iterator(); iterator.hasNext();) {
							FlowEntWithOpt flowEnt = iterator.next();
							if (customerIds.contains(flowEnt.getSupplierId()) && !flowIds.contains(flowEnt.getFlowId())) {
								iterator.remove();
							}
						}
					}
				}
			}
			logger.info("按月份查询到运营流程数：" + flowList.size());
			// flowList.sort((o1, o2) ->
			// o2.getWtime().compareTo(o1.getWtime()));
			List<FlowNode> flowNodeList = flowNodeService.findFlowNodeByIds(flowList.stream().map(FlowEntWithOpt::getNodeId).collect(Collectors.toList()));
			Map<String, FlowNode> flowNodeInfos = new HashMap<>();
			if (flowNodeList != null) {
				flowNodeList.forEach(node -> flowNodeInfos.put(node.getNodeId(), node));
				flowNodeList.clear();
			}
			// 封装返回结果
			for (FlowEntWithOpt ent : flowList) {
				FlowEntRespDto rsp = new FlowEntRespDto();
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setId(ent.getId());
				rsp.setProductId(ent.getProductId());
				rsp.setApplyTime(DateUtil.convert(ent.getWtime(), DateUtil.format1));
				FlowNode flowNode = flowNodeInfos.get(ent.getNodeId());
				if (flowNode != null) {
					rsp.setNodeName(flowNode.getNodeName());
					// 当前角色可处理
					if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
							&& (flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
						rsp.setCanOperat(true);
					}

				}
				resultList.add(rsp);
			}
			/*
			 * resultList.sort((o1, o2) -> { int temp1 = o1.isCanOperat() ? 1 :
			 * 0; int temp2 = o2.isCanOperat() ? 1 : 0; return temp2 - temp1;
			 * });
			 */
			logger.info("按月份分页查询运营记录结束，耗时：" + (System.currentTimeMillis() - startTime));
			/*
			 * int pages = (int) (Math.ceil(1.0 * resultList.size() /
			 * pageSize)); // 向上 if (page > pages) { return
			 * BaseResponse.success(pages + "", new ArrayList<>()); } int
			 * pageStart = (page - 1) * pageSize; int pageEnd = Math.min(page *
			 * pageSize, resultList.size());
			 */
			return BaseResponse.success(flowPageInfo.getTotalPages() + "", resultList);
		} catch (Exception e) {
			logger.info("按月份分页查询运营记录异常", e);
		}
		return BaseResponse.error("按月份查询所有运营记录失败");
	}

	@PostMapping("/getOperateByPage")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> getOperateByPage(String date, String productId, int entityType, int page, int pageSize) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份分页查询产品运营记录开始，月份：" + date + "，page：" + page + "，pageSize：" + pageSize);
		long _start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<FlowEntRespDto> resultList = new ArrayList<>();
			// 查询点击产品的流程
			List<FlowEnt> flowList = flowEntService.queryFlowEntByDate(onlineUser, entityType, FlowType.OPERATE.ordinal(), date, "", productId);
			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success("0", resultList);
			}
			flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));
			List<FlowNode> flowNodeList = flowNodeService.findFlowNodeByIds(flowList.stream().map(FlowEnt::getNodeId).collect(Collectors.toList()));
			Map<String, FlowNode> flowNodeInfos = new HashMap<>();
			if (flowNodeList != null) {
				flowNodeList.forEach(node -> flowNodeInfos.put(node.getNodeId(), node));
				flowNodeList.clear();
			}

			for (FlowEnt ent : flowList) {
				FlowEntRespDto rsp = new FlowEntRespDto();
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setId(ent.getId());
				rsp.setProductId(ent.getProductId());
				rsp.setApplyTime(DateUtil.convert(ent.getWtime(), DateUtil.format1));
				FlowNode flowNode = flowNodeInfos.get(ent.getNodeId());
				if (null != flowNode) {
					rsp.setNodeName(flowNode.getNodeName());
					if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
							&& (flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
						rsp.setCanOperat(true);
					}
				}
				resultList.add(rsp);
			}
			resultList.sort((o1, o2) -> {
				int temp1 = o1.isCanOperat() ? 1 : 0;
				int temp2 = o2.isCanOperat() ? 1 : 0;
				return temp2 - temp1;
			});
			logger.info("按月份分页查询产品运营记录结束，耗时：" + (System.currentTimeMillis() - _start));
			int pages = (int) (Math.ceil(1.0 * resultList.size() / pageSize)); // 向上
			if (page > pages) {
				return BaseResponse.success(pages + "", new ArrayList<>());
			}
			int pageStart = (page - 1) * pageSize;
			int pageEnd = Math.min(page * pageSize, resultList.size());
			return BaseResponse.success(pages + "", resultList.subList(pageStart, pageEnd));
		} catch (Exception e) {
			logger.error("按月份分页查询产品运营记录异常", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}
}
