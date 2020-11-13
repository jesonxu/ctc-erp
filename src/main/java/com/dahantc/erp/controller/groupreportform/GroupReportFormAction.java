package com.dahantc.erp.controller.groupreportform;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.service.IDepartmentService;

@Controller
@RequestMapping("/group")
public class GroupReportFormAction extends BaseAction {

	public static final Logger logger = LoggerFactory.getLogger(GroupReportFormAction.class);

	private static final String INCOME_HQL = "SELECT businessType, statsYear, SUM(receivables) FROM CustomerStatistics where deptId in :deptIds GROUP BY businessType, statsYear HAVING statsYear >= :statsYear";

	private static final String COST_HQL = "SELECT businessType, statsYear, SUM(cost) FROM CustomerStatistics where deptId in :deptIds GROUP BY businessType, statsYear HAVING statsYear >= :statsYear";

	private static final String CONSUME_HQL = "SELECT businessType, incomeExpenditureType, statsYear, SUM(cost) FROM Dailyexpenses where deptId in :deptIds GROUP BY businessType, incomeExpenditureType, statsYear HAVING statsYear >= :statsYear";

	private final List<String> PROJECT_NAME = Arrays.asList("云通讯收入", "4G收入", "物联网收入", "技术服务费收入", "补贴收入", "其他收入", "收入小计", "云通讯成本-采购", "云通讯成本-销售", "4G成本",
			"物联网成本", "成本小计", "毛利", "毛利率", "人力工资", "日常费用", "差旅费用", "行政支出", "房租物业水电费", "固定资产费用", "云服务器费用-云通讯", "云服务器费用-4G", "云服务器费用-物联网", "财务费用-利息支出", "财务费用-手续费",
			"费用小计", "附加税", "税前利润合计");

	private final List<String> COST_PROJECT_NAME = Arrays.asList("云通讯成本-采购", "云通讯成本-销售", "4G成本", "物联网成本");

	private final List<String> INCOME_PROJECT_NAME = Arrays.asList("云通讯收入", "4G收入", "物联网收入", "技术服务费收入", "补贴收入", "其他收入");

	private final List<String> CONSUME_PROJECT_NAME = Arrays.asList("人力工资", "日常费用", "差旅费用", "行政支出", "房租物业水电费", "固定资产费用", "云服务器费用-云通讯", "云服务器费用-4G",
			"云服务器费用-物联网", "财务费用-利息支出", "财务费用-手续费");

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IDepartmentService departmentService;

	/**
	 * 通信集团近年经营报表
	 */
	@ResponseBody
	@RequestMapping("/yearReportForm")
	public PageResult<YearColumnUI> getYearReportForm() {
		Map<String, Object> params = new HashMap<>();
		int yearCount = 4;
		// TreeMap 按照 List 排序
		Map<String, YearColumnUI> resultUiMap = new TreeMap<>((o1, o2) -> {
			if (PROJECT_NAME.contains(o1) && PROJECT_NAME.contains(o2)) {
				return PROJECT_NAME.indexOf(o1) - PROJECT_NAME.indexOf(o2);
			}
			return o1.compareTo(o2);
		});
		for (String projectName : PROJECT_NAME) {
			resultUiMap.put(projectName, new YearColumnUI(yearCount, projectName));
		}
		try {
			// 用户数据权限下能看到的部门
			List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
			if (ListUtils.isEmpty(deptIdList)) {
				return new PageResult<>();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			Date statsYear = DateUtil.convert((cal.get(Calendar.YEAR) - yearCount + 1) + "", "yyyy");
			params.put("statsYear", statsYear);
			params.put("deptIds", deptIdList);
			// 查询客户统计表 erp_customer_statistics，求收入
			List<Object[]> incomeList = baseDao.findByhql(INCOME_HQL, params, 0);
			List<Object[]> costList = baseDao.findByhql(COST_HQL, params, 0);
			List<Object[]> consumeList = baseDao.findByhql(CONSUME_HQL, params, 0);
			dealIncomeData(incomeList, resultUiMap);
			dealCostData(costList, resultUiMap);
			dealConsumeData(consumeList, resultUiMap);

			String incomeProName = "收入小计";
			String costProName = "成本小计";
			String consumeProName = "费用小计";
			String profitProName = "毛利";
			String profitMarginProName = "毛利率";
			String profitBeforeTaxProName = "税前利润合计";
			for (String projectName : resultUiMap.keySet()) {
				if (INCOME_PROJECT_NAME.contains(projectName)) { // 收入小计
					for (int i = 0; i < yearCount; i++) {
						resultUiMap.get(incomeProName).getYearData()[i] = resultUiMap.get(incomeProName).getYearData()[i]
								.add(resultUiMap.get(projectName).getYearData()[i]);
					}
				} else if (COST_PROJECT_NAME.contains(projectName)) { // 成本小计
					for (int i = 0; i < yearCount; i++) {
						if (resultUiMap.get(costProName).getYearData()[i] == null) {
							resultUiMap.get(costProName).getYearData()[i] = new BigDecimal(0);
						}
						resultUiMap.get(costProName).getYearData()[i] = resultUiMap.get(costProName).getYearData()[i]
								.add(resultUiMap.get(projectName).getYearData()[i]);
					}
				} else if (CONSUME_PROJECT_NAME.contains(projectName)) { // 费用小计
					for (int i = 0; i < yearCount; i++) {
						resultUiMap.get(consumeProName).getYearData()[i] = resultUiMap.get(consumeProName).getYearData()[i]
								.add(resultUiMap.get(projectName).getYearData()[i]);
					}
				}
			}

			for (int i = 0; i < yearCount; i++) {
				// 毛利 = 收入 - 成本
				resultUiMap.get(profitProName).getYearData()[i] = resultUiMap.get(incomeProName).getYearData()[i]
						.subtract(resultUiMap.get(costProName).getYearData()[i]);
				// 毛利率 = 毛利 / 收入 * 100
				if (resultUiMap.get(incomeProName).getYearData()[i].compareTo(new BigDecimal(0)) > 0) {
					resultUiMap.get(profitMarginProName).getYearData()[i] = resultUiMap.get(profitProName).getYearData()[i]
							.divide(resultUiMap.get(incomeProName).getYearData()[i], 4, BigDecimal.ROUND_CEILING).multiply(new BigDecimal(100));
				}
				// 税前利润合计 = 收入小计 - 成本小计 - 费用小计
				resultUiMap.get(profitBeforeTaxProName).getYearData()[i] = resultUiMap.get(incomeProName).getYearData()[i]
						.subtract(resultUiMap.get(costProName).getYearData()[i]).subtract(resultUiMap.get(consumeProName).getYearData()[i]);
			}

			// 转List
			List<YearColumnUI> list = new ArrayList<>();
			for (Map.Entry<String, YearColumnUI> entry : resultUiMap.entrySet()) {
				list.add(entry.getValue());
			}

			return new PageResult<YearColumnUI>(list, list.size());
		} catch (Exception e) {
			logger.error("", e);
		}
		return new PageResult<>();
	}

	private void dealIncomeData(List<Object[]> incomeList, Map<String, YearColumnUI> resultUiMap) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		int currentYear = calender.get(Calendar.YEAR);
		for (Object[] datas : incomeList) {
			int businessType = ((Number) datas[0]).intValue();
			calender.setTime((Date) datas[1]);
			int year = calender.get(Calendar.YEAR);
			String projectName = null;
			if (businessType == BusinessType.YTX.ordinal()) {
				projectName = "云通讯收入";
			} else if (businessType == BusinessType.G4.ordinal()) {
				projectName = "4G收入";
			} else if (businessType == BusinessType.IOT.ordinal()) {
				projectName = "物联网收入";
			}
			if (projectName != null) {
				resultUiMap.get(projectName).getYearData()[currentYear - year] = (BigDecimal) datas[2];
			}
		}
	}

