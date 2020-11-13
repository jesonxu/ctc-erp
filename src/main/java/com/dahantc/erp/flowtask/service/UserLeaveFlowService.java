package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.SpecialAttendanceType;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;

@Service("userLeaveFlowService")
public class UserLeaveFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(UserLeaveFlowService.class);

	private static final String FLOW_CLASS = Constants.USER_LEAVE_FLOW_CLASS;
	private static final String FLOW_NAME = Constants.USER_LEAVE_FLOW_NAME;

	@Autowired
	private IUserLeaveService userLeaveService;

	@Autowired
	private ISpecialAttendanceRecordService specialAttendanceRecordService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IParameterService parameterService;

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
	}

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal) {
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		String msg = "";
		User user = null;
		try {
			user = userService.read(flowEnt.getOssUserId());
		} catch (ServiceException e) {
			logger.error("查询流程发起人异常", e);
		}
		if (null == user) {
			msg = "流程发起人不能为空";
			logger.info(msg);
			return msg;
		}
		JSONObject flowMsgJson = JSONObject.parseObject(labelJsonVal);
		String leaveTypeStr = flowMsgJson.getString(Constants.USER_LEAVE_TYPE_KEY);
		if (StringUtil.isBlank(leaveTypeStr)) {
			msg = "请假类别不能为空";
			logger.info(msg);
			return msg;
		}
		Optional<LeaveType> leaveTypeOpt = LeaveType.getEnumsByMsg(leaveTypeStr);
		if (!leaveTypeOpt.isPresent()) {
			msg = "请假类型不正确：" + leaveTypeStr;
			logger.info(msg);
			return msg;
		}
		String leaveTimeFrom = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_FROM);
		String leaveTimeTo = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_TO);
		if (StringUtil.isBlank(leaveTimeFrom)) {
			msg = "请假开始时间不能为空";
			logger.info(msg);
		}
		if (StringUtil.isBlank(leaveTimeTo)) {
			msg = "请假结束时间不能为空";
			logger.info(msg);
		}
		String[] leaveTimes = null;
		BigDecimal days = null;
		// 2020/11/11-上午
