package com.dahantc.erp.vo.attendance.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.SpecialAttendanceType;
import com.dahantc.erp.enums.CheckinExceptionType;
import com.dahantc.erp.enums.CheckinType;
import com.dahantc.erp.enums.WorkStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.util.TimeListUtil;
import com.dahantc.erp.vo.attendance.dao.IAttendanceDao;
import com.dahantc.erp.vo.attendance.entity.Attendance;
import com.dahantc.erp.vo.attendance.service.IAttendanceService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.checkin.entity.Checkin;
import com.dahantc.erp.vo.checkin.service.ICheckinService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.user.entity.User;

@Service("attendanceService")
public class AttendanceServiceImpl implements IAttendanceService {
	private static Logger logger = LogManager.getLogger(AttendanceServiceImpl.class);

	@Autowired
	private IAttendanceDao attendanceDao;

	@Autowired
	private ICheckinService checkinService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IUserService userService;

	@Override
	public Attendance read(Serializable id) throws ServiceException {
		try {
			return attendanceDao.read(id);
		} catch (Exception e) {
			logger.error("读取员工出勤记录失败", e);
			throw new ServiceException("读取员工出勤记录失败", e);
		}
	}

	@Override
	public boolean save(Attendance entity) throws ServiceException {
		try {
			return attendanceDao.save(entity);
		} catch (Exception e) {
			logger.error("保存员工出勤记录失败", e);
			throw new ServiceException("保存员工出勤记录失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return attendanceDao.delete(id);
		} catch (Exception e) {
			logger.error("删除员工出勤记录失败", e);
			throw new ServiceException("删除员工出勤记录失败", e);
		}
	}

	@Override
	public boolean update(Attendance enterprise) throws ServiceException {
		try {
			return attendanceDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新员工出勤记录失败", e);
			throw new ServiceException("更新员工出勤记录失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<Attendance> objs) throws ServiceException {
		try {
			return attendanceDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<Attendance> objs) throws ServiceException {
		try {
			return attendanceDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<Attendance> objs) throws ServiceException {
		try {
			return attendanceDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return attendanceDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询员工出勤记录数量失败", e);
			throw new ServiceException("查询员工出勤记录数量失败", e);
		}
	}

	@Override
	public PageResult<Attendance> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return attendanceDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询员工出勤记录分页信息失败", e);
			throw new ServiceException("查询员工出勤记录分页信息失败", e);
		}
	}

	@Override
	public List<Attendance> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return attendanceDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询员工出勤记录失败", e);
			throw new ServiceException("查询员工出勤记录失败", e);
		}
	}

	@Override
	public List<Attendance> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return attendanceDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询员工出勤记录失败", e);
			throw new ServiceException("查询员工出勤记录失败", e);
		}
	}

	@Override
	public List<Attendance> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return attendanceDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询员工出勤记录失败", e);
			throw new ServiceException("查询员工出勤记录失败", e);
		}
	}

	@Override
	public Attendance readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return attendanceDao.readOneByProperty(property, value);
		} catch (Exception e) {
			logger.error("查询员工出勤记录失败", e);
			throw new ServiceException("查询员工出勤记录失败", e);
		}
	}

	public void buildUserAttendance(List<String> userIdList, Date date) {
		String dateStr = DateUtil.convert(date, DateUtil.format1);
		logger.info("生成员工出勤明细开始，日期：" + dateStr);

		if (CollectionUtils.isEmpty(userIdList)) {
			logger.info("员工列表为空");
			return;
		}
		SearchFilter userFilter = new SearchFilter();
		userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
		List<User> userList = new ArrayList<>();
		try {
			userList = userService.queryAllBySearchFilter(userFilter);
		} catch (ServiceException e) {
			logger.error("", e);
		}

		Date startTime = DateUtil.getDateStartDateTime(date);
		Date endTime = DateUtil.getDateEndDateTime(date, true);
		// 获取date的规定工作时间
		Map<String, Date> workTimeMap = getTodayWorkTime(date);
		SearchFilter filter = null;
		try {
			List<Attendance> addList = new ArrayList<>();
			List<Attendance> updateList = new ArrayList<>();
			for (User user : userList) {
				// 查员工在date的打卡记录
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("checkinTime", Constants.ROP_GE, startTime));
				filter.getRules().add(new SearchRule("checkinTime", Constants.ROP_LE, endTime));
				filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
				filter.getOrders().add(new SearchOrder("checkinTime", Constants.ROP_ASC));
				List<Checkin> userCheckinList = checkinService.queryAllBySearchFilter(filter);
				logger.info("员工：" + user.getRealName() + "在" + dateStr + "的打卡记录条数：" + (CollectionUtils.isEmpty(userCheckinList) ? 0 : userCheckinList.size()));

				// 查员工 请假/加班/外勤/出差 的时间包含date的记录
				String hql = "FROM SpecialAttendanceRecord WHERE NOT (endTime <= :dateStart OR startTime > :dateEnd) AND ossUserId = :ossUserId AND valid = 1 ORDER BY wtime DESC";
				Map<String, Object> param = new HashMap<>();
				param.put("dateStart", startTime);
				param.put("dateEnd", endTime);
				param.put("ossUserId", user.getOssUserId());
				List<Object> result = baseDao.findByhql(hql, param, 0);
				List<SpecialAttendanceRecord> specialAttendanceRecordList = null == result ? null
						: result.stream().map(obj -> (SpecialAttendanceRecord) obj).collect(Collectors.toList());

				logger.info("员工：" + user.getRealName() + "在" + dateStr + "的报备记录条数：" + (CollectionUtils.isEmpty(specialAttendanceRecordList) ? 0 : specialAttendanceRecordList.size()));

				boolean isCreate = false;
				Attendance attendance = null;
				// 查员工在date的出勤记录
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
				filter.getRules().add(new SearchRule("date", Constants.ROP_EQ, startTime));
				List<Attendance> userAttendanceList = queryAllBySearchFilter(filter);
				if (CollectionUtils.isEmpty(userAttendanceList)) {
					isCreate = true;
					attendance = new Attendance(user, date);
				} else {
					attendance = userAttendanceList.get(0);
				}

				attendance = buildAttendance(user, date, attendance, userCheckinList, specialAttendanceRecordList, workTimeMap);
				if (isCreate) {
					addList.add(attendance);
				} else {
					updateList.add(attendance);
				}
			}

			if (!CollectionUtils.isEmpty(addList)) {
				this.saveByBatch(addList);
			}
			if (!CollectionUtils.isEmpty(updateList)) {
				this.updateByBatch(updateList);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 生成单个员工一整天的出勤记录
	 * 
	 * @param user
	 *            员工
	 * @param date
	 *            日期
	 * @param attendance
	 *            出勤记录
	 * @param checkinList
	 *            打卡记录
	 * @param recordList
	 *            报备记录
	 * @param workTimeMap
	 *            工作时间
	 * @return
	 */
	private Attendance buildAttendance(User user, Date date, Attendance attendance, List<Checkin> checkinList,
									   List<SpecialAttendanceRecord> recordList, Map<String, Date> workTimeMap) {
		logger.info("生成员工：" + user.getRealName() + "在" + DateUtil.convert(date, DateUtil.format1) + "的出勤记录");
		if (null == attendance) {
			attendance = new Attendance(user, date);
		}
		// 规定的上班时间
		Date dateStart = DateUtil.getDateStartDateTime(date);
		Date dateEnd = DateUtil.getDateEndDateTime(date);
		Date amWorkStart = workTimeMap.get("amWorkStart");
		Date amWorkEnd = workTimeMap.get("amWorkEnd");
		Date pmWorkStart = workTimeMap.get("pmWorkStart");
		Date pmWorkEnd = workTimeMap.get("pmWorkEnd");

		// 是否未打卡
		boolean noCheckin = true;
		boolean noCheckout = true;
		// 是否特殊出勤
		boolean specialAttendance = false;

		// 实际开始工作的时间
		Date realWorkStart = null;
		Date realWorkEnd = null;

		if (!CollectionUtils.isEmpty(checkinList)) {
			// 有打卡记录 打卡记录按时间顺序排序
			checkinList.sort(Comparator.comparing(Checkin::getCheckinTime));

			// 查上班打卡记录，只找 正常打卡/时间异常 的记录，地点异常的不算
			List<Checkin> amCheckinList = checkinList.stream()
					.filter(check -> check.getCheckinType().equals(CheckinType.Checkin.getCode() + "") &&
							(StringUtil.isBlank(check.getExceptionType()) || StringUtil.equals(check.getExceptionType(), CheckinExceptionType.WRONG_TIME.ordinal() + ""))
					).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(amCheckinList)) {
				noCheckin = false;
				realWorkStart = amCheckinList.get(0).getCheckinTime();
			}

			// 查下班打卡记录，只找 正常打卡的记录
			List<Checkin> pmCheckoutList = checkinList.stream().filter(check -> check.getCheckinType().equals(CheckinType.Checkout.getCode() + "") && StringUtil.isBlank(check.getExceptionType())
			).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(pmCheckoutList)) {
				noCheckout = false;
				realWorkEnd = pmCheckoutList.get(0).getCheckinTime();
			}

			// 查外勤打卡
			List<Checkin> outsideCheckList = checkinList.stream().filter(check -> check.getCheckinType().equals(CheckinType.Outside.getCode() + ""))
					.collect(Collectors.toList());
			attendance.setOutside(CollectionUtils.isEmpty(outsideCheckList) ? 0 : outsideCheckList.size());
		}

		if (!CollectionUtils.isEmpty(recordList)) {
			specialAttendance = true;
			// 查请假
			List<SpecialAttendanceRecord> leaveLogList = recordList.stream()
					.filter(log -> SpecialAttendanceType.Leave.ordinal() == log.getSpecialAttendanceType()).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(leaveLogList)) {
				// 限制请假记录的时间范围在date之内 08:30:00 ~ 18:00:00
				leaveLogList = leaveLogList.stream().peek(log -> {
					if (log.getStartTime().before(amWorkStart)) {
						log.setStartTime(amWorkStart);
					}
					if (log.getEndTime().after(pmWorkEnd)) {
						log.setEndTime(pmWorkEnd);
					}
				}).collect(Collectors.toList());
				// 多条请假记录求并集，获得一个最大的时间范围或一组不重合的时间段
				List<SpecialAttendanceRecord> unionTimeList = TimeListUtil.splitTimeList(leaveLogList);
				JSONArray leaveInfo = new JSONArray();
				int leaveMins = 0;
				for (SpecialAttendanceRecord leaveTime : unionTimeList) {
					if (!leaveTime.getStartTime().before(amWorkEnd) && !leaveTime.getEndTime().after(pmWorkStart)) {
						// ----8----11-<-->-13----18---- 是休息时间，不算在请假时间
						continue;
					}
					Date start = leaveTime.getStartTime();
					Date end = leaveTime.getEndTime();

					if (!start.before(amWorkEnd) && !start.after(pmWorkStart)) {
						// ----8----11--<--13----18----，开始=下午上班
						start = pmWorkStart;
					}
					if (!end.before(amWorkEnd) && !end.after(pmWorkStart)) {
						// ----8----11-->--13----18----，结束=上午下班
						end = amWorkEnd;
					}
					if (!start.after(amWorkEnd) && !pmWorkStart.after(end)) {
						// ----8--<--11----13-->--18----，分成2段
						// 请假开始 ~ 上午下班
						int mins = DateUtil.getDiffmins(amWorkEnd, start);
						leaveMins += mins;
						JSONObject leaveInfoItem = getJSONItem(start, amWorkEnd, mins, leaveTime.getLeaveType() + "");
						leaveInfo.add(leaveInfoItem);
						// 下午上班 ~ 请假结束
						mins = DateUtil.getDiffmins(end, pmWorkStart);
						leaveMins += mins;
						leaveInfoItem = getJSONItem(pmWorkStart, end, mins, leaveTime.getLeaveType() + "");
						leaveInfo.add(leaveInfoItem);
					} else {
						// 结束 <= 上午下班 || 下午上班 <= 开始
						// ----8-->--11----13----18----
						// ----8----11----13--<--18----
						// 请假开始 ~ 请假结束
						int mins = DateUtil.getDiffmins(end, start);
						leaveMins += mins;
						JSONObject leaveInfoItem = getJSONItem(start, end, mins, leaveTime.getLeaveType() + "");
						leaveInfo.add(leaveInfoItem);
					}
				}
				attendance.setLeave(true);
				attendance.setLeaveInfo(leaveInfo.toJSONString());
				attendance.setLeaveMins(leaveMins);
			}

			// 查加班
			List<SpecialAttendanceRecord> overtimeLogList = recordList.stream()
					.filter(log -> SpecialAttendanceType.Overtime.ordinal() == log.getSpecialAttendanceType()).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(overtimeLogList)) {
				// 限制加班记录的时间范围在date之内 00:00:00 ~ 23:59:59
				overtimeLogList = overtimeLogList.stream().peek(log -> {
					if (log.getStartTime().before(dateStart)) {
						log.setStartTime(dateStart);
					}
					if (log.getEndTime().after(dateEnd)) {
						log.setEndTime(dateEnd);
					}
				}).collect(Collectors.toList());
				// 多条加班记录求并集，获得一个最大的时间范围或一组不重合的时间段
				List<SpecialAttendanceRecord> unionTimeList = TimeListUtil.combineTimeList(overtimeLogList);
				JSONArray overtimeInfo = new JSONArray();
				int overtimeMins = 0;
				for (SpecialAttendanceRecord overtime : unionTimeList) {
					int mins = DateUtil.getDiffmins(overtime.getEndTime(), overtime.getStartTime());
					overtimeMins += mins;
					JSONObject overtimeItem = getJSONItem(overtime.getEndTime(), overtime.getStartTime(), mins, null);
					overtimeInfo.add(overtimeItem);
				}
				attendance.setOvertime(true);
				attendance.setOvertimeInfo(overtimeInfo.toJSONString());
				attendance.setOvertimeMins(overtimeMins);
			}

			// 查外勤
			List<SpecialAttendanceRecord> outsideLogList = recordList.stream()
					.filter(log -> SpecialAttendanceType.Outside.ordinal() == log.getSpecialAttendanceType()).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(outsideLogList)) {
				// 限制外勤记录的时间范围在date之内 08:30:00 ~ 18:00:00
				outsideLogList = outsideLogList.stream().peek(log -> {
					if (log.getStartTime().before(amWorkStart)) {
						log.setStartTime(amWorkStart);
					}
					if (log.getEndTime().after(pmWorkEnd)) {
						log.setEndTime(pmWorkEnd);
					}
				}).collect(Collectors.toList());
				// 多条外勤记录求并集，获得一个最大的时间范围或一组不重合的时间段
				List<SpecialAttendanceRecord> unionTimeList = TimeListUtil.combineTimeList(outsideLogList);

				JSONArray outsideInfo = new JSONArray();
				int outsideMins = 0;
				for (SpecialAttendanceRecord outside : unionTimeList) {
					int mins = DateUtil.getDiffmins(outside.getEndTime(), outside.getStartTime());
					outsideMins += mins;
					JSONObject outsideInfoItem = getJSONItem(outside.getStartTime(), outside.getEndTime(), mins, null);
					outsideInfo.add(outsideInfoItem);
				}
				attendance.setOutsideInfo(outsideInfo.toJSONString());
				attendance.setOutsideMins(outsideMins);
			}

			// 查出差
			List<SpecialAttendanceRecord> businessTravelLogList = recordList.stream()
					.filter(log -> SpecialAttendanceType.BusinessTravel.ordinal() == log.getSpecialAttendanceType()).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(businessTravelLogList)) {
				// 限制出差记录的时间范围在date之内 00:00:00 ~ 23:59:59
				businessTravelLogList = businessTravelLogList.stream().peek(log -> {
					if (log.getStartTime().before(dateStart)) {
						log.setStartTime(dateStart);
					}
					if (log.getEndTime().after(pmWorkEnd)) {
						log.setEndTime(dateEnd);
					}
				}).collect(Collectors.toList());
				// 多条出差记录求并集，获得一个最大的时间范围或分段
				List<SpecialAttendanceRecord> unionTimeList = TimeListUtil.combineTimeList(businessTravelLogList);

				JSONArray businessTravelMinsInfo = new JSONArray();
				int businessTravelMins = 0;
				for (SpecialAttendanceRecord businessTravel : unionTimeList) {
					int mins = DateUtil.getDiffmins(businessTravel.getEndTime(), businessTravel.getStartTime());
					businessTravelMins += mins;
					JSONObject businessTravelInfoItem = getJSONItem(businessTravel.getStartTime(), businessTravel.getEndTime(), mins, null);
					businessTravelMinsInfo.add(businessTravelInfoItem);
				}
				attendance.setBusinessTravel(true);
				attendance.setBusinessTravelInfo(businessTravelMinsInfo.toJSONString());
				attendance.setBusinessTravelMins(businessTravelMins);
			}
		}

		/********************************* 根据上下班打卡确定出勤时长 *********************************/
		dealCheckin(noCheckin, noCheckout, realWorkStart, realWorkEnd, attendance, workTimeMap);

		if (attendance.isWork()) {
			attendance.setWorkStatus(WorkStatus.Normal.ordinal());
		}
		if (attendance.isLate() || attendance.isAbsenteeism()) {
			attendance.setWorkStatus(WorkStatus.Exceptional.ordinal());
		}
		if (specialAttendance) {
			attendance.setWorkStatus(WorkStatus.Special.ordinal());
		}
		return attendance;
	}

	private void dealCheckin(boolean noCheckin, boolean noCheckout, Date realWorkStart, Date realWorkEnd, Attendance attendance, Map<String, Date> workTimeMap) {
		attendance.cleanLate();
		attendance.cleanWork();
		attendance.cleanAbsenteeism();

		Date amWorkStart = workTimeMap.get("amWorkStart");
		Date amWorkEnd = workTimeMap.get("amWorkEnd");
		Date pmWorkStart = workTimeMap.get("pmWorkStart");
		Date pmWorkEnd = workTimeMap.get("pmWorkEnd");
		// 迟到、旷工的规定时间
		Date amLate = DateUtil.getIntervalMinuteFinal(amWorkStart, Constants.DEFAULT_LATE_MINUTES);
		Date amAbsenteeism = DateUtil.getIntervalMinuteFinal(amWorkStart, Constants.DEFAULT_ABSENTEEISM_MINUTES);


		// 工作情况
		JSONArray workInfo = new JSONArray();
		int workMins = 0;
		// 迟到情况
		JSONArray lateInfo = new JSONArray();
		int lateMins = 0;
		// 旷工情况
		JSONArray absenteeismInfo = new JSONArray();
		int absenteeismMins = 0;

		if (noCheckin || noCheckout) {
			// 缺卡
			attendance.setAbsenteeism(true);
			JSONObject absenteeismInfoItem;
			int mins = 0;
			if (noCheckin || !realWorkStart.after(amWorkStart)) {
				// 上班未打卡，或上班正常打卡但下班没打卡，算缺勤一整天
				// 因为下午未打卡的旷工
				// 上午旷工
				mins = DateUtil.getDiffmins(amWorkEnd, amWorkStart);
				absenteeismMins += mins;
				absenteeismInfoItem = getJSONItem(amWorkStart, amWorkEnd, mins,"noCheckout");
				absenteeismInfo.add(absenteeismInfoItem);
				// 下午旷工
				mins = DateUtil.getDiffmins(pmWorkEnd, pmWorkStart);
				absenteeismMins += mins;
				absenteeismInfoItem = getJSONItem(pmWorkStart, pmWorkEnd, mins,"noCheckout");
				absenteeismInfo.add(absenteeismInfoItem);
			} else if (!realWorkStart.after(amLate)) {
				// 迟到时间<5分钟，不算迟到，但记录迟到时间
				// 上午迟到
				mins = DateUtil.getDiffmins(realWorkStart, amWorkStart);
				lateMins += mins;
				JSONObject lateInfoItem = getJSONItem(amWorkStart, realWorkStart, mins,"checkinLate");
				lateInfo.add(lateInfoItem);
				// 因为下午未打卡的旷工
				// 上午旷工
				mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
				absenteeismMins += mins;
				absenteeismInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"noCheckout");
				absenteeismInfo.add(absenteeismInfoItem);
				// 下午旷工
				mins = DateUtil.getDiffmins(pmWorkEnd, pmWorkStart);
				absenteeismMins += mins;
				absenteeismInfoItem = getJSONItem(pmWorkStart, pmWorkEnd, mins,"noCheckout");
				absenteeismInfo.add(absenteeismInfoItem);
			} else if (!realWorkStart.after(amAbsenteeism)) {
				// 迟到时间(5，60]，算迟到，记录迟到时间
				// 上午迟到
				attendance.setLate(true);
				mins = DateUtil.getDiffmins(realWorkStart, amWorkStart);
				lateMins += mins;
				JSONObject lateInfoItem = getJSONItem(amWorkStart, realWorkStart, mins,"checkinLate");
				lateInfo.add(lateInfoItem);
				// 因为下午未打卡的旷工
				// 上午旷工
				mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
				absenteeismMins += mins;
				absenteeismInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"noCheckout");
				absenteeismInfo.add(absenteeismInfoItem);
				// 下午旷工
				mins = DateUtil.getDiffmins(pmWorkEnd, pmWorkStart);
				absenteeismMins += mins;
				absenteeismInfoItem = getJSONItem(pmWorkStart, pmWorkEnd, mins,"noCheckout");
				absenteeismInfo.add(absenteeismInfoItem);
			} else {
				// 迟到时间>60分钟，打卡算旷工，记录旷工时间
				if (!realWorkStart.after(amWorkEnd)) {
					// 上午下班前打卡的旷工
					// 上午旷工
					mins = DateUtil.getDiffmins(realWorkStart, amWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(amWorkStart, realWorkStart, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 因为下班未打卡的旷工
					// 上午旷工
					mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"noCheckout");
					absenteeismInfo.add(absenteeismInfoItem);
					// 下午旷工
					mins = DateUtil.getDiffmins(pmWorkEnd, pmWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(pmWorkStart, pmWorkEnd, mins,"noCheckout");
					absenteeismInfo.add(absenteeismInfoItem);
				} else if (!realWorkStart.after(pmWorkStart)) {
					// 下午上班前打卡的旷工
					// 上午旷工
					mins = DateUtil.getDiffmins(amWorkEnd, amWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(amWorkStart, amWorkEnd, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 因为下班未打卡的旷工
					// 下午旷工
					mins = DateUtil.getDiffmins(pmWorkEnd, pmWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(pmWorkStart, pmWorkEnd, mins,"noCheckout");
					absenteeismInfo.add(absenteeismInfoItem);
				} else {
					// 下午下班前打卡的旷工
					// 上午旷工
					mins = DateUtil.getDiffmins(amWorkEnd, amWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(amWorkStart, amWorkEnd, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 下午旷工
					mins = DateUtil.getDiffmins(pmWorkEnd, pmWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(pmWorkStart, pmWorkEnd, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 因为下班未打卡的旷工
					// 下午旷工
					mins = DateUtil.getDiffmins(pmWorkEnd, realWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(realWorkStart, pmWorkEnd, mins,"noCheckout");
					absenteeismInfo.add(absenteeismInfoItem);
				}
			}
		} else {
			attendance.setWork(true);
			JSONObject workInfoItem;
			int mins = 0;
			if (!realWorkStart.after(amWorkStart)) {
				// 上下班正常打卡
				// 上午上班
				mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
				workMins += mins;
				workInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"normal");
				workInfo.add(workInfoItem);
				// 下午上班
				mins = DateUtil.getDiffmins(realWorkEnd, pmWorkStart);
				workMins += mins;
				workInfoItem = getJSONItem(pmWorkStart, realWorkEnd, mins,"normal");
				workInfo.add(workInfoItem);
			} else if (!realWorkStart.after(amLate)) {
				// 迟到时间<5分钟，不算迟到，但记录迟到时间
				// 上午迟到
				mins = DateUtil.getDiffmins(realWorkStart, amWorkStart);
				lateMins += mins;
				JSONObject lateInfoItem = getJSONItem(amWorkStart, realWorkStart, mins,"checkinLate");
				lateInfo.add(lateInfoItem);
				// 上午上班
				mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
				workMins += mins;
				workInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"normal");
				workInfo.add(workInfoItem);
				// 下午上班
				mins = DateUtil.getDiffmins(realWorkEnd, pmWorkStart);
				workMins += mins;
				workInfoItem = getJSONItem(pmWorkStart, pmWorkStart, mins,"normal");
				workInfo.add(workInfoItem);
			} else if (!realWorkStart.after(amAbsenteeism)) {
				// 迟到时间(5，60]，算迟到，记录迟到时间
				attendance.setLate(true);
				// 上午迟到
				mins = DateUtil.getDiffmins(realWorkStart, amWorkStart);
				lateMins += mins;
				JSONObject lateInfoItem = getJSONItem(amWorkStart, realWorkStart, mins,"checkinLate");
				lateInfo.add(lateInfoItem);
				// 上午上班
				mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
				workMins += mins;
				workInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"checkinLate");
				workInfo.add(workInfoItem);
				// 下午上班
				mins = DateUtil.getDiffmins(realWorkEnd, pmWorkStart);
				workMins += mins;
				workInfoItem = getJSONItem(pmWorkStart, realWorkEnd, mins,"checkinLate");
				workInfo.add(workInfoItem);
			} else {
				// 迟到时间>60分钟，打卡算旷工，记录旷工时间
				attendance.setWork(true);
				attendance.setAbsenteeism(true);
				if (!realWorkStart.after(amWorkEnd)) {
					// 上午下班前打卡
					// 上午旷工
					mins = DateUtil.getDiffmins(realWorkStart, amWorkStart);
					absenteeismMins += mins;
					JSONObject absenteeismInfoItem = getJSONItem(amWorkStart, realWorkStart, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 上午上班
					mins = DateUtil.getDiffmins(amWorkEnd, realWorkStart);
					workMins += mins;
					workInfoItem = getJSONItem(realWorkStart, amWorkEnd, mins,"checkinAbsenteeism");
					workInfo.add(workInfoItem);
					// 下午上班
					mins = DateUtil.getDiffmins(realWorkEnd, pmWorkStart);
					workMins += mins;
					workInfoItem = getJSONItem(pmWorkStart, realWorkEnd, mins,"checkinAbsenteeism");
					workInfo.add(workInfoItem);
				} else if (!realWorkStart.after(pmWorkStart)) {
					// 下午上班前打卡
					// 上午旷工
					mins = DateUtil.getDiffmins(amWorkEnd, amWorkStart);
					absenteeismMins += mins;
					JSONObject absenteeismInfoItem = getJSONItem(amWorkStart, amWorkEnd, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 下午上班
					mins = DateUtil.getDiffmins(realWorkEnd, pmWorkStart);
					workMins += mins;
					workInfoItem = getJSONItem(pmWorkStart, realWorkEnd, mins,"checkinAbsenteeism");
					workInfo.add(workInfoItem);
				} else {
					// 下午下班前打卡
					// 上午旷工
					mins = DateUtil.getDiffmins(amWorkEnd, amWorkStart);
					absenteeismMins += mins;
					JSONObject absenteeismInfoItem = getJSONItem(amWorkStart, amWorkEnd, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 下午旷工
					mins = DateUtil.getDiffmins(realWorkStart, pmWorkStart);
					absenteeismMins += mins;
					absenteeismInfoItem = getJSONItem(pmWorkStart, realWorkStart, mins,"checkinAbsenteeism");
					absenteeismInfo.add(absenteeismInfoItem);
					// 下午上班
					mins = DateUtil.getDiffmins(realWorkEnd, realWorkStart);
					workMins += mins;
					workInfoItem = getJSONItem(realWorkStart, realWorkEnd, mins,"checkinAbsenteeism");
					workInfo.add(workInfoItem);
				}
			}
		}
		if (!workInfo.isEmpty()) {
			attendance.setWorkInfo(workInfo.toJSONString());
			attendance.setWorkMins(workMins);
		}
		if (!lateInfo.isEmpty()) {
			attendance.setLateInfo(lateInfo.toJSONString());
			attendance.setLateMins(lateMins);
		}
		if (!absenteeismInfo.isEmpty()) {
			attendance.setAbsenteeismInfo(absenteeismInfo.toJSONString());
			attendance.setAbsenteeismMins(absenteeismMins);
		}
	}



	private JSONObject getJSONItem(Date start, Date end, int mins, String note) {
		JSONObject item = new JSONObject();
		item.put("startTime", DateUtil.convert(start, DateUtil.format2));
		item.put("endTime", DateUtil.convert(end, DateUtil.format2));
		item.put("mins", mins);
		if (StringUtil.isNotBlank(note)) {
			item.put("note", note);
		}
		return item;
	}

	/**
	 * 获取日期规定的工作班时间
	 * 
	 * @param date
	 *            日期
	 * @return 当天工作时间map
	 */
	private Map<String, Date> getTodayWorkTime(Date date) {
		String workTime = null;
		try {
			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.WORK_TIME_KEY);
			if (parameter != null && parameter.getParamvalue() != null) {
				workTime = parameter.getParamvalue();
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		if (null == workTime) {
			logger.info("未获取到工作时间，使用默认工作时间：" + Constants.DEFAULT_WORK_TIME);
			workTime = Constants.DEFAULT_WORK_TIME;
		} else {
			logger.info("获取到工作时间：" + workTime);
		}
		// 工作时间字符串
		String[] workTimes = workTime.split(","); // [8:30-11:45, 13:15-18:00]
		String[] amWorkTime = workTimes[0].split("-"); // [8:30, 11:45]
		String[] pmWorkTime = workTimes[1].split("-"); // [13:15, 18:00]

		Map<String, Date> workTimeMap = new HashMap<>();
		String dateStr = DateUtil.convert(date, DateUtil.format1);

		// 上午开始
		String dateTimeStr = dateStr + " " + amWorkTime[0] + ":00";
		Date dateTime = DateUtil.convert(dateTimeStr, DateUtil.format2);
		workTimeMap.put("amWorkStart", dateTime);

		// 上午结束
		dateTimeStr = dateStr + " " + amWorkTime[1] + ":00";
		dateTime = DateUtil.convert(dateTimeStr, DateUtil.format2);
		workTimeMap.put("amWorkEnd", dateTime);

		// 下午开始
		dateTimeStr = dateStr + " " + pmWorkTime[0] + ":00";
		dateTime = DateUtil.convert(dateTimeStr, DateUtil.format2);
		workTimeMap.put("pmWorkStart", dateTime);

		// 下午结束
		dateTimeStr = dateStr + " " + pmWorkTime[1] + ":00";
		dateTime = DateUtil.convert(dateTimeStr, DateUtil.format2);
		workTimeMap.put("pmWorkEnd", dateTime);

		return workTimeMap;
	}
}
