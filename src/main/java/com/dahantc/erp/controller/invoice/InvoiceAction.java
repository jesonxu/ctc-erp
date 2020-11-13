package com.dahantc.erp.controller.invoice;

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
import com.dahantc.erp.dto.bill.ProductBillsDto;
import com.dahantc.erp.dto.invoice.InvoiceDto;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceStatus;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.invoice.entity.Invoice;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.invoice.service.IInvoiceInformationService;
import com.dahantc.erp.vo.invoice.service.IInvoiceService;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invoice")
public class InvoiceAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceAction.class);

	private static final String downLoadFile = "exportFile/invoice";

	private static final String[] title = new String[] { "开票日期", "申请人", "申请日期", "公司名称", "主体类型", "开票金额", "已收金额", "我司开票信息", "对方开票信息", "发票状态", "开票服务名称", "发票类型", "备注" };

	@Autowired
	private IInvoiceService invoiceService;

	@Autowired
	private IInvoiceInformationService invoiceInformationService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IProductBillsService productBillsService;

	/**
	 * 跳转到发票报表页面
	 */
	@RequestMapping("/toInvoiceSheet")
	public String toInvoiceSheet() {
		return "/views/manageConsole/invoiceSheet";
	}

	/**
	 * 跳转到作废发票流程页面
	 */
	@RequestMapping("/toVoidInvoicePage/{invoiceId}")
	public String toVoidInvoicePage(@PathVariable String invoiceId,@RequestParam BigDecimal receivables) {
		request.setAttribute("invoiceId", invoiceId);
		request.setAttribute("receivables", receivables);
		return "/views/manageConsole/voidInvoiceProcess";
	}

	@RequestMapping("/editInvoiceStatus")
	@ResponseBody
	public BaseResponse<String> editInvoiceStatus() {
		String invoiceId = request.getParameter("invoiceId");
		String withdrawRemark = request.getParameter("withdrawRemark");
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.noLogin("请先登录");
		}
		if (StringUtils.isBlank(invoiceId) || StringUtils.isBlank(withdrawRemark)) {
			return BaseResponse.error("请求参数异常");
		}
		try {
			Invoice invoice = invoiceService.read(invoiceId);
			if (StringUtil.isNotBlank(invoice.getRemark())) {
				invoice.setRemark(invoice.getRemark() + "\n" + withdrawRemark);
			} else {
				invoice.setRemark(withdrawRemark);
			}
			invoiceService.update(invoice);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}

		return BaseResponse.success("修改发票状态成功");
	}

	/**
	 * 按公司名称查找客户/供应商，自动补全
	 * 
	 * @return 包含公司名称和entityId的对象集合
	 */
	@RequestMapping("/queryAllCompanyByAuto")
	@ResponseBody
	public String queryAllCompanyNameByAuto() {
		// 供应商公司名称
		SearchFilter searchFilter = new SearchFilter();
		JSONObject req = new JSONObject();
		JSONArray jsonList = new JSONArray();
		req.put("code", 0);
		req.put("type", "success");
		req.put("content", jsonList);
		try {
			String keywords = request.getParameter("keywords");
			if (StringUtils.isNotBlank(keywords)) {
				searchFilter.getRules().add(new SearchRule("companyName", Constants.ROP_CN, keywords));
			}
			List<Supplier> suppliers = supplierService.queryAllBySearchFilter(searchFilter);
			List<Customer> customers = customerService.queryAllBySearchFilter(searchFilter);

			if (CollectionUtils.isEmpty(suppliers) && CollectionUtils.isEmpty(customers)) {
				req.put("code", 1);
				req.put("type", "error");
				return req.toString();
			}

			if (!CollectionUtils.isEmpty(suppliers)) {
				for (Supplier e : suppliers) {
					User user = userService.read(e.getOssUserId());
					JSONObject obj = new JSONObject();
					obj.put("id", e.getSupplierId());
					obj.put("companyName", e.getCompanyName());
					Optional.ofNullable(user).ifPresent(u -> obj.put("realName", u.getRealName()));
					jsonList.add(obj);
				}
			}
			if (!CollectionUtils.isEmpty(customers)) {
				for (Customer e : customers) {
					User user = userService.read(e.getOssuserId());
					JSONObject obj = new JSONObject();
					obj.put("id", e.getCustomerId());
					obj.put("companyName", e.getCompanyName());
					Optional.ofNullable(user).ifPresent(u -> obj.put("realName", u.getRealName()));
					jsonList.add(obj);
				}
			}
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return req.toString();
	}

	/**
	 * 分页查询发票记录
	 * @return
	 */
	@RequestMapping("readInvoiceByPage")
	@ResponseBody
	public BaseResponse<PageResult<InvoiceDto>> readInvoiceByPage() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		PageResult<Invoice> pageResult = new PageResult<>();
		PageResult<InvoiceDto> result = new PageResult<>();
		int pageSize = 15;
		int nowPage = 1;
		logger.info("查询发票记录开始");
		try {
			long _start = System.currentTimeMillis();
			if (StringUtils.isNotBlank(request.getParameter("limit"))) {
				pageSize = Integer.parseInt(request.getParameter("limit"));
			}
			if (StringUtils.isNotBlank(request.getParameter("page"))) {
				nowPage = Integer.parseInt(request.getParameter("page"));
			}
			// entityId（供应商表的supplierid，customer表的customerid）
			String entityId = request.getParameter("entityId");
			// 实体类型
			String entityType = request.getParameter("entityType");
			// 实体类型
			String companyName = request.getParameter("companyName");
			// 发票状态
			String invoiceStatus = request.getParameter("invoiceStatus");
			// 开票日期
			String applyDateStr = request.getParameter("applyDate");
			String applyDateEndStr = request.getParameter("applyDateEnd");
			// 封装搜索filter
			SearchFilter filter = buildSearchFilter(onlineUser, entityId, entityType, companyName, invoiceStatus, applyDateStr, applyDateEndStr);
			if (null != filter) {
				pageResult = invoiceService.queryByPages(pageSize, nowPage, filter);
			}
			// 封装返回对象
			List<InvoiceDto> dtoList = buildInvoiceDto(pageResult.getData());
			if (!CollectionUtils.isEmpty(dtoList)) {
				result.setData(dtoList);
				result.setCount(pageResult.getCount());
				result.setTotalPages(pageResult.getTotalPages());
				result.setCurrentPage(pageResult.getCurrentPage());
				result.setCode(pageResult.getCode());
			}
			logger.info("查询发票记录结束，查询到" + result.getCount() + "条记录，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("查询发票记录异常", e);
			result.setData(new ArrayList<>());
		}
		return BaseResponse.success(result);
	}

	/**
	 * 创建SearchFilter
	 *
	 * @param entityId
	 *            entityId（供应商表的supplierid，customer表的customerid）
	 * @param entityType
	 *            实体类型
	 * @param companyName
	 * 			  公司名称
	 * @param applyDateStr
	 *            开票日期区间开始
	 * @param applyDateEndStr
	 *            开票日期区间结束
	 * @return 构建完成的SearchFilter
	 */
	private SearchFilter buildSearchFilter(OnlineUser onlineUser, String entityId, String entityType, String companyName, String invoiceStatus, String applyDateStr, String applyDateEndStr) {
		// invoiceStatus只能为0到2直接的一位数字
		String regex = "[0-1]";
		Pattern pattern = Pattern.compile(regex);
		// 判断实体类型是否为空，不为空按实体类型查出公司名称。否则需在供应商表和customer表中单独查询。
		SearchFilter filter = new SearchFilter();
		Integer entityTypeValue = null;
		if (StringUtils.isNotBlank(entityType)) {
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, Integer.valueOf(entityType)));
			entityTypeValue = Integer.valueOf(entityType);
		}
		if (StringUtils.isNotBlank(entityId)) {
			filter.getRules().add(new SearchRule("entityId", Constants.ROP_EQ, entityId));
		} else if (StringUtils.isNotBlank(companyName)) {
			Set<String> entityIdSet = new HashSet<>();
			if (null == entityTypeValue) {
				List<Customer> customerList = customerService.readCustomers(onlineUser, null, entityId, null, null, companyName);
				if (!CollectionUtils.isEmpty(customerList)) {
					entityIdSet.addAll(customerList.stream().map(Customer::getCustomerId).collect(Collectors.toSet()));
				}
				List<Supplier> supplierList = supplierService.readSuppliers(onlineUser, null, entityId, null, null, SearchType.SUPPLIER.ordinal(), companyName);
				if (!CollectionUtils.isEmpty(supplierList)) {
					entityIdSet.addAll(supplierList.stream().map(Supplier::getSupplierId).collect(Collectors.toSet()));
				}
			} else if (EntityType.CUSTOMER.getCode() == entityTypeValue) {
				List<Customer> customerList = customerService.readCustomers(onlineUser, null, entityId, null, null, companyName);
				if (!CollectionUtils.isEmpty(customerList)) {
					entityIdSet.addAll(customerList.stream().map(Customer::getCustomerId).collect(Collectors.toSet()));
				}
			} else if (EntityType.SUPPLIER.getCode() == entityTypeValue || EntityType.SUPPLIER_DS.getCode() == entityTypeValue) {
				List<Supplier> supplierList = supplierService.readSuppliers(onlineUser, null, entityId, null, null, SearchType.SUPPLIER.ordinal(), companyName);
				if (!CollectionUtils.isEmpty(supplierList)) {
					entityIdSet.addAll(supplierList.stream().map(Supplier::getSupplierId).collect(Collectors.toSet()));
				}
			}
			if (entityIdSet.isEmpty()) {
				logger.info("未查找到客户/供应商");
				return null;
			}
			filter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, new ArrayList<>(entityIdSet)));
		}
		if (StringUtils.isNotBlank(invoiceStatus) && pattern.matcher(invoiceStatus).matches()) {
			filter.getRules().add(new SearchRule("invoiceStatus", Constants.ROP_EQ, Integer.valueOf(invoiceStatus)));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isBlank(applyDateStr)) {
			applyDateStr = sdf.format(new Date()) + " 00:00:00";
		}
		if (StringUtils.isBlank(applyDateEndStr)) {
			applyDateEndStr = sdf.format(new Date()) + " 23:59:59";
		}
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert2(applyDateStr)));
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.convert2(applyDateEndStr)));
		filter.getOrders().add(new SearchOrder("wtime", "desc"));
		return filter;
	}

	/**
	 * 封装dto
	 *
	 * @param invoiceList
	 *            发票对象列表
	 * @return 发票对象dto列表
	 */
	private List<InvoiceDto> buildInvoiceDto(List<Invoice> invoiceList) {
		List<InvoiceDto> dtoList = new ArrayList<>();
		try {
			if (!CollectionUtils.isEmpty(invoiceList)) {
				// 开票信息map { id -> 开票信息 }
				Map<String, String> bankInvoiceMap = new HashMap<>();
				// 公司名称map { entityId -> 公司名称 }
				Map<String, String> entityNameMap = new HashMap<>();
				// 用户姓名map { ossUserId -> 姓名 }
				Map<String, String> realNameMap = new HashMap<>();
				// 遍历查出的发票列表，封装
				for (Invoice invoice : invoiceList) {
					InvoiceDto dto = new InvoiceDto();
					dto.setId(invoice.getId());
					dto.setWtime(invoice.getWtime() == null ? "" : DateUtil.convert(invoice.getWtime(), DateUtil.format1));
					dto.setApplyTime(invoice.getApplyTime() == null ? "" : DateUtil.convert(invoice.getApplyTime(), DateUtil.format1));
					dto.setEntityId(invoice.getEntityId());
					dto.setEntityType(invoice.getEntityType());
					dto.setOssUserId(invoice.getOssUserId());
					// 申请人
					if (StringUtil.isBlank(invoice.getOssUserId())) {
						dto.setRealName("未知");
					} else if (realNameMap.containsKey(invoice.getOssUserId())) {
						dto.setRealName(realNameMap.get(invoice.getOssUserId()));
					} else {
						String name = "未知";
						try {
							User user = userService.read(invoice.getOssUserId());
							if (user != null) {
								name = user.getRealName();
							}
						} catch (ServiceException e) {
							logger.error("", e);
						}
						dto.setRealName(name);
						realNameMap.put(invoice.getOssUserId(), name);
					}
					// 公司名称
					if (StringUtil.isBlank(invoice.getEntityId())) {
						dto.setEntityName("未知");
					} else if (entityNameMap.containsKey(invoice.getEntityId())) {
						dto.setEntityName(entityNameMap.get(invoice.getEntityId()));
					} else {
						String name = "未知";
						try {
							if (EntityType.CUSTOMER.getCode() == invoice.getEntityType()) {
								Customer customer = customerService.read(invoice.getEntityId());
								if (customer != null) {
									name = customer.getCompanyName();
								}
							} else if (EntityType.SUPPLIER.getCode() == invoice.getEntityType() || EntityType.SUPPLIER_DS.getCode() == invoice.getEntityType()) {
								Supplier supplier = supplierService.read(invoice.getEntityId());
								if (supplier != null) {
									name = supplier.getCompanyName();
								}
							}
						} catch (ServiceException e) {
							logger.error("", e);
						}
						dto.setEntityName(name);
						entityNameMap.put(invoice.getEntityId(), name);
					}
					Optional.ofNullable(invoice.getActualReceivables()).ifPresent(e -> dto.setActualReceivables(e.toPlainString()));
					Optional.ofNullable(invoice.getReceivables()).ifPresent(e -> dto.setReceivables(e.toPlainString()));
					dto.setProductId(invoice.getProductId());
					dto.setInvoiceStatus(invoice.getInvoiceStatus());
					dto.setServiceName(invoice.getServiceName());
					dto.setInvoiceType(invoice.getInvoiceType());
					dto.setRemark(invoice.getRemark());
					// 我方开票信息
					if (StringUtil.isBlank(invoice.getBankInvoiceId())) {
						dto.setBankInvoiceInfo("无");
					} else if (bankInvoiceMap.containsKey(invoice.getBankInvoiceId())) {
						dto.setBankInvoiceInfo(bankInvoiceMap.get(invoice.getBankInvoiceId()));
					} else {
						String info = "无";
						try {
							InvoiceInformation bankInvoiceInformation = invoiceInformationService.read(invoice.getBankInvoiceId());
							if (bankInvoiceInformation != null) {
								info = getInvoiceInfo(bankInvoiceInformation);
							}
						} catch (ServiceException e) {
							logger.error("", e);
						}
						dto.setBankInvoiceInfo(info);
						bankInvoiceMap.put(invoice.getBankInvoiceId(), info);
					}
					// 对方开票信息
					if (StringUtil.isBlank(invoice.getOppositeBankInvoiceId())) {
						dto.setOppositeBankInvoiceInfo("无");
					} else if (bankInvoiceMap.containsKey(invoice.getOppositeBankInvoiceId())) {
						dto.setOppositeBankInvoiceInfo(bankInvoiceMap.get(invoice.getOppositeBankInvoiceId()));
					} else {
						String info = "无";
						// 先判断再查询
						try {
							InvoiceInformation bankInvoiceInformation = invoiceInformationService.read(invoice.getOppositeBankInvoiceId());
							if (bankInvoiceInformation != null) {
								info = getInvoiceInfo(bankInvoiceInformation);
							}
						} catch (ServiceException e) {
							logger.error("", e);
						}
						dto.setOppositeBankInvoiceInfo(info);
						bankInvoiceMap.put(invoice.getOppositeBankInvoiceId(), info);
					}
					dtoList.add(dto);
				}
			}
		} catch (Exception e) {
			logger.error("封装发票返回对象异常", e);
		}
		return dtoList;
	}

	/**
	 * 获取开票信息
	 *
	 * @param invoiceInfo
	 *            开票信息对象
	 * @return 开票信息字符串
	 */
	private String getInvoiceInfo(InvoiceInformation invoiceInfo) {
		StringBuilder info = new StringBuilder();
		info.append("公司名称：");
		info.append(invoiceInfo.getCompanyName());
		info.append("，税务号：");
		info.append(invoiceInfo.getTaxNumber());
		info.append("，开户银行：");
		info.append(invoiceInfo.getAccountBank());
		info.append("，银行账号：");
		info.append(invoiceInfo.getBankAccount());
		return info.toString();
	}

	/**
	 * 导出发票，与搜索条件一致
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/exportInvoice")
	public BaseResponse<UploadFileRespDto> exportInvoice() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			logger.error("未登录，获取数据失败");
			return BaseResponse.error("未登录，获取数据失败");
		}
		List<String[]> dataList = new ArrayList<>();
		UploadFileRespDto result = new UploadFileRespDto();
		String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
		String resourceDir = Constants.RESOURCE + File.separator + downLoadFile + File.separator + datePath;
		String fileName = "";
		String filePath = "";
		try {
			// entityId（供应商表的supplierid，customer表的customerid）
			String entityId = request.getParameter("entityId");
			// 实体类型
			String entityType = request.getParameter("entityType");
			// 实体类型
			String companyName = request.getParameter("companyName");
			// 发票状态
			String invoiceStatus = request.getParameter("invoiceStatus");
			// 开票日期
			String applyDateStr = request.getParameter("applyDate");
			String applyDateEndStr = request.getParameter("applyDateEnd");
			// 封装搜索filter
			SearchFilter filter = buildSearchFilter(onlineUser, entityId, entityType, companyName, invoiceStatus, applyDateStr, applyDateEndStr);
			if (null == filter) {
				return BaseResponse.error("导出发票报表失败");
			}
			List<Invoice> invoiceList = invoiceService.queryAllBySearchFilter(filter);
			// 封装返回对象
			List<InvoiceDto> dtoList = buildInvoiceDto(invoiceList);
			if (!CollectionUtils.isEmpty(dtoList)) {
				dataList = dtoList.stream().map(dto -> dto.toExportData(title.length)).collect(Collectors.toList());
			}
			// 导出文件名
			fileName = "发票报表" + DateUtil.convert(new Date(), DateUtil.format8);
			File dir = new File(resourceDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			filePath = resourceDir + File.separator + fileName + ".xls";
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			ParseFile.exportDataToExcel(dataList, file, title);
		} catch (Exception e) {
			logger.error("导出发票异常", e);
			return BaseResponse.error("导出发票异常");
		} finally {
			if (dataList != null) {
				dataList.clear();
				dataList = null;
			}
			result.setFilePath(filePath);
			result.setFileName(fileName + ".xls");
		}
		logger.info("导出发票结束" + result.toString());
		return BaseResponse.success(result);
	}

	/**
	 * 作废开票流程
	 *
	 * @desc 1.当发现账单和发票不正确的时候进行使用； 2.无账单流程只需要直接发票的状态为已作废；
	 *       3.有账单流程在选择好发票，并指定账单后，更改对应的账单金额，并更改发票状态为"已作废"
	 *       4.指定的账单填入的扣减金额的总和必须等于发票的开票金额
	 * @param productBillsDto
	 *            存有选择的发票和账单
	 * @return
	 */
	@PostMapping("voidInvoiceProcess")
	@ResponseBody
	public BaseResponse<String> voidInvoiceProcess(@RequestBody @Valid ProductBillsDto productBillsDto) {
		logger.info("作废发票记录开始");
		boolean result = false;
		try {
			if (validBillAndInvoice(productBillsDto)) {
				return BaseResponse.error("账单数据校验失败");
			}
			Invoice invoice = invoiceService.read(productBillsDto.getId());
			if (invoice.getInvoiceStatus() == InvoiceStatus.INVALID.ordinal()) {
				return BaseResponse.success("已经是作废状态");
			}
			Optional<FlowEnt> flowEntOptional = Optional.ofNullable(flowEntService.read(invoice.getFlowEntId()));
			if (!flowEntOptional.isPresent()) {
				logger.error("当前数据为旧数据，未绑定开票流程id");
				return BaseResponse.error("当前数据为旧数据，未绑定开票流程");
			}
			FlowEnt flowEnt = flowEntOptional.get();
			ErpFlow erpFlow = erpFlowService.read(flowEntOptional.get().getFlowId());
			// 如果是无账单开票流程
			String billType = flowEntService.hasBillOrNot(flowEnt.getFlowMsg(), erpFlow.getFlowClass());
			if (Constants.NO_BILL.equals(billType)) {
				logger.info("发票来自于无账单开票，不需要更新账单已开金额");
			} else if (Constants.HAS_BILL.equals(billType)) {
				// 还原账单已开票金额
				boolean restoreResult = productBillsService.restoreBill(productBillsDto);
				String msg = "更新账单的已开金额" + (restoreResult ? "成功" : "失败");
				logger.info("发票来自于账单开票流程，" + msg);
				if (!restoreResult) {
					return BaseResponse.error(msg);
				}
			} else {
				return BaseResponse.error("流程信息不正确");
			}
			// 更改发票为已作废状态
			invoice.setInvoiceStatus(InvoiceStatus.INVALID.ordinal());
			if (StringUtil.isNotBlank(invoice.getRemark())) {
				invoice.setRemark(invoice.getRemark() + "【作废备注：" + productBillsDto.getRemark() + "】");
			} else {
				invoice.setRemark("【作废备注：" + productBillsDto.getRemark() + "】");
			}
			result = invoiceService.update(invoice);
			logger.info("更新发票状态为已作废" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("作废发票记录异常", e);
			return BaseResponse.error("作废异常");
		}
		return BaseResponse.success("作废发票" + (result ? "成功" : "失败"));
	}

	/**
	 * 校验账单和发票的数据是否正确
	 * 
	 * @desc 满足如下两个条件：1.所有账单扣减金额之和=发票开票金额；2.每个账单的扣减金额必须小于等于自身的本次开票金额
	 * @param productBillsDto
	 * @return true校验失败 false校验成功
	 * @throws Exception
	 */
	private boolean validBillAndInvoice(ProductBillsDto productBillsDto) throws Exception {
		final BigDecimal[] totalDeductionAmount = {new BigDecimal("0").setScale(4)};
		List<String> wrongBill = new ArrayList<>();
		Optional.ofNullable(productBillsDto.getProductBillsJSONObjectList()).orElse(new ArrayList<>()).stream().collect(Collectors.toList())
				.forEach(productBills -> {
					if (productBills.getDeductionAmount().compareTo(productBillsDto.getReceivables()) == 1) {
						wrongBill.add(productBills.getId());
					}
					totalDeductionAmount[0] = totalDeductionAmount[0].add(productBills.getDeductionAmount());
				});
		if (!totalDeductionAmount[0].equals(productBillsDto.getReceivables().setScale(4))) {
			logger.error("存在数据错误账单，扣减金额总额不等于开票金额");
			return true;
		}
		if (wrongBill != null && wrongBill.size() > 0) {
			logger.error("存在数据错误账单，扣减金额大于本次开票金额，对应账单为：" + StringUtils.join(wrongBill, ","));
			return true;
		}
		return false;
	}

	/**
	 * 获取作废发票流程中涉及到的所有账单
	 */
	@ResponseBody
	@PostMapping("/getVoidInvoiceBills")
	public BaseResponse<ProductBillsDto> getVoidInvoiceBills(@RequestParam String invoiceId) {
		logger.info("获取发票对应的账单信息开始");
		ProductBillsDto productBills = new ProductBillsDto();
		try {
			Invoice invoice = invoiceService.read(invoiceId);
			if (null == invoice) {
				logger.info("发票不存在，invoiceId：" + invoiceId);
				return BaseResponse.error("发票不存在");
			}
			// 从发票对应的开票流程中，获取开票时勾选的账单
			if (StringUtil.isNotBlank(invoice.getFlowEntId())) {
				FlowEnt flowEnt = flowEntService.read(invoice.getFlowEntId());
				productBills.setReceivables(invoice.getReceivables());
				productBills.setProductBillsJSONObjectList(flowEnt.getProductBillsListFromJSON());
			}
		} catch (Exception e) {
			logger.error("获取发票对应账单异常");
		}
		return BaseResponse.success(productBills);
	}
}