//		if (leaveTimeFrom.contains("{")) {
//			JSONObject leaveInfo = JSON.parseObject(leaveTimeFrom);
//			leaveTimes = leaveInfo.getString("datetime").split(" - ");
//			//days = new BigDecimal(leaveInfo.getString("days")).setScale(2, BigDecimal.ROUND_HALF_UP);
//		} else {
//			leaveTimes = leaveTimeFrom.split(" - ");
//		}
//
//		//结束时间
//		if (leaveTimeTo.contains("{")) {
//			JSONObject leaveInfo = JSON.parseObject(leaveTimeTo);
//			leaveTimes = leaveInfo.getString("datetime").split(" - ");
//			//days = new BigDecimal(leaveInfo.getString("days")).setScale(2, BigDecimal.ROUND_HALF_UP);
//		} else {
//			leaveTimes = leaveTimeFrom.split(" - ");
//		}
//		Date leaveTimeStart = DateUtil.convert(leaveTimes[0], DateUtil.format2);
////		Date leaveTimeEnd = DateUtil.convert(leaveTimes[1], DateUtil.format2);
		Date leaveTimeStart = getDateFormate(leaveTimeFrom,true);
		Date leaveTimeEnd = getDateFormate(leaveTimeTo,false);
		LeaveType leaveType = leaveTypeOpt.get();
		msg = userLeaveService.checkUserLeave(leaveType, user, leaveTimeStart, leaveTimeEnd, flowEnt.getWtime(), days, flowEnt.getId());

		return msg;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		// TODO 根据请假日期更新考勤记录
		return true;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		flowMsgModify(auditResult, flowEnt, null);
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		String flowMsg = flowEnt.getFlowMsg();
		SpecialAttendanceRecord leaveLog = null;
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
		try {
			List<SpecialAttendanceRecord> leaveLogList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
			// 流程关联的请假记录
			if (!CollectionUtils.isEmpty(leaveLogList)) {
				leaveLog = leaveLogList.get(0);
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		if (AuditResult.CREATED.getCode() == auditResult) {
			// 创建
			if (null == leaveLog) {
				buildLogAndUpdateLeave(flowEnt);
			}
		} else if (AuditResult.CANCLE.getCode() == auditResult || AuditResult.REJECTED.getCode() == auditResult) {
			// 取消、驳回至发起人
			if (null != leaveLog) {
				updateLogAndRestoreLeave(leaveLog);
			}
		} else if (AuditResult.PASS.getCode() == auditResult) {
			// 通过、驳回至非发起人节点
			if (null != leaveLog) {
				updateLogAndUpdateLeave(leaveLog, flowMsg);
			}
		}
	}

	private SpecialAttendanceRecord buildLogAndUpdateLeave(FlowEnt flowEnt) {
		SpecialAttendanceRecord leaveLog = null;
		boolean result = false;
		try {
			User user = userService.read(flowEnt.getOssUserId());

			JSONObject flowMsgJson = JSONObject.parseObject(flowEnt.getFlowMsg());
			String leaveTypeStr = flowMsgJson.getString(Constants.USER_LEAVE_TYPE_KEY);
			Optional<LeaveType> leaveTypeOpt = LeaveType.getEnumsByMsg(leaveTypeStr);
			String leaveTimeFrom = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_FROM);
			String leaveTimeTo = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_TO);
			Date leaveTimeStart = getDateFormate(leaveTimeFrom,true);
			Date leaveTimeEnd = getDateFormate(leaveTimeTo,false);
			LeaveType leaveType = leaveTypeOpt.get();
			// TODO 根据时间段上午/下午获取请假天数
			BigDecimal days = userLeaveService.getLeaveDaysByTimeShot(leaveTimeStart, leaveTimeEnd);

			leaveLog = new SpecialAttendanceRecord();
			leaveLog.setSpecialAttendanceType(SpecialAttendanceType.Leave.ordinal());
			leaveLog.setOssUserId(user.getOssUserId());
			leaveLog.setDeptId(user.getDeptId());
			//TODO
			leaveLog.setStartTime(leaveTimeStart);
			leaveLog.setEndTime(leaveTimeEnd);
			leaveLog.setDays(days);
			leaveLog.setFlowEntId(flowEnt.getId());
			leaveLog.setLeaveType(leaveType.getCode());
			leaveLog.setWtime(flowEnt.getWtime());

			Map<String, BigDecimal> leaveDaysMap = null;
			// 年假、调休，要从假期记录扣减天数；其他请假，只生成请假记录，不生成假期记录
			if (leaveType == LeaveType.ANNUAL_LEAVE) {
				leaveDaysMap = getAnnualLeave(user.getOssUserId(), leaveTimeStart, leaveTimeEnd, flowEnt.getWtime(), days);
			} else if (leaveType == LeaveType.COMPENSATORY_LEAVE) {
				leaveDaysMap = getCompensatoryLeave(user.getOssUserId(), leaveTimeStart, leaveTimeEnd, flowEnt.getWtime(), days);
			}
			if (leaveDaysMap != null && !leaveDaysMap.isEmpty()) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
				List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
				Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
				JSONArray leaveInfoArray = new JSONArray();
				List<UserLeave> updateList = new ArrayList<>();
				for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
					UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
					if (leave != null) {
						leave.setUsedDays(leave.getUsedDays().add(leaveDay.getValue()));
						leave.setLeftDays(leave.getLeftDays().subtract(leaveDay.getValue()));
						updateList.add(leave);
					}
					JSONObject item = new JSONObject();
					item.put("userLeaveId", leaveDay.getKey());
					item.put("thisUsedDays", leaveDay.getValue().toPlainString());
					leaveInfoArray.add(item);
				}
				leaveLog.setLeaveInfo(leaveInfoArray.toJSONString());
				// 更新假期剩余天数
				if (!CollectionUtils.isEmpty(updateList)) {
					result = userLeaveService.updateByBatch(updateList);
					logger.info("扣减操作更新假期数据" + (result ? "成功" : "失败"));
				}
			}
			result = specialAttendanceRecordService.save(leaveLog);
			logger.info("保存请假记录" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return leaveLog;
	}

	private void updateLogAndRestoreLeave(SpecialAttendanceRecord leaveLog) {
		boolean result;
		try {
			leaveLog.setValid(EntityStatus.DELETED.ordinal());
			String leaveInfo = leaveLog.getLeaveInfo();
			JSONArray leaveInfoArray = JSON.parseArray(leaveInfo);
			if (leaveInfoArray != null && !leaveInfoArray.isEmpty()) {
				Map<String, BigDecimal> leaveDaysMap = new HashMap<>();
				for (Object leaveDay : leaveInfoArray) {
					leaveDaysMap.put(((JSONObject) leaveDay).getString("userLeaveId"), new BigDecimal(((JSONObject) leaveDay).getString("thisUsedDays")));
				}
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
				List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
				Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
				List<UserLeave> updateList = new ArrayList<>();
				for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
					UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
					if (leave != null) {
						leave.setUsedDays(leave.getUsedDays().subtract(leaveDay.getValue()));
						leave.setLeftDays(leave.getLeftDays().add(leaveDay.getValue()));
						updateList.add(leave);
					}
				}
				// 更新假期剩余天数
				if (!CollectionUtils.isEmpty(updateList)) {
					result = userLeaveService.updateByBatch(updateList);
					logger.info("还原操作更新假期数据" + (result ? "成功" : "失败"));
				}
			}
			result = specialAttendanceRecordService.update(leaveLog);
			logger.info("更新请假记录" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}

	private void updateLogAndUpdateLeave(SpecialAttendanceRecord leaveLog, String flowMsg) {
		boolean result;
		try {
			// 重新扣减假期前，是否需要还原假期
			boolean needRestore = true;
			// 是否需要扣减假期
			boolean needUpdate = false;
			// 请假记录当前状态是无效，说明是重新发起操作。
			if (EntityStatus.DELETED.ordinal() == leaveLog.getValid()) {
				// 由于驳回至发起人时，会还原假期，因此在重新通过时，就不需要再还原，但要进行扣除
				needRestore = false;
				needUpdate = true;
			}
			//leaveLog.setValid(EntityStatus.NORMAL.ordinal());
			// 获取节点审核后的请假信息
			JSONObject flowMsgJson = JSONObject.parseObject(flowMsg);
			String leaveTypeStr = flowMsgJson.getString(Constants.USER_LEAVE_TYPE_KEY);
			Optional<LeaveType> leaveTypeOpt = LeaveType.getEnumsByMsg(leaveTypeStr);
			String leaveTimeFrom = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_FROM);
			String leaveTimeTo = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_TO);
			String[] leaveTimes = null;
			Date leaveTimeStart = getDateFormate(leaveTimeFrom,true);
			Date leaveTimeEnd = getDateFormate(leaveTimeTo,false);
			LeaveType leaveType = leaveTypeOpt.get();
			// 请假天数
			BigDecimal days = userLeaveService.getLeaveDaysByTimeShot(leaveTimeStart, leaveTimeEnd);

			// 流程信息与请假记录进行比较
			if (days.compareTo(leaveLog.getDays()) != 0) {
				needUpdate = true;
				leaveLog.setDays(days);
			}
			if (leaveTimeStart.getTime() != leaveLog.getStartTime().getTime() || leaveTimeEnd.getTime() != leaveLog.getEndTime().getTime()) {
				needUpdate = true;
				leaveLog.setStartTime(new Timestamp(leaveTimeStart.getTime()));
				leaveLog.setEndTime(new Timestamp(leaveTimeEnd.getTime()));
			}
			if (leaveType.getCode() != leaveLog.getLeaveType()) {
				needUpdate = true;
				leaveLog.setLeaveType(leaveType.getCode());
			}

			// 判断需要更新请假记录，先还原假期，再重新扣减假期
			if (needUpdate) {
				// 还原假期
				if (needRestore) {
					String leaveInfo = leaveLog.getLeaveInfo();
					JSONArray leaveInfoArray = JSON.parseArray(leaveInfo);
					if (leaveInfoArray != null && !leaveInfoArray.isEmpty()) {
						Map<String, BigDecimal> leaveDaysMap = new HashMap<>();
						for (Object leaveDay : leaveInfoArray) {
							leaveDaysMap.put(((JSONObject) leaveDay).getString("userLeaveId"),
									new BigDecimal(((JSONObject) leaveDay).getString("thisUsedDays")));
						}
						SearchFilter filter = new SearchFilter();
						filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
						List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
						Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
						List<UserLeave> updateList = new ArrayList<>();
						for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
							UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
							if (leave != null) {
								leave.setUsedDays(leave.getUsedDays().subtract(leaveDay.getValue()));
								leave.setLeftDays(leave.getLeftDays().add(leaveDay.getValue()));
								updateList.add(leave);
							}
						}
						// 更新假期剩余天数
						if (!CollectionUtils.isEmpty(updateList)) {
							result = userLeaveService.updateByBatch(updateList);
							logger.info("还原操作更新假期数据" + (result ? "成功" : "失败"));
						}
					}
				}
				// 扣减假期
				Map<String, BigDecimal> leaveDaysMap = null;
				// 年假、调休，要从假期记录扣减天数；其他请假，只生成请假记录，不生成假期记录
				if (leaveType == LeaveType.ANNUAL_LEAVE) {
					leaveDaysMap = getAnnualLeave(leaveLog.getOssUserId(), leaveTimeStart, leaveTimeEnd, leaveLog.getWtime(), days);
				} else if (leaveType == LeaveType.COMPENSATORY_LEAVE) {
					leaveDaysMap = getCompensatoryLeave(leaveLog.getOssUserId(), leaveTimeStart, leaveTimeEnd, leaveLog.getWtime(), days);
				}
				if (leaveDaysMap != null && !leaveDaysMap.isEmpty()) {
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
					List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
					Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
					JSONArray leaveInfoArray = new JSONArray();
					List<UserLeave> updateList = new ArrayList<>();
					for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
						UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
						if (leave != null) {
							leave.setUsedDays(leave.getUsedDays().add(leaveDay.getValue()));
							leave.setLeftDays(leave.getLeftDays().subtract(leaveDay.getValue()));
							updateList.add(leave);
						}
						JSONObject item = new JSONObject();
						item.put("userLeaveId", leaveDay.getKey());
						item.put("thisUsedDays", leaveDay.getValue().toPlainString());
						leaveInfoArray.add(item);
					}
					leaveLog.setLeaveInfo(leaveInfoArray.toJSONString());
					// 更新假期剩余天数
					if (!CollectionUtils.isEmpty(updateList)) {
						result = userLeaveService.updateByBatch(updateList);
						logger.info("扣减操作更新假期数据" + (result ? "成功" : "失败"));
					}
				}
			}
			result = specialAttendanceRecordService.update(leaveLog);
			logger.info("更新请假记录" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}

	/**
	 * 获取年假
	 *
	 * @param ossUserId
	 *            员工
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 * @param applyTime
	 *            申请时间
	 * @param days
	 *            前端计算的天数
	 * @return
	 */
	private Map<String, BigDecimal> getAnnualLeave(String ossUserId, Date startTime, Date endTime, Date applyTime, BigDecimal days) {
		String msg = "";
		Date year = DateUtil.getYearFirst(startTime);
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
		filter.getRules().add(new SearchRule("leftDays", Constants.ROP_GE, BigDecimal.ZERO));
		filter.getOrRules().add(new SearchRule[] {
				new SearchRule("validEndDate", Constants.ROP_GE, endTime),
				new SearchRule("validEndDate", Constants.ROP_GE, startTime),
				new SearchRule("validEndDate", Constants.ROP_GE, applyTime)
		});
		filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, LeaveType.ANNUAL_LEAVE.getCode()));
		filter.getOrders().add(new SearchOrder("year", Constants.ROP_ASC));
		// { 年假id -> 本次使用天数 }
		Map<String, BigDecimal> leaveDayMap = new HashMap<>();
		try {
			List<UserLeave> leaveList = userLeaveService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(leaveList)) {
				msg = "员工" + DateUtil.convert(year, DateUtil.format1) + "没有可用年假";
				logger.info(msg);
				return null;
			}
			BigDecimal leftDays = new BigDecimal(0);
			for (UserLeave userLeave : leaveList) {
				leftDays = leftDays.add(userLeave.getLeftDays());
				if (leftDays.compareTo(days) <= 0) {
					leaveDayMap.put(userLeave.getId(), userLeave.getLeftDays());
				} else {
					leaveDayMap.put(userLeave.getId(), userLeave.getLeftDays().subtract(leftDays.subtract(days)));
					break;
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return leaveDayMap;
	}

	/**
	 * 获取调休
	 *
	 * @param ossUserId
	 *            员工
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 * @param applyTime
	 *            申请时间
	 * @param days
	 *            前端计算的天数
	 * @return
	 */
	private Map<String, BigDecimal> getCompensatoryLeave(String ossUserId, Date startTime, Date endTime, Date applyTime, BigDecimal days) {
		String msg = "";
		Date year = DateUtil.getYearFirst(startTime);
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
		filter.getRules().add(new SearchRule("leftDays", Constants.ROP_GE, BigDecimal.ZERO));
		filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, LeaveType.COMPENSATORY_LEAVE.getCode()));
		filter.getOrders().add(new SearchOrder("year", Constants.ROP_ASC));
		// { 年假id -> 本次使用天数 }
		Map<String, BigDecimal> leaveDayMap = new HashMap<>();
		try {
			List<UserLeave> leaveList = userLeaveService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(leaveList)) {
				msg = "员工" + DateUtil.convert(year, DateUtil.format1) + "没有可用年假";
				logger.info(msg);
				return null;
			}
			BigDecimal leftDays = new BigDecimal(0);
			for (UserLeave userLeave : leaveList) {
				leftDays = leftDays.add(userLeave.getLeftDays());
				if (leftDays.compareTo(days) <= 0) {
					leaveDayMap.put(userLeave.getId(), userLeave.getLeftDays());
				} else {
					leaveDayMap.put(userLeave.getId(), userLeave.getLeftDays().subtract(leftDays.subtract(days)));
					break;
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return leaveDayMap;
	}

	/**
	 * 根据 2020/11/11 上午 获取时间
	 * @param time 时间
	 * @param isStart 是否开始日期
	 * @return
	 */
	private Date getDateFormate(String time,boolean isStart){
		//获取工作时间
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
		// 处理中文字符
		Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher mat = pat.matcher(time);
		String newTime = mat.replaceAll("");
		String dateStr = DateUtil.convert(DateUtil.convert1(newTime) , DateUtil.format1);

		// 上午开始
		String dateTimeStrMB = dateStr + " " + amWorkTime[0] + ":00";
		// 上午结束
		String dateTimeStrME = dateStr + " " + amWorkTime[1] + ":00";
		// 下午开始
		String dateTimeStrAB = dateStr + " " + pmWorkTime[0] + ":00";
		// 下午结束
		String dateTimeStrAE = dateStr + " " + pmWorkTime[1] + ":00";

		if(time != null && time.contains("上午")){
			//time = time.replace("上午","00:00:00");
			if(isStart){
				time = dateTimeStrMB;
			}else{
				//结束时间为  ...上午
				time = dateTimeStrME;
			}

		}else if(time != null && time.contains("下午")){
			if(isStart){
				time = dateTimeStrAB;
			}else{
				time = dateTimeStrAE;
			}
		}
		String[] leaveTimes = null;
		Date leaveTimeStart = null;
		if (time.contains("{")) {
			JSONObject leaveInfo = JSON.parseObject(time);
			leaveTimes = leaveInfo.getString("datetime").split(" - ");
			leaveTimeStart = DateUtil.convert(leaveTimes[0], DateUtil.format2);
		}
		else {
			leaveTimeStart = DateUtil.convert(time, DateUtil.format2);
		}
		return leaveTimeStart;
	}
}
