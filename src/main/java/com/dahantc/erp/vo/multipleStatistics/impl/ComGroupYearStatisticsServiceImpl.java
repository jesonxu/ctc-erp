package com.dahantc.erp.vo.multipleStatistics.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.multipleStatistics.IComGroupYearStatisticsService;

@Service("comGroupYearStatisticsServiceImpl")
public class ComGroupYearStatisticsServiceImpl implements IComGroupYearStatisticsService {

	private static Logger logger = LogManager.getLogger(ComGroupYearStatisticsServiceImpl.class);

	private final List<String> PROJECT_NAME = Arrays.asList("云通讯收入", "4G收入", "物联网收入", "技术服务费收入", "补贴收入", "其他收入", "收入小计", "云通讯成本-采购", "云通讯成本-销售", "4G成本",
			"物联网成本", "成本小计", "毛利", "毛利率", "人力工资", "日常费用", "差旅费用", "行政支出", "房租物业水电费", "固定资产费用", "云服务器费用-云通讯", "云服务器费用-4G", "云服务器费用-物联网", "财务费用-利息支出", "财务费用-手续费",
			"费用小计", "附加税", "税前利润合计");

	private final List<String> COST_PROJECT_NAME = Arrays.asList("云通讯成本-采购", "云通讯成本-销售", "4G成本", "物联网成本");

	private final List<String> INCOME_PROJECT_NAME = Arrays.asList("云通讯收入", "4G收入", "物联网收入", "技术服务费收入", "补贴收入", "其他收入");

	private final List<String> CONSUME_PROJECT_NAME = Arrays.asList("人力工资", "日常费用", "差旅费用", "行政支出", "房租物业水电费", "固定资产费用", "云服务器费用-云通讯", "云服务器费用-4G",
			"云服务器费用-物联网", "财务费用-利息支出", "财务费用-手续费");

	private static final String INCOME_HQL = "SELECT businessType, statsYearMonth, SUM(receivables), statsYear FROM CustomerStatistics where deptId in :deptIds GROUP BY businessType, statsYearMonth HAVING statsYear = :statsYear";

	private static final String COST_HQL = "SELECT businessType, statsYearMonth, SUM(cost), statsYear FROM CustomerStatistics where deptId in :deptIds GROUP BY  businessType, statsYear, statsYearMonth HAVING statsYear = :statsYear";

	private static final String CONSUME_HQL = "SELECT businessType, incomeExpenditureType, statsYearMonth, SUM(cost), statsYear FROM Dailyexpenses where deptId in :deptIds GROUP BY businessType, incomeExpenditureType, statsYearMonth HAVING statsYear = :statsYear";

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IDepartmentService departmentService;

