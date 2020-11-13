package com.dahantc.erp.controller.attendance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.dto.attendance.MonthAttendanceDto;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.attendance.AttendanceDto;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.enums.WorkStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.attendance.entity.Attendance;
import com.dahantc.erp.vo.attendance.service.IAttendanceService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 特殊出勤报备记录action
 */
@Controller
@RequestMapping(value = "/attendance")
public class AttendanceAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(AttendanceAction.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private IAttendanceService attendanceService;

	/**
	 * 跳转员工出勤记录页面
	 */
	@RequestMapping(value = "/toAttendanceSheet")
	public String toAttendanceSheet() {
		return "/views/attendance/attendanceSheet";
	}

	/**
	 * 查员工出勤记录
	 * 
	 * @param date
	 *            日期
	 * @param keyword
	 *            关键词
	 * @param deptId
	 *            部门id
	 * @return
	 */
	@RequestMapping("/queryAttendance")
	@ResponseBody
	public BaseResponse<List<AttendanceDto>> queryAttendance(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String date,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String deptId,
			@RequestParam(required = false) String ossUserId,
			@RequestParam(required = false) String type
	) {
		logger.info("查询员工出勤记录开始，startDate: " + date + ", keyword: " + keyword + "，deptId：" + deptId);
		List<AttendanceDto> dtoList = new ArrayList<>();
		SearchFilter filter = new SearchFilter();

		if (StringUtil.isNotBlank(id)) {
			// 查指定id的记录
			filter.getRules().add(new SearchRule("id", Constants.ROP_EQ, id));
		} else if (StringUtil.isNotBlank(type) && "1".equals(type)) {
			// 查指定员工一整月的记录
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
			Date monthStart = DateUtil.convert(date, DateUtil.format4);
			Date monthEnd = DateUtil.getMonthEnd(monthStart);
			filter.getRules().add(new SearchRule("date", Constants.ROP_GE, monthStart));
			filter.getRules().add(new SearchRule("date", Constants.ROP_LE, monthEnd));
			filter.getOrders().add(new SearchOrder("date", Constants.ROP_ASC));
		} else {
			// 查一天的记录

			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 按权限查员工
			List<User> userList = userService.readUsers(onlineUser, null, null, deptId);
			if (CollectionUtils.isEmpty(userList)) {
				logger.info("用户数据权限下没有员工");
				return BaseResponse.success(dtoList);
			}
			// 只留下正常用户
			userList = userList.stream().filter(user -> user.getUstate() == UserStatus.ACTIVE.ordinal() && user.getStatus() == EntityStatus.NORMAL.ordinal())
					.collect(Collectors.toList());

			// 按 姓名，登录名，id，手机号 过滤
			if (StringUtils.isNotBlank(keyword)) {
				userList = userList.stream().filter(user -> {
					boolean match = false;
					if (user.getRealName() != null) {
						match = user.getRealName().contains(keyword);
					}
					if (!match && user.getLoginName() != null) {
						match = user.getLoginName().contains(keyword);
					}
					if (!match && user.getOssUserId() != null) {
						match = user.getOssUserId().contains(keyword);
					}
					if (!match && user.getContactMobile() != null) {
						match = user.getContactMobile().contains(keyword);
					}
					return match;
				}).collect(Collectors.toList());
			}
			// 搜索条件
			List<String> userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());
			// 00:00:00 <= <= 23:59:59
			Date startDate = DateUtil.getYesterdayStartDateTime();
			if (StringUtil.isNotBlank(date)) {
				startDate = DateUtil.convert(date, DateUtil.format1);
			}
			filter.getRules().add(new SearchRule("date", Constants.ROP_EQ, startDate));
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));

		}

		try {
			List<Attendance> attendanceList = attendanceService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(attendanceList)) {
				dtoList = buildDto(attendanceList);
				logger.info("查询到" + dtoList.size() + "条记录");
			} else {
				logger.info("未查询到员工特殊出勤报备记录");
			}
		} catch (ServiceException e) {
			logger.error("查询员工特殊出勤报备记录异常", e);
		}
		return BaseResponse.success(dtoList);
	}

	private List<AttendanceDto> buildDto(List<Attendance> attendanceList) {
		List<AttendanceDto> dtoList = new ArrayList<>();
		if (CollectionUtils.isEmpty(attendanceList)) {
			return dtoList;
		}
		// 查所有用户信息 {ossUserId -> {realName, deptName, parentDeptName}}
		HashMap<String, HashMap<String, String>> userDeptMap = userService.getUserAndDeptName();
		for (Attendance attendance : attendanceList) {
			AttendanceDto dto = new AttendanceDto();
			BeanUtils.copyProperties(attendance, dto);

			dto.setDate(DateUtil.convert(attendance.getDate(), DateUtil.format1));

			Map<String, String> userDeptInfo = userDeptMap.getOrDefault(attendance.getOssUserId(), new HashMap<>());
			dto.setDeptName(userDeptInfo.getOrDefault("deptName", "未知"));
			dto.setRealName(userDeptInfo.getOrDefault("realName", "未知"));

			if (attendance.isWork() || attendance.getWorkMins() > 0) {
				dto.setWork(1);
				dto.setWorkMins((attendance.getWorkMins() / 60) + "时" + (attendance.getWorkMins() % 60) + "分");
			}
			if (attendance.isLeave() || attendance.getLeaveMins() > 0) {
				dto.setLeave(1);
				dto.setLeaveMins((attendance.getLeaveMins() / 60) + "时" + (attendance.getLeaveMins() % 60) + "分");
			}
			if (attendance.isOvertime() || attendance.getOvertimeMins() > 0) {
				dto.setOvertime(1);
				dto.setOvertimeMins((attendance.getOvertimeMins() / 60) + "时" + (attendance.getOvertimeMins() % 60) + "分");
			}
			if (attendance.isOutside() || attendance.getOutsideMins() > 0) {
				dto.setOutside(attendance.getOutside());
				dto.setOutsideMins((attendance.getOutsideMins() / 60) + "时" + (attendance.getOutsideMins() % 60) + "分");
			}
			if (attendance.isBusinessTravel() || attendance.getBusinessTravelMins() > 0) {
				dto.setBusinessTravel(1);
				dto.setBusinessTravelMins((attendance.getBusinessTravelMins() / 60) + "时" + (attendance.getBusinessTravelMins() % 60) + "分");
			}
			if (attendance.isLate() || attendance.getLateMins() > 0) {
				dto.setLate(attendance.isLate() ? 1 : 0);
				dto.setLateMins((attendance.getLateMins() / 60) + "时" + (attendance.getLateMins() % 60) + "分");
			}
			if (attendance.isAbsenteeism() || attendance.getAbsenteeismMins() > 0) {
				dto.setAbsenteeism(1);
				dto.setAbsenteeismMins((attendance.getAbsenteeismMins() / 60) + "时" + (attendance.getAbsenteeismMins() % 60) + "分");
			}
			dtoList.add(dto);
		}
		return dtoList;
	}

	/**
	 * 查看一天的出勤记录的时间线
	 * @return
	 */
	@RequestMapping(value = "/toAttendanceTimeLine")
	public String toAttendanceTimeLine() {
		String id = request.getParameter("id");
		try {
			// 获取工作时间
			String workTime = null;
			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.WORK_TIME_KEY);
			if (parameter != null && parameter.getParamvalue() != null) {
				workTime = parameter.getParamvalue();
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
			String[] amStartHourMin = amWorkTime[0].split(":");
			String[] amEndHourMin = amWorkTime[1].split(":");
			String[] pmStartHourMin = pmWorkTime[0].split(":");
			String[] pmEndHourMin = pmWorkTime[1].split(":");

			JSONObject workTimeInfo = new JSONObject();
			workTimeInfo.put("amStartHour", amStartHourMin[0]);
			workTimeInfo.put("amStartMin", amStartHourMin[1]);
			workTimeInfo.put("amEndHour", amEndHourMin[0]);
			workTimeInfo.put("amEndMin", amEndHourMin[1]);
			workTimeInfo.put("pmStartHour", pmStartHourMin[0]);
			workTimeInfo.put("pmStartMin", pmStartHourMin[1]);
			workTimeInfo.put("pmEndHour", pmEndHourMin[0]);
			workTimeInfo.put("pmEndMin", pmEndHourMin[1]);
			request.setAttribute("workTimeInfo", workTimeInfo);

			Attendance attendance = attendanceService.read(id);

			JSONArray workInfos = new JSONArray();
			JSONArray leaveInfos = new JSONArray();
			JSONArray overtimeInfos = new JSONArray();
			JSONArray outsideInfos = new JSONArray();
			JSONArray businessTravelInfos = new JSONArray();
			JSONArray lateInfos = new JSONArray();
			JSONArray absenteeismInfos = new JSONArray();

			if (null != attendance) {
				String workInfoStr = attendance.getWorkInfo();
				if (StringUtil.isNotBlank(workInfoStr)) {
					JSONArray infos = JSON.parseArray(workInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						workInfos.add(item);
					}
				}

				String leaveInfoStr = attendance.getLeaveInfo();
				if (StringUtil.isNotBlank(leaveInfoStr)) {
					JSONArray infos = JSON.parseArray(leaveInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						leaveInfos.add(item);
					}
				}

				String overtimeInfoStr = attendance.getOvertimeInfo();
				if (StringUtil.isNotBlank(overtimeInfoStr)) {
					JSONArray infos = JSON.parseArray(overtimeInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						overtimeInfos.add(item);
					}
				}

				String outsideInfoStr = attendance.getOutsideInfo();
				if (StringUtil.isNotBlank(outsideInfoStr)) {
					JSONArray infos = JSON.parseArray(outsideInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						outsideInfos.add(item);
					}
				}

				String businessTravelInfoStr = attendance.getBusinessTravelInfo();
				if (StringUtil.isNotBlank(businessTravelInfoStr)) {
					JSONArray infos = JSON.parseArray(businessTravelInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						businessTravelInfos.add(item);
					}
				}

				String lateInfoStr = attendance.getLateInfo();
				if (StringUtil.isNotBlank(lateInfoStr)) {
					JSONArray infos = JSON.parseArray(lateInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						lateInfos.add(item);
					}
				}

				String absenteeismInfoStr = attendance.getAbsenteeismInfo();
				if (StringUtil.isNotBlank(absenteeismInfoStr)) {
					JSONArray infos = JSON.parseArray(absenteeismInfoStr);
					Calendar calendar = Calendar.getInstance();
					for (Object info : infos) {
						String startTimeStr = ((JSONObject) info).getString("startTime");
						Date startTime = DateUtil.convert(startTimeStr, DateUtil.format2);
						String endTimeStr =  ((JSONObject) info).getString("endTime");
						Date endTime = DateUtil.convert(endTimeStr, DateUtil.format2);
						calendar.setTime(startTime);
						JSONObject item = new JSONObject();
						item.put("info", startTimeStr + " ~ " + endTimeStr);
						item.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
						item.put("minute", calendar.get(Calendar.MINUTE));
						item.put("duration", DateUtil.getDiffmins(endTime, startTime));
						absenteeismInfos.add(item);
					}
				}
			}

			request.setAttribute("workInfos", workInfos);
			request.setAttribute("leaveInfos", leaveInfos);
			request.setAttribute("overtimeInfos", overtimeInfos);
			request.setAttribute("outsideInfos", outsideInfos);
			request.setAttribute("businessTravelInfos", businessTravelInfos);
			request.setAttribute("lateInfos", lateInfos);
			request.setAttribute("absenteeismInfos", absenteeismInfos);
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "/views/attendance/attendanceTimeLine";
	}

	@RequestMapping(value = "/toEditAttendance")
	public String toEditAttendance() {
		request.setAttribute("id", request.getParameter("id"));
		return "/views/attendance/editAttendance";
	}

	@RequestMapping(value = "/cleanLog")
	@ResponseBody
	public String cleanLog(@RequestParam String id, @RequestParam String type) {
		String msg;
		OnlineUser onlineUser = getOnlineUserAndOnther();
		logger.info("用户【" + onlineUser.getUser().getRealName() + "】清空出勤记录开始，type：" + type + "，id：" + id);
		try {
			Attendance attendance = attendanceService.read(id);
			if (null == attendance) {
				logger.info("出勤记录不存在，id：" + id);
				return "记录不存在";
			}
			String operation = null;
			if ("work".equals(type)) {
				attendance.cleanWork();
				operation = "清空工作信息";
			} else if ("leave".equals(type)) {
				attendance.cleanLeave();
				operation = "清空请假信息";
			} else if ("overtime".equals(type)) {
				attendance.cleanOvertime();
				operation = "清空加班信息";
			} else if ("outside".equals(type)) {
				attendance.cleanOutside();
				operation = "清空外勤信息";
			} else if ("businessTravel".equals(type)) {
				attendance.cleanBusinessTravel();
				operation = "清空出差信息";
			} else if ("late".equals(type)) {
				attendance.cleanLate();
				operation = "清空迟到信息";
			} else if ("absenteeism".equals(type)) {
				attendance.cleanAbsenteeism();
				operation = "清空旷工信息";
			}

			if (attendance.isLeave() || attendance.isOvertime() || attendance.isOutside() || attendance.isBusinessTravel()) {
				operation += "，出勤状况由【" + WorkStatus.getWorkStatus(attendance.getWorkStatus());
				attendance.setWorkStatus(WorkStatus.Special.ordinal());
				operation += "】变更为【" + WorkStatus.getWorkStatus(attendance.getWorkStatus()) + "】";
			} else if (attendance.isLate() || attendance.isAbsenteeism() || !attendance.isWork()) {
				operation += "，出勤状况由【" + WorkStatus.getWorkStatus(attendance.getWorkStatus());
				attendance.setWorkStatus(WorkStatus.Exceptional.ordinal());
				operation += "】变更为【" + WorkStatus.getWorkStatus(attendance.getWorkStatus()) + "】";
			} else {
				operation += "，出勤状况由【" + WorkStatus.getWorkStatus(attendance.getWorkStatus());
				attendance.setWorkStatus(WorkStatus.Normal.ordinal());
				operation += "】变更为【" + WorkStatus.getWorkStatus(attendance.getWorkStatus()) + "】";
			}

			// 保存修改记录
			JSONArray oldLog = null;
			String oldOperationLog = attendance.getOperationLog();
			if (StringUtil.isNotBlank(oldOperationLog)) {
				oldLog = JSON.parseArray(oldOperationLog);
			} else {
				oldLog = new JSONArray();
			}
			JSONObject log = new JSONObject();
			log.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
			log.put("ossUserId", onlineUser.getUser().getOssUserId());
			log.put("operator", onlineUser.getUser().getRealName());
			log.put("operation", operation);
			oldLog.add(log);
			attendance.setOperationLog(oldLog.toJSONString());
			boolean result = attendanceService.update(attendance);
			msg = "清空" + (result ? "成功" : "失败");
		} catch (ServiceException e) {
			msg = "清空时出现异常";
			logger.error(msg, e);
		}
		return msg;
	}

	@RequestMapping(value = "/addLog")
	@ResponseBody
	public String addLog(@RequestParam String id, @RequestParam String type, @RequestParam String time) {
		String msg;
		OnlineUser onlineUser = getOnlineUserAndOnther();
		logger.info("用户【" + onlineUser.getUser().getRealName() + "】添加出勤记录开始，type：" + type + "，id：" + id + "，time：" + time);
		try {
			Attendance attendance = attendanceService.read(id);
			if (null == attendance) {
				logger.info("出勤记录不存在，id：" + id);
				return "记录不存在";
			}
			String operation = null;
			JSONObject item = new JSONObject();
			String[] times = time.split("~");
			item.put("startTime", times[0].trim());
			item.put("endTime", times[1].trim());
			Date startTime = DateUtil.convert(times[0].trim(), DateUtil.format2);
			Date endTime = DateUtil.convert(times[1].trim(), DateUtil.format2);
			int mins = DateUtil.getDiffmins(endTime, startTime);
			item.put("mins", mins);
			String info = null;
			if ("work".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getWorkInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setWorkInfo(oldInfo.toJSONString());
				attendance.setWorkMins(attendance.getWorkMins() + mins);
				attendance.setWork(true);
				operation = "增加一条工作记录";
			} else if ("leave".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getLeaveInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setLeaveInfo(oldInfo.toJSONString());
				attendance.setLeaveMins(attendance.getLeaveMins() + mins);
				attendance.setLeave(true);
				operation = "增加一条请假记录";
			} else if ("overtime".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getOvertimeInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setOvertimeInfo(oldInfo.toJSONString());
				attendance.setOvertimeMins(attendance.getOvertimeMins() + mins);
				attendance.setOvertime(true);
				operation = "增加一条加班记录";
			} else if ("outside".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getOutsideInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setOutsideInfo(oldInfo.toJSONString());
				attendance.setOutsideMins(attendance.getOutsideMins() + mins);
				attendance.setOutside(attendance.getOutside() + 1);
				operation = "增加一条外勤记录";
			} else if ("businessTravel".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getBusinessTravelInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setBusinessTravelInfo(oldInfo.toJSONString());
				attendance.setBusinessTravelMins(attendance.getBusinessTravelMins() + mins);
				attendance.setBusinessTravel(true);
				operation = "增加一条出差记录";
			} else if ("late".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getLateInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setLateInfo(oldInfo.toJSONString());
				attendance.setLateMins(attendance.getLateMins() + mins);
				attendance.setLate(attendance.getLateMins() > Constants.DEFAULT_LATE_MINUTES);
				operation = "增加一条迟到记录";
			} else if ("absenteeism".equals(type)) {
				// 保存添加的记录
				JSONArray oldInfo = null;
				info = attendance.getAbsenteeismInfo();
				if (StringUtil.isNotBlank(info)) {
					oldInfo = JSON.parseArray(info);
				} else {
					oldInfo = new JSONArray();
				}
				oldInfo.add(item);
				attendance.setAbsenteeismInfo(oldInfo.toJSONString());
				attendance.setAbsenteeismMins(attendance.getAbsenteeismMins() + mins);
				attendance.setAbsenteeism(true);
				operation = "增加一条旷工记录";
			}

			// 重新判断
			int beforeWorkStatus = attendance.getWorkStatus();
			int afterWorkStatus;
			if (attendance.isLeave() || attendance.isOvertime() || attendance.isOutside() || attendance.isBusinessTravel()) {
				afterWorkStatus = WorkStatus.Special.ordinal();
			} else if (attendance.isLate() || attendance.isAbsenteeism() || !attendance.isWork()) {
				afterWorkStatus = WorkStatus.Exceptional.ordinal();
			} else {
				afterWorkStatus = WorkStatus.Normal.ordinal();
			}
			if (beforeWorkStatus != afterWorkStatus) {
				attendance.setWorkStatus(afterWorkStatus);
				operation += "，出勤状况由【" + WorkStatus.getWorkStatus(beforeWorkStatus) + "】变更为【" + WorkStatus.getWorkStatus(afterWorkStatus) + "】";
			}

			// 保存修改记录
			JSONArray oldLog = null;
			String oldOperationLog = attendance.getOperationLog();
			if (StringUtil.isNotBlank(oldOperationLog)) {
				oldLog = JSON.parseArray(oldOperationLog);
			} else {
				oldLog = new JSONArray();
			}
			JSONObject log = new JSONObject();
			log.put("wtime", DateUtil.convert(new Date(), DateUtil.format2));
			log.put("ossUserId", onlineUser.getUser().getOssUserId());
			log.put("operator", onlineUser.getUser().getRealName());
			log.put("operation", operation);
			oldLog.add(log);
			attendance.setOperationLog(oldLog.toJSONString());
			boolean result = attendanceService.update(attendance);
			msg = "增加" + (result ? "成功" : "失败");
		} catch (ServiceException e) {
			msg = "增加时出现异常";
			logger.error(msg, e);
		}
		return msg;
	}

	@RequestMapping(value = "/toMonthAttendance")
	public String toMonthAttendance() {
		return "/views/attendance/monthAttendance";
	}

	@RequestMapping("/queryMonthAttendance")
	@ResponseBody
	public BaseResponse<List<MonthAttendanceDto>> queryMonthAttendance(
			@RequestParam(required = false) String month,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String deptId
	) {
		logger.info("查询员工月出勤记录开始，月份: " + month + ", keyword: " + keyword + "，deptId：" + deptId);
		List<MonthAttendanceDto> dtoList = new ArrayList<>();
		SearchFilter filter = new SearchFilter();

			// 查一天的记录

			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 按权限查员工
			List<User> userList = userService.readUsers(onlineUser, null, null, deptId);
			if (CollectionUtils.isEmpty(userList)) {
				logger.info("用户数据权限下没有员工");
				return BaseResponse.success(dtoList);
			}
			// 只留下正常用户
			userList = userList.stream().filter(user -> user.getUstate() == UserStatus.ACTIVE.ordinal() && user.getStatus() == EntityStatus.NORMAL.ordinal())
					.collect(Collectors.toList());

			// 按 姓名，登录名，id，手机号 过滤
			if (StringUtils.isNotBlank(keyword)) {
				userList = userList.stream().filter(user -> {
					boolean match = false;
					if (user.getRealName() != null) {
						match = user.getRealName().contains(keyword);
					}
					if (!match && user.getLoginName() != null) {
						match = user.getLoginName().contains(keyword);
					}
					if (!match && user.getOssUserId() != null) {
						match = user.getOssUserId().contains(keyword);
					}
					if (!match && user.getContactMobile() != null) {
						match = user.getContactMobile().contains(keyword);
					}
					return match;
				}).collect(Collectors.toList());
			}
			// 搜索条件
			List<String> userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());
			// 月初 ~ 月末
			Date startDate = DateUtil.getLastMonthFirst();
			Date endDate = DateUtil.getLastMonthFinal();
			if (StringUtil.isNotBlank(month)) {
				startDate = DateUtil.convert(month, DateUtil.format4);
				endDate = DateUtil.getMonthFinal(startDate);
			}
			filter.getRules().add(new SearchRule("date", Constants.ROP_GE, startDate));
			filter.getRules().add(new SearchRule("date", Constants.ROP_LE, endDate));
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));


		try {
			List<Attendance> attendanceList = attendanceService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(attendanceList)) {
				dtoList = buildMonthDto(startDate, attendanceList);
				logger.info("查询到" + dtoList.size() + "条记录");
			} else {
				logger.info("未查询到员工特殊出勤报备记录");
			}
		} catch (ServiceException e) {
			logger.error("查询员工特殊出勤报备记录异常", e);
		}
		return BaseResponse.success(dtoList);
	}

	private List<MonthAttendanceDto> buildMonthDto(Date month, List<Attendance> attendanceList) {
		List<MonthAttendanceDto> dtoList = new ArrayList<>();
		if (CollectionUtils.isEmpty(attendanceList)) {
			return dtoList;
		}

		Map<String, List<Attendance>> userAttendanceMap = attendanceList.stream().collect(Collectors.groupingBy(Attendance::getOssUserId));

		// 查所有用户信息 {ossUserId -> {realName, deptName, parentDeptName}}
		HashMap<String, HashMap<String, String>> userDeptMap = userService.getUserAndDeptName();

		for (Map.Entry<String, List<Attendance>> entry : userAttendanceMap.entrySet()) {
			MonthAttendanceDto dto = new MonthAttendanceDto();
			dto.setMonth(DateUtil.convert(month, DateUtil.format4));
			dto.setDefaultWorkDays(new BigDecimal(DateUtil.getMonthDays(month)));
			Map<String, String> userDeptInfo = userDeptMap.getOrDefault(entry.getKey(), new HashMap<>());
			dto.setDeptName(userDeptInfo.getOrDefault("deptName", "未知"));
			dto.setRealName(userDeptInfo.getOrDefault("realName", "未知"));

			for (Attendance attendance : entry.getValue()) {
				if (WorkStatus.Normal.ordinal() == attendance.getWorkStatus()) {
					// 正常
					dto.setNormalAttendanceDays(dto.getNormalAttendanceDays().add(BigDecimal.ONE));
				} else if (WorkStatus.Special.ordinal() == attendance.getWorkStatus()) {
					// 特殊：请假、加班、外勤、出差
					dto.setSpecialAttendanceDays(dto.getSpecialAttendanceDays().add(BigDecimal.ONE));
					// 请假
					if (attendance.isLeave()) {
						// 请假情况
						JSONArray leaveInfos = JSON.parseArray(attendance.getLeaveInfo());
						BigDecimal leaveDays = new BigDecimal(attendance.getLeaveMins()).divide(new BigDecimal(480), 2, BigDecimal.ROUND_HALF_UP);
						dto.setLeaveDays(dto.getLeaveDays().add(leaveDays));
						Map<Integer, BigDecimal> leaveMap = dto.getLeaveDaysDetail() == null ? new HashMap<>() : dto.getLeaveDaysDetail();
						// 每一种假期类型
						for (Object leaveInfo : leaveInfos) {
							int leaveType = ((JSONObject) leaveInfo).getIntValue("leaveType");
							BigDecimal typeDays = leaveMap.getOrDefault(leaveType, new BigDecimal(0));
							leaveMap.put(leaveType, typeDays.add(leaveDays));
						}
						dto.setLeaveDaysDetail(leaveMap);
					}
					// 加班
					if (attendance.isOvertime()) {
						BigDecimal overtimeDays = new BigDecimal(attendance.getLeaveMins()).divide(new BigDecimal(480), 2, BigDecimal.ROUND_HALF_UP);
						dto.setOvertimeDays(dto.getOvertimeDays().add(overtimeDays));
					}
					// 外勤
					if (attendance.isOutside()) {
						BigDecimal outsideDays = new BigDecimal(attendance.getOutsideMins()).divide(new BigDecimal(480), 2, BigDecimal.ROUND_HALF_UP);
						dto.setOutsideDays(dto.getOutsideDays().add(outsideDays));
					}
					// 出差
					if (attendance.isBusinessTravel()) {
						BigDecimal businessTravelDays = new BigDecimal(attendance.getBusinessTravelMins()).divide(new BigDecimal(480), 2, BigDecimal.ROUND_HALF_UP);
						dto.setBusinessTravelDays(dto.getBusinessTravelDays().add(businessTravelDays));
					}
				} if (WorkStatus.Exceptional.ordinal() == attendance.getWorkStatus()) {
					// 异常：迟到、旷工
					dto.setExceptionalAttendanceDays(dto.getExceptionalAttendanceDays().add(BigDecimal.ONE));
					int mins = attendance.getLateMins();
					if (mins > 0) {
						BigDecimal lateMins = new BigDecimal(attendance.getLateMins());
						dto.setLateMins(dto.getLateMins().add(lateMins));
					}
					if (attendance.isAbsenteeism()) {
						BigDecimal absenteeismMins = new BigDecimal(attendance.getAbsenteeismMins());
						dto.setAbsenteeismMins(dto.getAbsenteeismMins().add(absenteeismMins));

						BigDecimal absenteeismDays = absenteeismMins.divide(new BigDecimal(480), 2, BigDecimal.ROUND_HALF_UP);
						dto.setAbsenteeismDsys(dto.getAbsenteeismDsys().add(absenteeismDays));
					}
				} if (WorkStatus.Unknown.ordinal() == attendance.getWorkStatus()) {
					// 未知
					dto.setUnknownAttendanceDays(dto.getUnknownAttendanceDays().add(BigDecimal.ONE));
				}
			}
			dto.setString();
			dtoList.add(dto);
		}

		return dtoList;
	}

}
