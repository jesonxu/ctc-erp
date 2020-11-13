package com.dahantc.erp.controller.userLeave;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
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
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.userLeave.UserLeaveDto;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;

@Controller
@RequestMapping(value = "/userLeave")
public class UserLeaveAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(UserLeaveAction.class);

	@Autowired
	private IUserLeaveService userLeaveService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISpecialAttendanceRecordService specialAttendanceRecordService;

	/**
	 * 跳转员工假期管理页面
	 */
	@RequestMapping(value = "/toUserLeave")
	public String toUserLeave() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		try {
			Map<String, Boolean> pagePermission = roleService.getPagePermission(onlineUser.getRoleId());
			request.setAttribute("pagePermission", roleService.getPagePermission(onlineUser.getRoleId()));
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return "/views/userLeave/userLeave";
	}

	/**
	 * 生成年假假期
	 * 
	 * @param userId
	 *            指定员工
	 * @param year
	 *            指定年份
	 * @return
	 */
	@RequestMapping("/buildUserLeave")
	@ResponseBody
	public BaseResponse<String> buildUserLeave(@RequestParam(required = false) String userId, @RequestParam(required = false) String year) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			logger.info("请先登录");
			return BaseResponse.noLogin("请先登录");
		}
		String msg = userLeaveService.buildAnnualLeave(onlineUser, userId, year, false);
		return BaseResponse.success(msg);
	}

	/**
	 * 查询员工的假期记录
	 * 
	 * @param year
	 *            年份
	 * @return
	 */
	@RequestMapping("/queryUserLeave")
	@ResponseBody
	public BaseResponse<List<UserLeaveDto>> queryUserLeave(@RequestParam(required = false) String year, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String deptId, @RequestParam String status) {
		logger.info("查询员工假期开始");
		List<UserLeaveDto> dtoList = new ArrayList<>();

		SearchFilter filter = new SearchFilter();
		Date queryYear = DateUtil.getThisYearFirst();
		if (StringUtil.isNotBlank(year)) {
			queryYear = DateUtil.convert(year, DateUtil.format11);
		}

		OnlineUser onlineUser = getOnlineUserAndOnther();
		Map<String, UserLeaveDto> dtoMap = buildDto(onlineUser, queryYear, keyword, deptId, status);
		if (dtoMap.isEmpty()) {
			return BaseResponse.success(dtoList);
		}
		List<String> userIdList = dtoMap.values().stream().map(UserLeaveDto::getOssUserId).collect(Collectors.toList());
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
		// 只查年假和调休
		filter.getRules().add(new SearchRule("year", Constants.ROP_EQ, queryYear));
		filter.getRules()
				.add(new SearchRule("leaveType", Constants.ROP_IN, Arrays.asList(LeaveType.ANNUAL_LEAVE.getCode(), LeaveType.COMPENSATORY_LEAVE.getCode())));
		if (StringUtil.isNotBlank(deptId)) {
			filter.getRules().add(new SearchRule("deptId", Constants.ROP_EQ, deptId));
		}
		try {
			List<UserLeave> userLeaveList = userLeaveService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(userLeaveList)) {
				for (UserLeave userLeave : userLeaveList) {
					if (!dtoMap.containsKey(userLeave.getOssUserId())) {
						continue;
					}
					UserLeaveDto dto = dtoMap.get(userLeave.getOssUserId());
					if (LeaveType.ANNUAL_LEAVE.getCode() == userLeave.getLeaveType()) {
						dto.setAnnualLeaveTotal(userLeave.getTotalDays() + "");
						dto.setAnnualLeaveLeft(userLeave.getLeftDays() + "");
					} else if (LeaveType.COMPENSATORY_LEAVE.getCode() == userLeave.getLeaveType()) {
						dto.setOvertimeTotal(userLeave.getTotalDays() + "");
						dto.setOvertimeLeft(userLeave.getLeftDays() + "");
					}
					dtoMap.put(userLeave.getOssUserId(), dto);
				}
			} else {
				logger.info("未查询到员工假期记录");
			}
			dtoList = new ArrayList<>(dtoMap.values());
			dtoList.sort(Comparator.comparing(UserLeaveDto::getDeptName));
		} catch (ServiceException e) {
			logger.error("查询员工假期异常", e);
		}
		return BaseResponse.success(dtoList);
	}

	private Map<String, UserLeaveDto> buildDto(OnlineUser onlineUser, Date year, String keyword, String deptId, String status) {
		Map<String, UserLeaveDto> dtoMap = new HashMap<>();
		// 按权限查员工
		List<User> userList = userService.readUsers(onlineUser, null, null, deptId);
		if (CollectionUtils.isEmpty(userList)) {
			logger.info("用户数据权限下没有员工");
			return dtoMap;
		}
		// 按 状态 过滤
		String[] statusCheck = status.split(",");
		if (statusCheck.length == 1) {
			int uStatus = Integer.parseInt(statusCheck[0]);
			userList = userList.stream().filter(user -> user.getUstate() == uStatus).collect(Collectors.toList());
		}
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
		// 参与计算工作时长的截止时间
		Date calDate = DateUtil.getYearLast(year);
		if (calDate.after(new Date())) {
			calDate = new Date();
		}

		// {ossUserId -> {realName, deptName, parentDeptName}}
		HashMap<String, HashMap<String, String>> userDeptMap = userService.getUserAndDeptName();
		for (User user : userList) {
			UserLeaveDto dto = new UserLeaveDto();
			dto.setOssUserId(user.getOssUserId());
			dto.setRealName(user.getRealName());
			dto.setDeptId(user.getDeptId());
			Map<String, String> userDeptInfo = userDeptMap.getOrDefault(user.getOssUserId(), new HashMap<>());
			dto.setDeptName(userDeptInfo.getOrDefault("deptName", "未知"));
			dto.setYear(DateUtil.convert(year, DateUtil.format11));
			// 入职时间，用于计算在职时长
			Timestamp entryTime = user.getEntryTime();
			if (entryTime != null) {
				dto.setEntryTime(DateUtil.convert(user.getEntryTime(), DateUtil.format1));
				int entryMonth = DateUtil.getDiffMonths(new Date(entryTime.getTime()), calDate);
				dto.setEntryMonth(entryMonth + "");
			} else {
				dto.setEntryTime("");
				dto.setEntryMonth("");
			}
			// 毕业时间，用于计算累计工作时长
			Date graduationDate = user.getGraduationDate();
			if (graduationDate != null) {
				dto.setGraduationDate(DateUtil.convert(user.getGraduationDate(), DateUtil.format1));
				int workMonth = DateUtil.getDiffMonths(graduationDate, calDate);
				dto.setWorkMonth(workMonth + "");
			} else {
				dto.setGraduationDate("");
				dto.setWorkMonth("");
			}
			dtoMap.put(user.getOssUserId(), dto);
		}
		return dtoMap;
	}

	/**
	 * 发起请假流程时获取此类型假期的信息，比如累计天数、剩余天数
	 * 
	 * @param leaveType
	 *            请假类型
	 * @return
	 */
	@RequestMapping("/getLeaveInfo")
	@ResponseBody
	public JSONObject getLeaveInfo(@RequestParam String leaveType, @RequestParam(required = false) String date) {
		JSONObject result = new JSONObject();
		String msg = "";
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			msg = "请先登录";
			logger.info(msg);
			result.put("msg", msg);
			return result;
		}

		// 检查请假类型
		Optional<LeaveType> leaveTypeOpt = LeaveType.getEnumsByMsg(leaveType);
		if (!leaveTypeOpt.isPresent()) {
			msg = "请假类型不正确，联系管理员";
			logger.info(msg);
			result.put("msg", msg);
			return result;
		}
		Date queryDate = null;
		if (StringUtil.isBlank(date)) {
			queryDate = DateUtil.getCurrentStartDateTime();
		} else {
			queryDate = DateUtil.convert(date, DateUtil.format11);
		}
		logger.info("查询员工假期信息开始，姓名：" + onlineUser.getUser().getRealName() + "，假期类型：" + leaveType + "，时间：" + DateUtil.convert(queryDate, DateUtil.format11));

		LeaveType type = leaveTypeOpt.get();
		BigDecimal leftDays;
		switch (type) {
		case ANNUAL_LEAVE:
			leftDays = userLeaveService.getAnnualLeaveLeftDays(onlineUser.getUser(), null, null, new Timestamp(queryDate.getTime()), LeaveType.ANNUAL_LEAVE.getCode(), null);
			msg = "剩余可用" + LeaveType.ANNUAL_LEAVE.getDesc() + leftDays.toPlainString() + "天";
			result.put("leftDays", leftDays.toPlainString());
			break;
		case COMPENSATORY_LEAVE:
			leftDays = userLeaveService.getCompensatoryLeaveLeftDays(onlineUser.getUser(), null, null, new Timestamp(queryDate.getTime()), LeaveType.COMPENSATORY_LEAVE.getCode(), null);
			msg = "剩余可用" + LeaveType.COMPENSATORY_LEAVE.getDesc() + leftDays.toPlainString() + "天";
			result.put("leftDays", leftDays.toPlainString());
			break;
		case WEDDING_LEAVE:
			leftDays = userLeaveService.getWeddingLeaveLeftDays(onlineUser.getUser(), null, null, null, LeaveType.WEDDING_LEAVE.getCode(), null);
			msg = "剩余可用" + LeaveType.WEDDING_LEAVE.getDesc() + leftDays.toPlainString() + "天，请假时需一次性请，不可拆分";
			result.put("leftDays", leftDays.toPlainString());
			break;
		}
		// 查询今年请假历史
		Date yearStart = DateUtil.getYearFirst(queryDate);
		Date yearEnd = DateUtil.getYearLast(queryDate);
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, yearStart));
		filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, yearEnd));
		filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, type.getCode()));
		filter.getRules().add(new SearchRule("valid", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
		try {
			List<SpecialAttendanceRecord> recordList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(recordList)) {
				int count = recordList.size();
				BigDecimal usedDays = BigDecimal.valueOf(recordList.stream().mapToDouble(log -> log.getDays().doubleValue()).sum()).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				msg = DateUtil.convert(yearStart, DateUtil.format11) + "年已请" + type.getDesc() + count + "次，共计" + usedDays.toPlainString() + "天。" + msg;
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}

		result.put("msg", msg);
		return result;
	}

	/**
	 * 获取假期类型下拉框
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
		logger.info("获取假期类型下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return result.toJSONString();
	}
}