	private void dealCostData(List<Object[]> costList, Map<String, YearColumnUI> resultUiMap) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		int currentYear = calender.get(Calendar.YEAR);
		for (Object[] datas : costList) {
			int businessType = ((Number) datas[0]).intValue();
			calender.setTime((Date) datas[1]);
			int year = calender.get(Calendar.YEAR);
			String projectName = null;
			if (businessType == BusinessType.YTX.ordinal()) {
				projectName = "云通讯成本-采购";
			} else if (businessType == BusinessType.G4.ordinal()) {
				projectName = "4G成本";
			} else if (businessType == BusinessType.IOT.ordinal()) {
				projectName = "物联网成本";
			}
			if (projectName != null) {
				resultUiMap.get(projectName).getYearData()[currentYear - year] = (BigDecimal) datas[2];
			}
		}
	}

	private void dealConsumeData(List<Object[]> consumeList, Map<String, YearColumnUI> resultUiMap) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		int currentYear = calender.get(Calendar.YEAR);
		for (Object[] datas : consumeList) {
			int businessType = 0;
			if (datas[0] != null) {
				businessType = ((Number) datas[0]).intValue();
			}
			int incomeExpenditureType = 0;
			if (datas[1] != null) {
				incomeExpenditureType = ((Number) datas[1]).intValue();
			}
			calender.setTime((Date) datas[2]);
			int year = calender.get(Calendar.YEAR);
			String projectName = null;
			if (incomeExpenditureType == IncomeExpenditureType.SERVERFEES.ordinal()) {
				if (businessType == BusinessType.YTX.ordinal()) {
					projectName = "云服务器费用-云通讯";
				} else if (businessType == BusinessType.G4.ordinal()) {
					projectName = "云服务器费用-4G";
				} else if (businessType == BusinessType.IOT.ordinal()) {
					projectName = "云服务器费用-物联网";
				}
			} else if (incomeExpenditureType == IncomeExpenditureType.MANPWAGES.ordinal()) {
				projectName = "人力工资";
			} else if (incomeExpenditureType == IncomeExpenditureType.DAILYFEES.ordinal()) {
				projectName = "日常费用";
			} else if (incomeExpenditureType == IncomeExpenditureType.TRAVELFEES.ordinal()) {
				projectName = "差旅费用";
			} else if (incomeExpenditureType == IncomeExpenditureType.ADMINISTRATIVEFEES.ordinal()) {
				projectName = "行政支出";
			} else if (incomeExpenditureType == IncomeExpenditureType.PROPERTYFEE.ordinal()) {
				projectName = "房租物业水电费";
			} else if (incomeExpenditureType == IncomeExpenditureType.FIXEDASSETSFEES.ordinal()) {
				projectName = "固定资产费";
			} else if (incomeExpenditureType == IncomeExpenditureType.INTERESTFEES.ordinal()) {
				projectName = "财务费用-利息支出";
			} else if (incomeExpenditureType == IncomeExpenditureType.SERVICECHARGE.ordinal()) {
				projectName = "财务费用-手续费";
			} else if (incomeExpenditureType == IncomeExpenditureType.ADDITIONALCOST.ordinal()) {
				projectName = "附加税";
			}
			if (projectName != null) {
				resultUiMap.get(projectName).getYearData()[currentYear - year] = (BigDecimal) datas[3];
			}
		}
	}

}
