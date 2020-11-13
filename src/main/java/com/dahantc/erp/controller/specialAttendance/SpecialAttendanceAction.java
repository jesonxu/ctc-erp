package com.dahantc.erp.controller.specialAttendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.specialAttendance.SpecialAttendanceDto;
import com.dahantc.erp.enums.SpecialAttendanceType;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 特殊出勤报备记录action
 */
@Controller
@RequestMapping(value = "/specialAttendance")
public class SpecialAttendanceAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(SpecialAttendanceAction.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private ISpecialAttendanceRecordService specialAttendanceRecordService;

	/**
	 * 跳转员工特殊出勤报备记录页面
	 */
	@RequestMapping(value = "/toSpecialAttendanceRecord")
	public String toSpecialAttendanceRecord() {
		request.setAttribute("type", request.getParameter("type"));
		request.setAttribute("ossUserId", request.getParameter("ossUserId"));
		return "/views/specialAttendance/specialAttendanceRecord";
	}

	/**
	 * 查询员工的特殊出勤报备记录
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param ossUserId
	 *            员工
	 * @param type
	 *            特殊出勤类型
	 * @param valid
	 *            是否有效
	 * @return
	 */
	@RequestMapping("/querySpecialAttendanceRecord")
	@ResponseBody
	public BaseResponse<List<SpecialAttendanceDto>> querySpecialAttendanceRecord(
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String ossUserId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String deptId,
			@RequestParam(required = false) String type,
			@RequestParam String valid) {
		logger.info("查询员工特殊出勤报备记录开始, startDate: " + startDate + ", endDate: " + endDate);
		List<SpecialAttendanceDto> dtoList = new ArrayList<>();
		List<String> userIdList = new ArrayList<>();
		try {
			// 全部为空，或者keyword/deptId不全为空，按权限查
			if (StringUtils.isAllBlank(ossUserId, keyword, deptId) || !StringUtils.isAllBlank(keyword, deptId)) {
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
				if (CollectionUtils.isEmpty(userList)) {
					logger.info("没有符合条件的员工");
					return BaseResponse.success(dtoList);
				}
				userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());
			} else if (StringUtils.isNotBlank(ossUserId) && StringUtils.isAllBlank(keyword, deptId)) {
				// 只有ossUserId不为空，只查该用户
				User user = userService.read(ossUserId);
				if (null == user) {
					logger.info("员工不存在：" + ossUserId);
					return BaseResponse.success(dtoList);
				}
				userIdList.add(ossUserId);
			}

			String hql = "FROM SpecialAttendanceRecord WHERE ossUserId in (:ossUserId) ";
			Map<String, Object> param = new HashMap<>();
			param.put("ossUserId", userIdList);

			// 特殊出勤类型
			if (StringUtil.isNotBlank(type)) {
				Optional<SpecialAttendanceType> leaveTypeOpt = SpecialAttendanceType.getEnumsByCode(Integer.parseInt(type));
				if (!leaveTypeOpt.isPresent()) {
					logger.info("特殊出勤类型不正确：" + type);
					return BaseResponse.success(dtoList);
				}
				hql += " AND specialAttendanceType = :specialAttendanceType";
				param.put("specialAttendanceType", Integer.parseInt(type));
			}

			// 开始结束时间
			Date startTime = null;
			if (StringUtil.isNotBlank(startDate)) {
				startTime = DateUtil.convert(startDate + " 00:00:00", DateUtil.format1);
			} else {
				startTime = DateUtil.getThisMonthFirst();
			}
			Date endTime = null;
			if (StringUtil.isNotBlank(endDate)) {
				endTime = DateUtil.convert(endDate + " 23:59:59", DateUtil.format1);
			} else {
				endTime = DateUtil.getCurrentEndDateTime();
			}
			hql += " and (NOT (endTime < :startTime OR startTime > :endTime)) ";
			param.put("startTime", startTime);
			param.put("endTime", endTime);

			String[] validCheck = valid.split(",");
			if (validCheck.length == 1) {
				hql += " and valid=:valid";
				param.put("valid", Integer.parseInt(validCheck[0]));
			}
			hql += "  ORDER BY wtime DESC";

			List<SpecialAttendanceRecord> recordList = specialAttendanceRecordService.findByhql(hql, param, 0);
			if (!CollectionUtils.isEmpty(recordList)) {
				dtoList = buildDto(recordList);
				logger.info("查询到" + dtoList.size() + "条记录");
			} else {
				logger.info("未查询到员工特殊出勤报备记录");
			}
		} catch (ServiceException e) {
			logger.error("查询员工特殊出勤报备记录异常", e);
		}
		return BaseResponse.success(dtoList);
	}

	private List<SpecialAttendanceDto> buildDto(List<SpecialAttendanceRecord> recordList) {
		List<SpecialAttendanceDto> dtoList = new ArrayList<>();
		if (CollectionUtils.isEmpty(recordList)) {
			return dtoList;
		}
		// 查所有用户信息 {ossUserId -> {realName, deptName, parentDeptName}}
		HashMap<String, HashMap<String, String>> userDeptMap = userService.getUserAndDeptName();
		for (SpecialAttendanceRecord record : recordList) {
			SpecialAttendanceDto dto = new SpecialAttendanceDto();

			Map<String, String> userDeptInfo = userDeptMap.getOrDefault(record.getOssUserId(), new HashMap<>());
			dto.setDeptId(record.getDeptId());
			dto.setDeptName(userDeptInfo.getOrDefault("deptName", "未知"));
			dto.setRealName(userDeptInfo.getOrDefault("realName", "未知"));
			dto.setOssUserId(record.getOssUserId());
			dto.setWtime(DateUtil.convert(record.getWtime(), DateUtil.format1));
			dto.setSpecialAttendanceType(SpecialAttendanceType.getTypeDesc(record.getSpecialAttendanceType()));
			dto.setLeaveType(record.getLeaveType() != -1 ? LeaveType.getLeaveType(record.getLeaveType()) : "");
			dto.setDays(record.getDays().toPlainString());
			dto.setStartTime(DateUtil.convert(record.getStartTime(), DateUtil.format2));
			dto.setEndTime(DateUtil.convert(record.getEndTime(), DateUtil.format2));
			dto.setValid(record.getValid() + "");
			dto.setTimeState(record.getTimeState() + "");
			dtoList.add(dto);
		}
		return dtoList;
	}

	/**
	 * 获取特殊出勤类型下拉框
	 */
	@RequestMapping("/getSpecialAttendanceSelect")
	@ResponseBody
	public String getSpecialAttendanceSelect() {
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		SpecialAttendanceType[] types = SpecialAttendanceType.values();
		for (SpecialAttendanceType type : types) {
			JSONObject json = new JSONObject();
			json.put("value", type.ordinal());
			json.put("name", type.getDesc());
			result.add(json);
		}
		logger.info("获取特殊出勤类型下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return result.toJSONString();
	}

	/**
	 * 获取请假类型下拉框
	 */
	@RequestMapping("/getLeaveTypeSelect")
	@ResponseBody
	public String getLeaveTypeSelect() {
		long _start = System.currentTimeMillis();
		JSONArray result = new JSONArray();
		LeaveType[] types = LeaveType.values();
		for (LeaveType type : types) {
			JSONObject json = new JSONObject();
			json.put("value", type.getCode());
			json.put("name", type.getDesc());
			result.add(json);
		}
		logger.info("获取请假下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return result.toJSONString();
	}
}
