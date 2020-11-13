package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;
import com.dahantc.erp.vo.cashflow.service.ICashFlowService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.royalty.dao.IRoyaltyDao;
import com.dahantc.erp.vo.royalty.entity.Royalty;
import com.dahantc.erp.vo.saleAnalysisStatistics.entity.SaleAnalysis;
import com.dahantc.erp.vo.saleAnalysisStatistics.service.ISaleAnalysisService;

/**
 * 个人业绩分析统计表生成任务
 */
@Component
public class BackTaskMonthSaleAnalysisStatisticsTask implements SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(BackTaskMonthSaleAnalysisStatisticsTask.class);

	private static final String CUSTSTATISHQL = "SELECT customerId,deptId,productType,saleUserId,businessType,SUM(successCount),SUM(receivables),SUM(cost),SUM(grossProfit) "
			+ "FROM CustomerStatistics WHERE statsYearMonth = :statsYearMonth GROUP BY customerId,deptId,productType,saleUserId,businessType ORDER BY deptId,saleUserId,customerId DESC";

	private static final String DELETEHQL = "DELETE FROM erp_saleanalysis_statistics WHERE statsdate >= ? AND statsdate < ?";

	private static String CRON = "0 0 6 * * ?";

	@Autowired
	ICustomerProductService customerProductService;

	@Autowired
	ISaleAnalysisService saleAnalysisService;

	@Autowired
	IRoyaltyDao royaltyDao;

	@Autowired
	ICashFlowService cashFlowService;
	@Autowired
	IBaseDao baseDao;

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			@SuppressWarnings("deprecation")
			public void run() {
				logger.info("BackTask-MonthSaleAnalysisStatistics-Task is running...");
				Date currentDate = DateUtil.getDayStart(new Date());
				if (currentDate.getDate() == 3) {
					// 每月3号重新统计上个月1号到今天
					currentDate = DateUtil.getLastMonthFirst(currentDate);
				} else {
					// 重新统计这个月1号到金额
					currentDate = DateUtil.getThisMonthFirst(currentDate);
				}
				try {
					deleteCurrentMonthData(currentDate);
					monthSaleAnalysisStatistics(currentDate);
				} catch (Exception e) {
					logger.error("BackTask-MonthSaleAnalysisStatistics-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Date nextExecutionTime = null;
				logger.info("BackTask-MonthSaleAnalysisStatistics-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	public void deleteCurrentMonthData(Date currentDate) throws BaseException {
		logger.info("删除" + DateUtil.convert(currentDate, DateUtil.format4) + "月员工业绩分析表数据");
		// 如果是3号删除上个月的
		baseDao.executeSqlUpdte(DELETEHQL,
				new Object[] { new Timestamp(currentDate.getTime()), new Timestamp(DateUtil.getNextMonthFirst(currentDate).getTime()) },
				new Type[] { StandardBasicTypes.TIMESTAMP, StandardBasicTypes.TIMESTAMP });
	}

	/**
	 * 获取所有产品的账期
	 *
	 * @return {customerId+productType -> 账期+个月}
	 */
	public Map<String, String> getAllProductAccountPeriod() {
		try {
			List<CustomerProduct> list = customerProductService.queryAllByFilter(null);
			if (!CollectionUtils.isEmpty(list)) {
				return list.stream().collect(
						Collectors.toMap(product -> product.getCustomerId() + product.getProductType(), product -> product.getBillPeriod() + "个月", (v1, v2) -> {
							if (v1.length() > v2.length()) {
								return v2;
							} else if (v1.length() < v2.length()) {
								return v1;
							}
							return v1.compareTo(v2) < 0 ? v1 : v2;
						}));
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return null;
	}

	public void monthSaleAnalysisStatistics(Date currentDate) {
		// TODO 产品类型
		logger.info("统计" + DateUtil.convert(currentDate, DateUtil.format4) + "月员工业绩分析表数据");
		List<SaleAnalysis> saleAnalysiss = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		Map<String, BigDecimal> royaltyMap = new HashMap<>();
		Map<String, BigDecimal> arriveAccountwMap = new HashMap<>();
		Map<String, BigDecimal> arrearsMap = new HashMap<>();
		try {
			// 查询客户统计表，生成基础数据，录入至个人业绩表实体类。
			params.put("statsYearMonth", currentDate);
			// params.put("productType", ProductType.SMS.ordinal());
			List<Object[]> custStatisList = baseDao.findByhql(CUSTSTATISHQL, params, 0);
			if (custStatisList != null && custStatisList.size() > 0) {
				// 获取所有产品的账期
				Map<String, String> cacheAccountPeriod = getAllProductAccountPeriod();
				// customerId,deptId,productType,saleUserId,businesstype,SUM(successCount),SUM(receivables),SUM(cost),SUM(grossProfit)
				for (Object[] custStatis : custStatisList) {
					SaleAnalysis saleAnalysis = new SaleAnalysis();
					saleAnalysis.setCustomerId(custStatis[0].toString());
					saleAnalysis.setDeptId(custStatis[1].toString());
					saleAnalysis.setProductType((int) custStatis[2]);
					saleAnalysis.setSaleUserId(custStatis[3].toString());
					saleAnalysis.setBusinessType((int) custStatis[4]);
					saleAnalysis.setSuccessCount((long) custStatis[5]);
					saleAnalysis.setExpenses((BigDecimal) custStatis[6]);
					saleAnalysis.setCostSum((BigDecimal) custStatis[7]);
					saleAnalysis.setGrossProfit((BigDecimal) custStatis[8]);
					if (saleAnalysis.getSuccessCount() != 0) {
						saleAnalysis.setCostUnitPrice(saleAnalysis.getCostSum().divide(BigDecimal.valueOf(saleAnalysis.getSuccessCount()), 6, BigDecimal.ROUND_HALF_UP));
						saleAnalysis.setSaleUnitPrice(saleAnalysis.getExpenses().divide(BigDecimal.valueOf(saleAnalysis.getSuccessCount()), 6, BigDecimal.ROUND_HALF_UP));
					}
					saleAnalysis.setStatsDate(java.sql.Date.valueOf(DateUtil.convert(currentDate, DateUtil.format1)));
					saleAnalysis.setStatsYearMonth(java.sql.Date.valueOf(DateUtil.convert(currentDate, DateUtil.format4) + "-01"));
					saleAnalysis.setStatsYear(java.sql.Date.valueOf(DateUtil.convert(currentDate, DateUtil.format11) + "-01-01"));
					saleAnalysis.setAccountPeriod(cacheAccountPeriod == null
							|| StringUtils.isBlank(cacheAccountPeriod.get(saleAnalysis.getCustomerId() + saleAnalysis.getProductType())) ? "1个月"
									: cacheAccountPeriod.get(saleAnalysis.getCustomerId() + saleAnalysis.getProductType()));
					saleAnalysiss.add(saleAnalysis);
				}
			}
			// 查询客户产品表
			List<CustomerProduct> customerProductList = customerProductService.queryAllByFilter(new SearchFilter());
			// 查询权益提成表
			SearchFilter royaltyFilter = new SearchFilter();
			royaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, new Timestamp(currentDate.getTime())));
			royaltyFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, new Timestamp(DateUtil.getNextMonthFirst(currentDate).getTime())));
			List<Royalty> royaltyList = royaltyDao.queryAllBySearchFilter(royaltyFilter);
			// 通过现金流表查询到款金额，根据现金流产品类型去除掉不相关到款
			SearchFilter cashFlowFilter = new SearchFilter();
			cashFlowFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, new Timestamp(currentDate.getTime())));
			cashFlowFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, new Timestamp(DateUtil.getNextMonthFirst(currentDate).getTime())));
			cashFlowFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
			List<CashFlow> cashFlowList = cashFlowService.queryAllBySearchFilter(cashFlowFilter);
			// 根据客户统计表生成业绩实体表，循环需要的数据入值至实体表
			for (SaleAnalysis saleAnalysis : saleAnalysiss) {
				String customerId = saleAnalysis.getCustomerId();
				String saleUserId = saleAnalysis.getSaleUserId();
				int productType = saleAnalysis.getProductType();
				int businessType = saleAnalysis.getBusinessType();
				String key = customerId + "," + saleUserId + "," + productType;
				// TODO 付费类型暂时无法确定，设置结算方式
				for (CustomerProduct customerProduct : customerProductList) {
					if (customerProduct.getProductType() == productType && customerId.equals(customerProduct.getCustomerId())) {
						saleAnalysis.setSettleType(customerProduct.getSettleType());
					}
				}
				// 设置权益提成，预付结余
				if (royaltyList != null && royaltyList.size() > 0) {
					for (Royalty royalty : royaltyList) {
						if (customerId.equals(royalty.getEntityid()) && saleUserId.equals(royalty.getOssuserid())
								&& productType == royalty.getProductType()) {
							if (royaltyMap.containsKey(key)) {
								royaltyMap.put(key, royaltyMap.get(key).add(royalty.getRoyalty()));
							} else {
								royaltyMap.put(key, royalty.getRoyalty());
							}
							// TODO 查询上月月底的余额，或本月昨天的余额
						}
					}
					if (!royaltyMap.isEmpty()) {
						for (String royalty : royaltyMap.keySet()) {
							if (royalty.equals(key)) {
								saleAnalysis.setRoyalty(royaltyMap.get(key));
							}
						}
					}
				}
				// TODO 到款从账单查
				if (cashFlowList != null && cashFlowList.size() > 0) {
					for (CashFlow cashFlow : cashFlowList) {
						if (customerId.equals(cashFlow.getEntityId()) && productType == cashFlow.getProductType()
								&& businessType == cashFlow.getBusinessType()) {
							// 到款
							if (arriveAccountwMap.containsKey(key)) {
								arriveAccountwMap.put(key,arriveAccountwMap.get(key).add(cashFlow.getActualReceivables()));
							} else {
								arriveAccountwMap.put(key, cashFlow.getActualReceivables());
							}
							// 欠款
							if (arrearsMap.containsKey(key)) {
								arrearsMap.put(key, arrearsMap.get(key).add(cashFlow.getReceivables().subtract(cashFlow.getActualReceivables())));
							} else {
								arrearsMap.put(key, cashFlow.getReceivables().subtract(cashFlow.getActualReceivables()));
							}
						}
					}
					if (!arriveAccountwMap.isEmpty()) {
						for (String arriveAccountw : arriveAccountwMap.keySet()) {
							if (arriveAccountw.equals(key)) {
								saleAnalysis.setReceivables(arriveAccountwMap.get(key));
							}
						}
					}
					if (!arrearsMap.isEmpty()) {
						for (String arrears : arrearsMap.keySet()) {
							if (arrears.equals(key)) {
								saleAnalysis.setArrears(arrearsMap.get(key));
							}
						}
					}
				}
			}
			saleAnalysisService.saveByBatch(saleAnalysiss);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}