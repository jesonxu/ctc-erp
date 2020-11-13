package com.dahantc.erp.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.DeepCloneUtil;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.CheckinDataType;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.checkin.entity.Checkin;
import com.dahantc.erp.vo.checkin.service.ICheckinService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.springframework.util.CollectionUtils;

/**
 * 企业微信打卡记录同步任务
 * 
 * @author 8523
 *
 */
@Component
public class BackTaskSyncCheckinTask implements SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(BackTaskSyncCheckinTask.class);

	private static String CRON = "0 10 0 ? * *";

	@Autowired
	private IUserService userService;

	@Autowired
	private Environment ev;

	@Autowired
	private ICheckinService checkinService;

	@Autowired
	private IBaseDao baseDao;

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), r -> new Thread(r, "打卡同步线程池"));

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTaskSyncCheckinTask is running...");
				try {
					initwxParam();
					syncCheckinData();
				} catch (Exception e) {
					logger.error("BackTaskSyncCheckinTask is error...", e);
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTaskSyncCheckinTask：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	public void initwxParam() {
		WeixinMessage.initwxParam(ev);
	}

	/**
	 * 同步企业微信打卡记录
	 */
	private void syncCheckinData() {
		try {
			// 获取最后一次同步的打卡记录时间
			Date lastDate = getLastCheckinDate();
			Date startDate;
			Date endDate = DateUtil.getYesterdayEndDateTime();
			// 打卡记录为空，或每月1号，重新同步上个月初到昨天的打卡记录
			Calendar calendar = Calendar.getInstance();
			if (lastDate == null || calendar.get(Calendar.DAY_OF_MONTH) == 1) {
				startDate = DateUtil.getLastMonthFirst();
			} else {
				// 开始时间，是最后一天的下一天00:00:00 往前推延迟天数的日期
				startDate = DateUtil.getDaysOfDistance(DateUtil.getNextDayStart(lastDate), Constants.CHECKIN_DELAY_DAYS);
				if (startDate.after(endDate)) {
					startDate = DateUtil.getYesterdayStartDateTime();
				}
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
			filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.ordinal()));
			int count = userService.getCount(filter);
			if (count == 0) {
				logger.info("有效用户为空，退出任务。。。");
				return;
			}
			logger.info("查询到用户数：" + count);
			int pageSize = WeixinMessage.getWeixinParam().getUserListSize();
			int pages = count / pageSize + 1;
			// 需要分页
			for (int page = 1; page <= pages; page++) {
				PageResult<User> pageResult = userService.queryByPages(pageSize, page, filter);
				ArrayList<User> userList = new ArrayList<>(pageResult.getData());
				if (CollectionUtils.isEmpty(userList)) {
					continue;
				}
				delOldData(startDate, endDate, userList);
				executor.execute(new CheckinSyncTask(userList, startDate, endDate));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 同步企业微信打卡记录的线程类，企业微信限制一次只能查100个用户的打卡记录，用户数大于100要分批查询
	 */
	class CheckinSyncTask implements Runnable {
		private List<User> userList;
		private Date startDate;
		private Date endDate;

		public CheckinSyncTask(ArrayList<User> userList, Date startDate, Date endDate) {
			this.userList = DeepCloneUtil.clone(userList);
			userList.clear();
			this.startDate = new Date(startDate.getTime());
			this.endDate = new Date(endDate.getTime());
		}

		@Override
		public void run() {
			// 提交json数据
			JSONObject postData = new JSONObject();
			postData.put("opencheckindatatype", CheckinDataType.All.getCode());
			String[] userIdList = new String[userList.size()];
			userList.stream().map(User::getOssUserId).collect(Collectors.toList()).toArray(userIdList);
			postData.put("useridlist", userIdList);

			Date currentDate = startDate;
			Date nextDate;
			try {
				while (currentDate.before(endDate)) {
					// 企业微信限制每次最多只能查一个月
					if (DateUtil.getDiffDays(endDate, currentDate) > 30) {
						nextDate = DateUtil.getDaysOfDistance(currentDate, 30);
					} else {
						nextDate = endDate;
					}
					// 查询时间到秒，而不是毫秒
					postData.put("starttime", currentDate.getTime() / 1000);
					postData.put("endtime", nextDate.getTime() / 1000);

					List<Checkin> checkinList = new ArrayList<>();
					// 发送请求，接收响应数据
					String pageResponse = WeixinMessage.getCheckInData(postData.toString());
					JSONObject responseJson = JSON.parseObject(pageResponse);
					if (responseJson.getIntValue("errcode") == 0) {
						JSONArray checkInData = responseJson.getJSONArray("checkindata");
						if (checkInData != null && checkInData.size() > 0) {
							for (Object data : checkInData) {
								Checkin checkin = new Checkin();
								checkin.toCheckin((JSONObject) data);
								checkinList.add(checkin);
							}
						}
					}
					if (checkinList.size() > 0) {
						checkinService.saveByBatch(checkinList);
					}
					currentDate = nextDate;
				}

			} catch (Exception e) {
				logger.info(e);
			} finally {
				if (userList != null) {
					userList.clear();
					userList = null;
				}
			}
		}
	}

	/**
	 * 获取最后一条打卡记录的时间
	 * 
	 * @return
	 */
	private Date getLastCheckinDate() {
		Date date = null;
		String sql = "select max(checkintime) from erp_checkin";
		try {
			List<Object> result = (List<Object>) baseDao.selectSQL(sql);
			if (result != null && result.get(0) != null) {
				date = new Date(((java.sql.Timestamp) result.get(0)).getTime());
				logger.info("最近的打卡记录日期：" + DateUtil.convert(date, DateUtil.format1));
			} else {
				logger.info("打卡记录为空，重新开始同步");
			}
		} catch (Exception e) {
			logger.error("获取最近的打卡记录日期异常", e);
		}
		return date;
	}

	/**
	 * 删除打卡记录历史数据
	 * 
	 * @param startDate
	 *            开始日期00:00:00
	 * @param endDate
	 *            结束日期23:59:59
	 */
	private void delOldData(Date startDate, Date endDate, List<User> userList) {
		if (CollectionUtils.isEmpty(userList)) {
			return;
		}
		String userIds = userList.stream().map(user -> "'" + user.getOssUserId() + "'").collect(Collectors.joining(","));
		String sql = "delete from erp_checkin where checkintime >= '" + DateUtil.convert(startDate, DateUtil.format2) + "' and checkintime <= '"
				+ DateUtil.convert(endDate, DateUtil.format2) + "' and ossuserid in (" + userIds + ")";
		try {
			baseDao.executeUpdateSQL(sql);
			logger.info("删除历史打卡记录成功：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1));
		} catch (Exception e) {
			logger.info("删除历史打卡记录异常：" + DateUtil.convert(startDate, DateUtil.format1) + " ~ " + DateUtil.convert(endDate, DateUtil.format1), e);
		}
	}
}