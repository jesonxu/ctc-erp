package com.dahantc.erp.task;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;
import com.dahantc.erp.vo.dailyExpenseTj.entity.Dailyexpenses;
import com.dahantc.erp.vo.dailyExpenseTj.service.IDailyexpensesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * 日常费用统计表生成任务
 *
 */
@Component
public class BackTaskDailyExpensesStatisticsTask implements SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(BackTaskDailyExpensesStatisticsTask.class);

	private static String CRON = "0 30 0 * * ?";

	@Autowired
	IChargeRecordService chargeRecordService;

	@Autowired
	IDailyexpensesService dailyexpensesService;

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTask-DailyExpensesStatistics-Task is running...");
				try {
					// 根据收支表生成日常费用统计表
					dailyExpensesStatistics();
				} catch (Exception e) {
					logger.error("BackTask-DailyExpensesStatistics-Task is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-DailyExpensesStatistics-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	public void dailyExpensesStatistics() {
		// 1.查询昨日收支表信息
		SearchFilter filter = new SearchFilter();
		Map<String, BigDecimal> chargeTypeMap = new HashMap<String, BigDecimal>();
		List<Dailyexpenses> dailyexpensesList = new ArrayList<Dailyexpenses>();
		Date tjDate = DateUtil.getYesterdayStartDateTime();
		try {
			logger.info("开始生成日常费用统计表记录");
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, new Timestamp(DateUtil.getYesterdayStartDateTime().getTime())));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, new Timestamp(DateUtil.getCurrentStartDateTime().getTime())));
			List<ChargeRecord> chargeRecordList = chargeRecordService.queryAllBySearchFilter(filter);
			if (chargeRecordList != null && chargeRecordList.size() > 0) {
				for (ChargeRecord chargeRecord : chargeRecordList) {
					if (chargeRecord.getChargeType() != IncomeExpenditureType.PREPURCHASE.getCode()
							&& chargeRecord.getChargeType() != IncomeExpenditureType.ADVANCE.getCode()
							&& chargeRecord.getChargeType() != IncomeExpenditureType.DEPOSIT.getCode()
							&& chargeRecord.getChargeType() != IncomeExpenditureType.REMUNERATION.getCode()
							&& chargeRecord.getChargeType() != IncomeExpenditureType.BILL.getCode()) {
						int businessType = chargeRecord.getBusinessType();
						int chargeType = chargeRecord.getChargeType();
						String deptId = chargeRecord.getDeptId();
						String key = businessType + "," + chargeType + "," + deptId;
						// TODO 怎么加上创建者
						BigDecimal chargePrice = chargeRecord.getChargePrice();
						if (chargeTypeMap.containsKey(key)) {
							chargeTypeMap.put(key, new BigDecimal(chargePrice.longValue() + chargeTypeMap.get(key).longValue()));
						} else {
							chargeTypeMap.put(key, chargePrice);
						}
					}
				}
			} else {
				logger.info(DateUtil.convert(new Date(), DateUtil.format1) + "日收支记录表为空");
			}
			// 2.根据收支表信息，插入至日常费用统计表
			if (chargeRecordList != null && chargeTypeMap.size() > 0) {
				for (String key : chargeTypeMap.keySet()) {
					String[] data = key.split(",");
					Dailyexpenses dailyexpenses = new Dailyexpenses();
					dailyexpenses.setBusinessType(Integer.parseInt(data[0]));
					dailyexpenses.setIncomeExpenditureType(Integer.parseInt(data[1]));
					dailyexpenses.setDeptId(data[3]);
					dailyexpenses.setCost(chargeTypeMap.get(key));
					dailyexpenses.setStatsDate(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format1)));
					dailyexpenses.setStatsYearMonth(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format4) + "-01"));
					dailyexpenses.setStatsYear(java.sql.Date.valueOf(DateUtil.convert(tjDate, DateUtil.format11) + "-01-01"));
					dailyexpensesList.add(dailyexpenses);
				}
				dailyexpensesService.saveByBatch(dailyexpensesList);
			}
			logger.info("日常费用统计表批量插入成功" + (chargeTypeMap == null ? 0 : chargeTypeMap.size()) + "条");
		} catch (Exception e) {
			logger.error("日常费用统计表生成失败", e);
		}
	}
}