package com.dahantc.erp.controller.saleStatistics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.saleStatistics.SaleStatisticsRspDto;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/saleStatistics")
public class SaleStatisticsAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(SaleStatisticsAction.class);

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IRoleService roleService;

	/**
	 * 获取统计时间
	 */
	@RequestMapping("/getSaleStatisticsTime")
	public String getStatisticsTime(@RequestParam String deptIds, @RequestParam String customerTypeId, @RequestParam String customerId,
			@RequestParam String productId, @RequestParam String customerKeyWord) {
		try {
			Date startTime = null;
			Date nowTime = new Timestamp(System.currentTimeMillis());
			logger.info("查询统计时间开始");

			if (StringUtils.isNotBlank(productId)) { // 点击产品
				CustomerProduct product = customerProductService.read(productId);
				if (product != null) {
					Customer customer = customerService.read(product.getCustomerId());
					if (customer != null) {
						startTime = customer.getWtime();
					}
				}
			} else if (StringUtils.isNotBlank(customerId)) { // 点击客户
				Customer customer = customerService.read(customerId);
				if (customer != null) {
					startTime = customer.getWtime();
				}
			} else { // 未点击产品或者客户，按数据权限查询
				List<Customer> customerList = customerService.readCustomers(getOnlineUserAndOnther(), deptIds, customerId, customerTypeId, customerKeyWord);
				if (!ListUtils.isEmpty(customerList)) {
					customerList.sort((o1, o2) -> o1.getWtime().compareTo(o2.getWtime()));
					startTime = customerList.get(0).getWtime();
				}
			}

			// 获取最早时间到当前时间的年份
			List<Integer> years = new ArrayList<>();
			if (startTime != null && startTime.before(nowTime)) {
				Calendar startCal = Calendar.getInstance();
				Calendar nowCal = Calendar.getInstance();
				startCal.setTime(startTime);
				nowCal.setTime(nowTime);
				years = Stream.iterate(startCal.get(Calendar.YEAR), item -> item + 1).limit(nowCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR) + 1)
						.collect(Collectors.toList());
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			if (!isSale() || !customerTypeService.validatePublicType(customerTypeId)) {
				request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			}
			request.setAttribute("onlyShowBasic", isSale() && customerTypeService.validatePublicType(customerTypeId));
			request.setAttribute("years", years);
			return "/views/saleStatistics/saleStatisticsTime";
		} catch (ServiceException e) {
			logger.error("查询统计时间异常", e);
		}
		return "";
	}

	/**
	 * 根据客户的合同日期，查询时间范围内新增合作客户
	 * 
	 * @param dateType
	 *            日期类型
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param customerIdList
	 *            客户查询范围
	 * @return
	 * @throws ServiceException
	 */
	private Map<String, Integer> queryNewContractNum(int dateType, String startTime, String endTime, List<String> customerIdList) throws ServiceException {
		logger.info("查询时间范围内的合同客户开始，startTime：" + startTime + "，endTime：" + endTime);
		long _start = System.currentTimeMillis();
		Map<String, Integer> newContractNumMap = new HashMap<>();
		// 根据客户的合同日期，查询时间范围内新增合作客户
		SearchFilter cusfilter = new SearchFilter();
		cusfilter.getRules().add(new SearchRule("contractDate", Constants.ROP_GE, DateUtil.convert2(startTime)));
		cusfilter.getRules().add(new SearchRule("contractDate", Constants.ROP_LE, DateUtil.convert2(endTime)));
		cusfilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
		List<Customer> customerList = customerService.queryAllBySearchFilter(cusfilter);
		logger.info("查询时间范围内的合同客户结束，查询到" + (customerList == null ? 0 : customerList.size()) + "条客户记录，耗时：" + (System.currentTimeMillis() - _start));

		if (!ListUtils.isEmpty(customerList)) {
			for (Customer customer : customerList) {
				String date = "";
				if (dateType == 0) {
					// 按周查询的需要精确到天的维度
					date = DateUtil.convert(customer.getContractDate(), DateUtil.format1);
				} else {
					// 其日期类型只要到月
					date = DateUtil.convert(customer.getContractDate(), DateUtil.format4);
				}
				if (!newContractNumMap.containsKey(date)) {
					newContractNumMap.put(date, 0);
				}
				newContractNumMap.put(date, newContractNumMap.get(date) + 1);
			}
		}
		return newContractNumMap;
	}

	/**
	 * 获取查询范围内的每一天/月 对应日期类型的时间范围标题
	 * 
	 * @param dateType
	 *            日期类型
	 * @return
	 */
	private Map<String, String> getDate(int dateType, int reqYear) {
		// 按周：2019-12-25，第四周（12-23到12-29）
		// 按月：2019-12，2019-12
		// 按季：2019-12，2019-10-01到2019-12-31
		// 按年：2019-12，2019年
		logger.info("生成 日期-时间标题 对应关系开始");
		long _start = System.currentTimeMillis();
		Map<String, String> map = new HashMap<>();
		String startTime = "";
		String endTime = "";
		LocalDate local = LocalDate.now();
		if (dateType == 0) {
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
			DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format1);
			local = local.plusDays(7 - local.getDayOfWeek().getValue());
			String[] temp = new String[4];
			temp[0] = "第一周（" + local.plusDays(-6 - 7 * 3).format(fmt) + "到" + local.plusDays(-7 * 3).format(fmt) + "）";
			temp[1] = "第二周（" + local.plusDays(-6 - 7 * 2).format(fmt) + "到" + local.plusDays(-7 * 2).format(fmt) + "）";
			temp[2] = "第三周（" + local.plusDays(-6 - 7 * 1).format(fmt) + "到" + local.plusDays(-7 * 1).format(fmt) + "）";
			temp[3] = "第四周（" + local.plusDays(-6 - 7 * 0).format(fmt) + "到" + local.plusDays(-7 * 0).format(fmt) + "）";

			for (int i = 0; i < 28; i++) {
				map.put(local.plusDays(-i).format(ymdfmt), temp[(27 - i) / 7]);
			}
			endTime = local.format(ymdfmt) + " 23:59:59";
			startTime = local.plusDays(-27).format(ymdfmt) + " 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 1) {
			DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
			// 年初
			local = LocalDate.parse(reqYear + "-01-01");
			LocalDate templocal = local;
			for (int i = 0; i < 12; i++) {
				templocal = local.plusMonths(i);
				map.put(templocal.format(ymfmt), templocal.format(ymfmt));
			}
			endTime = templocal.format(ymfmt) + "-" + templocal.lengthOfMonth() + " 23:59:59";
			startTime = local.format(ymfmt) + "-01 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 2) {
			DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
			// 年初
			local = LocalDate.parse(reqYear + "-01-01");
			String[] temp = new String[4];
			temp[0] = "yyyy-01-01到yyyy-03-31";
			temp[1] = "yyyy-04-01到yyyy-06-31";
			temp[2] = "yyyy-07-01到yyyy-09-31";
			temp[3] = "yyyy-10-01到yyyy-12-31";
			LocalDate templocal = local;
			for (int i = 0; i < 12; i++) {
				templocal = local.plusMonths(i);
				map.put(templocal.format(ymfmt), temp[i / 3].replace("yyyy", templocal.getYear() + ""));
			}
			endTime = templocal.format(ymfmt) + "-" + templocal.lengthOfMonth() + " 23:59:59";
			startTime = local.format(ymfmt) + "-01 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		} else if (dateType == 3) {
			DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
			// 年末
			local = LocalDate.parse(reqYear + "-12-01");
			int thisYear = reqYear;
			LocalDate templocal = local;
			while (templocal.getYear() != thisYear - 3) {
				map.put(templocal.format(ymfmt), templocal.getYear() + "年");
				templocal = templocal.plusMonths(-1);
			}
			endTime = local.format(ymfmt) + "-" + local.lengthOfMonth() + " 23:59:59";
			startTime = (thisYear - 2) + "-01-01 00:00:00";
			map.put("startTime", startTime);
			map.put("endTime", endTime);
		}
		logger.info("生成 日期-时间标题 对应关系结束，耗时：" + (System.currentTimeMillis() - _start));
		return map;
	}

	/**
	 * 获取从开始时间到结束时间之间的每天/月
	 * 
	 * @param dateType
	 *            日期类型
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param <T>
	 * @return
	 */
	private <T> Map<String, T> getMonthOrDay(int dateType, String startTime, String endTime) {
		logger.info("获取时间范围内的每天/月开始，startTime：" + startTime + "，endTime：" + endTime);
		long _start = System.currentTimeMillis();
		Map<String, T> map = new LinkedHashMap<>();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateUtil.format2);
		DateTimeFormatter ymfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
		DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format1);
		LocalDate start = LocalDate.parse(startTime, fmt);
		LocalDate end = LocalDate.parse(endTime, fmt);
		while (!start.isAfter(end)) {
			if (dateType == 0) {
				map.put(start.format(ymdfmt), null);
				start = start.plusDays(1);
			} else {
				map.put(start.format(ymfmt), null);
				start = start.plusMonths(1);
			}
		}
		logger.info("获取时间范围内的每天/月结束，耗时：" + (System.currentTimeMillis() - _start));
		return map;
	}

	@RequestMapping("/toStatisticsSheet")
	public String toStatisticsSheet() {
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
		request.setAttribute("params", params);
		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("title", request.getParameter("title"));
		return "/views/sheet/saleNewStatisticsSheet";
	}

	/**
	 * 获取统计详情
	 * 
	 * @param deptIds
	 *            部门id（过滤条件）
	 * @param customerTypeId
	 *            客户类型（点击条件）
	 * @param customerId
	 *            客户id（点击条件）
	 * @param productId
	 *            产品id（点击条件）
	 * @param dateType
	 *            时间类型，0-周，1-月，2-季
	 * @param customerKeyWord
	 *            客户关键词（过滤条件）
	 * @return
	 */
	@ResponseBody
	@PostMapping("/getStatisticsDetail")
	public BaseResponse<JSONArray> getStatisticsDetail(@RequestParam String deptIds, @RequestParam String customerTypeId, @RequestParam String customerId,
			@RequestParam String productId, @RequestParam int dateType, @RequestParam String customerKeyWord, @RequestParam int reqYear) {
		long _start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			logger.info("用户：" + onlineUser.getUser().getLoginName() + "，获取统计记录开始，日期类型：" + dateType + "，年份：" + reqYear);

			// 获取查询范围内的每一天/月 对应日期类型的时间范围标题
			// 比如按周：2019-12-25，第四周（12-23到12-29）
			Map<String, String> datemap = getDate(dateType, reqYear);
			String startTime = datemap.remove("startTime");
			String endTime = datemap.remove("endTime");

			long __start = System.currentTimeMillis();

			// 查账单表的过滤器
			SearchFilter billFilter = new SearchFilter();

			// 查询产品账单记录
			List<String> customerIdList = new ArrayList<>();
			if (StringUtils.isNotBlank(productId)) {
				// 点击产品
				CustomerProduct customerProduct = customerProductService.read(productId);
				if (customerProduct == null) {
					logger.info("未查询到产品，产品id：" + productId);
					return BaseResponse.success(new JSONArray());
				}
				customerIdList.add(customerProduct.getCustomerId());
				billFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			} else if (StringUtils.isNotBlank(customerId)) {
				// 点击客户
				Customer customer = customerService.read(customerId);
				if (customer == null) {
					logger.info("未查询到客户，客户id：" + customerId);
					return BaseResponse.success(new JSONArray());
				}
				customerIdList.add(customerId);
				billFilter.getRules().add(new SearchRule("entityId", Constants.ROP_EQ, customerId));
			} else {
				// 未点击产品或者客户，按数据权限和过滤条件查询所有客户
				logger.info("未点击客户或产品，按权限和条件查询客户开始");
				List<Customer> customerList = customerService.readCustomers(onlineUser, deptIds, "", customerTypeId, customerKeyWord);
				if (customerList == null || customerList.isEmpty()) {
					logger.info("按权限和条件未查询到客户");
					return BaseResponse.success(new JSONArray());
				}
				logger.info("按权限和条件查询到" + customerList.size() + "条客户记录" + (System.currentTimeMillis() - __start));
				customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				billFilter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, customerIdList));
			}

			// 根据客户查产品
			List<String> productIdList = new ArrayList<>();
			logger.info("查询客户产品开始");
			__start = System.currentTimeMillis();
			SearchFilter productFilter = new SearchFilter();
			if (StringUtils.isNotBlank(productId)) { // 产品条件
				productFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			} else if (!ListUtils.isEmpty(customerIdList)) { // 有产品的情况下不需要客户条件
				productFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
			}
			List<CustomerProduct> customerProductList = customerProductService.queryAllByFilter(productFilter);
			if (customerProductList != null && customerProductList.size() > 0) {
				logger.info("查询到" + customerProductList.size() + "条客户产品记录，耗时：" + (System.currentTimeMillis() - __start));
				productIdList = customerProductList.stream().map(CustomerProduct::getProductId).collect(Collectors.toList());
			} else {
				logger.info("未查询到客户产品");
			}

			// 按日期存放新增合同客户数
			logger.info("获取时间范围内每天/月的新增合同客户数开始");
			__start = System.currentTimeMillis();
			Map<String, Integer> newContractNumMap = queryNewContractNum(dateType, startTime, endTime, customerIdList);
			logger.info("获取时间范围内每天/月的新增合同客户数结束，总耗时：" + (System.currentTimeMillis() - __start));

			// 按日期存放新增客户的产品的loginname（查每天/月的新增客户的账号）
			logger.info("获取时间范围内每天/月的新增客户产品开始");
			__start = System.currentTimeMillis();
			Map<String, Set<String>> cusUseDateMap = queryNewUseProduct(dateType, startTime, endTime, customerIdList);
			logger.info("获取时间范围内每天/月的新增客户产品结束，总耗时：" + (System.currentTimeMillis() - __start));

			// 查询账单记录
			List<ProductBills> billList;
			if (dateType == 0) { // 按周为维度的情况，不看账单
				billList = new ArrayList<>();
			} else {
				logger.info("查询客户产品账单开始");
				__start = System.currentTimeMillis();
				billFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert2(startTime)));
				billFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.convert2(endTime)));
				billFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
				billList = productBillsService.queryAllBySearchFilter(billFilter);
				logger.info("查询到" + (billList == null ? 0 : billList.size()) + "条产品账单记录，耗时：" + (System.currentTimeMillis() - __start));
			}
			// 获取从开始到结束的每天/月（按周的话是每天）
			Map<String, ProductBills> dateBillMap = this.getMonthOrDay(dateType, startTime, endTime);

			// 用来记录每天/月出了账单的产品id，在后面用来排除，以获取未出账单的产品
			Map<String, Set<String>> hasBillProductIdMap = new HashMap<>();

			// 遍历查询出的账单记录，将每个账单的数据累计到查询时间范围内的每天/月中
			if (!ListUtils.isEmpty(billList)) {
				logger.info("将账单数据累计到每天/月中开始");
				__start = System.currentTimeMillis();
				for (ProductBills bill : billList) {
					String date = "";
					if (dateType == 0) {
						// 按周查询的需要精确到天的维度
						date = DateUtil.convert(bill.getWtime(), DateUtil.format1);
					} else {
						date = DateUtil.convert(bill.getWtime(), DateUtil.format4);
					}
					if (dateBillMap.get(date) == null) {
						dateBillMap.put(date, new ProductBills());
						hasBillProductIdMap.put(date, new HashSet<>());
					}
					// 记录在某天/月此产品出了账单
					hasBillProductIdMap.get(date).add(bill.getProductId());

					// 将每条账单记录的数据累加到对应天/月份中
					ProductBills temp = dateBillMap.get(date);
					temp.setSupplierCount(temp.getSupplierCount() + bill.getSupplierCount());
					temp.setPlatformCount(temp.getPlatformCount() + bill.getPlatformCount());
					temp.setPayables(temp.getPayables().add(bill.getPayables()));
					temp.setActualPayables(temp.getActualPayables().add(bill.getActualPayables()));
					temp.setActualReceivables(temp.getActualReceivables().add(bill.getActualReceivables()));
					temp.setReceivables(temp.getReceivables().add(bill.getReceivables()));
					temp.setCost(temp.getCost().add(bill.getCost()));
				}
				logger.info("将账单数据累计到每天/月中结束，耗时：" + (System.currentTimeMillis() - __start));
			}

			// 查询客户统计详情表，获取每个产品的统计数据
			List<Map<String, Object>> statisticsList = queryStatistics(dateType, startTime, endTime, customerTypeId, customerId, productId, deptIds,
					customerKeyWord, customerIdList);
			DateFormat df = new SimpleDateFormat(dateType == 0 ? "yyyy-MM-dd" : "yyyy-MM");
			// 以 统计日期,产品id 为key 生成统计map
			Map<String, List<Map<String, Object>>> statisticsMap = statisticsList.stream().collect(Collectors.groupingBy(item -> {
				Map<String, Object> infoMap = (Map<String, Object>) item;
				return df.format(infoMap.get("date")) + "," + infoMap.get("productId");
			}));

			List<SaleStatisticsRspDto> proResultList = new ArrayList<>();

			// 遍历每天/月的账单
			logger.info("遍历生成每天/月的统计对象开始");
			__start = System.currentTimeMillis();
			for (Entry<String, ProductBills> entry : dateBillMap.entrySet()) {
				logger.info("生成" + entry.getKey() + "统计对象开始");
				long ___start = System.currentTimeMillis();

				SaleStatisticsRspDto dto = null;
				// 分两步: 账单 + 未出账单 (未出账单的产品，用统计详情表表的成功数、成本、应收)，

				// 第一步，账单
				if (null != entry.getValue()) {
					dto = new SaleStatisticsRspDto(entry.getValue());
				} else {
					dto = new SaleStatisticsRspDto();
				}

				dto.setDate(entry.getKey());

				// 第二步，未出账单的产品，用统计详情表的成功数、成本、应收
				// =============================第二步开始==============================
				BigDecimal cost = new BigDecimal(0); // 当天/月成本
				BigDecimal receivables = new BigDecimal(0); // 当天/月应收
				BigDecimal profit = new BigDecimal(0); // 当天/月毛利

				// 获取未出账单的产品，先获取所有产品的信息，排除当天/月出了账单的产品，剩下的就是未出账单的产品
				List<String> productIdColne = new ArrayList<String>(Arrays.asList(new String[productIdList.size()]));
				Collections.copy(productIdColne, productIdList);
				Set<String> set = hasBillProductIdMap.get(entry.getKey());
				if (null != set && !set.isEmpty()) {
					productIdColne.removeAll(set);
				}

				// 遍历所有未出账单的产品
				logger.info("第二步，统计" + entry.getKey() + "未出账单的产品的数据开始，未出账单产品数：" + productIdColne.size());
				long ____start = System.currentTimeMillis();
				// 遍历所有未出账单的产品，获取该产品在当天/月的统计记录
				for (String productid : productIdColne) {
					// 获取该产品在当天/月的统计记录
					List<Map<String, Object>> dataList = statisticsMap.get(entry.getKey() + "," + productid);
					if (dataList != null && dataList.size() > 0) {
						for (Map<String, Object> data : dataList) {
							long successCount = (long) data.get("successCount");
							BigDecimal productCost = (BigDecimal) data.get("cost");
							BigDecimal productReceivables = (BigDecimal) data.get("receivables");
							BigDecimal productGrossProfit = (BigDecimal) data.get("grossProfit");
							cost = cost.add(productCost); // 累计到当天/月成本
							receivables = receivables.add(productReceivables); // 累计到当天/月应收
							profit = profit.add(productGrossProfit); // 累计到当天/月应收
							dto.setPlatformCount(dto.getPlatformCount() + successCount);
						}
					}
				}
				cost.setScale(2, BigDecimal.ROUND_CEILING);
				receivables.setScale(2, BigDecimal.ROUND_CEILING);
				profit.setScale(2, BigDecimal.ROUND_CEILING);
				logger.info("第二步，统计" + entry.getKey() + "未出账单的产品的数据结束，耗时：" + (System.currentTimeMillis() - ____start));
				// =============================第二步结束==============================

				// 将计算出的成本和应收加到统计对象中
				String preCost = dto.getCost();
				if (NumberUtils.isParsable(preCost)) {
					dto.setCost(cost.add(new BigDecimal(preCost)).toString());
				} else {
					dto.setCost(String.format("%.2f", cost));
				}
				String receivablesStr = dto.getReceivables();
				if (NumberUtils.isParsable(receivablesStr)) {
					dto.setReceivables(receivables.add(new BigDecimal(receivablesStr)).toString());
				} else {
					dto.setReceivables(String.format("%.2f", receivables));
				}
				String profitStr = dto.getProfit();
				if (NumberUtils.isParsable(profitStr)) {
					dto.setProfit(profit.add(new BigDecimal(profitStr)).toString());
				} else {
					dto.setProfit(String.format("%.2f", profit));
				}
				// 设置当天/月新增客户数和新增发送量
				logger.info("获取新增客户数和新增发送量开始");
				____start = System.currentTimeMillis();
				dto.setNewCusCount(newContractNumMap.getOrDefault(entry.getKey(), 0));
				Set<String> productUseSet = cusUseDateMap.getOrDefault(entry.getKey(), new HashSet<>());
				if (!productUseSet.isEmpty()) {
					for (String productid : productUseSet) {
						if (!ListUtils.isEmpty(statisticsMap.get(entry.getKey() + "," + productid))) {
							for (Map<String, Object> data : statisticsMap.get(entry.getKey() + "," + productid)) {
								dto.setNewCusSendCount(dto.getNewCusSendCount() + (long) data.get("successCount"));
							}
						}
					}
				}
				logger.info("获取新增客户数和新增发送量结束，耗时：" + (System.currentTimeMillis() - ____start));

				proResultList.add(dto);
				logger.info("生成" + entry.getKey() + "统计对象结束，耗时：" + (System.currentTimeMillis() - ___start));
			}
			logger.info("遍历生成每天/月的统计对象开始，总耗时：" + (System.currentTimeMillis() - __start));

			// 按周、月份或者季度等合并记录
			logger.info("合并每天/月的统计对象到时间标题开始");
			__start = System.currentTimeMillis();
			List<SaleStatisticsRspDto> resultList = new ArrayList<>();
			String lastDate = "";
			for (SaleStatisticsRspDto rsp : proResultList) {
				if (!resultList.isEmpty()) {
					lastDate = resultList.get(resultList.size() - 1).getDate();
				}
				// 获取本对象的日期对应的时间标题，比如 2019-12-25 对应是 第四周（12-23到12-29）
				String thisDate = datemap.get(rsp.getDate());
				if (lastDate.equals(thisDate)) {
					SaleStatisticsRspDto temp = resultList.get(resultList.size() - 1);
					temp.addCustomerCount(rsp.getCustomerCount());
					temp.addPlatformCount(rsp.getPlatformCount());
					temp.addReceivables(rsp.getReceivables());
					temp.addActualReceivables(rsp.getActualReceivables());
					temp.addPayables(rsp.getPayables());
					temp.addActualPayables(rsp.getActualPayables());
					temp.addCost(rsp.getCost());
					temp.addProfit(rsp.getProfit());
					temp.addNewCusCount(rsp.getNewCusCount());
					temp.addNewCusSendCount(rsp.getNewCusSendCount());
				} else {
					rsp.setDate(thisDate);
					resultList.add(rsp);
				}
			}
			logger.info("合并每天/月的统计对象到时间标题结束，耗时：" + (System.currentTimeMillis() - __start));

			JSONArray json = JSONArray.parseArray(JSONObject.toJSONString(resultList));
			logger.info("用户：" + onlineUser.getUser().getLoginName() + "，查询统计记录结束，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("查询统计记录异常", e);
		}
		return BaseResponse.success("未查询到数据", new JSONArray());
	}

	/**
	 * 查询客户统计详情表获取成功数
	 *
	 * @param dateType
	 *            日期类型
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param customerTypeId
	 *            客户类型（点击条件）
	 * @param customerId
	 *            客户id（点击条件）
	 * @param productId
	 *            产品id（点击条件）
	 * @param deptIds
	 *            部门id（过滤条件）
	 * @param customerKeyWord
	 *            关键词（过滤条件）
	 * @param customerIdList
	 *            由过滤条件查出的客户id
	 * @return
	 * @throws BaseException
	 */
	private List<Map<String, Object>> queryStatistics(int dateType, String startTime, String endTime, String customerTypeId, String customerId,
			String productId, String deptIds, String customerKeyWord, List<String> customerIdList) throws BaseException {
		logger.info("查询客户统计详情表获取成功数开始");
		long _start = System.currentTimeMillis();
		List<Map<String, Object>> result = new ArrayList<>();
		String sqlStart;
		String sqlMiddle = "";
		String sqlEnd;
		Map<String, Object> params = new HashMap<>();
		if (dateType == 0) {
			// 按周查询的需要精确到天的维度
			sqlStart = "SELECT statsDate,customerId,productId,successCount,cost,receivables,grossProfit FROM CustomerStatistics WHERE statsDate >= :startTime AND statsDate <= :endTime AND businessType = "
					+ BusinessType.YTX.ordinal();
			sqlEnd = " GROUP BY statsDate,customerId,productId";
		} else if (dateType == 3) {
			// 按年
			sqlStart = "SELECT statsYear,customerId,productId,sum(successCount),sum(cost),sum(receivables),sum(grossProfit) FROM CustomerStatistics WHERE statsYear >= :startTime AND statsYear <= :endTime AND businessType = "
					+ BusinessType.YTX.ordinal();
			sqlEnd = " GROUP BY statsYear,customerId,productId";
		} else {
			// 按月、按季
			sqlStart = "SELECT statsYearMonth,customerId,productId,sum(successCount),sum(cost),sum(receivables),sum(grossProfit) FROM CustomerStatistics WHERE statsYearMonth >= :startTime AND statsYearMonth <= :endTime AND businessType = "
					+ BusinessType.YTX.ordinal();
			sqlEnd = " GROUP BY statsYearMonth,customerId,productId";
		}
		params.put("startTime", DateUtil.convert2(startTime));
		params.put("endTime", DateUtil.convert2(endTime));

		if (StringUtil.isNotBlank(productId)) {
			// 点击产品
			logger.info("查询统计，点击产品id：" + productId);
			sqlMiddle += " AND productId = :productId";
			params.put("productId", productId);
		} else if (StringUtil.isNotBlank(customerId)) {
			// 点击客户
			logger.info("查询统计，点击客户id：" + customerId);
			sqlMiddle += " AND customerId = :customerId";
			params.put("customerId", customerId);
		} else {
			// 未点击产品或客户，按数据权限和过滤条件
			OnlineUser onlineUser = getOnlineUserAndOnther();
			User user = onlineUser.getUser();
			Role role = roleService.read(onlineUser.getRoleId());
			int dataPermission = role.getDataPermission();
			logger.info("查询统计，未点击客户或产品，按数据权限和过滤条件查询");
			// 数据权限
			if (DataPermission.Self.ordinal() == dataPermission) {
				sqlMiddle += " AND saleUserId = :saleUserId";
				params.put("saleUserId", user.getOssUserId());
			} else if (DataPermission.Dept.ordinal() == dataPermission) {
				sqlMiddle += " AND deptId = :deptId";
				params.put("deptId", user.getDeptId());
			} else if (DataPermission.Customize.ordinal() == dataPermission) {
				sqlMiddle += " AND deptId IN :deptIds";
				// 用户数据权限下的部门
				List<String> userDeptIds = new ArrayList<>(Arrays.asList(role.getDeptIds().split(",")));
				if (StringUtil.isNotBlank(deptIds)) {
					// 部门过滤条件
					List<String> searchdeptIds = new ArrayList<>(Arrays.asList(deptIds.split(",")));
					searchdeptIds = searchdeptIds.stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
					if (searchdeptIds.size() > 0) {
						// 取交集
						userDeptIds.retainAll(searchdeptIds);
					}
				}
				params.put("deptIds", userDeptIds);
			} else if (DataPermission.All.ordinal() == dataPermission || DataPermission.Flow.ordinal() == dataPermission) {
				// 全部、流程 权限
				if (StringUtil.isNotBlank(deptIds)) {
					// 部门过滤条件
					List<String> searchdeptIds = new ArrayList<>(Arrays.asList(deptIds.split(",")));
					searchdeptIds = searchdeptIds.stream().filter(StringUtil::isNotBlank).collect(Collectors.toList());
					if (searchdeptIds.size() > 0) {
						sqlMiddle += " AND deptId IN :deptIds";
						params.put("deptIds", searchdeptIds);
					}
				}
			}

			if (StringUtil.isNotBlank(customerTypeId)) {
				// 点击客户类型
				sqlMiddle += " AND customerTypeId = :customerTypeId";
				params.put("customerTypeId", customerTypeId);
			}

			if (StringUtil.isNotBlank(customerKeyWord) || DataPermission.Flow.ordinal() == dataPermission) {
				// 关键词搜索得到的客户id，或者按流程权限查到的客户id
				sqlMiddle += " AND customerId IN :customerIdList";
				params.put("customerIdList", customerIdList);
			}
		}
		String sql = sqlStart + sqlMiddle + sqlEnd;
		List<Object> statisticsList = baseDao.findByhql(sql, params, 0);
		if (ListUtils.isEmpty(statisticsList)) {
			logger.info("未查询到记录");
			return result;
		}
		logger.info("查询到" + statisticsList.size() + "条统计记录");
		for (Object obj : statisticsList) {
			Map<String, Object> map = new HashMap<>();
			Object[] ob = (Object[]) obj;
			map.put("date", (java.sql.Date) ob[0]);
			map.put("customerId", (String) ob[1]);
			map.put("productId", (String) ob[2]);
			map.put("successCount", ((Long) ob[3]));
			map.put("cost", ((BigDecimal) ob[4]));
			map.put("receivables", ((BigDecimal) ob[5]));
			map.put("grossProfit", ((BigDecimal) ob[6]));
			result.add(map);
		}
		logger.info("查询客户统计详情表获取成功数结束，耗时：" + (System.currentTimeMillis() - _start));
		return result;
	}

	/**
	 * 根据客户的使用日期，查询时间范围内每个周期每天/月所有新增客户的产品集合
	 *
	 * @param dateType
	 *            日期类型
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param customerIdList
	 *            客户查询范围
	 * @return
	 * @throws ServiceException
	 */
	private Map<String, Set<String>> queryNewUseProduct(int dateType, String startTime, String endTime, List<String> customerIdList) throws ServiceException {
		logger.info("查询时间范围内开始使用的客户开始，startTime：" + startTime + "，endTime：" + endTime);
		long _start = System.currentTimeMillis();
		Map<String, Set<String>> cusUseDateMap = new HashMap<>();
		// 根据客户的使用日期，查询新合作客户
		SearchFilter cusfilter = new SearchFilter();
		cusfilter.getRules().add(new SearchRule("useDate", Constants.ROP_GE, DateUtil.convert2(startTime)));
		cusfilter.getRules().add(new SearchRule("useDate", Constants.ROP_LE, DateUtil.convert2(endTime)));
		cusfilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, customerIdList));
		List<Customer> cusList = customerService.queryAllBySearchFilter(cusfilter);
		logger.info("查询时间范围内开始使用的客户结束，查询到" + (cusList == null ? 0 : cusList.size()) + "条客户记录，耗时：" + (System.currentTimeMillis() - _start));
		if (ListUtils.isEmpty(cusList)) {
			return cusUseDateMap;
		}

		// 根据日期类型，获取客户使用日期所属的一个时间周期的每天/月（只获取一个周期，因为到下个周期就不算新增客户了）
		// 按周，客户A 9月9日合作，则map里存放：客户A的id -> [2019-12-09, 2019-12-10, 2019-12-11,
		// 2019-12-12, 2019-12-13, 2019-12-14, 2019-12-15]
		// 按月，客户B 10月合作，则map里存放：客户B的id -> [2019-10]
		// 按季，客户C 10月合作，则map里存放：客户C的id -> [2019-10, 2019-11, 2019-12]
		// 按年，客户D 9月合作，则map里存放：客户D的id -> [2019-09, 2019-10, 2019-11, 2019-12]
		logger.info("获取使用日期所属的一个周期开始");
		_start = System.currentTimeMillis();
		Map<String, List<String>> cusDateMap = new HashMap<String, List<String>>();
		for (Customer customer : cusList) {
			// 获取查询时间范围内所有date的集合
			List<String> dateList = new ArrayList<>();
			if (dateType == 0) {
				// 按周，从使用日期开始，到所属的周结束日期
				DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format1);
				LocalDate local = customer.getUseDate().toLocalDateTime().toLocalDate();
				dateList.add(ymdfmt.format(local));
				while (local.getDayOfWeek().getValue() != 7) {
					local = local.plusDays(1);
					dateList.add(ymdfmt.format(local));
				}

			} else if (dateType == 1) {
				// 按月，使用日期所属的月
				dateList.add(DateUtil.convert(customer.getUseDate(), DateUtil.format4));
			} else if (dateType == 2) {
				// 按季，从使用日期所属的月，到该季结束
				DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
				LocalDate local = customer.getUseDate().toLocalDateTime().toLocalDate();
				dateList.add(ymdfmt.format(local));
				while (local.getMonthValue() % 3 != 0) {
					local = local.plusMonths(1);
					dateList.add(ymdfmt.format(local));
				}
			} else if (dateType == 3) {
				// 按年，从使用日期所属的月，到该年结束
				DateTimeFormatter ymdfmt = DateTimeFormatter.ofPattern(DateUtil.format4);
				LocalDate local = customer.getUseDate().toLocalDateTime().toLocalDate();
				dateList.add(ymdfmt.format(local));
				while (local.getMonthValue() != 12) {
					local = local.plusMonths(1);
					dateList.add(ymdfmt.format(local));
				}
			}
			cusDateMap.put(customer.getCustomerId(), dateList);
		}
		logger.info("获取使用日期所属的一个周期结束，耗时：" + (System.currentTimeMillis() - _start));

		// 查询客户的产品，把每个产品下的账号，放到该客户的周期内的每天/月中，最后再整合
		logger.info("根据使用客户查询产品开始");
		_start = System.currentTimeMillis();
		cusfilter = new SearchFilter();
		List<String> cusIds = cusList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
		cusfilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, cusIds));
		List<CustomerProduct> cusProList = customerProductService.queryAllByFilter(cusfilter);
		logger.info("根据使用客户查询产品结束，查询到" + (cusProList == null ? 0 : cusProList.size()) + "条产品记录，耗时：" + (System.currentTimeMillis() - _start));

		// 每天/月有哪些新增客户的账号
		logger.info("获取每天/月有哪些新增客户的产品开始");
		_start = System.currentTimeMillis();
		for (CustomerProduct product : cusProList) {
			// 日期
			List<String> cusDateList = cusDateMap.get(product.getCustomerId());
			// 将每个客户的周期内的每天/月的产品，整合到当天/月
			for (String date : cusDateList) {
				if (!cusUseDateMap.containsKey(date)) {
					cusUseDateMap.put(date, new HashSet<>());
				}
				cusUseDateMap.get(date).add(product.getProductId());
			}
		}
		logger.info("获取每天/月有哪些新增客户的产品结束，耗时：" + (System.currentTimeMillis() - _start));
		return cusUseDateMap;
	}
}