	@Override
	public List<RowData> queryComYearStatistics(Date year, OnlineUser user) {
		logger.info("查询[通信集团近年经营报表-月份详情]开始");
		Map<String, RowData> comYearStatistics = new TreeMap<>((o1, o2) -> {
			if (PROJECT_NAME.contains(o1) && PROJECT_NAME.contains(o2)) {
				return PROJECT_NAME.indexOf(o1) - PROJECT_NAME.indexOf(o2);
			}
			return o1.compareTo(o2);
		});
		for (String projectName : PROJECT_NAME) {
			comYearStatistics.put(projectName, new RowData(projectName));
		}
		try {
			// 用户数据权限下能看到的部门
			List<String> deptIdList = departmentService.getDeptIdsByPermission(user);
			if (ListUtils.isEmpty(deptIdList)) {
				return null;
			}
			Map<String, Object> params = new HashMap<>();
			params.put("statsYear", year);
			params.put("deptIds", deptIdList);
			List<Object> incomeResult = baseDao.findByhql(INCOME_HQL, params, 0);
			List<Object> costResult = baseDao.findByhql(COST_HQL, params, 0);
			List<Object> consumeResult = baseDao.findByhql(CONSUME_HQL, params, 0);
			dealIncomeData(incomeResult, comYearStatistics);
			dealCostData(costResult, comYearStatistics);
			dealConsumeData(consumeResult, comYearStatistics);
			String incomeProName = "收入小计";
			String costProName = "成本小计";
			String consumeProName = "费用小计";
			String profitProName = "毛利";
			String profitMarginProName = "毛利率";
			String profitBeforeTaxProName = "税前利润合计";
			for (String projectName : comYearStatistics.keySet()) {
				if (INCOME_PROJECT_NAME.contains(projectName)) {
					for (int i = 0; i < 12; i++) {
						if (comYearStatistics.get(projectName).getMonthData()[i] != null) {
							if (comYearStatistics.get(incomeProName).getMonthData()[i] == null) {
								comYearStatistics.get(incomeProName).getMonthData()[i] = new BigDecimal(0);
							}
							comYearStatistics.get(incomeProName).getMonthData()[i] = comYearStatistics.get(incomeProName).getMonthData()[i]
									.add(comYearStatistics.get(projectName).getMonthData()[i]);
						}
					}
				} else if (COST_PROJECT_NAME.contains(projectName)) {
					for (int i = 0; i < 12; i++) {
						if (comYearStatistics.get(projectName).getMonthData()[i] != null) {
							if (comYearStatistics.get(costProName).getMonthData()[i] == null) {
								comYearStatistics.get(costProName).getMonthData()[i] = new BigDecimal(0);
							}
							comYearStatistics.get(costProName).getMonthData()[i] = comYearStatistics.get(costProName).getMonthData()[i]
									.add(comYearStatistics.get(projectName).getMonthData()[i]);
						}
					}
				} else if (CONSUME_PROJECT_NAME.contains(projectName)) {
					for (int i = 0; i < 12; i++) {
						if (comYearStatistics.get(projectName).getMonthData()[i] != null) {
							if (comYearStatistics.get(consumeProName).getMonthData()[i] == null) {
								comYearStatistics.get(consumeProName).getMonthData()[i] = new BigDecimal(0);
							}
							comYearStatistics.get(consumeProName).getMonthData()[i] = comYearStatistics.get(consumeProName).getMonthData()[i]
									.add(comYearStatistics.get(projectName).getMonthData()[i]);
						}
					}
				}
			}
			for (int i = 0; i < 12; i++) {
				if (comYearStatistics.get(incomeProName).getMonthData()[i] != null) {
					comYearStatistics.get(profitProName).getMonthData()[i] = comYearStatistics.get(incomeProName).getMonthData()[i]
							.subtract(comYearStatistics.get(costProName).getMonthData()[i] == null ? new BigDecimal(0)
									: comYearStatistics.get(costProName).getMonthData()[i]);
					if (comYearStatistics.get(incomeProName).getMonthData()[i].compareTo(new BigDecimal(0)) > 0) {
						comYearStatistics.get(profitMarginProName).getMonthData()[i] = comYearStatistics.get(profitProName).getMonthData()[i]
								.divide(comYearStatistics.get(incomeProName).getMonthData()[i], 4, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100));
					}
					comYearStatistics.get(profitBeforeTaxProName).getMonthData()[i] = comYearStatistics.get(incomeProName).getMonthData()[i]
							.subtract(comYearStatistics.get(costProName).getMonthData()[i] == null ? new BigDecimal(0)
									: comYearStatistics.get(costProName).getMonthData()[i])
							.subtract(comYearStatistics.get(consumeProName).getMonthData()[i] == null ? new BigDecimal(0)
									: comYearStatistics.get(consumeProName).getMonthData()[i]);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		List<RowData> list = new ArrayList<>();
		for (Map.Entry<String, RowData> entry : comYearStatistics.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	private void dealIncomeData(List<Object> incomeResult, Map<String, RowData> comYearStatistics) {
		for (Object obj : incomeResult) {
			Object[] datas = (Object[]) obj;
			int businessType = ((Number) datas[0]).intValue();
			Calendar calender = Calendar.getInstance();
			calender.setTime((Date) datas[1]);
			int month = calender.get(Calendar.MONTH);
			String projectName = null;
			if (businessType == BusinessType.YTX.ordinal()) {
				projectName = "云通讯收入";
			} else if (businessType == BusinessType.G4.ordinal()) {
				projectName = "4G收入";
			} else if (businessType == BusinessType.IOT.ordinal()) {
				projectName = "物联网收入";
			}
			if (projectName != null) {
				if (comYearStatistics.get(projectName) == null) {
					comYearStatistics.put(projectName, new RowData(projectName));
				}
				comYearStatistics.get(projectName).getMonthData()[month] = (BigDecimal) datas[2];
			}
		}
	}

	private void dealCostData(List<Object> costResult, Map<String, RowData> comYearStatistics) {
		for (Object obj : costResult) {
			Object[] datas = (Object[]) obj;
			int businessType = ((Number) datas[0]).intValue();
			Calendar calender = Calendar.getInstance();
			calender.setTime((Date) datas[1]);
			int month = calender.get(Calendar.MONTH);
			String projectName = null;
			if (businessType == BusinessType.YTX.ordinal()) {
				projectName = "云通讯成本-采购";
			} else if (businessType == BusinessType.G4.ordinal()) {
				projectName = "4G成本";
			} else if (businessType == BusinessType.IOT.ordinal()) {
				projectName = "物联网成本";
			}
			if (projectName != null) {
				if (comYearStatistics.get(projectName) == null) {
					comYearStatistics.put(projectName, new RowData(projectName));
				}
				comYearStatistics.get(projectName).getMonthData()[month] = (BigDecimal) datas[2];
			}
		}
	}

	private void dealConsumeData(List<Object> consumeResult, Map<String, RowData> comYearStatistics) {
		for (Object obj : consumeResult) {
			Object[] datas = (Object[]) obj;
			int businessType = 0;
			if (datas[0] != null) {
				businessType = ((Number) datas[0]).intValue();
			}
			int incomeExpenditureType = 0;
			if (datas[1] != null) {
				incomeExpenditureType = ((Number) datas[1]).intValue();
			}
			Calendar calender = Calendar.getInstance();
			calender.setTime((Date) datas[2]);
			int month = calender.get(Calendar.MONTH);
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
				if (comYearStatistics.get(projectName) == null) {
					comYearStatistics.put(projectName, new RowData(projectName));
				}
				comYearStatistics.get(projectName).getMonthData()[month] = (BigDecimal) datas[3];
			}
		}
	}

	public class RowData {

		private String projectName;

		private BigDecimal[] monthData = new BigDecimal[12];

		private BigDecimal total;

		public RowData(String projectName) {
			this.projectName = projectName;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public BigDecimal[] getMonthData() {
			return monthData;
		}

		public void setMonthData(BigDecimal[] monthData) {
			this.monthData = monthData;
		}

		public BigDecimal getTotal() {
			if (total == null) {
				BigDecimal tempTotal = null;
				for (BigDecimal data : monthData) {
					if (data != null) {
						if (tempTotal == null) {
							tempTotal = new BigDecimal(0);
						}
						tempTotal = tempTotal.add(data);
					}
				}
				return tempTotal;
			}
			return total;
		}

		public void setTotal(BigDecimal total) {
			this.total = total;
		}

	}

}
