package com.dahantc.erp.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.dao.BaseException;
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
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.attendance.service.IAttendanceService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 生成出勤记录的任务
 * 
 * @author 8523
 *
 */
@Component
public class BackTaskBuildAttendanceTask implements SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(BackTaskBuildAttendanceTask.class);

	private static String CRON = "0 30 0 ? * *";

	@Autowired
	private IUserService userService;

	@Autowired
	private IAttendanceService attendanceService;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTaskBuildAttendanceTask is running...");
				try {
					build();
				} catch (Exception e) {
					logger.error("BackTaskBuildAttendanceTask is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTaskBuildAttendanceTask：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	/**
	 * 生成出勤记录
	 */
	private void build() {
		try {
			// 获取日期开始
			Date startDate = getLastAttendanceDate();
			if (null == startDate) {
				startDate = DateUtil.getYesterdayStartDateTime();
			}
			Date endDate = DateUtil.getYesterdayEndDateTime();

			Date date = startDate;
			while (date.getTime() < endDate.getTime()) {
				// 获取今天打卡的用户
				List<String> userIdList = getCheckinUsers(date);
				if (CollectionUtils.isEmpty(userIdList)) {
					logger.info("今天无用户打卡：" + DateUtil.convert(date, DateUtil.format1));
				} else {
					attendanceService.buildUserAttendance(userIdList, date);
				}
				date = DateUtil.getNextDayStart(date);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 获取客户统计表（新表）最近的统计日期
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Date getLastAttendanceDate() {
		// 默认昨天
		Date date = null;
		String sql = "select max(date) from erp_attendance";
		try {
			List<Object> result = (List<Object>) baseDao.selectSQL(sql);
			if (result != null && result.get(0) != null) {
				date = new Date(((java.sql.Timestamp) result.get(0)).getTime());
				logger.info("获取到员工出勤记录表最近的日期：" + DateUtil.convert(date, DateUtil.format1));
			} else {
				logger.info("第一次生成员工出勤记录，从最早的打卡记录开始生成");
				sql = "select min(checkintime) from erp_checkin";
				result = (List<Object>) baseDao.selectSQL(sql);
				if (result != null && result.get(0) != null) {
					date = new Date(((java.sql.Timestamp) result.get(0)).getTime());
					logger.info("获取到打卡记录表最早的日期：" + DateUtil.convert(date, DateUtil.format1));
				} else {
					logger.info("未获取到打卡记录日期");
				}
			}
		} catch (Exception e) {
			logger.error("获取员工出勤记录表最近的日期异常", e);
		}
		return date;
	}

	// 获取当天的打卡的员工
	private List<String> getCheckinUsers(Date date) {
		String dateStr = DateUtil.convert(date, DateUtil.format1);
		List<String> checkinUserList = new ArrayList<>();
		String sql = "select distinct ossuserid from erp_checkin where checkintime >= ? and checkintime <= ?";
		try {
			List<Object> result = baseDao.selectSQL(sql, new Object[] {dateStr + " 00:00:00", dateStr + " 23:59:59"});
			if (!CollectionUtils.isEmpty(result)) {
				checkinUserList = result.stream().map(String::valueOf).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("", e);
		}
		return checkinUserList;
	}
}