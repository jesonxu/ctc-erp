package com.dahantc.erp.controller.realRoyalty;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.realRoyalty.RealRoyaltyDto;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.ParseFile;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.balanceinterest.entity.BalanceInterest;
import com.dahantc.erp.vo.balanceinterest.service.IBalanceInterestService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.operateCost.entity.OperateCost;
import com.dahantc.erp.vo.operateCost.service.IOperateCostService;
import com.dahantc.erp.vo.productBills.entity.FsExpenseincomeInfo;
import com.dahantc.erp.vo.productBills.entity.OperateCostDetail;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.royalty.entity.RealRoyalty;
import com.dahantc.erp.vo.royalty.service.IRealRoyaltyService;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 实际提成
 */
@Controller
@RequestMapping("/realRoyalty")
public class RealRoyaltyAction extends BaseAction {
	public static final Logger logger = LoggerFactory.getLogger(RealRoyaltyAction.class);

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IRealRoyaltyService realRoyaltyService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IBalanceInterestService balanceInterestService;

	@Autowired
	private IProductBillsService billService;

	@Autowired
	private IOperateCostService operateCostService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	/**
	 * 查询销售实际提成（账单尺度）
	 * 
	 * @param customerId
	 *            客户id
	 * @param productId
	 *            产品id
	 * @param queryDate
	 *            月份
	 * @return
	 */
	@ResponseBody
	@PostMapping("/getRealRoyaltyDetail")
	public BaseResponse<Object> getRealRoyaltyDetail(@RequestParam String customerId, @RequestParam(required = false) String productId,
			@RequestParam(required = false) String queryDate) {
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}

			List<RealRoyaltyDto> resultList = new ArrayList<>();
			logger.info("查询销售实际提成开始，月份：" + queryDate);
			long _start = System.currentTimeMillis();

			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			Date nextMonthDate = DateUtil.getNextMonthFirst(monthDate);

