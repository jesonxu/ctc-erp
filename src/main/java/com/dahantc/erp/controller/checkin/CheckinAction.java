package com.dahantc.erp.controller.checkin;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.checkin.CheckinDetailDto;
import com.dahantc.erp.dto.checkin.CheckinDto;
import com.dahantc.erp.enums.CheckinType;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.checkin.entity.Checkin;
import com.dahantc.erp.vo.checkin.service.ICheckinService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * @author : 8523
 * @date : 2020/10/16 9:09
 */
@Controller
@RequestMapping("/checkin")
@Validated
public class CheckinAction extends BaseAction {

	private static Logger logger = LogManager.getLogger(CheckinAction.class);

	@Autowired
	private ICheckinService checkinService;

	@Autowired
	private IUserService userService;

	@RequestMapping("/toCheckinSheet")
	public String toCheckinSheet() {
		return "/views/checkin/checkinSheet";
	}

	/**
	 * 查询打卡记录
	 *
	 * @param date
	 *            日期
	 * @param keyword
	 *            关键词
	 * @param deptId
	 *            部门
	 * @param status
	 *            打卡状态
	 * @param type
	 *            打卡类型
	 * @return
	 */
	@RequestMapping("/queryCheckin")
	@ResponseBody
	public BaseResponse<List<CheckinDto>> queryCheckin(
			@RequestParam(required = false) String date,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String deptId,
			@RequestParam String status,
			@RequestParam(required = false) String type) {
		logger.info("查询打卡记录开始，日期：" + date);
		List<CheckinDto> dtoList = new ArrayList<>();

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
		SearchFilter filter = new SearchFilter();
		// 00:00:00 <= <= 23:59:59
		Date startDate = DateUtil.getYesterdayStartDateTime();
		Date endDate = DateUtil.getYesterdayEndDateTime();
		if (StringUtil.isNotBlank(date)) {
			startDate = DateUtil.convert(date, DateUtil.format1);
			endDate = DateUtil.getTodayEndTime(date);
		}
		filter.getRules().add(new SearchRule("checkinTime", Constants.ROP_GE, startDate));
		filter.getRules().add(new SearchRule("checkinTime", Constants.ROP_LE, endDate));
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
		// 打卡状态
		if (StringUtil.isNotBlank(status)) {
			String[] statuses = status.split(",");
			if (statuses.length == 1) {
				if (status.equals("0")) {
					// 正常
					filter.getRules().add(new SearchRule("exceptionType", Constants.ROP_EQ, ""));
				} else if (status.equals("1")) {
					// 异常
					filter.getRules().add(new SearchRule("exceptionType", Constants.ROP_NE, ""));
				}
			}
		}
		try {
			List<Checkin> checkinList = checkinService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(checkinList)) {
				logger.info("打卡记录为空");
				return BaseResponse.success(dtoList);
			}
			Map<String, CheckinDto> dtoMap = buildDto(checkinList, userList, DateUtil.convert(startDate, DateUtil.format1));
			dtoList = new ArrayList<>(dtoMap.values());
			// 打卡状态
			if (StringUtil.isNotBlank(status)) {
				String[] statuses = status.split(",");
				if (statuses.length == 1) {
					if (status.equals("0")) {
						// 正常
						dtoList = dtoList.stream().filter(dto -> {
							if (("正常打卡".equals(dto.getCheckinInfo()) && "正常打卡".equals(dto.getCheckoutInfo())) || dto.getOutsideCheckTimes() > 0) {
								return true;
							} else {
								return false;
							}
						}).collect(Collectors.toList());
					} else if (status.equals("1")) {
						// 异常
						dtoList = dtoList.stream().filter(dto -> {
							if ((!"正常打卡".equals(dto.getCheckinInfo()) || !"正常打卡".equals(dto.getCheckoutInfo())) && dto.getOutsideCheckTimes() == 0) {
								return true;
							} else {
								return false;
							}
						}).collect(Collectors.toList());
					}
				}
			}
			dtoList.sort(Comparator.comparing(CheckinDto::getDeptName));
		} catch (ServiceException e) {
			logger.error("查询打卡记录异常", e);
		}
		return BaseResponse.success(dtoList);
	}

	private Map<String, CheckinDto> buildDto(List<Checkin> checkinList, List<User> userList, String date) {
		Map<String, CheckinDto> dtoMap = new HashMap<>();
		// 打卡记录按ossUserId分组
		Map<String, List<Checkin>> userCheckinMap = checkinList.stream().collect(Collectors.groupingBy(Checkin::getOssUserId));
		// 查所有用户信息 {ossUserId -> {realName, deptName, parentDeptName}}
		HashMap<String, HashMap<String, String>> userDeptMap = userService.getUserAndDeptName();
		// 遍历按权限查到的用户
		for (User user : userList) {
			CheckinDto dto = new CheckinDto();
			dto.setDate(date);
			dto.setOssUserId(user.getOssUserId());
			dto.setRealName(user.getRealName());
			dto.setDeptId(user.getDeptId());
			Map<String, String> userDeptInfo = userDeptMap.getOrDefault(user.getOssUserId(), new HashMap<>());
			dto.setDeptName(userDeptInfo.getOrDefault("deptName", "未知"));
			List<Checkin> userCheckinList = userCheckinMap.get(user.getOssUserId());
			if (!CollectionUtils.isEmpty(userCheckinList)) {
				for (Checkin checkin : userCheckinList) {
					if (checkin.getCheckinType().equals(CheckinType.Checkin.getCode() + "")) {
						// 上班打卡
						dto.setCheckinTime(DateUtil.convert(checkin.getCheckinTime().getTime(), DateUtil.format2));
						dto.setCheckinLocationTitle(checkin.getLocationTitle());
						dto.setCheckinLocationDetail(checkin.getLocationDetail());
						dto.setCheckinLocation(checkin.getLat() + "," + checkin.getLng());
						if (StringUtil.isBlank(checkin.getExceptionType())) {
							dto.setCheckinInfo("正常打卡");
						} else {
							dto.setCheckinInfo(checkin.getExceptionTypeName());
						}
					} else if (checkin.getCheckinType().equals(CheckinType.Checkout.getCode() + "")) {
						// 上班打卡
						dto.setCheckoutTime(DateUtil.convert(checkin.getCheckinTime().getTime(), DateUtil.format2));
						dto.setCheckoutLocationTitle(checkin.getLocationTitle());
						dto.setCheckoutLocationDetail(checkin.getLocationDetail());
						dto.setCheckoutLocation(checkin.getLat() + "," + checkin.getLng());
						if (StringUtil.isBlank(checkin.getExceptionType())) {
							dto.setCheckoutInfo("正常打卡");
						} else {
							dto.setCheckoutInfo(checkin.getExceptionTypeName());
						}
					} else if (checkin.getCheckinType().equals(CheckinType.Outside.getCode() + "")) {
						// 外出打卡
						dto.setOutsideCheckTimes(dto.getOutsideCheckTimes() + 1);
					}
				}
			}
			dtoMap.put(user.getOssUserId(), dto);
		}
		return dtoMap;
	}

	@RequestMapping("/queryCheckinDetail")
	@ResponseBody
	public BaseResponse<List<CheckinDetailDto>> queryCheckinDetail(@RequestParam(required = false) String date, @RequestParam String ossUserId) {
		logger.info("查询打卡记录开始，日期：" + date);
		List<CheckinDetailDto> dtoList = new ArrayList<>();
		if (StringUtil.isBlank(ossUserId)) {
			logger.info("用户不能为空");
			return BaseResponse.success(dtoList);
		}
		try {
			User user = userService.read(ossUserId);


		SearchFilter filter = new SearchFilter();
		// 00:00:00 <= <= 23:59:59
		Date startDate = DateUtil.getYesterdayStartDateTime();
		Date endDate = DateUtil.getYesterdayEndDateTime();
		if (StringUtil.isNotBlank(date)) {
			startDate = DateUtil.convert(date, DateUtil.format1);
			endDate = DateUtil.getTodayEndTime(date);
		}
		filter.getRules().add(new SearchRule("checkinTime", Constants.ROP_GE, startDate));
		filter.getRules().add(new SearchRule("checkinTime", Constants.ROP_LE, endDate));
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
		filter.getOrders().add(new SearchOrder("checkinTime", Constants.ROP_ASC));

			List<Checkin> checkinList = checkinService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(checkinList)) {
				logger.info("打卡记录为空");
				return BaseResponse.success(dtoList);
			}
			dtoList = buildDetailDto(checkinList, user, DateUtil.convert(startDate, DateUtil.format1));
		} catch (ServiceException e) {
			logger.error("查询打卡记录详情异常", e);
		}
		return BaseResponse.success(dtoList);
	}

	private List<CheckinDetailDto> buildDetailDto(List<Checkin> checkinList, User user, String date) {
		List<CheckinDetailDto> dtoList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(checkinList)) {
			for (Checkin checkin : checkinList) {
				CheckinDetailDto dto = new CheckinDetailDto();
				dto.setId(checkin.getId());
				dto.setOssUserId(user.getOssUserId());
				dto.setRealName(user.getRealName());
				dto.setGroupName(checkin.getGroupName());
				dto.setCheckinType(checkin.getCheckinType());
				dto.setCheckinTypeName(checkin.getCheckinTypeName());
				dto.setExceptionType(checkin.getExceptionType());
				dto.setExceptionTypeName(checkin.getExceptionTypeName());
				dto.setCheckinTime(DateUtil.convert(checkin.getCheckinTime().getTime(), DateUtil.format2));
				dto.setLocationTitle(checkin.getLocationTitle());
				dto.setLocationDetail(checkin.getLocationDetail());
				dto.setLat(new BigDecimal(checkin.getLat()).divide(BigDecimal.valueOf(1000000), 6, BigDecimal.ROUND_HALF_UP).toPlainString());
				dto.setLng(new BigDecimal(checkin.getLng()).divide(BigDecimal.valueOf(1000000), 6, BigDecimal.ROUND_HALF_UP).toPlainString());
				dto.setNotes(checkin.getNotes());
				dto.setMediaIds(checkin.getMediaIds());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * 生成验证码
	 */
	@GetMapping(value = "/getMedia")
	public void getMedia(HttpServletRequest request, HttpServletResponse response, @RequestParam String mediaId) {
		OutputStream outputStream = null;
		try {
			byte[] data = WeixinMessage.getMedia(mediaId);
			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (Exception e) {
			logger.info("获取附件失败", e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}
}
