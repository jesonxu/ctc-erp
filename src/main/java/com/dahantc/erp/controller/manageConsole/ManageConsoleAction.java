package com.dahantc.erp.controller.manageConsole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.manageConsole.entity.BusinessReportUI;
import com.dahantc.erp.controller.manageConsole.entity.ProductBusinessUI;
import com.dahantc.erp.controller.manageConsole.entity.SaleAnalysisUI;
import com.dahantc.erp.controller.manageConsole.entity.SaleBusinessDetailUI;
import com.dahantc.erp.controller.manageConsole.entity.SaleReceivablesDetailUI;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.enums.JobType;
import com.dahantc.erp.enums.MenuGroup.ConsoleType;
import com.dahantc.erp.enums.PagePermission;
import com.dahantc.erp.enums.SettleType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.multipleStatistics.IComGroupYearStatisticsService;
import com.dahantc.erp.vo.multipleStatistics.IRegionStatisticsService;
import com.dahantc.erp.vo.multipleStatistics.impl.ComGroupYearStatisticsServiceImpl.RowData;
import com.dahantc.erp.vo.multipleStatistics.impl.RegionStatisticsServiceImpl;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.saleAnalysisStatistics.entity.SaleAnalysis;
import com.dahantc.erp.vo.saleAnalysisStatistics.service.ISaleAnalysisService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("manageConsole")
public class ManageConsoleAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ManageConsoleAction.class);

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IRegionStatisticsService regionStatisticsService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IComGroupYearStatisticsService comGroupYearStatisticsService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISaleAnalysisService saleAnalysisService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IProductTypeService productTypeService;

	/**
	 * 跳转到管理工作台
	 */
	@RequestMapping("/toManageConsole")
	public String toManageConsole(Model model) {
		String roleId = getOnlineUserAndOnther().getRoleId();
		try {
			List<String> manageConsolePerssionList = Arrays.asList(PagePermission.values()).stream()
					.filter(permission -> permission.getConsoleType().equals(ConsoleType.MANAGER_CONSOLE)).map(PagePermission::getDesc)
					.collect(Collectors.toList());
			Role role = roleService.read(roleId);
			Set<String> tablePermissionList = role.getPagePermissionMap().entrySet().stream()
					.filter(entry -> entry.getValue() && manageConsolePerssionList.contains(entry.getKey())).map(Entry::getKey).collect(Collectors.toSet());
			model.addAttribute("tablePermission", StringUtils.join(tablePermissionList.iterator(), ","));
			return "/views/manageConsole/manageConsole";
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "";
	}

	/**
	 * 跳转到业绩概况页面
	 */
	@RequestMapping("/toBusinessReport")
	public String toBusinessReport() {
		return "/views/manageConsole/businessReport";
	}

	/**
	 * 业绩概况查询
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/readBusinessReport")
	public BaseResponse<JSONArray> readBusinessReport() {
		Map<String, BusinessReportUI> resultMap = new TreeMap<String, BusinessReportUI>(new Comparator<String>() {
			public int compare(String obj1, String obj2) {
				return obj1.compareTo(obj2);
			}
		});
		List<Object[]> list = null;
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			logger.info("用户：" + onlineUser.getUser().getLoginName() + "，查询业绩概况开始");
			List<String> deptIdList = departmentService.getDeptIdsByPermission(onlineUser);
			if (ListUtils.isEmpty(deptIdList)) {
				return BaseResponse.success("未查询到部门", new JSONArray());
			}
			String deptids = String.join(",", deptIdList.stream().map(deptid -> "'" + deptid + "'").collect(Collectors.toList()));
			list = (List<Object[]>) baseDao
					.selectSQL("select statsyear, sum(receivables), sum(cost), sum(grossprofit) from erp_customer_statistics where deptid in (" + deptids
							+ ") GROUP BY statsyear");
			if (list != null && list.size() > 0) {
				for (Object[] datas : list) {
					Date statsYear = (Date) datas[0];
					String year = DateUtil.convert(statsYear, DateUtil.format11);
					BigDecimal incomeTotal = new BigDecimal(datas[1].toString());
					BigDecimal costTotal = new BigDecimal(datas[2].toString());
					BigDecimal grossTotal = new BigDecimal(datas[3].toString());

					BusinessReportUI ui = new BusinessReportUI();
					ui.setYear(year);
					ui.setIncomeTotal(incomeTotal.doubleValue());// 总收入
					ui.setCostTotal(costTotal.doubleValue());// 总成本
					ui.setGrossTotal(grossTotal.doubleValue());// 总毛利
					if (incomeTotal.doubleValue() != 0) {
						ui.setGrossProfit(grossTotal.divide(incomeTotal, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue() + "%");// 毛利率
					} else {
						ui.setGrossProfit("0");
					}
					ui.setNetProfit(grossTotal.doubleValue());// 净利润，暂时设置毛利润值，后面减掉其他费用
					resultMap.put(year, ui);
				}
			}
			List<Object[]> dailyList = (List<Object[]>) baseDao
					.selectSQL("SELECT statsyear, sum(cost) from erp_dailyexpenses_statistics where deptid in (" + deptids + ") group by statsyear");
			if (dailyList != null && dailyList.size() > 0) {
				for (Object[] datas : dailyList) {
					Date statsYear = (Date) datas[0];
					String year = DateUtil.convert(statsYear, DateUtil.format11);
					BigDecimal costTotal = new BigDecimal(datas[1].toString());
					BusinessReportUI ui = resultMap.get(year);
					if (ui == null) {
						ui = new BusinessReportUI();
						ui.setYear(year);
						ui.setGrossProfit("0");
						resultMap.put(year, ui);
					}
					ui.setNetProfit(ui.getNetProfit() - costTotal.doubleValue());
				}
			}
			JSONArray json = JSONArray.parseArray(JSONObject.toJSONString(resultMap.values()));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("查询业绩概况异常", e);
		} finally {
			if (list != null) {
				list.clear();
				list = null;
			}
			if (resultMap != null) {
				resultMap.clear();
				resultMap = null;
			}
		}
		return BaseResponse.success("未查询到数据", new JSONArray());
	}

	/**
	 * 跳转到产品收入结构表
	 */
	@RequestMapping("/toProductBusiness")
	public String toProductBusiness() {
		return "/views/manageConsole/productBusiness";
	}

	/**
	 * 查询产品收入结构
	 */
	@ResponseBody
	@RequestMapping("/readProductBusiness")
	@SuppressWarnings("deprecation")
	public BaseResponse<JSONArray> readProductBusiness() {
		Map<Integer, ProductBusinessUI> resultMap = new TreeMap<>((obj1, obj2) -> obj1 - obj2);
		List<Object[]> list = null;
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.noLogin("请先登录");
			}
			logger.info("用户：" + onlineUser.getUser().getLoginName() + "，查询产品收入结构报表开始");
			// 默认本年
			Date date = DateUtil.getThisYearFirst();
			String year = request.getParameter("year");
			if (StringUtil.isNotBlank(year)) {
				date = DateUtil.convert(year, DateUtil.format11);
			}
			List<String> deptIdList = departmentService.getDeptIdsByPermission(onlineUser);
			if (ListUtils.isEmpty(deptIdList)) {
				return BaseResponse.success("未查询到部门", new JSONArray());
			}
			String deptids = String.join(",", deptIdList.stream().map(deptid -> "'" + deptid + "'").collect(Collectors.toList()));
			list = baseDao.selectSQL(
					"select producttype, sum(successcount), sum(receivables), sum(cost), sum(grossProfit), statsyearmonth from erp_customer_statistics where deptid in ("
							+ deptids + ") and businesstype = ? and statsyearmonth >= ? and statsyearmonth < ?  GROUP BY producttype, statsyearmonth",
					new Object[] { BusinessType.YTX.ordinal(), date, DateUtil.getNextYearFirst(date) });

			if (list != null && list.size() > 0) {
				for (Object[] datas : list) {
					int productType = new Integer(datas[0].toString());
					long successCount = new Long(datas[1].toString());
					BigDecimal income = new BigDecimal(datas[2].toString());
					BigDecimal cost = new BigDecimal(datas[3].toString());
					BigDecimal gross = new BigDecimal(datas[4].toString());
					Date yearMonth = (Date) datas[5];

					ProductBusinessUI ui = new ProductBusinessUI();
					ui.setMonth((yearMonth.getMonth() + 1) + "月");
					ui.setProductType(productTypeService.getProductTypeNameByValue(productType));
					ui.setSuccessCount(successCount);
					ui.setEquityIncome(income.doubleValue());
					ui.setCost(cost.doubleValue());
					ui.setGross(gross.doubleValue());
					if (successCount != 0) {
						ui.setSalePrice(income.divide(new BigDecimal(successCount), 6, BigDecimal.ROUND_HALF_UP).doubleValue());
						ui.setCostPrice(cost.divide(new BigDecimal(successCount), 6, BigDecimal.ROUND_HALF_UP).doubleValue());
					}
					resultMap.put(productType + yearMonth.getMonth() * 100, ui);
				}
			}
			// 预付结余，用权益提成表月末的余额，如果是查本月，就取昨天的余额
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			// 后付欠款
			list = baseDao.selectSQL(
					"SELECT producttype, sum(receivables-actualReceivables), DATE_FORMAT(wtime,'%Y-%m') yearmonth from erp_cash_flow where deptid in (" + deptids
							+ ") and businesstype = ? and entitytype = 1 and receivables > actualReceivables and wtime >= ? and wtime < ? GROUP BY producttype, yearMonth",
					new Object[] { BusinessType.YTX.ordinal(), date, DateUtil.getNextYearFirst(date) });
			if (list != null && list.size() > 0) {
				for (Object[] datas : list) {
					int productType = new Integer(datas[0].toString());
					BigDecimal arrears = (BigDecimal) datas[1];
					String yMonth = (String) datas[2];
					Date yearMonth = DateUtil.convert4(yMonth);

					ProductBusinessUI ui = resultMap.get(productType + yearMonth.getMonth() * 100);
					if (ui == null) {
						ui = new ProductBusinessUI();
						ui.setMonth((yearMonth.getMonth() + 1) + "月");
						ui.setProductType(productTypeService.getProductTypeNameByValue(productType));
						resultMap.put(productType + yearMonth.getMonth() * 100, ui);
					}
					ui.setArrears(arrears.doubleValue());
				}
			}
			// 查统计明细表获取总成功数
			List<Customer> customerList = customerService.readCustomers(onlineUser, "", "", "", "");
			List<String> customerIdList = CollectionUtils.isEmpty(customerList) ? null
					: customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
			Set<String> loginNameSet = customerProductService.queryLoginNameByCustomer(customerIdList);
			if (!CollectionUtils.isEmpty(loginNameSet)) {
				String loginNames = String.join(",", loginNameSet.stream().map(loginName -> "'" + loginName + "'").collect(Collectors.toList()));
				list = baseDao.selectSQL(
						"select producttype, sum(successcount), DATE_FORMAT(statsdate,'%Y-%m') yearmonth from erp_customerproducttj where loginname in ("
								+ loginNames + ") and businesstype = ? and statsdate >= ? and statsdate < ? GROUP BY producttype, yearmonth",
						new Object[] { BusinessType.YTX.ordinal(), date, DateUtil.getNextYearFirst(date) });
				if (!CollectionUtils.isEmpty(list)) {
					for (Object[] datas : list) {
						int productType = new Integer(datas[0].toString());
						Number totalSuccessCount = (Number) datas[1];
						String yMonth = (String) datas[2];
						Date yearMonth = DateUtil.convert4(yMonth);

						ProductBusinessUI ui = resultMap.get(productType + yearMonth.getMonth() * 100);
						if (ui == null) {
							ui = new ProductBusinessUI();
							ui.setMonth((yearMonth.getMonth() + 1) + "月");
							ui.setProductType(productTypeService.getProductTypeNameByValue(productType));
							resultMap.put(productType + yearMonth.getMonth() * 100, ui);
						}
						ui.setTotalSuccessCount(totalSuccessCount.longValue());
					}
				}
			}
			JSONArray json = JSONArray.parseArray(JSON.toJSONString(resultMap.values()));
			return BaseResponse.success(json);
		} catch (Exception e) {
			logger.error("查询业绩概况异常", e);
		} finally {
			if (list != null) {
				list.clear();
				list = null;
			}
			if (resultMap != null) {
				resultMap.clear();
				resultMap = null;
			}
		}
		return BaseResponse.success("未查询到数据", new JSONArray());
	}

	/**
	 * 跳转各大区域经营状况表
	 */
	@RequestMapping("/toRegionReport")
	public String toRegionReport() {
		return "/views/manageConsole/regionReportSheet";
	}

	@RequestMapping("/queryRegionReportData")
	@ResponseBody
	public BaseResponse<Object> queryRegionReportData() {
		long startTime = System.currentTimeMillis();
		Map<String, Map<String, RegionStatisticsServiceImpl.RegionStatisticsData[]>> result = regionStatisticsService
				.queryRegionStatistics(getOnlineUserAndOnther());
		logger.info("查询云通讯区域统计耗时：" + (System.currentTimeMillis() - startTime));
		if (result == null || result.isEmpty()) {
			return BaseResponse.error("查询失败");
		} else {
			return BaseResponse.success(result);
		}
	}

	/**
	 * 跳转通信集团近年经营报表
	 */
	@RequestMapping("/toComGroupReport")
	public String toComGroupReport() {
		request.setAttribute("year", request.getParameter("year"));
		return "/views/manageConsole/comGroupYearReport";
	}

	// 通信集团近年经营报表-XX年的月份详情
	@RequestMapping("/queryComGroupReportData")
	@ResponseBody
	public BaseResponse<Object> queryComGroupReportData() {
		long startTime = System.currentTimeMillis();
		String year = request.getParameter("year");
		logger.info("查询[通信集团" + year + "经营报表-月份详情]开始");
		List<RowData> result = comGroupYearStatisticsService.queryComYearStatistics(DateUtil.convert(year, "yyyy"), getOnlineUserAndOnther());
		logger.info("查询[通信集团" + year + "经营报表-月份详情]结束，耗时：" + (System.currentTimeMillis() - startTime));
		if (result == null || result.isEmpty()) {
			return BaseResponse.error("查询失败");
		} else {
			return BaseResponse.success(result);
		}
	}

	/**
	 * 跳转到销售业绩页面
	 */
	@RequestMapping("/toSaleBusinessDetail")
	public String toSaleBusinessDetail() {
		return "/views/manageConsole/saleBusinessDetail";
	}

	/**
	 * 查询销售业绩明细
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/readSaleBusinessDetail")
	public BaseResponse<JSONArray> readSaleBusinessDetail(@RequestParam String year) {
		logger.info("查询销售业绩明细开始，年份：" + year);
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		String statsyear = year + "-01-01 00:00:00";
		String yearend = year + "-12-31 23:59:59";

		List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
		if (ListUtils.isEmpty(deptIdList)) {
			return BaseResponse.error(new JSONArray());
		}
		String deptids = String.join(",", deptIdList.stream().map(deptid -> "'" + deptid + "'").collect(Collectors.toList()));
		// 每个销售的业绩数据
		Map<String, SaleBusinessDetailUI> saleDataMap = new HashMap<>();
		// 用户id -> {真实姓名、部门名、事业部名}
		Map<String, HashMap<String, String>> nameMap = userService.getUserAndDeptName();
		try {
			// 日期字符串转换成对象再转字符串，防止攻击
			Date statsYear = DateUtil.convert2(statsyear);
			Date yearEnd = DateUtil.convert2(yearend);
			statsyear = DateUtil.convert(statsYear, DateUtil.format2);
			yearend = DateUtil.convert(yearEnd, DateUtil.format2);

			// 查询统计表获取收入和毛利润
			String statisticsSql = "select deptid,saleuserid,statsyearmonth,sum(receivables),sum(grossprofit) from erp_customer_statistics"
					+ " where statsyear = '" + statsyear + "' group by deptid,saleuserid,statsyearmonth having deptid in (" + deptids + ")";
			List<Object[]> statisticsList = (List<Object[]>) baseDao.selectSQL(statisticsSql);
			logger.info("查询到" + (statisticsList == null ? 0 : statisticsList.size()) + "条统计数据，耗时：" + (System.currentTimeMillis() - _start));
			// 将统计结果按销售id分组
			Map<String, List<Object[]>> statisticsMap = new HashMap<>();
			if (statisticsList != null && statisticsList.size() > 0) {
				statisticsMap = statisticsList.stream().filter(data -> data[1] != null).collect(Collectors.groupingBy(data -> (String) data[1]));
			}
			// 从用户表按岗位类型查出的销售id集合
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("jobType", Constants.ROP_CN, JobType.Sales.name()));
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
			List<User> userSaleList = userService.queryAllBySearchFilter(filter);
			Set<String> saleIdSet = ListUtils.isEmpty(userSaleList) ? new HashSet<>()
					: new HashSet<>(userSaleList.stream().map(User::getOssUserId).collect(Collectors.toSet()));
			logger.info("查询用户表获取的销售数：" + saleIdSet.size());
			// 从统计表查出的销售id集合
			Set<String> statisticsSaleId = statisticsMap.keySet();
			logger.info("查询统计表获取的销售数：" + statisticsSaleId.size());
			// 取并集，不漏掉销售
			saleIdSet.addAll(statisticsSaleId);
			logger.info("取并集最终获取的销售数：" + saleIdSet.size());
			// 查询每个销售的客户，用来获取收款记录
			Map<String, ArrayList<String>> customerIdMap = getCustomerByUser(new ArrayList<>(saleIdSet));
			// 遍历所有销售
			for (String ossUserId : saleIdSet) {
				// 当前销售的名字、部门、事业部名
				HashMap<String, String> info = nameMap.getOrDefault(ossUserId, new HashMap<>());

				// 当前销售的业绩数据，没有就新建
				SaleBusinessDetailUI saleUi = saleDataMap.getOrDefault(ossUserId, new SaleBusinessDetailUI());
				saleUi.setRegionName(info.getOrDefault("regionName", "-"));
				// 没有名字则显示id
				saleUi.setSaleName(info.getOrDefault("realName", ossUserId));
				saleUi.setDeptName(info.getOrDefault("deptName", "-"));
				String parentDeptName = info.getOrDefault("parentDeptName", "-");
				saleUi.setParentDeptName(parentDeptName);

				// 当前销售所在事业部的业绩数据汇总，没有就新建
				SaleBusinessDetailUI deptUi;
				if (saleDataMap.containsKey(parentDeptName + "汇总")) {
					deptUi = saleDataMap.get(parentDeptName + "汇总");
				} else {
					deptUi = new SaleBusinessDetailUI();
					deptUi.setRegionName(saleUi.getRegionName());
					deptUi.setParentDeptName(parentDeptName + "汇总");
					deptUi.setDeptName(parentDeptName + "汇总");
				}

				// 当前销售的客户id，用来查收支表记录
				List<String> customerIdList = customerIdMap.getOrDefault(ossUserId, null);
				List<Object[]> chargeList = null;
				if (customerIdList != null) {
					// 每个id前后加上单引号，用来查SQL
					customerIdList = customerIdList.stream().map(id -> "'" + id + "'").collect(Collectors.toList());

					// 查询该销售，或属于该销售的客户的每个月的收款记录
					StringBuffer sqlsb = new StringBuffer("select DATE_FORMAT(actualpaytime, '%m'),sum(chargeprice) from erp_charge_record");
					sqlsb.append(" where businesstype = ").append(BusinessType.YTX.ordinal());
					sqlsb.append(" and chargetype in (").append(IncomeExpenditureType.PREPURCHASE.ordinal()).append(",")
							.append(IncomeExpenditureType.ADVANCE.ordinal()).append(",").append(IncomeExpenditureType.BILL.ordinal()).append(")");
					sqlsb.append(" and actualpaytime >= '").append(statsyear).append("' and actualpaytime <= '").append(yearend).append("'");
					sqlsb.append(" and (createrid = '").append(ossUserId).append("'");
					sqlsb.append(" or supplierid in (").append(String.join(",", customerIdList)).append("))");
					sqlsb.append(" group by DATE_FORMAT(actualpaytime, '%m')");
					chargeList = (List<Object[]>) baseDao.selectSQL(sqlsb.toString());
					if (chargeList != null && chargeList.size() > 0) {
						// 遍历查到的每月收款
						for (Object[] chargeRecord : chargeList) {
							String chargeMonth = (String) chargeRecord[0];
							BigDecimal income = (BigDecimal) chargeRecord[1];
							// 累加到合计
							saleUi.addSumIncome(income);

							deptUi.addSumIncome(income);
							// 设置到销售月数据
							BigDecimal[] saleMonthData = saleUi.getMonthData(chargeMonth);
							saleMonthData[2] = income;
							saleUi.setMonthData(chargeMonth, saleMonthData);
							// 累加到事业部月数据
							BigDecimal[] deptMonthData = deptUi.getMonthData(chargeMonth);
							deptMonthData[2] = deptMonthData[2].add(income);
							deptUi.setMonthData(chargeMonth, deptMonthData);
						}
					}
				}

				// 遍历当前销售的每个月的统计数据
				List<Object[]> monthDataList = statisticsMap.getOrDefault(ossUserId, new ArrayList<>());
				for (Object[] data : monthDataList) {
					String statsMonth = DateUtil.convert((java.sql.Date) data[2], DateUtil.format6);
					BigDecimal receivables = (BigDecimal) data[3];
					BigDecimal grossProfit = (BigDecimal) data[4];
					// 累计到该销售的总计
					saleUi.addSumReceivables(receivables);
					saleUi.addSumGrossProfit(grossProfit);
					// 累计到事业部的总计
					deptUi.addSumReceivables(receivables);
					deptUi.addSumGrossProfit(grossProfit);
					// 设置到销售月数据
					BigDecimal[] saleMonthData = saleUi.getMonthData(statsMonth);
					saleMonthData[0] = receivables;
					saleMonthData[1] = grossProfit;
					saleUi.setMonthData(statsMonth, saleMonthData);
					// 累加到事业部月数据
					BigDecimal[] deptMonthData = deptUi.getMonthData(statsMonth);
					deptMonthData[0] = deptMonthData[0].add(receivables);
					deptMonthData[1] = deptMonthData[1].add(grossProfit);
					deptUi.setMonthData(statsMonth, deptMonthData);
				}
				saleDataMap.put(ossUserId, saleUi);
				saleDataMap.put(parentDeptName + "汇总", deptUi);
			}
			ArrayList<SaleBusinessDetailUI> uiList = new ArrayList<>(saleDataMap.values());
			// 按区域、事业部、部门排序
			uiList.sort((o1, o2) -> {
				if (o1.getRegionName().equals(o2.getRegionName())) {
					if (o1.getParentDeptName().equals(o2.getParentDeptName())) {
						return o1.getDeptName().compareTo(o2.getDeptName());
					} else {
						return o1.getParentDeptName().compareTo(o2.getParentDeptName());
					}
				} else {
					return o1.getRegionName().compareTo(o2.getRegionName());
				}
			});
			result = JSONArray.parseArray(JSON.toJSONString(uiList));
			logger.info("查询销售业绩明细结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.info("查询销售业绩明细异常", e);
		} finally {
			if (nameMap != null) {
				nameMap.clear();
				nameMap = null;
			}
			if (saleDataMap != null) {
				saleDataMap.clear();
				saleDataMap = null;
			}
		}
		return BaseResponse.success(result);
	}

	/**
	 * 根据销售id，获取属于这些销售的客户，没有客户的销售没有K，V
	 *
	 * @param userIdList
	 *            查询的范围
	 * @return 销售id -> [客户id]
	 */
	private HashMap<String, ArrayList<String>> getCustomerByUser(List<String> userIdList) {
		logger.info("获取用户和客户对应关系开始");
		long _start = System.currentTimeMillis();
		HashMap<String, ArrayList<String>> customerIdMap = new HashMap<>();
		try {
			if (!ListUtils.isEmpty(userIdList)) {
				// 查询销售的所有客户
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, userIdList));
				List<Customer> customerList = customerService.queryAllBySearchFilter(filter);
				logger.info("根据用户查询到" + (customerList == null ? 0 : customerList.size()) + "条客户记录，耗时：" + (System.currentTimeMillis() - _start));
				// 按销售id存放客户id
				if (!ListUtils.isEmpty(customerList)) {
					Map<String, List<Customer>> customerMap = customerList.stream().collect(Collectors.groupingBy(Customer::getOssuserId));
					for (Map.Entry<String, List<Customer>> customers : customerMap.entrySet()) {
						List<Customer> userCustomer = customers.getValue();
						ArrayList<String> userCustomerId = userCustomer.stream().map(Customer::getCustomerId).collect(Collectors.toCollection(ArrayList::new));
						String ossUserId = customers.getKey();
						customerIdMap.put(ossUserId, userCustomerId);
					}
				}
				logger.info("获取用户和客户对应关系结束，耗时：" + (System.currentTimeMillis() - _start));
			}
		} catch (Exception e) {
			logger.info("获取用户和客户对应关系异常", e);
		}
		return customerIdMap;
	}

	/**
	 * 跳转到销售业绩明细页面
	 */
	@RequestMapping("/toSaleDetail")
	public String toSaleReceivablesDetail() {
		return "/views/manageConsole/saleReceivablesDetail";
	}

	@ResponseBody
	@RequestMapping("/readSaleReceivablesDetail")
	public BaseResponse<JSONArray> readSaleReceivablesDetail(@RequestParam String year) {
		logger.info("查询各事业部/销售人员收款明细表开始，年份：" + year);
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("parentid", Constants.ROP_NE, "0"));
			List<Department> dept = departmentService.queryAllBySearchFilter(filter);
			logger.info("查询所有部门信息耗时：" + (System.currentTimeMillis() - _start));
			Map<String, Department> tempMap = new HashMap<>();
			if (dept != null && !dept.isEmpty()) {
				for (Department department : dept) {
					tempMap.put(department.getDeptid(), department);
				}
			}
			// 按数据权限查的部门
			List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
			if (ListUtils.isEmpty(deptIdList)) {
				return BaseResponse.error(new JSONArray());
			}
			String deptids = String.join(",", deptIdList.stream().map(deptid -> "'" + deptid + "'").collect(Collectors.toList()));

			// 日期字符串转换成对象再转字符串，防止攻击
			String statsyear = DateUtil.convert(DateUtil.convert2(year + "-01-01 00:00:00"), DateUtil.format2);
			String yearend = DateUtil.convert(DateUtil.convert2(year + "-12-31 23:59:59"), DateUtil.format2);

			_start = System.currentTimeMillis();
			String sql = "SELECT sum(bil.receivables) as totalRece, sum(bil.actualreceivables) as totalAcRece, cus.deptid, cus.ossuserid "
					+ "FROM erp_bill as bil LEFT JOIN erp_customer as cus on bil.entityid=cus.customerid WHERE entityType=1 and bil.wtime>=? and bil.wtime<=? GROUP BY cus.ossuserid having deptid in ("
					+ deptids + ")";
			List<?> billList = (List<?>) baseDao.selectSQL(sql, new Object[] { statsyear, yearend });
			logger.info("查询账单信息耗时：" + (System.currentTimeMillis() - _start));

			_start = System.currentTimeMillis();
			Map<String, String> cacheUserMap = userService.queryAllBySearchFilter(null).stream()
					.collect(Collectors.toMap(User::getOssUserId, User::getRealName, (k1, k2) -> k2));

			BigDecimal totalReceivables = new BigDecimal(0);
			BigDecimal totalActualreceivables = new BigDecimal(0);
			for (Object obj : billList) {
				Object[] objs = (Object[]) obj;
				BigDecimal receivables = (BigDecimal) objs[0];
				BigDecimal actualreceivables = (BigDecimal) objs[1];
				String deptid = (String) objs[2];
				String ossuserid = (String) objs[3];
				Department department = tempMap.get(getBusinessDept(tempMap, deptid));
				if (department != null) {
					SaleReceivablesDetailUI ui = new SaleReceivablesDetailUI(department.getDeptname(), cacheUserMap.get(ossuserid), receivables,
							actualreceivables);
					totalReceivables = totalReceivables.add(receivables);
					totalActualreceivables = totalActualreceivables.add(actualreceivables);
					ui.calculatePercent();
					result.add(ui);
				}
			}
			result.sort((o1, o2) -> {
				SaleReceivablesDetailUI ui1 = (SaleReceivablesDetailUI) o1;
				SaleReceivablesDetailUI ui2 = (SaleReceivablesDetailUI) o2;
				if (StringUtils.equals(ui1.getDeptName(), ui2.getDeptName())) {
					return ui1.getSalerName().compareTo(ui2.getSalerName());
				}
				return ui1.getDeptName().compareTo(ui2.getDeptName());
			});
			SaleReceivablesDetailUI totalUi = new SaleReceivablesDetailUI("合计", "-", totalReceivables, totalActualreceivables);
			totalUi.calculatePercent();
			result.add(totalUi);
		} catch (Exception e) {
			logger.info("查询各事业部/销售人员收款明细表异常", e);
		}
		return BaseResponse.success(result);
	}

	private String getBusinessDept(Map<String, Department> tempMap, String deptid) {
		if (!tempMap.containsKey(deptid)) {
			return deptid;
		}
		if ("1".equals(tempMap.get(deptid).getParentid())) {
			return deptid;
		}
		return getBusinessDept(tempMap, tempMap.get(deptid).getParentid());
	}

	/**
	 * 跳转通信集团近年经营报表
	 */
	@RequestMapping("/toYearForm")
	public String toStatisticsSheet() {
		return "/views/manageConsole/groupYearForm";
	}

	/**
	 * 跳转到销售业绩明细
	 */
	@RequestMapping("/toSaleManaAchievementReport")
	public String toSaleManaAchievementReport() {
		return "/views/manageConsole/saleManaAchievementReport";
	}

	/**
	 * 查询员工绩效分析表
	 */
	@ResponseBody
	@RequestMapping("/readSaleanalysis")
	public PageResult<SaleAnalysisUI> readSaleanalysis() {
		String year = request.getParameter("year");
		SearchFilter filter = new SearchFilter();
		List<SaleAnalysisUI> saleAnalysisUIList = new ArrayList<>();
		try {
			// 用户数据权限下能看到的部门
			List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
			if (ListUtils.isEmpty(deptIdList)) {
				return new PageResult<>();
			}
			if (StringUtil.isNotBlank(year)) {
				filter.getRules().add(new SearchRule("statsYearMonth", Constants.ROP_EQ, DateUtil.convert(year, DateUtil.format1)));
			}
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
			List<SaleAnalysis> saleAnalysisList = saleAnalysisService.queryAllBySearchFilter(filter);
			// 查询部门信息
			Map<String, String> deptNameMap = new HashMap<String, String>();
			Map<String, String> saleUserNameMap = new HashMap<String, String>();
			Map<String, String> customerNameMap = new HashMap<String, String>();
			List<Department> deptList = departmentService.queryAllBySearchFilter(new SearchFilter());
			for (Department dept : deptList) {
				deptNameMap.put(dept.getDeptid(), dept.getDeptname());
			}
			List<Customer> customerList = customerService.queryAllBySearchFilter(new SearchFilter());
			for (Customer customer : customerList) {
				customerNameMap.put(customer.getCustomerId(), customer.getCompanyName());
			}
			List<User> userList = userService.queryAllBySearchFilter(new SearchFilter());
			for (User user : userList) {
				saleUserNameMap.put(user.getOssUserId(), user.getRealName());
			}
			if (saleAnalysisList != null && saleAnalysisList.size() > 0) {
				for (SaleAnalysis saleAnalysis : saleAnalysisList) {
					SaleAnalysisUI ui = new SaleAnalysisUI();
					String deptId = saleAnalysis.getDeptId();
					String customerId = saleAnalysis.getCustomerId();
					String saleUserId = saleAnalysis.getSaleUserId();
					if (deptNameMap.containsKey(deptId)) {
						ui.setDeptName(deptNameMap.get(deptId));
					}
					if (customerNameMap.containsKey(customerId)) {
						ui.setCustomerName(customerNameMap.get(customerId));

					}
					if (saleUserNameMap.containsKey(saleUserId)) {
						ui.setSaleUserName(saleUserNameMap.get(saleUserId));
					}
					ui.setReceivables(saleAnalysis.getReceivables());
					ui.setSuccessCount(saleAnalysis.getSuccessCount());
					ui.setSaleUnitPrice(saleAnalysis.getSaleUnitPrice());
					ui.setExpenses(saleAnalysis.getExpenses());
					ui.setCostUnitPrice(saleAnalysis.getCostUnitPrice());
					ui.setCostSum(saleAnalysis.getCostSum());
					ui.setGrossProfit(saleAnalysis.getGrossProfit());
					ui.setRoyalty(saleAnalysis.getRoyalty());
					ui.setSettleType(SettleType.getDescs()[saleAnalysis.getSettleType()]);
					ui.setAccounTperiod(saleAnalysis.getAccountPeriod());
					ui.setArrears(saleAnalysis.getArrears());
					ui.setCurrentbalance(saleAnalysis.getCurrentbalance());
					ui.setSellingExpenses(saleAnalysis.getSellingExpenses());
					ui.setCorrectGrossprofit(saleAnalysis.getCorrectGrossprofit());
					saleAnalysisUIList.add(ui);
				}
			}
			return new PageResult<SaleAnalysisUI>(saleAnalysisUIList, saleAnalysisUIList.size());
		} catch (Exception e) {
			logger.error("", e);
		}
		return new PageResult<>();
	}
}