			List<CustomerProduct> productList = new ArrayList<>();
			// 查出产品
			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) {
				CustomerProduct product = customerProductService.read(productId);
				if (product != null) {
					productList.add(product);
				}
			} else {
				filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
				productList = customerProductService.queryAllByFilter(filter);
			}
			if (CollectionUtils.isEmpty(productList)) {
				logger.info("未查询到产品");
				return BaseResponse.success(resultList);
			}
			// {productId -> Product}
			Map<String, CustomerProduct> productMap = productList.stream().collect(Collectors.toMap(CustomerProduct::getProductId, product -> product));
			List<String> productIdList = productList.stream().map(CustomerProduct::getProductId).collect(Collectors.toList());

			// 销售信息map {ossUserId -> {销售名，部门名，父部门名，区域名}}
			HashMap<String, HashMap<String, String>> userInfoMap = userService.getUserAndDeptName();

			// 查询月份之间的实际提成记录
			SearchFilter realRoyaltyFilter = new SearchFilter();
			realRoyaltyFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, productIdList));
			realRoyaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, monthDate));
			realRoyaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, nextMonthDate));
			realRoyaltyFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<RealRoyalty> realRoyaltyList = realRoyaltyService.queryAllBySearchFilter(realRoyaltyFilter);

			// 实际提成表存在改造前的历史数据，这部分数据的字段不够，得查账单获取
			List<ProductBills> historyBillList = new ArrayList<>();
			Map<String, ProductBills> historyBillMap = new HashMap<>();

			if (!CollectionUtils.isEmpty(realRoyaltyList)) {
				logger.info("时间范围：" + DateUtil.convert(monthDate, DateUtil.format1) + "~" + DateUtil.convert(nextMonthDate, DateUtil.format1) + "查询到提成记录"
						+ realRoyaltyList.size() + "条");
				// 获取提成表历史数据的账单id（账单编号是新加字段，为空的就是历史数据）
				List<String> billIdList = realRoyaltyList.stream().filter(realRoyalty -> StringUtil.isBlank(realRoyalty.getBillNumber()))
						.map(RealRoyalty::getBillId).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(billIdList)) {
					SearchFilter billFilter = new SearchFilter();
					billFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, billIdList));
					historyBillList = billService.queryAllBySearchFilter(billFilter);
					if (!CollectionUtils.isEmpty(historyBillList)) {
						// 查出的账单备用
						historyBillMap = historyBillList.stream().collect(Collectors.toMap(ProductBills::getId, v -> v));
					}
				}

				for (RealRoyalty realRoyalty : realRoyaltyList) {
					Map<String, String> userInfo = userInfoMap.getOrDefault(realRoyalty.getOssUserId(), new HashMap<>());
					List<RealRoyaltyDto> list = buildRealRoyaltyDetailDto(realRoyalty, productMap.get(realRoyalty.getProductId()), userInfo, historyBillMap,
							false);
					if (!CollectionUtils.isEmpty(list)) {
						resultList.addAll(list);
					}
				}
			}

			logger.info("查询销售实际提成结束，耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	@ResponseBody
	@RequestMapping("/exportMonthRealRoyaltyDetail")
	public void exportMonthRealRoyaltyDetail(@RequestParam String selectDate) {
		File file = null;
		try {
			Date startDate = DateUtil.convert(selectDate, DateUtil.format4);
			Date endDate = DateUtil.getNextMonthFirst(startDate);
			file = File.createTempFile("利润提成表", ".csv");
			exportMonthRealRoyaltyDetail(startDate, endDate, file);

			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment; 利润提成表.csv");

			try (OutputStream outputStream = response.getOutputStream(); InputStream inputStream = new FileInputStream(file);) {
				IOUtils.copy(inputStream, outputStream);
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (file != null) {
				file.delete();
			}
		}

	}

	private void exportMonthRealRoyaltyDetail(Date startDate, Date endDate, File file) {

		try {
			// 查所有的利润提成记录
			SearchFilter realRoyaltyFilter = new SearchFilter();
			realRoyaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
			realRoyaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
			List<RealRoyalty> realRoyaltyList = realRoyaltyService.queryAllBySearchFilter(realRoyaltyFilter);

			// 实际提成表存在改造前的历史数据，这部分数据的字段不够，得查账单获取
			List<ProductBills> historyBillList = new ArrayList<>();
			Map<String, ProductBills> historyBillMap = new HashMap<>();

			List<RealRoyaltyDto> resultList = new ArrayList<>();

			if (!CollectionUtils.isEmpty(realRoyaltyList)) {

				List<CustomerProduct> productList = customerProductService.queryAllByFilter(null);

				// 销售信息map {ossUserId -> {销售名，部门名，父部门名，区域名}}
				HashMap<String, HashMap<String, String>> userInfoMap = userService.getUserAndDeptName();

				// {productId -> Product}
				Map<String, CustomerProduct> productMap = productList.stream().collect(Collectors.toMap(CustomerProduct::getProductId, product -> product));

				List<String> billIdList = realRoyaltyList.stream().map(RealRoyalty::getBillId).collect(Collectors.toList());

				if (!CollectionUtils.isEmpty(billIdList)) {
					SearchFilter billFilter = new SearchFilter();
					billFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, billIdList));

					historyBillList = billService.queryAllBySearchFilter(billFilter);
				}

				if (!CollectionUtils.isEmpty(historyBillList)) {
					// 查出的账单备用
					historyBillMap = historyBillList.stream().collect(Collectors.toMap(ProductBills::getId, v -> v));
				}

				for (RealRoyalty realRoyalty : realRoyaltyList) {
					Map<String, String> userInfo = userInfoMap.getOrDefault(realRoyalty.getOssUserId(), new HashMap<>());
					List<RealRoyaltyDto> list = buildRealRoyaltyDetailDto(realRoyalty, productMap.get(realRoyalty.getProductId()), userInfo, historyBillMap,
							true);
					if (!CollectionUtils.isEmpty(list)) {
						resultList.addAll(list);
					}
				}

				List<Customer> custList = customerService.queryAllBySearchFilter(null);
				Map<String, String> cacheCustMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(custList)) {
					custList.forEach(cust -> {
						cacheCustMap.put(cust.getCustomerId(), cust.getCompanyName());
					});
					custList.clear();
					custList = null;
				}

				String[] title = new String[] { "部门", "销售", "客户名称", "产品名称", "账单编号", "销账时间", "到款名称", "到款时间", "到款金额", "销账金额", "发送量", "账单金额", "毛利润", "运营成本",
						"利润提成", "罚息金额" };

				resultList.sort((dto1, dto2) -> {
					if (StringUtils.equals(dto1.getDeptName(), dto2.getDeptName())) {
						if (StringUtils.equals(dto1.getSaleName(), dto2.getSaleName())) {
							return StringUtils.compare(dto1.getCompanyName(), dto2.getCompanyName());
						} else {
							return StringUtils.compare(dto1.getSaleName(), dto2.getSaleName());
						}
					} else {
						return StringUtils.compare(dto1.getDeptName(), dto2.getDeptName());
					}
				});

				List<String[]> dataList = new ArrayList<>();
				resultList.forEach(dto -> {
					List<String> list = new ArrayList<>();
					list.add(dto.getDeptName());
					list.add(dto.getSaleName());
					list.add(cacheCustMap.getOrDefault(dto.getCustomerId(), ""));
					list.add(dto.getProductName());
					list.add(dto.getBillNumber());
					list.add(dto.getWriteOffTime());
					list.add(dto.getIncomeName());
					list.add(dto.getIncomeDate());
					list.add(dto.getIncomeCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					list.add(dto.getThisCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					list.add(dto.getSendCount() + "");
					list.add(dto.getBillMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					list.add(dto.getGrossProfit().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					list.add(dto.getOperateCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					list.add(dto.getRoyalty().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					list.add(dto.getPenaltyInterest().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

					dataList.add(list.toArray(new String[] {}));
				});

				ParseFile.exportDataToExcel(dataList, file, title);

			}

		} catch (Exception e) {
			logger.error("", e);
		}

	}

	private List<RealRoyaltyDto> buildRealRoyaltyDetailDto(RealRoyalty realRoyalty, CustomerProduct product, Map<String, String> userInfo,
			Map<String, ProductBills> historyBillMap, boolean needIncomeInfo) {
		if (product == null) {
			return null;
		}

		ArrayList<RealRoyaltyDto> list = new ArrayList<>();

		ProductBills bill = null;

		// 账单编号为空，说明是提成表的历史数据，需要从账单表查数据
		if (StringUtil.isBlank(realRoyalty.getBillNumber())) {
			bill = historyBillMap.get(realRoyalty.getBillId());
			if (bill != null) {
				// 更新历史数据
				updateHistoryRealRoyalty(realRoyalty, bill);
			}
		}

		RealRoyaltyDto dto = new RealRoyaltyDto();
		dto.setDeptName(userInfo.getOrDefault("deptName", ""));
		dto.setSaleName(userInfo.getOrDefault("realName", ""));
		dto.setCustomerId(product.getCustomerId());
		dto.setProductId(product.getProductId());
		dto.setProductName(product.getProductName());
		dto.setSettleType(SettleType.getSettleType(product.getSettleType()));
		dto.setBillId(realRoyalty.getBillId());
		dto.setBillNumber(realRoyalty.getBillNumber());
		dto.setSendCount(realRoyalty.getSendCount());
		dto.setBillMoney(realRoyalty.getBillMoney());
		dto.setGrossProfit(realRoyalty.getGrossProfit());
		dto.setOperateCost(realRoyalty.getOperateCost());
		dto.setProfit(realRoyalty.getProfit());
		dto.setPenaltyInterest(realRoyalty.getPenaltyInterest());
		dto.setRoyalty(realRoyalty.getRoyalty());
		dto.setWriteOffTime(DateUtil.convert(realRoyalty.getWtime(), DateUtil.format1));
		// 单账单没有余额计息，也无法算出修正后提成

		if (!needIncomeInfo) {
			list.add(dto);
		} else {
			try {
				if (bill == null) {
					bill = productBillsService.read(realRoyalty.getBillId());
				}

				SearchFilter searchFilter = new SearchFilter();

				if ((bill.getBillStatus() != BillStatus.WRITING_OFF.ordinal() && bill.getBillStatus() != BillStatus.WRITED_OFF.ordinal())
						|| CollectionUtils.isEmpty(bill.getFsExpenseIncomeInfos())) {
					list.add(dto);
				} else {
					List<String> incomeIdList = new ArrayList<>();
					Map<String, BigDecimal> costMap = new HashMap<>();

					for (FsExpenseincomeInfo info : bill.getFsExpenseIncomeInfos()) {
						incomeIdList.add(info.getFsExpenseIncomeId());
						costMap.put(info.getFsExpenseIncomeId(), info.getThisCostNumber());
					}

					searchFilter.getRules().clear();
					searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, incomeIdList));
					List<FsExpenseIncome> incomeList = fsExpenseIncomeService.queryAllBySearchFilter(searchFilter);

					for (FsExpenseIncome fsExpenseIncome : incomeList) {
						RealRoyaltyDto clone = dto.clone();
						clone.setIncomeName(fsExpenseIncome.getDepict());
						clone.setIncomeCost(fsExpenseIncome.getCost());
						clone.setIncomeDate(DateUtil.convert(fsExpenseIncome.getOperateTime(), DateUtil.format1));
						clone.setThisCost(costMap.get(fsExpenseIncome.getId()));
						list.add(clone);
					}

				}
			} catch (Exception e) {
				logger.error("", e);
			}

		}

		return list;
	}

	@RequestMapping("/toRealRoyaltySheet")
	public String toRealRoyaltySheet() {
		JSONObject params = new JSONObject();
		params.put("deptIds", request.getParameter("deptIds"));
		params.put("sale_open_customer_type_id", request.getParameter("sale_open_customer_type_id"));
		params.put("sale_customer_id", request.getParameter("sale_customer_id"));
		params.put("sale_customer_opts", request.getParameter("sale_customer_opts"));
		params.put("sale_product_id", request.getParameter("sale_product_id"));
		params.put("sale_product_settle_type", request.getParameter("sale_product_settle_type"));
		params.put("sale_operate_year", request.getParameter("sale_operate_year"));
		params.put("sale_operate_month", request.getParameter("sale_operate_month"));
		params.put("sale_settlement_year", request.getParameter("sale_settlement_year"));
		params.put("sale_settlement_month", request.getParameter("sale_settlement_month"));
		params.put("sale_statistic_year", request.getParameter("sale_statistic_year"));
		params.put("sale_open_dept_id", request.getParameter("sale_open_dept_id"));
		params.put("sale_open_sub_dept_id", request.getParameter("sale_open_sub_dept_id"));
		params.put("sale_open_customer_type_name", request.getParameter("sale_open_customer_type_name"));
		params.put("customerKeyWord", request.getParameter("customerKeyWord"));
		params.put("yearMonth", request.getParameter("yearMonth"));
		request.setAttribute("params", params);
		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("title", StringEscapeUtils.unescapeHtml4(request.getParameter("title")));
		return "/views/salesheet/realRoyaltySheet";
	}

	@RequestMapping("/toRealRoyaltySheet2Manager")
	public String toRealRoyaltySheet2Manager() {
		List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
		boolean isManager = !CollectionUtils.isEmpty(deptIdList);
		request.setAttribute("isManager", isManager);
		return "/views/manageConsole/realRoyaltySheet";
	}

	/**
	 * 查询销售实际提成（客户尺度）
	 *
	 * @param userId
	 *            销售
	 * @param deptId
	 *            部门
	 * @param queryDate
	 *            月份
	 * @return
	 */
	@ResponseBody
	@PostMapping("/getRealRoyalty")
	public BaseResponse<List<RealRoyaltyDto>> getRealRoyalty(@RequestParam(required = false) String userId, @RequestParam(required = false) String deptId,
			@RequestParam(required = false) String queryDate) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		List<RealRoyaltyDto> resultList = new ArrayList<>();
		// 销售信息map {ossUserId -> {销售名，部门名，父部门名，区域名}}
		HashMap<String, HashMap<String, String>> userInfoMap = new HashMap<>();
		// 实际提成表存在改造前的历史数据，这部分数据的字段不够，得查账单获取
		List<ProductBills> historyBillList = new ArrayList<>();
		Map<String, ProductBills> historyBillMap = new HashMap<>();

		List<RealRoyalty> realRoyaltyList = new ArrayList<>();
		Map<String, List<RealRoyalty>> realRoyaltyMap = new HashMap<>();

		List<BalanceInterest> balanceInterestList = new ArrayList<>();
		Map<String, List<BalanceInterest>> balanceInterestMap = new HashMap<>();
		List<Customer> customerList = null;
		try {

			logger.info("查询销售实际提成开始，月份：" + queryDate);
			long _start = System.currentTimeMillis();

			Date monthDate = DateUtil.getCurrentMonthFirstDay();
			if (StringUtils.isNotBlank(queryDate)) {
				monthDate = DateUtil.convert4(queryDate);
			}
			Date nextMonthDate = DateUtil.getNextMonthFirst(monthDate);

			if (StringUtil.isNotBlank(userId)) {
				SearchFilter customerfilter = new SearchFilter();
				customerfilter.getRules().add(new SearchRule("ossuserId", Constants.ROP_EQ, userId));
				customerList = customerService.queryAllBySearchFilter(customerfilter);
			} else {
				customerList = customerService.readCustomers(onlineUser, deptId, "", "", "");
			}
			// 过滤掉公共池客户
			if (!CollectionUtils.isEmpty(customerList)) {
				customerList = customerList.stream().filter(customer -> StringUtil.isNotBlank(customer.getOssuserId())).collect(Collectors.toList());
			}
			if (CollectionUtils.isEmpty(customerList)) {
				logger.info("用户：" + onlineUser.getUser().getRealName() + "的数据权限下没有客户");
				return BaseResponse.success(resultList);
			}
			List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());

			// 销售信息map {ossUserId -> {销售名，部门名，父部门名，区域名}}
			userInfoMap = userService.getUserAndDeptName();

			// 查询月份之间的实际提成记录
			SearchFilter realRoyaltyFilter = new SearchFilter();
			realRoyaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, monthDate));
			realRoyaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, nextMonthDate));
			realRoyaltyList = realRoyaltyService.queryAllBySearchFilter(realRoyaltyFilter);
			// 提成按客户分组
			if (!CollectionUtils.isEmpty(realRoyaltyList)) {
				logger.info("时间范围：" + DateUtil.convert(monthDate, DateUtil.format1) + "~" + DateUtil.convert(nextMonthDate, DateUtil.format1) + "查询到提成记录"
						+ realRoyaltyList.size() + "条");
				// 获取提成表历史数据的账单id（账单编号是新加字段，为空的就是历史数据）
				List<String> billIdList = realRoyaltyList.stream().filter(realRoyalty -> StringUtil.isBlank(realRoyalty.getBillNumber()))
						.map(RealRoyalty::getBillId).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(billIdList)) {
					SearchFilter billFilter = new SearchFilter();
					billFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, billIdList));
					historyBillList = billService.queryAllBySearchFilter(billFilter);
					if (!CollectionUtils.isEmpty(historyBillList)) {
						// 查出的账单备用
						historyBillMap = historyBillList.stream().collect(Collectors.toMap(ProductBills::getId, v -> v));
					}
				}
				realRoyaltyMap = realRoyaltyList.stream().collect(Collectors.groupingBy(RealRoyalty::getEntityId));
			}

			// 查客户余额计息
			SearchFilter balanceInterestFilter = new SearchFilter();
			balanceInterestFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
			balanceInterestFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, monthDate));
			balanceInterestFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, nextMonthDate));
			balanceInterestList = balanceInterestService.queryAllBySearchFilter(balanceInterestFilter);
			// 计息按客户分组
			if (!CollectionUtils.isEmpty(balanceInterestList)) {
				logger.info("时间范围：" + DateUtil.convert(monthDate, DateUtil.format1) + "~" + DateUtil.convert(nextMonthDate, DateUtil.format1) + "查询到余额计息"
						+ balanceInterestList.size() + "条");
				balanceInterestMap = balanceInterestList.stream().collect(Collectors.groupingBy(BalanceInterest::getCustomerId));
			}

			// 遍历所有客户，封装提成对象
			for (Customer customer : customerList) {
				List<RealRoyalty> royaltyList = realRoyaltyMap.getOrDefault(customer.getCustomerId(), new ArrayList<>());
				List<BalanceInterest> interestList = balanceInterestMap.getOrDefault(customer.getCustomerId(), new ArrayList<>());
				Map<String, String> userInfo = userInfoMap.getOrDefault(customer.getOssuserId(), new HashMap<>());
				RealRoyaltyDto dto = buildRealRoyaltyDto(customer, userInfo, royaltyList, interestList, historyBillMap);
				// 实际提成，计息，罚息全为0，不展示
				if (dto.getRealRoyalty().compareTo(BigDecimal.ZERO) == 0 && dto.getPenaltyInterest().compareTo(BigDecimal.ZERO) == 0
						&& dto.getBalanceInterest().compareTo(BigDecimal.ZERO) == 0)
					continue;
				resultList.add(dto);
			}
			resultList.sort((o1, o2) -> {
				if (o1.getDeptName().equals(o2.getDeptName())) {
					if (o1.getSaleName().equals(o2.getSaleName())) {
						return o1.getCompanyName().compareTo(o2.getCompanyName());
					} else {
						return o1.getSaleName().compareTo(o2.getSaleName());
					}
				} else {
					return o1.getDeptName().compareTo(o2.getDeptName());
				}
			});
			logger.info("查询销售实际提成结束，耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (null != userInfoMap) {
				userInfoMap.clear();
			}
			if (null != customerList) {
				customerList.clear();
			}
			if (null != realRoyaltyList) {
				realRoyaltyList.clear();
			}
			if (null != realRoyaltyMap) {
				realRoyaltyMap.clear();
			}
			if (null != balanceInterestList) {
				balanceInterestList.clear();
			}
			if (null != balanceInterestMap) {
				balanceInterestMap.clear();
			}
		}
		return BaseResponse.error("未查询到数据");
	}

	/**
	 * 根据客户的销账账单封装提成对象
	 * 
	 * @param customer
	 *            客户
	 * @param userInfo
	 *            销售信息 {ossUserId -> {销售名，部门名，父部门名，区域名}}
	 * @param realRoyaltyList
	 *            实际提成列表
	 * @param balanceInterestList
	 *            余额计息列表
	 * @param historyBillMap
	 *            提成对应的账单 {billId -> ProductBills}
	 * @return
	 */
	private RealRoyaltyDto buildRealRoyaltyDto(Customer customer, Map<String, String> userInfo, List<RealRoyalty> realRoyaltyList,
			List<BalanceInterest> balanceInterestList, Map<String, ProductBills> historyBillMap) {
		RealRoyaltyDto dto = new RealRoyaltyDto();
		try {
			dto.setDeptName(userInfo.getOrDefault("deptName", ""));
			dto.setSaleName(userInfo.getOrDefault("realName", ""));
			dto.setCustomerId(customer.getCustomerId());
			dto.setCompanyName(customer.getCompanyName());
			if (!CollectionUtils.isEmpty(realRoyaltyList)) {
				for (RealRoyalty realRoyalty : realRoyaltyList) {
					// 账单编号为空，说明是提成表的历史数据，需要从账单表查数据
					if (StringUtil.isBlank(realRoyalty.getBillNumber())) {
						ProductBills bill = historyBillMap.get(realRoyalty.getBillId());
						if (bill != null) {
							// 更新历史数据
							updateHistoryRealRoyalty(realRoyalty, bill);
						}
					}
					dto.addBillMoney(realRoyalty.getBillMoney());
					dto.addSendCount(realRoyalty.getSendCount());
					dto.addGrossProfit(realRoyalty.getGrossProfit());
					dto.addOperateCost(realRoyalty.getOperateCost());
					dto.addProfit(realRoyalty.getProfit());
					// 累计利润提成
					dto.addRoyalty(realRoyalty.getRoyalty());
					// 账单罚息必须为正
					if (realRoyalty.getPenaltyInterest().compareTo(BigDecimal.ZERO) > 0) {
						dto.addPenaltyInterest(realRoyalty.getPenaltyInterest());
					}
				}
			}
			// 累计余额计息
			if (!CollectionUtils.isEmpty(balanceInterestList)) {
				for (BalanceInterest balanceInterest : balanceInterestList) {
					dto.addBalanceInterest(balanceInterest.getInterest());
				}
			}
			// 修正后提成 = 利润提成 + 余额计息 - 账单罚息
			dto.setRealRoyalty(dto.getRoyalty().add(dto.getBalanceInterest()).subtract(dto.getPenaltyInterest()));
		} catch (Exception e) {
			logger.error("封装销售提成对象异常", e);
		}
		return dto;
	}

	/**
	 * 实际提成表的历史数据更新
	 *
	 * @param realRoyalty
	 *            提成记录
	 * @param productBills
	 *            对应账单
	 *
	 * @return
	 */
	private void updateHistoryRealRoyalty(RealRoyalty realRoyalty, ProductBills productBills) {
		// 账单编号
		if (StringUtil.isNotBlank(productBills.getBillNumber())) {
			realRoyalty.setBillNumber(productBills.getBillNumber());
		} else {
			// 很早之前创建的账单没有账单编号
			realRoyalty.setBillNumber(productBills.getId());
		}
		// 发送量
		realRoyalty.setSendCount(productBills.getSupplierCount());
		// 账单金额
		realRoyalty.setBillMoney(productBills.getReceivables());
		// 毛利润
		realRoyalty.setGrossProfit(productBills.getGrossProfit());
		// 运营成本
		BigDecimal operateCostTotal = new BigDecimal(0);
		// 保存运营成本到运营成本表
		OperateCostDetail operateCostDetail = productBills.obtainOperateCost();
		OperateCost op = operateCostService.saveOperateCostByBill(operateCostDetail, productBills);
		if (null != op) {
			operateCostTotal = operateCostTotal.add(op.getCustomerFixedCost()).add(op.getUnifiedSingleCostTotal()).add(op.getProductSingleCostTotal())
					.add(op.getBillMoneyCost()).add(op.getBillGrossProfitCost());
		}
		realRoyalty.setOperateCost(operateCostTotal);
		try {
			realRoyaltyService.update(realRoyalty);
			logger.info("更新历史提成记录成功，记录id：" + realRoyalty.getId());
		} catch (Exception e) {
			logger.error("更新历史提成记录异常，记录id：" + realRoyalty.getId());
		}
	}
}
