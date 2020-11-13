package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;

/**
 * 检测员工入职工作时间，计算年假
 *
 * @author 8523
 *
 */
@Component
public class BackTaskCalculateAnnualLeaveTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskCalculateAnnualLeaveTask.class);

	private static String CRON = "0 20 0 ? * *";

	@Autowired
	private IUserService userService;

	@Autowired
	private IUserLeaveService userLeaveService;

	@Autowired
	private IParameterService parameterService;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTaskCalculateAnnualLeaveTask is running...");
				try {
					detectUser();
				} catch (Exception e) {
					logger.error("BackTaskCalculateAnnualLeaveTask is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTaskCalculateAnnualLeaveTask：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	private void detectUser() {
		Date today = DateUtil.getCurrentEndDateTime();
		String todayStr = DateUtil.convert(today, DateUtil.format1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.YEAR, -1);
		SearchFilter filter = new SearchFilter();
		// 获取一年前的今天，只有入职满一年才有年假
		filter.getRules().add(new SearchRule("entryTime", Constants.ROP_LE, cal.getTime()));
		filter.getRules().add(new SearchRule("graduationDate", Constants.ROP_NE, null));
		try {
			List<User> userList = userService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(userList)) {
				logger.info("没有入职满一年的员工，退出任务。。。");
				return;
			}
			// 只查今年的年假
			Date year = DateUtil.getYearFirst(today);
			List<String> userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());
			filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
			filter.getRules().add(new SearchRule("year", Constants.ROP_EQ, year));
			filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, LeaveType.ANNUAL_LEAVE.getCode()));
			List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
			Map<String, UserLeave> userLeaveMap = CollectionUtils.isEmpty(userLeaveList) ? new HashMap<>()
					: userLeaveList.stream().collect(Collectors.toMap(UserLeave::getOssUserId, v -> v));
			String calDateStr = DateUtil.convert(new Date(), DateUtil.format1);
			// 每年的年假的有效期为 该年的1月1日 ~ 下一年的重置日期；不设置重置日期，则有效期 为 该年的1月1日 ~ 该年的12月31日
			Date validStartDate = year;
			Date validEndDate = DateUtil.getYearLast(year);
			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.ANNUAL_LEAVE_RESET_KEY);
			if (parameter != null && parameter.getParamvalue() != null) {
				String resetDateStr = year + "-" + parameter.getParamvalue();
				Date resetDate = DateUtil.convert(resetDateStr, DateUtil.format1);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(resetDate);
				calendar.add(Calendar.YEAR, 1);
				validEndDate = DateUtil.getYesterdayEndDateTime(calendar.getTime());
			}

			List<UserLeave> updateList = new ArrayList<>();
			List<UserLeave> addList = new ArrayList<>();
			for (User user : userList) {
				String remark = "";
				int leaveDays = 0;
				Date graduationDate = user.getGraduationDate();
				// 累计工作时长
				int workMonth = DateUtil.getDiffMonths(graduationDate, today);
				if (workMonth < 0) {
					remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，在" + calDateStr
							+ "未毕业，不计算年假";
					logger.info(remark);
					continue;
				} else if (workMonth < 12) {
					remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止" + calDateStr
							+ "累计工作" + workMonth + "个月，不满一年，年假天数为0";
					logger.info(remark);
					leaveDays = 0;
				} else if (workMonth < 120) {
					remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止" + calDateStr
							+ "累计工作" + workMonth + "个月，已满一年不满十年，年假天数为5";
					logger.info(remark);
					leaveDays = 5;
				} else if (workMonth < 240) {
					remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止" + calDateStr
							+ "累计工作" + workMonth + "个月，已满十年不满二十年，年假天数为10";
					logger.info(remark);
					leaveDays = 10;
				} else {
					remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止" + calDateStr
							+ "累计工作" + workMonth + "个月，已满二十年，年假天数为15";
					logger.info(remark);
					leaveDays = 15;
				}

				UserLeave oldUserLeave = userLeaveMap.get(user.getOssUserId());
				if (oldUserLeave != null) {
					if (oldUserLeave.getTotalDays().compareTo(new BigDecimal(leaveDays)) != 0) {
						// 剩余天数 + (重新计算的总天数 - 上次计算的总天数)
						oldUserLeave.setLeftDays(oldUserLeave.getLeftDays().add(new BigDecimal(leaveDays).subtract(oldUserLeave.getTotalDays())));
						oldUserLeave.setTotalDays(new BigDecimal(leaveDays));
						oldUserLeave.setRemark(oldUserLeave.getRemark() + "\n" + remark);
						updateList.add(oldUserLeave);
					}
				} else {
					UserLeave userLeave = new UserLeave(user);
					userLeave.setOssUserId(user.getOssUserId());
					userLeave.setDeptId(user.getDeptId());
					userLeave.setYear(year);
					userLeave.setTotalDays(new BigDecimal(leaveDays));
					userLeave.setLeaveType(LeaveType.ANNUAL_LEAVE.getCode());
					userLeave.setLeftDays(userLeave.getTotalDays());
					userLeave.setRemark(remark);
					userLeave.setValidStartDate(validStartDate);
					userLeave.setValidEndDate(validEndDate);
					addList.add(userLeave);
				}
			}
			boolean result = false;
			if (!CollectionUtils.isEmpty(updateList)) {
				result = userLeaveService.updateByBatch(updateList);
				logger.info("更新" + updateList.size() + "条员工年假数据" + (result ? "成功" : "失败"));
			}
			if (!CollectionUtils.isEmpty(addList)) {
				result = userLeaveService.saveByBatch(addList);
				logger.info("新增" + addList.size() + "条员工年假数据" + (result ? "成功" : "失败"));
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}
}
