package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.ParamType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.billpenaltyinterest.service.IBillPenaltyInterestService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.operateCost.entity.OperateCost;
import com.dahantc.erp.vo.operateCost.service.IOperateCostService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.productBills.entity.FsExpenseincomeInfo;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.royalty.entity.RealRoyalty;
import com.dahantc.erp.vo.royalty.service.IRealRoyaltyService;

/**
 * 计算实际提成的定时任务 每天查询昨日销账的账单，处理之后写入实际提成表
 */
@Component
public class BackTaskRealRoyaltyTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskRealRoyaltyTask.class);

	private static String CRON = "0 0 1 * * ?";

	@Autowired
	private IRealRoyaltyService realRoyaltyService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IOperateCostService operateCostService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IBillPenaltyInterestService billPenaltyInterestService;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				try {
					// 执行任务
					logger.info("BackTask-Royalty-Task is running...");
					createRoyaltyTask();
				} catch (Exception e) {
					logger.error("BackTask-Royalty-Task is error...", e);
				}
			}

		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-Royalty-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void createRoyaltyTask() {
		try {
			// 提成比例map {产品类型 -> 提成比例}
			Map<String, BigDecimal> royaltyRatioMap = getRoyaltyRatio(ParamType.COMMISSION_RATION.ordinal());
			Map<String, BigDecimal> oldCustomerRoyaltyRatioMap = getRoyaltyRatio(ParamType.OLD_CUSTOMER_COMMISSION_RATION.ordinal());
			Date startDate = DateUtil.getDateFromToday(-1);
			Date endDate = DateUtil.getDateEndDateTime(startDate);
			// 查昨日销账的账单，昨天00:00:00 ~ 昨天23:59:59
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("billStatus", Constants.ROP_EQ, BillStatus.WRITED_OFF.ordinal()));
			filter.getRules().add(new SearchRule("writeOffTime", Constants.ROP_GE, startDate));
			filter.getRules().add(new SearchRule("writeOffTime", Constants.ROP_LE, endDate));
			List<ProductBills> billList = productBillsService.queryAllBySearchFilter(filter);
			if (billList.isEmpty()) {
				logger.info("昨日无销账的账单，处理结束");
				return;
			}
			logger.info("查询到昨日销账账单" + billList.size() + "条，开始计算实际提成");
			// 删除昨天的实际提成，防止重复写提成记录
			delOldData(startDate, endDate);

			SearchFilter cusProductFilter = new SearchFilter();
			List<String> productIdList = billList.stream().map(ProductBills::getProductId).collect(Collectors.toList());
			cusProductFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, productIdList));
			List<CustomerProduct> customerProductList = customerProductService.queryAllByFilter(cusProductFilter);
			Map<String, CustomerProduct> productMap = customerProductList.stream()
					.collect(Collectors.toMap(CustomerProduct::getProductId, v -> v, (v1, v2) -> v1));

			List<RealRoyalty> resultList = new ArrayList<>();
			for (ProductBills productBills : billList) {
				CustomerProduct cusProduct = productMap.get(productBills.getProductId());
				if (null == cusProduct) {
					logger.info("客户产品不存在，productId：" + productBills.getProductId());
					continue;
				}
				String productType = cusProduct.getProductType() + "";

				RealRoyalty realRoyalty = new RealRoyalty();
				realRoyalty.setDeptId(productBills.getDeptId());
				realRoyalty.setProductId(productBills.getProductId());
				realRoyalty.setProductType(cusProduct.getProductType());
				realRoyalty.setOssUserId(cusProduct.getOssUserId());
				realRoyalty.setBillId(productBills.getId());
				if (StringUtil.isNotBlank(productBills.getBillNumber())) {
					realRoyalty.setBillNumber(productBills.getBillNumber());
				} else {
					realRoyalty.setBillNumber(productBills.getId());
				}
				realRoyalty.setEntityId(cusProduct.getCustomerId());
				realRoyalty.setWtime(getCalculateRoyaltyTime(productBills));
				realRoyalty.setSendCount(productBills.getSupplierCount());
				// 账单金额
				realRoyalty.setBillMoney(productBills.getReceivables());
				// 毛利润
				realRoyalty.setGrossProfit(productBills.getGrossProfit());
				// 运营成本
				BigDecimal operateCostTotal = new BigDecimal(0);
				SearchFilter opFilter = new SearchFilter();
				opFilter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productBills.getProductId()));
				opFilter.getRules().add(new SearchRule("billMonth", Constants.ROP_EQ, productBills.getWtime()));
				List<OperateCost> operateCostList = operateCostService.queryAllBySearchFilter(opFilter);
				if (!CollectionUtils.isEmpty(operateCostList)) {
					// 累计运营成本
					for (OperateCost op : operateCostList) {
						operateCostTotal = operateCostTotal.add(op.getCustomerFixedCost()).add(op.getUnifiedSingleCostTotal())
								.add(op.getProductSingleCostTotal()).add(op.getBillMoneyCost()).add(op.getBillGrossProfitCost());
					}
				}
				realRoyalty.setOperateCost(operateCostTotal);
				// 利润 = 账单毛利润 - 运营成本 （账单毛利润 = 账单金额 - 综合成本）
				BigDecimal profit = productBills.getGrossProfit().subtract(operateCostTotal);
				realRoyalty.setProfit(profit);
				// 利润提成 = 利润 x 产品提成比例
				if (royaltyRatioMap.containsKey(productType)) {
					BigDecimal royalty = BigDecimal.ZERO;

					// 判断新老客户
					boolean isOldCust = true;
					if (productMap.get(productBills.getProductId()).getFirstGenerateBillTime() != null) {
						Date firstBillTime = productMap.get(productBills.getProductId()).getFirstGenerateBillTime();
						Calendar cal = Calendar.getInstance();
						cal.setTime(firstBillTime);
						cal.set(Calendar.MONTH, 0);
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);
						firstBillTime = cal.getTime();

						Date lastDate = DateUtil.getNextYearFirst(DateUtil.getNextYearFirst(firstBillTime));
						if (productBills.getWtime().getTime() < lastDate.getTime()) {
							isOldCust = false;
						}
					}

					if (isOldCust) {
						royalty = profit.multiply(oldCustomerRoyaltyRatioMap.get(productType));
					} else {
						royalty = profit.multiply(royaltyRatioMap.get(productType));
					}

					realRoyalty.setRoyalty(royalty);
				} else {
					logger.info("提成比例：" + productType + "不存在");
				}

				boolean flag = true;

				Date today = DateUtil.getCurrentStartDateTime();
				Calendar cal = Calendar.getInstance();
				cal.set(today.getYear(), today.getMonth(), 4, 0, 0, 0);
				cal.set(Calendar.MILLISECOND, 0);

				if (productBills.getWriteOffTime().getTime() < cal.getTime().getTime()) { // 这个月前三天销账的
					List<FsExpenseincomeInfo> fsExpenseIncomeInfos = productBills.getFsExpenseIncomeInfos();
					if (!CollectionUtils.isEmpty(fsExpenseIncomeInfos)) {
						SearchFilter searchFilter = new SearchFilter();
						searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN,
								fsExpenseIncomeInfos.stream().map(FsExpenseincomeInfo::getFsExpenseIncomeId).collect(Collectors.toList())));
						searchFilter.getRules().add(new SearchRule("operateTime", Constants.ROP_GE, DateUtil.getThisMonthFirst(today)));
						List<FsExpenseIncome> result = fsExpenseIncomeService.queryAllBySearchFilter(searchFilter); // 表示销账的到款有不是上个月的
						if (!CollectionUtils.isEmpty(result)) {
							flag = false;
						}
					}
				}

				if (flag) {
					String sql = "DELETE FROM erp_bill_penalty_interest WHERE wtime >= ? AND billid = ?";
					baseDao.executeSqlUpdte(sql, new Object[] { DateUtil.getThisMonthFirst(today), productBills.getId() },
							new Type[] { StandardBasicTypes.TIMESTAMP, StandardBasicTypes.STRING });
				}

				// 账单罚息
				realRoyalty.setPenaltyInterest(billPenaltyInterestService.queryPenaltyInterestByBillId(productBills.getId()).getPenaltyInterest());

				resultList.add(realRoyalty);
			}
			boolean result = realRoyaltyService.saveByBatch(resultList);
			logger.info("保存实际提成" + resultList.size() + "条" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("计算实际提成异常", e);
		}
	}

	// 月初1-3号的销账，如经查询，对应账单最后一笔到款为上个月的，提成计算到上个月。
	private Timestamp getCalculateRoyaltyTime(ProductBills productBills) {
		Date calculateRoyaltyDate = null;
		try {
			Date currentMonthFirstDay = DateUtil.getCurrentMonthFirstDay();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentMonthFirstDay);
			cal.add(Calendar.DATE, 3);
			// 1~3号销账，且本该在上月销账的账单
			if (productBills.getWriteOffTime().getTime() < cal.getTime().getTime() && productBills.getFinalReceiveTime().getTime() < currentMonthFirstDay.getTime()) {
				List<FsExpenseincomeInfo> fsExpenseIncomeInfos = productBills.getFsExpenseIncomeInfos();
				if (!CollectionUtils.isEmpty(fsExpenseIncomeInfos)) {
					List<String> fsExpenseIncomeIdList = fsExpenseIncomeInfos.stream().map(FsExpenseincomeInfo::getFsExpenseIncomeId).collect(Collectors.toList());
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, fsExpenseIncomeIdList));
					List<FsExpenseIncome> list = fsExpenseIncomeService.queryAllBySearchFilter(searchFilter);
					boolean lastMonth = true;
					if (!CollectionUtils.isEmpty(list)) {
						for (FsExpenseIncome income : list) {
							if (income.getOperateTime().getTime() >= currentMonthFirstDay.getTime()) {
								lastMonth = false;
								break;
							}
						}
					}
					if (lastMonth) {
						calculateRoyaltyDate = DateUtil.getLastMonthFinal(currentMonthFirstDay);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		if (calculateRoyaltyDate == null) {
			calculateRoyaltyDate = productBills.getWriteOffTime();
		}
		return new Timestamp(calculateRoyaltyDate.getTime());
	}

	/**
	 * 获取产品类型的提成比例
	 *
	 * @return {产品类型 -> 提成比例}
	 */
	private Map<String, BigDecimal> getRoyaltyRatio(int paramType) {
		Map<String, BigDecimal> royaltyMap = new HashMap<>();
		try {
			SearchFilter filterParameter = new SearchFilter();
			filterParameter.getRules().add(new SearchRule("paramType", Constants.ROP_EQ, paramType));
			List<Parameter> list = parameterService.findAllByCriteria(filterParameter);
			if (list != null) {
				for (Parameter parameter : list) {
					royaltyMap.put(parameter.getParamkey(), new BigDecimal(parameter.getParamvalue()));
				}
			}
		} catch (Exception e) {
			logger.error("获取提成信息失败", e);
		}
		return royaltyMap;
	}

	/**
	 * 删除实际提成表的历史数据，以实现覆盖统计
	 *
	 * @param startDate
	 *            开始时间 yyyy-MM-dd 00:00:00
	 * @param endDate
	 *            结束时间 yyyy-MM-dd 23:59:59
	 */
	private void delOldData(Date startDate, Date endDate) {
		String sql = "delete from erp_real_royalty where wtime >= '" + DateUtil.convert(startDate, DateUtil.format2) + "' and wtime <= '"
				+ DateUtil.convert(endDate, DateUtil.format2) + "'";
		try {
			baseDao.executeUpdateSQL(sql);
			logger.info("删除实际提成表历史数据结束：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1));
		} catch (Exception e) {
			logger.error("删除实际提成表历史数据异常：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1), e);
		}
	}
}
