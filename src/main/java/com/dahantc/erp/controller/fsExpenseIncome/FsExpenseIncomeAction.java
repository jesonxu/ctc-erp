package com.dahantc.erp.controller.fsExpenseIncome;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.NumberUtils;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.fsExpenseIncome.AssociateBillPageParam;
import com.dahantc.erp.dto.fsExpenseIncome.ExpenseIncomePageParam;
import com.dahantc.erp.dto.fsExpenseIncome.FileMd5Info;
import com.dahantc.erp.dto.fsExpenseIncome.FsExpenseIncomeDto;
import com.dahantc.erp.dto.fsExpenseIncome.ProductBillInfo;
import com.dahantc.erp.dto.fsExpenseIncome.UploadFileInfoResp;
import com.dahantc.erp.dto.operate.UploadFileRespDto;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FeeType;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.enums.RelateStatus;
import com.dahantc.erp.task.AutoWriteOffBillTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.FileMd5Utl;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.accountbalance.service.IAccountBalanceService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 财务的收支导入
 */
@Controller
@RequestMapping("/fsExpenseIncome")
public class FsExpenseIncomeAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(FsExpenseIncomeAction.class);

	private static final String UPLOAD_FILE = "upFile/fsexpendincome";

	/** 查询供应商账单SQL */
	private static final String PRODUCT_BILL_FOR_SUPPLIER_SQL = "SELECT b.id , b.actualpayables , b.actualreceivables ,"
			+ "  b.payables , b.productid , b.receivables , b.entityid, b.entitytype, s.companyname,s.deptid,p.productname,b.wtime "
			+ "  FROM erp_bill b INNER JOIN erp_supplier s ON b.entityid = s.supplierid" + "  INNER JOIN erp_product p ON p.productid = b.productid ";

	/** 查询客户账单SQL */
	private static final String PRODUCT_BILL_FOR_CUSTOMER_SQL = "SELECT b.id , b.actualpayables , b.actualreceivables ,"
			+ "  b.payables , b.productid , b.receivables , b.entityid, b.entitytype, s.companyname ,s.deptid ,p.productname,b.wtime "
			+ "  FROM erp_bill b INNER JOIN erp_customer s ON b.entityid = s.customerid  "
			+ "  INNER JOIN erp_customer_product p ON p.productid = b.productid  ";

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private AutoWriteOffBillTask autoWriteOffBillTask;

	@Autowired
	private IAccountBalanceService accountBalanceService;

	/**
	 * 
	 * @param customerId
	 * @param productId
	 * @return
	 */
	@PostMapping("/readFsExpenseIncomesByProduct")
	@ResponseBody
	public BaseResponse<List<FsExpenseIncomeDto>> readFsExpenseIncomesByProduct(@RequestParam(required = false) String customerId,
			@RequestParam(required = false) String productId, @RequestParam(required = false) String ids, @RequestParam(required = false) String self) {
		List<FsExpenseIncomeDto> dtos = new ArrayList<>();
		try {
			if (StringUtils.isBlank(customerId)) {
				try {
					if (StringUtils.isBlank(productId)) {
						return BaseResponse.success(dtos); 
					}
					customerId = customerProductService.read(productId).getCustomerId();
				} catch (Exception e) {
					logger.info("id为" + productId + "产品不存在");
				}
			}
			Customer customer = customerService.read(customerId);
			if (customer == null) {
				logger.info("id为" + customerId + "客户不存在");
				return BaseResponse.success(dtos);
			}
			if (!StringUtils.equals(customer.getOssuserId(), getOnlineUser().getOssUserId())) {
				return BaseResponse.success(dtos);
			}

			SearchFilter filter = new SearchFilter();
			if (StringUtils.equals("T", self)) {
				if (StringUtils.isBlank(ids)) {
					filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				} else {
					filter.getRules().add(new SearchRule("id", Constants.ROP_IN, ids.split(",")));
				}
			} else {
				filter.getOrRules().add(new SearchRule[] { new SearchRule("relateStatus", Constants.ROP_EQ, RelateStatus.UNRELATE.ordinal()),
						new SearchRule("customerId", Constants.ROP_EQ, customerId) });
			}
			filter.getRules().add(new SearchRule("isIncome", Constants.ROP_EQ, 0));
			filter.getRules().add(new SearchRule("remainRelatedCost", Constants.ROP_GT, BigDecimal.ZERO));
			filter.getOrders().add(new SearchOrder("relateStatus", Constants.ROP_DESC));
			filter.getOrders().add(new SearchOrder("operateTime", Constants.ROP_ASC));
			List<FsExpenseIncome> result = fsExpenseIncomeService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(result)) {
				return BaseResponse.success(result.stream().map(fsExpenseIncome -> {
					return new FsExpenseIncomeDto(fsExpenseIncome);
				}).collect(Collectors.toList()));
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return BaseResponse.success(dtos);
	}

	/**
	 * 跳转至 财务数据导入页面（数据展示页面）
	 */
	@RequestMapping("/toImportPage")
	public String toImportPage(String from, String year) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null) {
			return "/";
		}
		Map<String, Boolean> pagePermission = null;
		try {
			pagePermission = roleService.getPagePermission(onlineUser.getRoleId());
		} catch (ServiceException e) {
			logger.error("查询用户的权限出现异常", e);
		}
		request.setAttribute("from", from);
		request.setAttribute("year", year);
		// 获取用户的页面权限
		request.setAttribute("pagePermission", pagePermission);
		return "/views/sheet/importExpenseIncomeSheet";
	}

	/**
	 * 跳转至 财务数据导入页面（数据展示页面）
	 */
	@RequestMapping("/toIncomeSheet")
	public String toIncomeSheet() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null) {
			return "/";
		}
		Map<String, Boolean> pagePermission = null;
		boolean isSale = StringUtils.contains(onlineUser.getUser().getJobType(), JobType.Sales.name());
		try {
			pagePermission = roleService.getPagePermission(onlineUser.getRoleId());
		} catch (ServiceException e) {
			logger.error("查询用户的权限出现异常", e);
		}
		// 获取用户的页面权限
		request.setAttribute("pagePermission", pagePermission);
		request.setAttribute("isSale", isSale);
		return "/views/sheet/importExpenseIncomeSheet";
	}

	/**
	 * 跳转至 财务数据导入页面(Excel上传页面)
	 */
	@RequestMapping("/toUploadPage")
	public String toUploadPage(@RequestParam("year") String year) {
		request.setAttribute("year", year);
		return "/views/fsExpenseIncome/importExpenseIncome";
	}

	/** 关联 */
	@RequestMapping("/toAssociate")
	public String toAssociate(@RequestParam("expenseIncomeId") String expenseIncomeId, @RequestParam("from") String from) {
		FsExpenseIncome fsExpenseIncome = null;
		try {
			fsExpenseIncome = fsExpenseIncomeService.read(expenseIncomeId);
		} catch (ServiceException e) {
			logger.error("根据Id查询导入收入异常", e);
		}
		request.setAttribute("expenseIncome", fsExpenseIncome);
		request.setAttribute("from", from);
		return "/views/fsExpenseIncome/expenseAssociate";
	}

	/** 关联 */
	@RequestMapping("/toCustomerAssociate")
	public String toCustomerAssociate(@RequestParam("expenseIncomeId") String expenseIncomeId, @RequestParam("from") String from) {
		FsExpenseIncome fsExpenseIncome = null;
		try {
			fsExpenseIncome = fsExpenseIncomeService.read(expenseIncomeId);
		} catch (ServiceException e) {
			logger.error("根据Id查询导入收入异常", e);
		}
		if (fsExpenseIncome == null || fsExpenseIncome.getIsIncome() == 1) {
			return "";
		}
		request.setAttribute("expenseIncome", fsExpenseIncome);
		request.setAttribute("from", from);
		return "/views/fsExpenseIncome/customerAssociate";
	}

	@ResponseBody
	@GetMapping("/exportFsExpenseIncome")
	public BaseResponse<UploadFileRespDto> exportFsExpenseIncome(ExpenseIncomePageParam pageParam) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		logger.info("导出页面收支数据开始");
		long start = System.currentTimeMillis();
		String realName = "";
		if (onlineUser == null || onlineUser.getUser() == null) {
			logger.error("未登录，获取数据失败");
			return BaseResponse.error("未登录，获取数据失败");
		}
		try {
			realName = onlineUser.getUser().getRealName();
			List<Customer> customerList = customerService.readCustomers(onlineUser, "", "", "", "");
			// 封装searchFilter查询条件
			SearchFilter searchFilter = buildSearchFilter(pageParam, customerList);
			String fileName = "FsExpenseIncome-" + DateUtil.convert(new Date(), DateUtil.format8);
			UploadFileRespDto export = fsExpenseIncomeService.exportFsExpenseIncome(searchFilter, fileName);
			return BaseResponse.success(export);
		} catch (Exception e) {
			logger.error("导出页面查询收支导入数据异常,请联系管理员", e);
			return BaseResponse.error("导出页面查询收支导入数据异常,请联系管理员");
		} finally {
			logger.info("用户【" + realName + "】导出页面收支数据结束,共耗时:" + (System.currentTimeMillis() - start));
		}
	}

	/**
	 * 分页查询导入的财务收支记录
	 */
	@ResponseBody
	@RequestMapping("/queryExpenseIncomePage")
	public PageResult<FsExpenseIncomeDto> queryExpenseIncomePage(ExpenseIncomePageParam pageParam) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		PageResult<FsExpenseIncomeDto> result = new PageResult<>();
		logger.info(pageParam.getFrom() + " 页面查询收支导入数据");
		if (onlineUser == null || onlineUser.getUser() == null) {
			result.setMsg("未登录，获取数据失败");
			return result;
		}
		try {
			// 按权限查客户
			List<Customer> customerList = customerService.readCustomers(onlineUser, "", "", "", "", pageParam.getCompanyName());
			// 封装searchFilter查询条件
			SearchFilter searchFilter = buildSearchFilter(pageParam, customerList);
			PageResult<FsExpenseIncome> pageResult = fsExpenseIncomeService.queryByPages(pageParam.getPageSize(), pageParam.getPage(), searchFilter);
			if (pageResult != null && pageResult.getData() != null && !pageResult.getData().isEmpty()) {
				// 读取客户名称 {customerId -> companyName}
				Map<String, String> companyNameMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(customerList)) {
					customerList.forEach(customer -> {
						companyNameMap.put(customer.getCustomerId(), customer.getCompanyName());
					});
				}

				// 客户的付费类型
				List<CustomerProduct> allProductList = customerProductService.queryAllByFilter(null);
				// 产品按客户分组 {客户id -> [产品]}
				Map<String, List<CustomerProduct>> groupMap = null;
				if (!CollectionUtils.isEmpty(allProductList)) {
					groupMap = allProductList.stream().collect(Collectors.groupingBy(CustomerProduct::getCustomerId));
				}
				// 获取每个客户的付费方式
				Map<String, String> cacheSettleTypeMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(groupMap)) {
					groupMap.forEach((customerId, products) -> {
						if (CollectionUtils.isEmpty(products)) {
							cacheSettleTypeMap.put(customerId, "暂无产品");
						} else {
							int lastSettleType = -1;
							for (CustomerProduct product : products) {
								if (lastSettleType == -1) {
									lastSettleType = product.getSettleType();
								} else if (lastSettleType != product.getSettleType()) {
									cacheSettleTypeMap.put(customerId, "预付+后付");
									return;
								}
							}
							if (!cacheSettleTypeMap.containsKey(customerId)) {
								cacheSettleTypeMap.put(customerId, lastSettleType == 2 ? "后付" : "预付");
							}
						}
					});
				}

				List<FsExpenseIncome> pageList = pageResult.getData();
				// 导入人
				List<String> userIds = pageList.stream().map(FsExpenseIncome::getUserId).collect(Collectors.toList());
				SearchFilter userFilter = new SearchFilter();
				userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIds));
				List<User> users = userService.queryAllBySearchFilter(userFilter);
				Map<String, String> userNames = users.stream().collect(Collectors.toMap(User::getOssUserId, User::getRealName));
				result.setData(pageList.stream().map(fsExpenseIncome -> {
					FsExpenseIncomeDto fsExpenseIncomeDto = new FsExpenseIncomeDto(fsExpenseIncome);
					fsExpenseIncomeDto.setCreatorName(userNames.get(fsExpenseIncome.getUserId()));
					if (StringUtils.isNotBlank(fsExpenseIncomeDto.getCustomerId()) && companyNameMap.get(fsExpenseIncomeDto.getCustomerId()) != null) {
						fsExpenseIncomeDto.setCustomerName(companyNameMap.get(fsExpenseIncomeDto.getCustomerId()));
						fsExpenseIncomeDto.setSettleType(StringUtils.isBlank(cacheSettleTypeMap.get(fsExpenseIncomeDto.getCustomerId())) ? "暂无产品"
								: cacheSettleTypeMap.get(fsExpenseIncomeDto.getCustomerId()));
					}
					return fsExpenseIncomeDto;
				}).collect(Collectors.toList()));
				result.setCount(pageResult.getCount());
				result.setCurrentPage(pageResult.getCurrentPage());
				result.setMsg(pageResult.getMsg());
				result.setCode(pageResult.getCode());
				result.setTotalPages(pageResult.getTotalPages());
			}
		} catch (ServiceException e) {
			logger.error("分页查询导入的收支数据异常", e);
		}
		return result;
	}

	/**
	 * 封装searchFilter查询条件
	 * 
	 * @param pageParam
	 * @param customerList
	 * @return
	 * @throws ServiceException
	 */
	private SearchFilter buildSearchFilter(ExpenseIncomePageParam pageParam, List<Customer> customerList) throws ServiceException {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getOrders().add(new SearchOrder("customerId", Constants.ROP_DESC));
		// 查关联到用户数据权限下的客户，或未关联的到款
		if (!CollectionUtils.isEmpty(customerList)) {
			List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
			searchFilter.getOrRules().add(new SearchRule[] { new SearchRule("customerId", Constants.ROP_IN, customerIdList),
					new SearchRule("relateStatus", Constants.ROP_EQ, RelateStatus.UNRELATE.ordinal()), new SearchRule("customerId", Constants.ROP_EQ, null) });
		} else {
			searchFilter.getOrRules().add(new SearchRule[] { new SearchRule("relateStatus", Constants.ROP_EQ, RelateStatus.UNRELATE.ordinal()),
					new SearchRule("customerId", Constants.ROP_EQ, null) });
		}

		if (StringUtils.isNotBlank(pageParam.getWriteoffstatus())) {
			// 销账状态
			String[] ustates = pageParam.getWriteoffstatus().split(",");
			// 未销账 剩余金额>0；已销账 剩余金额=0
			if (ustates.length == 1) {
				searchFilter.getRules().add(new SearchRule("remainRelatedCost",
						(("" + BillStatus.RECONILED.ordinal()).equals(ustates[0]) ? Constants.ROP_GT : Constants.ROP_EQ), BigDecimal.ZERO));
			}
		} else if (StringUtil.isNotBlank(pageParam.getCheckOut())) {
			// 核销状态，只查未核销完的到款（未核销，部分核销）
			searchFilter.getRules().add(new SearchRule("remainCheckOut", Constants.ROP_GT, BigDecimal.ZERO));
		} else {
			// 默认查未销账
			searchFilter.getRules().add(new SearchRule("remainRelatedCost", Constants.ROP_GT, BigDecimal.ZERO));
		}
		// 关联状态
		if (StringUtils.isNotBlank(pageParam.getLinkStatus())) {
			String[] linkStatusStrArr = pageParam.getLinkStatus().split(",");
			if (linkStatusStrArr.length == 1 && NumberUtils.isNumber(linkStatusStrArr[0])) {
				searchFilter.getRules().add(new SearchRule("relateStatus", Constants.ROP_EQ, Integer.valueOf(linkStatusStrArr[0])));
			}
		}
		String year = pageParam.getYear();
		if (StringUtil.isBlank(year)) {
			year = DateUtil.convert(new Date(), DateUtil.format11);
		}
		// 年份第一天
		String startTime = year + "-01-01 00:00:00";
		// 下一年的第一天
		String endTime = (Integer.parseInt(year) + 1) + "-01-01 00:00:00";
		// 开始时间
		if (StringUtil.isNotBlank(pageParam.getStartTime())) {
			startTime = pageParam.getStartTime() + " 00:00:00";
		}
		// 结束时间
		if (StringUtil.isNotBlank(pageParam.getEndTime())) {
			endTime = pageParam.getEndTime() + " 23:59:59";
		}
		// 时间类型 1-到账时间 2-导入时间 默认到账时间
		String timeType = pageParam.getTimeType();
		if (StringUtils.isBlank(timeType) || "1".equals(timeType)) {
			searchFilter.getRules().add(new SearchRule("operateTime", Constants.ROP_GE, DateUtil.convert(startTime, DateUtil.format2)));
			searchFilter.getRules().add(new SearchRule("operateTime", Constants.ROP_LT, DateUtil.convert(endTime, DateUtil.format2)));
			searchFilter.getOrders().add(new SearchOrder("operateTime", Constants.ROP_DESC));
		} else if ("2".equals(timeType)) {
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert(startTime, DateUtil.format2)));
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.convert(endTime, DateUtil.format2)));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		}
		// 内容摘要
		String depict = pageParam.getDepict();
		if (StringUtils.isNotBlank(depict)) {
			searchFilter.getRules().add(new SearchRule("depict", Constants.ROP_CN, depict));
		}
		if (StringUtil.isNotBlank(pageParam.getBankName())) {
			searchFilter.getRules().add(new SearchRule("bankName", Constants.ROP_CN, pageParam.getBankName()));
		}
		return searchFilter;
	}

	/**
	 * 上传消费支出Excel
	 */
	@ResponseBody
	@RequestMapping("/uploadExpenseIncome")
	public BaseResponse<Boolean> uploadExpenseIncome(String fileInfos) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null) {
			return BaseResponse.error("未登录，请先登录");
		}
		if (StringUtil.isBlank(fileInfos)) {
			return BaseResponse.error("请选择上传文件");
		}
		List<UploadFileInfoResp> uploadFileInfos = null;
		try {
			uploadFileInfos = JSON.parseArray(fileInfos, UploadFileInfoResp.class);
		} catch (Exception e) {
			logger.error("请求参数解析异常");
		}
		if (uploadFileInfos == null || uploadFileInfos.isEmpty()) {
			return BaseResponse.error("请选择上传文件");
		}
		StringBuilder resultMsg = new StringBuilder();
		try {
			// 上个月和本月的
			List<String> sheetFilter = new ArrayList<>();
			int month = Calendar.getInstance().get(Calendar.MONTH);
			// 本月
			String thisMonth = (month + 1) + "月";
			String lastMonth = (month == 0 ? 12 : month) + "月";
			sheetFilter.add(thisMonth);
			sheetFilter.add(lastMonth);
			if (thisMonth.length() < 3) {
				sheetFilter.add("0" + thisMonth);
			}
			if (lastMonth.length() < 3) {
				sheetFilter.add("0" + lastMonth);
			}
			// 解析Excel
			List<String[]> priceList = new ArrayList<>();
			// 失败文件名称
			List<String> failFiles = new ArrayList<>();
			// 已经解析过的文件
			List<String> parsedFiles = new ArrayList<>();
			for (UploadFileInfoResp uploadFileInfo : uploadFileInfos) {
				String fileName = uploadFileInfo.getFileName();
				String path = uploadFileInfo.getFilePath();
				boolean canParse = FileMd5Utl.readAndUpdate(uploadFileInfo.getMd5());
				if (!canParse) {
					parsedFiles.add(fileName);
					continue;
				}
				// 可以解析
				if (path.lastIndexOf(".") >= 0) {
					String fileType = path.substring(path.lastIndexOf(".") + 1);
					if ("xls".equals(fileType)) {
						// 解析excel2003文件
						priceList.addAll(ParseFile.parseExcel2003(new File(path), sheetFilter));
					} else if ("xlsx".equals(fileType)) {
						// 解析excel2007文件
						priceList.addAll(ParseFile.parseExcel2007(new File(path), sheetFilter, DateUtil.format1));
					} else {
						failFiles.add(fileName);
					}
				} else {
					failFiles.add(fileName);
				}
			}
			if (!priceList.isEmpty()) {
				resultMsg.append("上传成功：");
				List<FsExpenseIncome> importInfos = new ArrayList<>(priceList.size());
				List<String> existSerialNumbers = new ArrayList<>();
				for (String[] priceInfo : priceList) {
					// 一条数据到收入那一列，至少有10列，第11列是支出，第12列是备注
					if (priceInfo.length < 10) {
						logger.info("本条数据不完整，跳过数据：" + JSON.toJSONString(priceInfo));
						continue;
					}
					// 流水号，唯一标识一条数据
					String serialNumber = priceInfo[1];
					if (StringUtil.isBlank(serialNumber)) {
						logger.info("本条数据的流水号不正确，跳过数据：" + JSON.toJSONString(priceInfo));
						continue;
					}
					// 去重一，查本次导入的前面读取的数据有没有本条数据的流水号
					if (existSerialNumbers.contains(serialNumber)) {
						logger.info("本条数据的流水号重复，跳过数据：" + JSON.toJSONString(priceInfo));
						continue;
					}
					existSerialNumbers.add(serialNumber);
					// 去重二，查表中有没有本条数据的流水号
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("serialNumber", Constants.ROP_EQ, serialNumber));
					filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
					List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(filter);
					if (!ListUtils.isEmpty(incomeList)) {
						logger.info("本条数据的流水号已导入过，跳过数据：" + JSON.toJSONString(priceInfo));
						continue;
					}
					// 去重之后，将一条数据转换为一个对象
					FsExpenseIncome expenseIncome = new FsExpenseIncome();
					expenseIncome.setWtime(new Timestamp(System.currentTimeMillis()));
					// 导入用户
					expenseIncome.setUserId(onlineUser.getUser().getOssUserId());
					expenseIncome.setUserDeptId(onlineUser.getUser().getDeptId());
					// 到款时间
					String receiveTime = priceInfo[0];
					if (StringUtil.isNotBlank(receiveTime)) {
						Date time;
						if (receiveTime.contains("-")) {
							time = DateUtil.convert(receiveTime, DateUtil.format1);
						} else {
							time = DateUtil.convert(receiveTime, DateUtil.format12);
						}
						if (time != null) {
							expenseIncome.setOperateTime(new Timestamp(time.getTime()));
						}
					}
					expenseIncome.setSerialNumber(priceInfo[1]);
					expenseIncome.setBankName(priceInfo[2]);
					// 部门名称
					String deptName = priceInfo[4];
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("deptname", Constants.ROP_EQ, deptName));
					searchFilter.getRules().add(new SearchRule("flag", Constants.ROP_EQ, 0));
					List<Department> departments = departmentService.queryAllBySearchFilter(searchFilter);
					if (departments != null && !departments.isEmpty()) {
						// 利用第一个作为推荐部门数据
						Department department = departments.get(0);
						expenseIncome.setDeptId(department.getDeptid());
						expenseIncome.setDeptName(department.getDeptname());
					}
					expenseIncome.setRegionName(priceInfo[5]);
					// 收入分类
					String priceType = priceInfo[6];
					if (StringUtil.isNotBlank(priceType)) {
						Optional<FeeType> feeTypeOptional = FeeType.getEnumsByMsg(priceType);
						feeTypeOptional.ifPresent(feeType -> expenseIncome.setFeeType(feeType.getCode()));
					}
					expenseIncome.setOperator(priceInfo[7]);
					// 摘要
					priceInfo[8] = StringUtils.isBlank(priceInfo[8]) ? "" : priceInfo[8].trim();
					expenseIncome.setDepict(priceInfo[8]);
					// 收入
					BigDecimal income = null;
					if (StringUtil.isNotBlank(priceInfo[9]) && NumberUtils.isNumber(priceInfo[9])) {
						income = new BigDecimal(priceInfo[9]);
					}
					// 支出
					BigDecimal expense = null;
					if (priceInfo.length > 10) {
						if (StringUtil.isNotBlank(priceInfo[10]) && NumberUtils.isNumeric(priceInfo[10])) {
							expense = new BigDecimal(priceInfo[10]);
						}
					}
					if (income == null && expense == null) {
						logger.info("本条数据的收支金额未填写，跳过数据：" + JSON.toJSONString(priceInfo));
						continue;
					}
					// 是否是收入: 0--是,1--否(支出)
					if (income != null) {
						expenseIncome.setIsIncome(0);
						expenseIncome.setCost(income);
						expenseIncome.setRemainRelatedCost(income);
						expenseIncome.setRemainCheckOut(income);
					} else {
						expenseIncome.setIsIncome(1);
						expenseIncome.setCost(expense);
						expenseIncome.setRemainRelatedCost(expense);
					}
					if (priceInfo.length > 11) {
						String remark = priceInfo[11];
						if (StringUtil.isNotBlank(remark)) {
							expenseIncome.setRemark(remark);
						}
					}
					importInfos.add(expenseIncome);
				}
				if (!importInfos.isEmpty()) {
					boolean saveResult = fsExpenseIncomeService.saveByBatch(importInfos);
					autoWriteOffBillTask.interrupt();
					if (!saveResult) {
						logger.error("保存导入到款信息失败" + JSON.toJSONString(importInfos));
						return BaseResponse.error("保存信息异常");
					}
					logger.info("导入收支数据成功，解析成功总数：" + importInfos.size());
					resultMsg.append("解析成功总数：").append(priceList.size()).append("，上传成功：").append(importInfos.size());
				}
			} else {
				resultMsg.append(" 上传失败 ");
				if (!parsedFiles.isEmpty()) {
					resultMsg.append(String.join(",", parsedFiles)).append("，已经上传过，请勿再次上传");
				}
			}
			if (!failFiles.isEmpty()) {
				resultMsg.append("文件[").append(String.join(",", failFiles)).append("]格式错误");
			}
		} catch (Exception e) {
			logger.error("上传财务收支表出错", e);
			resultMsg.append("解析异常，上传失败");
		}
		return BaseResponse.success(resultMsg.toString());
	}

	/** 上传文件 */
	@PostMapping("/upLoadFile")
	@ResponseBody
	public BaseResponse<List<UploadFileInfoResp>> uploadFile(@RequestParam("file") MultipartFile[] files) {
		List<UploadFileInfoResp> uploadFileInfos = new ArrayList<>();
		// 文件的Md5信息
		Map<String, FileMd5Info> fileMd5Infos = new HashMap<>();
		// 上传文件的描述
		StringBuilder fileDepict = new StringBuilder();
		try {
			if (files != null && files.length > 0) {
				fileMd5Infos = FileMd5Utl.readMd5();
				for (MultipartFile multipartFile : files) {
					// 文件的Md5编码
					String md5Val = DigestUtils.md5Hex(multipartFile.getInputStream());
					String docFileName = multipartFile.getOriginalFilename();
					FileMd5Info fileMd5Info = fileMd5Infos.get(md5Val);
					long now = System.currentTimeMillis();
					// 一周内没有上传 或者上传一小时后，没有解析
					if (fileMd5Info == null || (!fileMd5Info.getParsed() && (now - fileMd5Info.getUploadTime()) > 1000 * 60 * 60)) {
						UploadFileRespDto dto = new UploadFileRespDto();
						if (StringUtils.isNotBlank(docFileName)) {
							String ext = docFileName.substring(docFileName.lastIndexOf(".") + 1);
							String reName = System.currentTimeMillis() + md5Val + "." + ext;
							String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
							String resourceDir = Constants.RESOURCE + File.separator + UPLOAD_FILE + File.separator + datePath;
							File dir = new File(resourceDir);
							if (!dir.exists()) {
								boolean fileCreateResult = dir.mkdirs();
								if (!fileCreateResult) {
									fileDepict.append(" 文件").append(docFileName).append("上传失败 ");
									continue;
								}
							}
							String disPath = resourceDir + File.separator + reName;
							File disFile = new File(disPath);
							multipartFile.transferTo(disFile);
							dto.setFileName(docFileName);
							dto.setFilePath(disPath);
							uploadFileInfos.add(new UploadFileInfoResp(docFileName, disPath, md5Val));
							fileMd5Infos.put(md5Val, new FileMd5Info(docFileName, disPath, System.currentTimeMillis(), false));
						}
					} else {
						logger.info("文件已存在：" + JSON.toJSONString(fileMd5Infos.get(md5Val)));
						String uploadTime = DateUtil.convert(fileMd5Info.getUploadTime(), DateUtil.format2);
						fileDepict.append(" 文件").append(docFileName).append("在").append(uploadTime).append("上传 ");
					}
					if (!uploadFileInfos.isEmpty()) {
						FileMd5Utl.writeMd5(fileMd5Infos);
					} else {
						return BaseResponse.error("上传失败：" + fileDepict.toString());
					}
				}
			} else {
				logger.info("上传文件失败");
				return BaseResponse.error("上传失败：" + fileDepict.toString());
			}
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
			return BaseResponse.error("上传异常");
		} finally {
			if (fileMd5Infos != null) {
				fileMd5Infos.clear();
				fileMd5Infos = null;
			}
		}
		return BaseResponse.success("上传成功：" + fileDepict.toString(), uploadFileInfos);
	}

	/** 关闭弹出框的时候 删除上传文件信息 */
	@ResponseBody
	@RequestMapping("/delUploadFileInfo")
	public BaseResponse<Boolean> delUploadFileInfo(String md5s) {
		if (StringUtil.isBlank(md5s)) {
			return BaseResponse.error("请求参数错误");
		}
		List<String> fileMd5List = Arrays.asList(md5s.split(","));
		return FileMd5Utl.readAndDelete(fileMd5List);
	}

	/** 绑定导入收入和账单 */
	@ResponseBody
	@RequestMapping(value = "/bindFsExpenseIncome")
	public BaseResponse<Boolean> bindFsExpenseIncome(String productBillId, String id, BigDecimal cost) {
		BaseResponse<Boolean> response = BaseResponse.error("关联失败");
		logger.info("查询财务收支记录开始");
		try {
			long startTime = System.currentTimeMillis();
			if (cost == null || new BigDecimal("0").compareTo(cost) >= 0) {
				response.setMsg("关联金额不能为0");
				return response;
			}
			response = fsExpenseIncomeService.saveBindFsExpenseIncome(productBillId, id, cost);
			logger.info("查询财务收支记录结束，耗时：" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			logger.info("查询财务收支记录异常", e);
		}
		return response;
	}

	/** 绑定导入收入和客户 */
	@ResponseBody
	@RequestMapping(value = "/bindCustomer")
	public BaseResponse<Boolean> bindCustomer(String customerId, String incomeId) {
		BaseResponse<Boolean> response = BaseResponse.error("关联失败");
		logger.info("关联客户开始");
		try {
			long startTime = System.currentTimeMillis();
			FsExpenseIncome fsExpenseIncome = fsExpenseIncomeService.read(incomeId);
			Customer customer = customerService.read(customerId);
			if (fsExpenseIncome == null || fsExpenseIncome.getIsIncome() != 0 || customer == null) {
				return response;
			}
			fsExpenseIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
			fsExpenseIncome.setCustomerId(customerId);
			fsExpenseIncome.setDeptId(customer.getDeptId());
			Department department = departmentService.read(customer.getDeptId());
			if (null != department) {
				fsExpenseIncome.setDeptName(department.getDeptname());
			} else {
				fsExpenseIncome.setDeptName(null);
			}
			boolean flag = fsExpenseIncomeService.update(fsExpenseIncome);
			if (flag) {
				// 通知到销售
				User user = userService.read(customer.getOssuserId());
				if (user != null) {
					WeixinMessage.sendMessageByMobile(user.getContactMobile(),
							"您有一笔到款：到款信息【" + fsExpenseIncome.getDepict() + "】，到款时间【" + DateUtil.convert(fsExpenseIncome.getOperateTime(), DateUtil.format1)
									+ "】，到款金额【" + fsExpenseIncome.getCost() + "】，关联客户【" + customer.getCompanyName() + "】成功，请确认并尽快销账！");
				}

				return BaseResponse.success("关联成功");
			}
			logger.info("查询财务收支记录结束，耗时：" + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			logger.info("查询财务收支记录异常", e);
		}
		return response;
	}

	/** 根据权限查询系统账单信息 */
	@ResponseBody
	@PostMapping("/readProductBills")
	public PageResult<ProductBillInfo> readProductBills(AssociateBillPageParam associateBill) {
		String customerId = request.getParameter("customerId");
		PageResult<ProductBillInfo> result = new PageResult<>();
		result.setTotalPages(0);
		result.setCount(0);
		result.setCurrentPage(1);
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (onlineUser == null || onlineUser.getUser() == null || associateBill == null) {
			return result;
		}
		if (associateBill.paramCheck() != null) {
			result.setMsg(associateBill.paramCheck());
			return result;
		}
		result.setCurrentPage(associateBill.getPage());
		try {
			String producIds = null;
			if (StringUtils.isNotBlank(customerId)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				List<CustomerProduct> list = customerProductService.queryAllByFilter(filter);
				if (!CollectionUtils.isEmpty(list)) {
					producIds = list.stream().map(CustomerProduct::getCustomerId).map(id -> "'" + id + "'").collect(Collectors.joining(","));
				}
			}
			Role role = roleService.read(onlineUser.getRoleId());
			if (role == null) {
				return result;
			}
			StringBuilder productBillSql = new StringBuilder();
			if (!associateBill.fromCustomer()) {
				productBillSql.append(PRODUCT_BILL_FOR_SUPPLIER_SQL);
			} else {
				productBillSql.append(PRODUCT_BILL_FOR_CUSTOMER_SQL);
			}
			if (associateBill.getIncome() == 0) {
				// 收入
				productBillSql.append(" where b.receivables > b.actualreceivables ");
			} else {
				// 支出
				productBillSql.append(" where b.payables > b.actualpayables ");
			}
			if (StringUtils.isNotBlank(producIds)) {
				productBillSql.append(" AND b.productId IN(").append(producIds).append(")");
			}
			// 主题的Id
			List<String> deptIds = new ArrayList<>();
			int dataPermission = role.getDataPermission();
			if (dataPermission == DataPermission.Self.ordinal()) {
				// 自己
				productBillSql.append(" AND s.ossuserid = '").append(onlineUser.getUser().getOssUserId()).append("' ");
			} else if (dataPermission == DataPermission.Flow.ordinal()) {
				// 流程权限
				EntityType entityType = EntityType.SUPPLIER;
				if (associateBill.fromCustomer()) {
					entityType = EntityType.CUSTOMER;
				}
				List<String> entityIds = flowEntService.queryFlowEntityId(role.getRoleid(), onlineUser.getUser().getOssUserId(), entityType);
				if (entityIds != null && !entityIds.isEmpty()) {
					String ids = entityIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","));
					productBillSql.append(" AND b.entityid IN(").append(ids).append(")");
				} else {
					// 流程对应的客户id
					productBillSql.append(" AND b.entityid IN('')");
				}
			} else if (dataPermission == DataPermission.Dept.ordinal()) {
				deptIds.add(onlineUser.getUser().getDeptId());
				// 部门
				List<String> subDeptIdList = departmentService.getSubDeptIds(onlineUser.getUser().getDeptId());
				if (subDeptIdList != null) {
					deptIds.addAll(subDeptIdList);
				}
				productBillSql.append(" AND s.deptid IN (").append(deptIds.stream().map(id -> ("'" + id + "'")).collect(Collectors.joining(","))).append(")");

			} else if (dataPermission == DataPermission.Customize.ordinal()) {
				// 自定义
				String deptIdStr = role.getDeptIds();
				if (StringUtil.isNotBlank(deptIdStr)) {
					deptIds.addAll(Arrays.asList(deptIdStr.split(",")));
				}
				productBillSql.append(" AND s.deptid IN (").append(deptIds.stream().map(id -> ("'" + id + "'")).collect(Collectors.joining(","))).append(")");
			}

			// 部门条件
			if (StringUtil.isNotBlank(associateBill.getDeptId())) {
				productBillSql.append(" AND s.deptid = '").append(associateBill.getDeptId()).append("'");
			}
			if (StringUtil.isNotBlank(associateBill.getStartTime())) {
				Date startTime = DateUtil.convert(associateBill.getStartTime(), DateUtil.format1);
				if (startTime != null) {
					productBillSql.append(" AND  b.wtime >= '").append(DateUtil.convert(startTime, DateUtil.format1)).append(" 00:00:00' ");
				}
			}
			if (StringUtil.isNotBlank(associateBill.getEndTime())) {
				Date endTime = DateUtil.convert(associateBill.getEndTime(), DateUtil.format1);
				if (endTime != null) {
					productBillSql.append(" AND b.wtime <= '").append(endTime).append(" 23:59:59' ");
				}
			}
			// 对象id
			if (StringUtil.isNotBlank(associateBill.getEntityName())) {
				productBillSql.append(" AND s.companyname like '%").append(associateBill.getEntityName()).append("%' ");
			}
			productBillSql.append(" order by b.wtime desc ");
			// 查询总数
			int total = 0;
			// 查询数量
			String productBillCountSql = "select count(1) from ( " + productBillSql.toString() + " ) r";
			List<?> productBillInfoCount = baseDao.selectSQL(productBillCountSql);
			if (productBillInfoCount != null && !productBillInfoCount.isEmpty()) {
				Object totalObj = productBillInfoCount.get(0);
				if (totalObj instanceof BigInteger) {
					BigInteger totalRow = (BigInteger) totalObj;
					total = totalRow.intValue();
				}
			}
			productBillInfoCount = null;
			// 如果没有查到就不用再查了
			if (total == 0) {
				return result;
			}
			// 分页
			productBillSql.append(" limit ").append(associateBill.getPageStart()).append(",").append(associateBill.getPageSize());
			// 查询出来的账单信息
			List<ProductBillInfo> productBillInfoList = new ArrayList<>();
			// 产品账单的部门id
			List<String> billDeptIdList = new ArrayList<>();
			// 查询产品账单详细信息
			List<?> productBillList = baseDao.selectSQL(productBillSql.toString());
			if (productBillList != null && !productBillList.isEmpty()) {
				for (Object productBill : productBillList) {
					if (productBill.getClass().isArray()) {
						Object[] billInfo = (Object[]) productBill;
						if (billInfo.length >= 11) {
							productBillInfoList.add(new ProductBillInfo(billInfo));
						}
					}
				}
				billDeptIdList.addAll(productBillInfoList.stream().map(ProductBillInfo::getDeptId).distinct().collect(Collectors.toList()));
			}
			if (!billDeptIdList.isEmpty()) {
				SearchFilter deptFilter = new SearchFilter();
				deptFilter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, billDeptIdList));
				List<Department> departmentList = departmentService.queryAllBySearchFilter(deptFilter);
				if (departmentList != null && !departmentList.isEmpty()) {
					Map<String, String> deptNameMap = departmentList.stream().collect(Collectors.toMap(Department::getDeptid, Department::getDeptname));
					productBillInfoList.forEach(productBill -> productBill.setDeptName(deptNameMap.get(productBill.getDeptId())));
				}
			}
			result.setData(productBillInfoList);
			int pageCount = total / associateBill.getPageSize();
			if (total % associateBill.getPageSize() > 0) {
				pageCount = pageCount + 1;
			}
			result.setTotalPages(pageCount);
			result.setCount(total);
		} catch (Exception e) {
			logger.info("查询产品账单异常", e);
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/readSaleIncomeInfo")
	public BaseResponse<JSONObject> readSaleIncomeInfo(@RequestParam String startTime, @RequestParam String endTime,
			@RequestParam(required = false) String timeType) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		startTime += " 00:00:00";
		endTime += " 23:59:59";
		JSONObject result = new JSONObject();
		JSONObject incomeInfo = fsExpenseIncomeService.querySaleIncome(DateUtil.convert2(startTime), DateUtil.convert2(endTime), timeType, onlineUser);
		if (incomeInfo != null) {
			String totalIncomeStr = incomeInfo.getString("totalIncome");
			result.put("totalIncome", totalIncomeStr);
			String totalWriteOffStr = incomeInfo.getString("totalWriteOff");
			result.put("totalWriteOff", totalWriteOffStr);
		}
		BigDecimal totalBalance = accountBalanceService.queryTotalBalance(DateUtil.convert1(endTime), onlineUser);
		result.put("totalBalance", totalBalance.toPlainString());
		return BaseResponse.success(result);
	}

	@Autowired
	public void setFsExpenseIncomeService(IFsExpenseIncomeService fsExpenseIncomeService) {
		this.fsExpenseIncomeService = fsExpenseIncomeService;
	}

	@Autowired
	public void setRoleService(IRoleService roleService) {
		this.roleService = roleService;
	}

	@Autowired
	public void setDepartmentService(IDepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setBaseDao(IBaseDao baseDao) {
		this.baseDao = baseDao;
	}

	@Autowired
	public void setFlowEntService(IFlowEntService flowEntService) {
		this.flowEntService = flowEntService;
	}
}
