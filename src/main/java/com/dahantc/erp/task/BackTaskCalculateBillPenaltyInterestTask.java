package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.billpenaltyinterest.entity.BillPenaltyInterest;
import com.dahantc.erp.vo.billpenaltyinterest.service.IBillPenaltyInterestService;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.productBills.entity.FsExpenseincomeInfo;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;

/**
 * 计算账单的罚息（每天覆盖型计算）
 *
 */
@Component
public class BackTaskCalculateBillPenaltyInterestTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskCalculateBillPenaltyInterestTask.class);

	private static String CRON = "0 0 7 * * ?";

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IBillPenaltyInterestService billPenaltyInterestService;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				logger.info("BackTask-CalculateBillPenaltyInterest-Task is running...");

				doCalculate();
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Date nextExecutionTime = null;
				logger.info("BackTask-CalculateBalanceInterest-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void doCalculate() {
		logger.info("计算罚息任务开始。。。");

		boolean isFirst = false;
		try {
			List<?> list = baseDao.selectSQL("SELECT COUNT(id) FROM erp_bill_penalty_interest");
			if (CollectionUtils.isEmpty(list) || ((Number) list.get(0)).longValue() == 0) {
				isFirst = true;
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		// 查询罚息率
		BigDecimal penaltyInterestRatio = parameterService.getPenaltyInterestRatio();
		if (penaltyInterestRatio == null) {
			logger.info("系统参数不存在：罚息利率不存在或者小于0，任务退出");
			return;
		}

		Date currentDate = DateUtil.getCurrentStartDateTime();

		// 每月4号查本月1~3号销账，且到款全是上月的账单，删除这些账单本月的罚息
		if (currentDate.getDate() == 4) {
			List<ProductBills> result = getThreeDayBeforeWritedOffBill(currentDate);
			if (!CollectionUtils.isEmpty(result)) {
				updatePenaltyInterestBillIds(result, currentDate);
			} else {
				logger.info("当前无需要删除3天前销账账单的罚息");
			}
		}

		// 查询当前时间已经逾期的，未销账的账单（已对账、销账中）
		List<ProductBills> overdueBills = getOverdueBills(currentDate);
		if (!CollectionUtils.isEmpty(overdueBills)) {
			doCalculatePenaltyInterest(overdueBills, currentDate, penaltyInterestRatio);
		}
		// 查上月销账的逾期账单
		List<ProductBills> overdueWritedOffBills = getOverdueWritedOffBills(currentDate, isFirst);
		if (!CollectionUtils.isEmpty(overdueWritedOffBills)) {
			doCalculateWritedOffPenaltyInterest(overdueWritedOffBills, penaltyInterestRatio);
		}

		logger.info("计算罚息任务结束");
	}

	/** 查询销账但是逾期账单的罚息计算 */
	@SuppressWarnings("deprecation")
	private void doCalculateWritedOffPenaltyInterest(List<ProductBills> overdueWritedOffBills, BigDecimal penaltyInterestRatio) {
		List<BillPenaltyInterestInfo> list = new ArrayList<>();
		SearchFilter searchFilter = new SearchFilter();

		for (ProductBills productBills : overdueWritedOffBills) {

			// 查询是否已经记录了
			try {
				searchFilter.getRules().clear();
				searchFilter.getRules().add(new SearchRule("billId", Constants.ROP_EQ, productBills.getId()));
				List<BillPenaltyInterest> result = billPenaltyInterestService.queryAllBySearchFilter(searchFilter);
				if (!CollectionUtils.isEmpty(result)) {
					continue;
				}
			} catch (ServiceException e) {
				logger.error("", e);
				continue;
			}

			Date date = DateUtil.getNextDayStart(productBills.getFinalReceiveTime());
			Date penaltyInterestEndDate = productBills.getWriteOffTime();

			for (; date.getYear() < penaltyInterestEndDate.getYear()
					|| date.getMonth() <= penaltyInterestEndDate.getMonth(); date = DateUtil.getNextMonthFirst(date)) {
				if (date.getYear() == penaltyInterestEndDate.getYear() && date.getMonth() == penaltyInterestEndDate.getMonth()) { // 当前月份
					BillPenaltyInterestInfo info = new BillPenaltyInterestInfo();
					info.setReceive(productBills.getReceivables());
					info.setCustomerId(productBills.getEntityId());
					info.setBillId(productBills.getId());
					info.setPenaltyDays(DateUtil.getDiffDays(DateUtil.getDateStartDateTime(penaltyInterestEndDate), DateUtil.getDateStartDateTime(date)) + 1);
					info.setWtime(DateUtil.getThisMonthFirst(penaltyInterestEndDate)); // 销账月份罚息
					info.setPenaltyInterestMonth(DateUtil.getThisMonthFirst(date)); // 罸到当前月
					list.add(info);
				} else { // 其他月份
					BillPenaltyInterestInfo info = new BillPenaltyInterestInfo();
					info.setReceive(productBills.getReceivables());
					info.setCustomerId(productBills.getEntityId());
					info.setBillId(productBills.getId());
					info.setPenaltyDays(
							DateUtil.getDiffDays(DateUtil.getDateStartDateTime(DateUtil.getNextMonthFirst(date)), DateUtil.getDateStartDateTime(date)));
					info.setWtime(DateUtil.getThisMonthFirst(penaltyInterestEndDate)); // 销账月份罚息
					info.setPenaltyInterestMonth(DateUtil.getThisMonthFirst(date)); // 罸到当前月
					list.add(info);
				}
			}
		}

		for (BillPenaltyInterestInfo info : list) {
			try {
				BillPenaltyInterest buildEntity = buildEntity(penaltyInterestRatio, info);
				billPenaltyInterestService.save(buildEntity);
				logger.info("保存一条罚息记录：" + JSON.toJSONString(buildEntity));
			} catch (Exception e) {
				logger.error("保存账单billId【" + info.getBillId() + "】，【" + DateUtil.convert(info.getPenaltyInterestMonth(), DateUtil.format4) + "】月份罚息异常", e);
			}
		}
	}

	/** 查询销账但是逾期的账单 */
	private List<ProductBills> getOverdueWritedOffBills(Date currentDate, boolean isFirst) {
		List<ProductBills> result = null;
		try {
			String hql = "FROM ProductBills WHERE finalReceiveTime < writeOffTime";
			Map<String, Object> params = null;
			if (!isFirst) {
				hql += " AND writeOffTime >= :startTime AND writeOffTime < :endTime";
				params = new HashMap<>();
				params.put("startTime", DateUtil.getLastMonthFirst(currentDate));
				params.put("endTime", currentDate);
			}
			result = baseDao.findByhql(hql, params, 0);
			if (!CollectionUtils.isEmpty(result)) {
				result.stream().filter(bill -> {
					try {
						SearchFilter searchFilter = new SearchFilter();
						searchFilter.getRules().add(new SearchRule("billId", Constants.ROP_EQ, bill.getId()));
						List<BillPenaltyInterest> list = billPenaltyInterestService.queryAllBySearchFilter(searchFilter);
						if (CollectionUtils.isEmpty(list)) {
							return true;
						}
					} catch (Exception e) {
						logger.error("", e);
					}
					return false;
				}).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error("查询销账但是逾期的账单异常", e);
		}
		return result;
	}

	/**
	 * 计算逾期未销账账单的罚息（已对账、销账中）
	 * 
	 * @param overdueBills
	 *            逾期账单
	 * @param currentDate
	 *            今天
	 * @param penaltyInterestRatio
	 *            罚息率（0.0001）
	 */
	@SuppressWarnings("deprecation")
	private void doCalculatePenaltyInterest(List<ProductBills> overdueBills, Date currentDate, BigDecimal penaltyInterestRatio) {
		List<BillPenaltyInterestInfo> list = new ArrayList<>();
		// finalReceiveTime，收款截止日期，一般是账单月次月的月底，如2019-12账单，收款截止是 2020-01-31
		// currentDate 2020-02-25，则计算到 2020-02-24 的罚息
		Date penaltyInterestEndDate = DateUtil.getLastDayStart(currentDate);
		for (ProductBills productBills : overdueBills) {
			// 收款截止的次月（2020-02） = 昨天的月份（2020-02）（即逾期不超过1个月）
			if (productBills.getFinalReceiveTime().getYear() == penaltyInterestEndDate.getYear()
					&& productBills.getFinalReceiveTime().getMonth() + 1 == penaltyInterestEndDate.getMonth()) {
				// 逾期范围2020-01-31 ~ 2020-02-24 --> 2月罚息
				BillPenaltyInterestInfo info = new BillPenaltyInterestInfo();
				info.setReceive(productBills.getReceivables());
				info.setCustomerId(productBills.getEntityId());
				info.setHistory(false);
				info.setBillId(productBills.getId());
				// 当月计算罚息的天数
				info.setPenaltyDays(DateUtil.getDiffDays(DateUtil.getDateStartDateTime(penaltyInterestEndDate),
						DateUtil.getDateStartDateTime(productBills.getFinalReceiveTime())));
				// 罚息放到罚息截止日期所在月
				info.setWtime(DateUtil.getThisMonthFirst(penaltyInterestEndDate));
				// 是罚息截止日期所在月的罚息
				info.setPenaltyInterestMonth(DateUtil.getThisMonthFirst(penaltyInterestEndDate));
				list.add(info);
			} else {
				// 逾期超过一个月
				// finalReceiveTime 收款截止是2020-01-31
				// currentDate 2020-06-06，则计算到 2020-06-05 的罚息
				// 计算罚息的开始日期是收款截止日期的次日 2020-02-01
				Date date = DateUtil.getNextDayStart(productBills.getFinalReceiveTime());
				// 遍历从 罚息开始 到 计算罚息结束 的每个月
				for (; date.getYear() < penaltyInterestEndDate.getYear()
						|| date.getMonth() <= penaltyInterestEndDate.getMonth(); date = DateUtil.getNextMonthFirst(date)) {
					if (date.getYear() == penaltyInterestEndDate.getYear() && date.getMonth() == penaltyInterestEndDate.getMonth()) {
						// 当前月份
						BillPenaltyInterestInfo info = new BillPenaltyInterestInfo();
						info.setReceive(productBills.getReceivables());
						info.setCustomerId(productBills.getEntityId());
						info.setHistory(false);
						info.setBillId(productBills.getId());
						// 当前月的罚息天数是 月初 到 罚息结束 +1天（06-01~06-05，+1天，共5天）
						info.setPenaltyDays(
								DateUtil.getDiffDays(DateUtil.getDateStartDateTime(penaltyInterestEndDate), DateUtil.getDateStartDateTime(date)) + 1);
						// 罚息放到6月
						info.setWtime(DateUtil.getThisMonthFirst(penaltyInterestEndDate));
						// 6月的罚息
						info.setPenaltyInterestMonth(DateUtil.getThisMonthFirst(penaltyInterestEndDate));
						list.add(info);
					} else {
						// 历史月份
						BillPenaltyInterestInfo info = new BillPenaltyInterestInfo();
						info.setReceive(productBills.getReceivables());
						info.setCustomerId(productBills.getEntityId());
						info.setHistory(true);
						info.setBillId(productBills.getId());
						// 罚息天数是一整个月
						info.setPenaltyDays(
								DateUtil.getDiffDays(DateUtil.getDateStartDateTime(DateUtil.getNextMonthFirst(date)), DateUtil.getDateStartDateTime(date)));
						// 罚息放到该月
						info.setWtime(DateUtil.getThisMonthFirst(penaltyInterestEndDate));
						// 是该月的罚息
						info.setPenaltyInterestMonth(DateUtil.getThisMonthFirst(date));
						list.add(info);
					}
				}
			}
		}

		for (BillPenaltyInterestInfo info : list) {
			if (!info.isHistory()) { // 当前月份
				if (info.getPenaltyDays() > 0) {
					try {
						BillPenaltyInterest buildEntity = buildEntity(penaltyInterestRatio, info);
						baseDao.saveOrUpdate(buildEntity);
						logger.info("更新一条罚息记录：" + JSON.toJSONString(buildEntity));
					} catch (Exception e) {
						logger.error("保存账单billId【" + info.getBillId() + "】，【" + DateUtil.convert(info.getPenaltyInterestMonth(), DateUtil.format4) + "】月份罚息异常",
								e);
					}
				}
			} else { // 查询其他月份的计息
				try {
					BillPenaltyInterest entity = buildEntity(penaltyInterestRatio, info);
					BillPenaltyInterest billPenaltyInterest = billPenaltyInterestService.read(entity.getId());
					if (billPenaltyInterest == null) {
						billPenaltyInterestService.save(entity);
						logger.info("保存罚息记录：" + JSON.toJSONString(entity));
					}
				} catch (Exception e) {
					logger.error("保存账单billId【" + info.getBillId() + "】，【" + DateUtil.convert(info.getPenaltyInterestMonth(), DateUtil.format4) + "】月份罚息异常", e);
				}
			}
		}
	}

	private BillPenaltyInterest buildEntity(BigDecimal penaltyInterestRatio, BillPenaltyInterestInfo info) throws ServiceException {
		BillPenaltyInterest billPenaltyInterest = new BillPenaltyInterest();
		billPenaltyInterest.setId(info.getBillId() + DateUtil.convert(info.getPenaltyInterestMonth(), "yyyyMM"));
		billPenaltyInterest.setBillId(info.getBillId());
		billPenaltyInterest.setCustomerId(info.getCustomerId());
		billPenaltyInterest.setPenaltyInterestRatio(penaltyInterestRatio);
		billPenaltyInterest.setPenaltyInterestDays(info.getPenaltyDays());
		billPenaltyInterest.setPenaltyInterestMonth(new Timestamp(info.getPenaltyInterestMonth().getTime()));
		billPenaltyInterest.setWtime(new Timestamp(info.getPenaltyInterestMonth().getTime()));
		BigDecimal penaltyInterest = penaltyInterestRatio.multiply(new BigDecimal(info.getPenaltyDays())).multiply(info.getReceive());
		billPenaltyInterest.setPenaltyInterest(penaltyInterest.setScale(6, BigDecimal.ROUND_HALF_UP));
		return billPenaltyInterest;
	}

	/** 查询当前时间还在逾期的，未销账的账单（已对账、销账中） */
	private List<ProductBills> getOverdueBills(Date currentDate) {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("billStatus", Constants.ROP_IN,
					Arrays.asList(new Integer[] { BillStatus.WRITING_OFF.ordinal(), BillStatus.RECONILED.ordinal() })));
			searchFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.CUSTOMER.ordinal()));
			searchFilter.getRules().add(new SearchRule("finalReceiveTime", Constants.ROP_LT, currentDate));
			List<ProductBills> list = productBillsService.queryAllBySearchFilter(searchFilter);

			if (!CollectionUtils.isEmpty(list)) {
				return list;
			}
		} catch (ServiceException e) {
			logger.error("查询需要计算罚息的账单异常", e);
		}

		return new ArrayList<>();
	}

	/**
	 * 判断销账账单的到款是上个月的到款，则删除这些账单本月的罚息
	 * 
	 * @param billsList
	 *            1~3号销账账单
	 * @param currentDate
	 *            今天
	 */
	private void updatePenaltyInterestBillIds(List<ProductBills> billsList, Date currentDate) {
		List<String> billIdList = new ArrayList<>();

		try {
			for (ProductBills productBills : billsList) {
				List<FsExpenseincomeInfo> fsExpenseIncomeInfos = productBills.getFsExpenseIncomeInfos();
				if (!CollectionUtils.isEmpty(fsExpenseIncomeInfos)) {
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN,
							fsExpenseIncomeInfos.stream().map(FsExpenseincomeInfo::getFsExpenseIncomeId).collect(Collectors.toList())));
					searchFilter.getRules().add(new SearchRule("operateTime", Constants.ROP_GE, DateUtil.getThisMonthFirst(currentDate)));
					List<FsExpenseIncome> result = fsExpenseIncomeService.queryAllBySearchFilter(searchFilter); // 表示销账的到款有不是上个月的
					// 1~3号销账账单，到款全是上月
					if (CollectionUtils.isEmpty(result)) {
						billIdList.add("'" + productBills.getId() + "'");
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("查询这个月前三天销账的销账账单是上个月到款的账单异常", e);
		}

		try {
			if (billIdList.size() == 0) {
				logger.info("无这个月前三天销账的销账账单是上个月到款的账单");
			} else { // 删除这个月已经罚息的记录
				logger.info("该条罚息符合本月前三天销账到款为上个月的账单，更新账单罚息为0，billId：" + StringUtils.join(billIdList, ","));
				String sql = "UPDATE erp_bill_penalty_interest SET penaltyinterest = 0, penaltyinterestdays = 0 WHERE wtime >= ? AND billid IN " + "("
						+ StringUtils.join(billIdList, ",") + ")";
				baseDao.executeSqlUpdte(sql, new Object[] { DateUtil.getThisMonthFirst(currentDate) }, new Type[] { StandardBasicTypes.TIMESTAMP });
				logger.info("删除这个月前三天销账的账单是上个月到款的账单的罚息成功");
			}
		} catch (BaseException e) {
			logger.error("这个月前三天销账的销账账单是上个月到款账单的利息删除异常", e);
		}
	}

	/** 查询这个月前三天销账的账单 */
	private List<ProductBills> getThreeDayBeforeWritedOffBill(Date currentDate) {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("writeOffTime", Constants.ROP_LT, currentDate));
			searchFilter.getRules().add(new SearchRule("writeOffTime", Constants.ROP_GE, DateUtil.getThisMonthFirst(currentDate)));
			searchFilter.getRules().add(new SearchRule("billStatus", Constants.ROP_EQ, BillStatus.WRITED_OFF.ordinal()));

			return productBillsService.queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("查询这个月前三天销账的账单异常", e);
		}

		return null;
	}

	@SuppressWarnings("unused")
	private class BillPenaltyInterestInfo {

		private String customerId;

		private boolean isHistory;

		private String billId;

		private BigDecimal receive;

		private Date penaltyInterestMonth;

		private int penaltyDays;

		private Date wtime;

		public BigDecimal getReceive() {
			return receive;
		}

		public void setReceive(BigDecimal receive) {
			this.receive = receive;
		}

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public boolean isHistory() {
			return isHistory;
		}

		public void setHistory(boolean isHistory) {
			this.isHistory = isHistory;
		}

		public String getBillId() {
			return billId;
		}

		public void setBillId(String billId) {
			this.billId = billId;
		}

		public Date getPenaltyInterestMonth() {
			return penaltyInterestMonth;
		}

		public void setPenaltyInterestMonth(Date penaltyInterestMonth) {
			this.penaltyInterestMonth = penaltyInterestMonth;
		}

		public int getPenaltyDays() {
			return penaltyDays;
		}

		public void setPenaltyDays(int penaltyDays) {
			this.penaltyDays = penaltyDays;
		}

		public Date getWtime() {
			return wtime;
		}

		public void setWtime(Date wtime) {
			this.wtime = wtime;
		}

	}

}
