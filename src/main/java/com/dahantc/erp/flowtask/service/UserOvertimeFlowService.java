package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.SpecialAttendanceType;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;

@Service("userOvertimeFlowService")
public class UserOvertimeFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(UserOvertimeFlowService.class);

	private static final String FLOW_CLASS = Constants.USER_OVERTIME_FLOW_CLASS;
	private static final String FLOW_NAME = Constants.USER_OVERTIME_FLOW_NAME;

	@Autowired
	private IUserLeaveService userLeaveService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISpecialAttendanceRecordService specialAttendanceRecordService;

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
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		// TODO 根据请假日期更新考勤记录
		SpecialAttendanceRecord overtimeLog = null;
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
		try {
			List<SpecialAttendanceRecord> overtimeLogList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
			// 流程关联的加班记录
			if (!CollectionUtils.isEmpty(overtimeLogList)) {
				overtimeLog = overtimeLogList.get(0);
				String leaveInfo = overtimeLog.getLeaveInfo();
				JSONArray leaveInfoArray = JSON.parseArray(leaveInfo);
				if (leaveInfoArray != null && !leaveInfoArray.isEmpty()) {
					Map<String, BigDecimal> leaveDaysMap = new HashMap<>();
					for (Object leaveDay : leaveInfoArray) {
						leaveDaysMap.put(((JSONObject) leaveDay).getString("userLeaveId"), new BigDecimal(((JSONObject) leaveDay).getString("thisWorkDays")));
					}
					filter = new SearchFilter();
					filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
					List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
					Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
					List<UserLeave> updateList = new ArrayList<>();
					String dateStr = DateUtil.convert(flowEnt.getWtime(), DateUtil.format1);
					User user = userService.read(flowEnt.getOssUserId());
					// 更新备注
					for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
						UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
						if (leave != null) {
							String remark = "[" + dateStr + "]员工【" + user.getRealName() + "】发起加班流程，增加调休天数：" + leaveDay.getValue().toPlainString() + "，加班日期：" + DateUtil.convert(overtimeLog.getStartTime(), DateUtil.format2) + " ~ " + DateUtil.convert(overtimeLog.getEndTime(), DateUtil.format2);
							leave.setRemark(StringUtil.isBlank(leave.getRemark()) ? remark : leave.getRemark() + "\n" + remark);
							updateList.add(leave);
						}
					}
					// 更新假期剩余天数
					if (!CollectionUtils.isEmpty(updateList)) {
						boolean result = userLeaveService.updateByBatch(updateList);
						logger.info("归档操作更新调休数据" + (result ? "成功" : "失败"));
					}
				}
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return true;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		flowMsgModify(auditResult, flowEnt, null);
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		String flowMsg = flowEnt.getFlowMsg();
		SpecialAttendanceRecord overtimeLog = null;
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
		try {
			List<SpecialAttendanceRecord> overtimeLogList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
			// 流程关联的加班记录
			if (!CollectionUtils.isEmpty(overtimeLogList)) {
				overtimeLog = overtimeLogList.get(0);
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		if (AuditResult.CREATED.getCode() == auditResult) {
			// 创建
			if (null == overtimeLog) {
				buildLogAndUpdateLeave(flowEnt);
			}
		} else if (AuditResult.CANCLE.getCode() == auditResult || AuditResult.REJECTED.getCode() == auditResult) {
			// 取消、驳回至发起人
			if (null != overtimeLog) {
				updateLogAndRestoreLeave(overtimeLog);
			}
		} else if (AuditResult.PASS.getCode() == auditResult) {
			// 通过、驳回至非发起人节点
			if (null != overtimeLog) {
				updateLogAndUpdateLeave(overtimeLog, flowMsg);
			}
		}
	}

	private SpecialAttendanceRecord buildLogAndUpdateLeave(FlowEnt flowEnt) {
		SpecialAttendanceRecord workLog = null;
		boolean result = false;
		try {
			User user = userService.read(flowEnt.getOssUserId());

			JSONObject flowMsgJson = JSONObject.parseObject(flowEnt.getFlowMsg());
			String workTime = flowMsgJson.getString(Constants.USER_OVER_TIME_KEY);
			String[] workTimes = null;
			if (workTime.contains("{")) {
				JSONObject leaveInfo = JSON.parseObject(workTime);
				workTimes = leaveInfo.getString("datetime").split(" - ");
			} else {
				workTimes = workTime.split(" - ");
			}
			Date workTimeStart = DateUtil.convert(workTimes[0], DateUtil.format2);
			Date workTimeEnd = DateUtil.convert(workTimes[1], DateUtil.format2);
			// 加班天数
			BigDecimal days = userLeaveService.getLeaveDays(workTimeStart, workTimeEnd);

			workLog = new SpecialAttendanceRecord();
			workLog.setSpecialAttendanceType(SpecialAttendanceType.Overtime.ordinal());
			workLog.setOssUserId(user.getOssUserId());
			workLog.setDeptId(user.getDeptId());
			workLog.setStartTime(new Timestamp(workTimeStart.getTime()));
			workLog.setEndTime(new Timestamp(workTimeEnd.getTime()));
			workLog.setDays(days);
			workLog.setFlowEntId(flowEnt.getId());
			workLog.setLeaveType(99);
			workLog.setWtime(flowEnt.getWtime());
			// 找调休
			Date year = DateUtil.getYearFirst(new Date(flowEnt.getWtime().getTime()));
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, flowEnt.getOssUserId()));
			filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, LeaveType.COMPENSATORY_LEAVE.getCode()));
			List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
			UserLeave leave = null;
			if (CollectionUtils.isEmpty(userLeaveList)) {
				leave = new UserLeave();
				leave.setYear(year);
				leave.setLeaveType(LeaveType.COMPENSATORY_LEAVE.getCode());
				leave.setValidStartDate(year);
				leave.setOssUserId(user.getOssUserId());
				leave.setDeptId(user.getDeptId());
				userLeaveService.save(leave);
//				leave.setValidEndDate();
			} else {
				leave = userLeaveList.get(0);
			}
			// 增加总天数和剩余天数
			leave.setTotalDays(leave.getTotalDays().add(days));
			leave.setLeftDays(leave.getLeftDays().add(days));

			JSONArray leaveInfoArray = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("userLeaveId", leave.getId());
			item.put("thisWorkDays", days);
			leaveInfoArray.add(item);
			workLog.setLeaveInfo(leaveInfoArray.toJSONString());
			// 更新假期剩余天数
			result = userLeaveService.save(leave);
			logger.info("加班流程更新调休数据" + (result ? "成功" : "失败"));
			// 保存加班记录
			result = specialAttendanceRecordService.save(workLog);
			logger.info("保存加班记录" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return workLog;
	}

	private void updateLogAndRestoreLeave(SpecialAttendanceRecord overtimeLog) {
		boolean result;
		try {
			overtimeLog.setValid(EntityStatus.DELETED.ordinal());
			String leaveInfo = overtimeLog.getLeaveInfo();
			JSONArray leaveInfoArray = JSON.parseArray(leaveInfo);
			if (leaveInfoArray != null && !leaveInfoArray.isEmpty()) {
				Map<String, BigDecimal> leaveDaysMap = new HashMap<>();
				for (Object leaveDay : leaveInfoArray) {
					leaveDaysMap.put(((JSONObject) leaveDay).getString("userLeaveId"), new BigDecimal(((JSONObject) leaveDay).getString("thisWorkDays")));
				}
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
				List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
				Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
				List<UserLeave> updateList = new ArrayList<>();
				// 扣减调休天数
				for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
					UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
					if (leave != null) {
						leave.setTotalDays(leave.getTotalDays().subtract(leaveDay.getValue()));
						leave.setLeftDays(leave.getLeftDays().subtract(leaveDay.getValue()));
						updateList.add(leave);
					}
				}
				// 更新假期剩余天数
				if (!CollectionUtils.isEmpty(updateList)) {
					result = userLeaveService.updateByBatch(updateList);
					logger.info("还原操作更新调休数据" + (result ? "成功" : "失败"));
				}
			}
			result = specialAttendanceRecordService.update(overtimeLog);
			logger.info("更新请假记录" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}

	private void updateLogAndUpdateLeave(SpecialAttendanceRecord overtimeLog, String flowMsg) {
		boolean result;
		try {
			// 重新增加假期前，是否需要还原假期
			boolean needRestore = true;
			// 是否需要增加假期
			boolean needUpdate = false;
			// 请假记录当前状态是无效，说明是重新发起操作。
			if (EntityStatus.DELETED.ordinal() == overtimeLog.getValid())  {
				// 由于驳回至发起人时，会还原假期，因此在重新通过时，就不需要再还原，但要进行增加
				needRestore = false;
				needUpdate = true;
			}
			//overtimeLog.setValid(EntityStatus.NORMAL.ordinal());
			// 获取节点审核后的加班信息
			JSONObject flowMsgJson = JSONObject.parseObject(flowMsg);
			String workTime = flowMsgJson.getString(Constants.USER_OVER_TIME_KEY);
			String[] workTimes = null;
			if (workTime.contains("{")) {
				JSONObject labelValue = JSON.parseObject(workTime);
				workTimes = labelValue.getString("datetime").split(" - ");
			} else {
				workTimes = workTime.split(" - ");
			}
			Date workTimeStart = DateUtil.convert(workTimes[0], DateUtil.format2);
			Date workTimeEnd = DateUtil.convert(workTimes[1], DateUtil.format2);
			// 请假天数
			BigDecimal days = userLeaveService.getLeaveDays(workTimeStart, workTimeEnd);

			// 流程信息与请假记录进行比较
			if (days.compareTo(overtimeLog.getDays()) != 0) {
				needUpdate = true;
			}
			if (workTimeStart.getTime() != overtimeLog.getStartTime().getTime() || workTimeEnd.getTime() != overtimeLog.getEndTime().getTime()) {
				needUpdate = true;
			}

			// 判断需要更新请假记录，先还原假期，再重新扣减假期
			if (needUpdate) {
				// 还原假期
				if (needRestore) {
					String leaveInfo = overtimeLog.getLeaveInfo();
					JSONArray leaveInfoArray = JSON.parseArray(leaveInfo);
					if (leaveInfoArray != null && !leaveInfoArray.isEmpty()) {
						Map<String, BigDecimal> leaveDaysMap = new HashMap<>();
						for (Object leaveDay : leaveInfoArray) {
							leaveDaysMap.put(((JSONObject) leaveDay).getString("userLeaveId"), new BigDecimal(((JSONObject) leaveDay).getString("thisWorkDays")));
						}
						SearchFilter filter = new SearchFilter();
						filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(leaveDaysMap.keySet())));
						List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
						Map<String, UserLeave> userLeaveMap = userLeaveList.stream().collect(Collectors.toMap(UserLeave::getId, v -> v));
						List<UserLeave> updateList = new ArrayList<>();
						for (Map.Entry<String, BigDecimal> leaveDay : leaveDaysMap.entrySet()) {
							UserLeave leave = userLeaveMap.getOrDefault(leaveDay.getKey(), null);
							if (leave != null) {
								leave.setTotalDays(leave.getTotalDays().subtract(leaveDay.getValue()));
								leave.setLeftDays(leave.getLeftDays().subtract(leaveDay.getValue()));
								updateList.add(leave);
							}
						}
						// 更新假期剩余天数
						if (!CollectionUtils.isEmpty(updateList)) {
							result = userLeaveService.updateByBatch(updateList);
							logger.info("还原操作更新调休数据" + (result ? "成功" : "失败"));
						}
					}
				}
				// 查调休
				Date year = DateUtil.getYearFirst(new Date(overtimeLog.getWtime().getTime()));
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, LeaveType.COMPENSATORY_LEAVE.getCode()));
				filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, overtimeLog.getOssUserId()));
				List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
				UserLeave leave = userLeaveList.get(0);
				// 增加调休总天数和剩余天数
				leave.setTotalDays(leave.getTotalDays().add(days));
				leave.setLeftDays(leave.getLeftDays().add(days));
				// 更新调休剩余天数
				result = userLeaveService.update(leave);
				logger.info("加班流程更新调休数据" + (result ? "成功" : "失败"));
				// 更新请假记录
				JSONArray leaveInfoArray = new JSONArray();
				JSONObject item = new JSONObject();
				item.put("userLeaveId", leave.getId());
				item.put("thisWorkDays", days);
				leaveInfoArray.add(item);
				overtimeLog.setLeaveInfo(leaveInfoArray.toJSONString());
				overtimeLog.setStartTime(new Timestamp(workTimeStart.getTime()));
				overtimeLog.setEndTime(new Timestamp(workTimeEnd.getTime()));
				overtimeLog.setDays(days);
			}

			result = specialAttendanceRecordService.update(overtimeLog);
			logger.info("更新加班记录" + (result ? "成功" : "失败"));
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}
}
