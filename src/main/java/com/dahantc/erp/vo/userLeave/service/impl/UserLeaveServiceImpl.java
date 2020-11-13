package com.dahantc.erp.vo.userLeave.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.enums.MaritalStatus;
import com.dahantc.erp.enums.UserStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userLeave.dao.IUserLeaveDao;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;

@Service("userLeaveService")
public class UserLeaveServiceImpl implements IUserLeaveService {
	private static Logger logger = LogManager.getLogger(UserLeaveServiceImpl.class);

	@Autowired
	private IUserLeaveDao userLeaveDao;

	@Autowired
	private IUserService userService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private ISpecialAttendanceRecordService specialAttendanceRecordService;

	@Override
	public UserLeave read(Serializable id) throws ServiceException {
		try {
			return userLeaveDao.read(id);
		} catch (Exception e) {
			logger.error("读取员工假期数据失败", e);
			throw new ServiceException("读取员工假期数据失败", e);
		}
	}

	@Override
	public boolean save(UserLeave entity) throws ServiceException {
		try {
			return userLeaveDao.save(entity);
		} catch (Exception e) {
			logger.error("保存员工假期数据失败", e);
			throw new ServiceException("保存员工假期数据失败", e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return userLeaveDao.delete(id);
		} catch (Exception e) {
			logger.error("删除员工假期数据失败", e);
			throw new ServiceException("删除员工假期数据失败", e);
		}
	}

	@Override
	public boolean update(UserLeave enterprise) throws ServiceException {
		try {
			return userLeaveDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新员工假期数据失败", e);
			throw new ServiceException("更新员工假期数据失败", e);
		}
	}

	@Override
	public boolean updateByBatch(List<UserLeave> objs) throws ServiceException {
		try {
			return userLeaveDao.updateByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean saveByBatch(List<UserLeave> objs) throws ServiceException {
		try {
			return userLeaveDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean deleteByBatch(List<UserLeave> objs) throws ServiceException {
		try {
			return userLeaveDao.deleteByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return userLeaveDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询员工假期数据数量失败", e);
			throw new ServiceException("查询员工假期数据数量失败", e);
		}
	}

	@Override
	public PageResult<UserLeave> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return userLeaveDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询员工假期数据分页信息失败", e);
			throw new ServiceException("查询员工假期数据分页信息失败", e);
		}
	}

	@Override
	public List<UserLeave> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return userLeaveDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询员工假期数据失败", e);
			throw new ServiceException("查询员工假期数据失败", e);
		}
	}

	@Override
	public List<UserLeave> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return userLeaveDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询员工假期数据失败", e);
			throw new ServiceException("查询员工假期数据失败", e);
		}
	}

	@Override
	public List<UserLeave> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return userLeaveDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询员工假期数据失败", e);
			throw new ServiceException("查询员工假期数据失败", e);
		}
	}

	@Override
	public UserLeave readOneByProperty(String property, Object value) throws ServiceException {
		try {
			return userLeaveDao.readOneByProperty(property, value);
		} catch (Exception e) {
			logger.error("查询员工假期数据失败", e);
			throw new ServiceException("查询员工假期数据失败", e);
		}
	}

	/**
	 * 生成/重新计算员工在指定年的年假 每年1月1号生成当年的年假，次年3月3号失效
	 * 1月1号生成时工作时间不满足下一档，但在当年12月31号前，满足了下一档，需要增加当年的年假天数
	 * 
	 * @param onlineUser
	 *            当前用户
	 * @param ossUserId
	 *            指定员工，为空时给所有员工生成
	 * @param year
	 *            年份
	 * @param all
	 *            是否包括已禁用的员工
	 * @return
	 */
	@Override
	public String buildAnnualLeave(OnlineUser onlineUser, String ossUserId, String year, Boolean all) {
		String msg = "";
		Date today = new Date();
		String todayStr = DateUtil.convert(today, DateUtil.format1);
		Date targetYear = StringUtil.isNotBlank(year) ? DateUtil.convert(year, DateUtil.format11) : DateUtil.getThisYearFirst();
		Date targetYearLast = DateUtil.getYearLast(targetYear);
		year = DateUtil.convert(targetYear, DateUtil.format11);
		logger.info("生成年假开始，目标年份：" + year + "，当前时间：" + todayStr);

		SearchFilter filter = new SearchFilter();
		if (StringUtil.isNotBlank(ossUserId)) {
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
		}
		if (!all) {
			filter.getRules().add(new SearchRule("ustate", Constants.ROP_EQ, UserStatus.ACTIVE.getValue()));
			filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
		}
		try {
			List<User> userList = userService.queryAllBySearchFilter(filter);
			if (CollectionUtils.isEmpty(userList)) {
				msg = "查找员工的结果为空";
				logger.info(msg);
				return msg;
			}
			List<String> userIdList = userList.stream().map(User::getOssUserId).collect(Collectors.toList());

			// 每年的年假的有效期为 该年的1月1日 ~ 下一年的重置日期；不设置重置日期，则有效期 为 该年的1月1日 ~ 该年的12月31日
			Date validStartDate = targetYear;
			Date validEndDate = targetYearLast;
			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.ANNUAL_LEAVE_RESET_KEY);
			if (parameter != null && parameter.getParamvalue() != null) {
				String resetDateStr = year + "-" + parameter.getParamvalue();
				Date resetDate = DateUtil.convert(resetDateStr, DateUtil.format1);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(resetDate);
				calendar.add(Calendar.YEAR, 1);
				validEndDate = DateUtil.getYesterdayEndDateTime(calendar.getTime());
			}
			logger.info(year + "年度年假的有效时间：" + DateUtil.convert(validStartDate, DateUtil.format2) + " ~ " + DateUtil.convert(validEndDate, DateUtil.format2));

			// 查员工当年已存在的年假记录
			SearchFilter leaveFilter = new SearchFilter();
			leaveFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userIdList));
			leaveFilter.getRules().add(new SearchRule("year", Constants.ROP_EQ, targetYear));
			leaveFilter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, LeaveType.ANNUAL_LEAVE.getCode()));
			List<UserLeave> existLeaveList = queryAllBySearchFilter(leaveFilter);
			// ossUserId -> 年假
			Map<String, UserLeave> userLeaveMap = null;
			if (CollectionUtils.isEmpty(existLeaveList)) {
				userLeaveMap = new HashMap<>();
			} else {
				userLeaveMap = existLeaveList.stream().collect(Collectors.toMap(UserLeave::getOssUserId, v -> v));
			}

			// 是生成往年的年假，直接用该年的年底计算入职时间和工作时间
			Date calDate = today;
			if (today.after(targetYearLast)) {
				calDate = targetYearLast;
			}

			List<UserLeave> addList = new ArrayList<>();
			List<UserLeave> updateList = new ArrayList<>();
			int badCount = 0;

			for (User user : userList) {
				int leaveDays = 0;
				Timestamp entryTime = user.getEntryTime();
				Date graduationDate = user.getGraduationDate();
				String remark = "";
				if (null == entryTime) {
					logger.info("员工【" + user.getRealName() + "】入职时间为空，无法计算年假，跳过");
					badCount++;
					continue;
				} else if (null == graduationDate) {
					logger.info("员工【" + user.getRealName() + "】毕业时间为空，无法计算年假，跳过");
					badCount++;
					continue;
				} else {
					String calDateStr = DateUtil.convert(calDate, DateUtil.format1);
					// 在职时长，满1年才给年假
					int entryMonth = DateUtil.getDiffMonths(new Date(entryTime.getTime()), calDate);
					if (entryMonth < 0) {
						remark = "[" + todayStr + "]员工【" + user.getRealName() + "】入职时间" + DateUtil.convert(entryTime, DateUtil.format1) + "，在" + calDateStr
								+ "未入职，不计算年假";
						logger.info(remark);
						badCount++;
						continue;
					} else if (entryMonth < 12) {
						remark = "[" + todayStr + "]员工【" + user.getRealName() + "】入职时间" + DateUtil.convert(entryTime, DateUtil.format1) + "，截止" + calDateStr
								+ "在职" + entryMonth + "个月，入职不满一年，年假天数为0";
						logger.info(remark);
					} else { // 入职满1年，按累计工作时长计算年假
						// 累计工作时长
						int workMonth = DateUtil.getDiffMonths(new Date(graduationDate.getTime()), calDate);
						if (workMonth < 0) {
							remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，在"
									+ calDateStr + "未毕业，不计算年假";
							logger.info(remark);
							badCount++;
							continue;
						} else if (workMonth < 12) {
							remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止"
									+ calDateStr + "累计工作" + workMonth + "个月，不满一年，年假天数为0";
							logger.info(remark);
							leaveDays = 0;
						} else if (workMonth < 120) {
							remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止"
									+ calDateStr + "累计工作" + workMonth + "个月，已满一年不满十年，年假天数为5";
							logger.info(remark);
							leaveDays = 5;
						} else if (workMonth < 240) {
							remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止"
									+ calDateStr + "累计工作" + workMonth + "个月，已满十年不满二十年，年假天数为10";
							logger.info(remark);
							leaveDays = 10;
						} else {
							remark = "[" + todayStr + "]员工【" + user.getRealName() + "】参加工作时间：" + DateUtil.convert(graduationDate, DateUtil.format1) + "，截止"
									+ calDateStr + "累计工作" + workMonth + "个月，已满二十年，年假天数为15";
							logger.info(remark);
							leaveDays = 15;
						}
					}
				}
				UserLeave userLeave = userLeaveMap.get(user.getOssUserId());
				if (userLeave == null) {
					userLeave = new UserLeave();
					userLeave.setOssUserId(user.getOssUserId());
					userLeave.setDeptId(user.getDeptId());
					userLeave.setYear(targetYear);
					userLeave.setTotalDays(new BigDecimal(leaveDays));
					userLeave.setLeaveType(LeaveType.ANNUAL_LEAVE.getCode());
					userLeave.setLeftDays(userLeave.getTotalDays());
					userLeave.setRemark(remark);
					userLeave.setValidStartDate(validStartDate);
					userLeave.setValidEndDate(validEndDate);
					addList.add(userLeave);
				} else {
					// 重新计算的年假 比 上次计算的年假 多，增加剩余年假
					BigDecimal oldLeaveDays = userLeave.getTotalDays();
					String change = "员工【" + user.getRealName() + "】原本年假：" + oldLeaveDays + "，重新计算后的年假：" + leaveDays + "，更新年假天数和剩余天数";
					remark += ";" + change;
					logger.info(change);
					userLeave.setRemark(userLeave.getRemark() + "\n" + remark);
					userLeave.setTotalDays(new BigDecimal(leaveDays));
					userLeave.setLeftDays(userLeave.getLeftDays().add(new BigDecimal(leaveDays)).subtract(oldLeaveDays));
					userLeave.setWtime(today);
					userLeave.setValidStartDate(validStartDate);
					userLeave.setValidEndDate(validEndDate);
					updateList.add(userLeave);
				}
			}
			if (CollectionUtils.isEmpty(addList)) {
				msg = "新生成的年假记录为空";
			} else {
				boolean result = saveByBatch(addList);
				msg = "新生成的年假记录条数" + addList.size() + "，保存" + (result ? "成功" : "失败");
			}
			if (CollectionUtils.isEmpty(updateList)) {
				msg += "；更新的年假记录为空";
			} else {
				boolean result = updateByBatch(updateList);
				msg += "；更新的年假记录条数" + updateList.size() + "，更新" + (result ? "成功" : "失败");
			}
			if (badCount > 0) {
				msg += "；" + badCount + "个员工的信息不完善，未生成年假";
			}
			logger.info(msg);
		} catch (ServiceException e) {
			msg = "生成年假记录异常，请联系管理员";
			logger.error(msg, e);
		}
		return msg;
	}

	/**
	 * 检查是否
	 * 
	 * @param leaveType
	 *            请假类别
	 * @param user
	 *            请假人
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
	@Override
	public String checkUserLeave(LeaveType leaveType, User user, Date startTime, Date endTime, Timestamp applyTime, BigDecimal days, String flowEntId) {
		if (null == user) {
			return "员工不能为空";
		}
		String result = "";
		// 请假天数
		BigDecimal day = getLeaveDays(startTime, endTime);
		BigDecimal leftDays = null;
		String leaveTypeName = "";
		switch (leaveType) {
		case PERSONAL_LEAVE: 					// 0事假
		case SICK_LEAVE:	  					// 1病假
		case HOME_LEAVE:	  					// 2探亲假
		case MATERNITY_OR_PATERNITY_LEAVE:	  	// 5产假及看护假
		case LACTATION_LEAVE:				  	// 6哺乳假
		case BEREAVEMENT_LEAVE:				  	// 7丧假
		case PAID_SICK_LEAVE:				  	// 8带薪病假
			break;
		case ANNUAL_LEAVE: { // 3年假
			// 剩余可用年假天数
			leftDays = getAnnualLeaveLeftDays(user, startTime, endTime, applyTime, LeaveType.ANNUAL_LEAVE.getCode(), flowEntId);
			leaveTypeName = LeaveType.ANNUAL_LEAVE.getDesc();
			break;
		}
		case WEDDING_LEAVE: { // 4婚假
			// 剩余可用调休天数
			leftDays = getWeddingLeaveLeftDays(user, null, null, null, LeaveType.WEDDING_LEAVE.getCode(), flowEntId);
			leaveTypeName = LeaveType.WEDDING_LEAVE.getDesc();
			break;
		}
		case COMPENSATORY_LEAVE: { // 9调休
			// 剩余可用调休天数
			leftDays = getCompensatoryLeaveLeftDays(user, startTime, endTime, applyTime, LeaveType.COMPENSATORY_LEAVE.getCode(), flowEntId);
			leaveTypeName = LeaveType.COMPENSATORY_LEAVE.getDesc();
			break;
		}

		}
		if (leftDays != null && day.compareTo(leftDays) > 0) {
			result = "剩余可用" + leaveTypeName + leftDays + "天，小于请假天数";
			logger.info(result);
		}
		return result;
	}

	@Override
	public List<UserLeave> readLeaveByUser(LeaveType leaveType, User user, Date startTime, Date endTime) {
		return null;
	}

	/**
	 * 获取可用年假天数
	 * 
	 * @param user
	 *            员工
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 * @param applyTime
	 *            申请时间
	 * @param leaveType
	 *            请假类型
	 * @param flowEntId
	 *            请假流程id
	 * @return
	 */
	@Override
	public BigDecimal getAnnualLeaveLeftDays(User user, Date startTime, Date endTime, Timestamp applyTime, int leaveType, String flowEntId) {
		BigDecimal leftDays = new BigDecimal(0);
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
		filter.getRules().add(new SearchRule("leftDays", Constants.ROP_GE, BigDecimal.ZERO));
		if (startTime == null && endTime == null) {
			// 申请时，仅查询结束有效期大于现在的
			filter.getRules().add(new SearchRule("validEndDate", Constants.ROP_GE, applyTime));
		} else {
			filter.getOrRules().add(new SearchRule[] {
					new SearchRule("validEndDate", Constants.ROP_GE, endTime),
					new SearchRule("validEndDate", Constants.ROP_GE, startTime),
					new SearchRule("validEndDate", Constants.ROP_GE, applyTime)
			});
		}
		filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, leaveType));
		filter.getOrders().add(new SearchOrder("year", Constants.ROP_ASC));
		List<UserLeave> leaveList = null;
		try {
			// 查剩余可用天数
			leaveList = queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(leaveList)) {
				leftDays = BigDecimal.valueOf(leaveList.stream().mapToDouble(leave -> leave.getLeftDays().doubleValue()).sum()).setScale(2,
						BigDecimal.ROUND_HALF_UP);
			}
			// 本流程占用的天数，也算在可用天数中
			if (StringUtil.isNotBlank(flowEntId)) {
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEntId));
				filter.getRules().add(new SearchRule("valid", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
				List<SpecialAttendanceRecord> applyingList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(applyingList)) {
					leftDays = leftDays.add(applyingList.get(0).getDays());
				}
			}
			if (leftDays.compareTo(BigDecimal.ZERO) == 0) {
				logger.info("员工：" + user.getRealName() + "没有可用年假");
			} else {
				logger.info("员工：" + user.getRealName() + "剩余可用年假：" + leftDays.toPlainString() + "天");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return leftDays;
	}

	/**
	 * 获取可用调休天数
	 * 
	 * @param user
	 *            员工
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 * @param applyTime
	 *            申请时间
	 * @param leaveType
	 *            请假类型
	 * @param flowEntId
	 *            请假流程id
	 * @return
	 */
	@Override
	public BigDecimal getCompensatoryLeaveLeftDays(User user, Date startTime, Date endTime, Timestamp applyTime, int leaveType, String flowEntId) {
		BigDecimal leftDays = new BigDecimal(0);
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
		filter.getRules().add(new SearchRule("leftDays", Constants.ROP_GE, BigDecimal.ZERO));
		// 加班获得的调休一直有效
//		filter.getRules().add(new SearchRule("validEndDate", Constants.ROP_EQ, null));
		filter.getRules().add(new SearchRule("leaveType", Constants.ROP_EQ, leaveType));
		filter.getOrders().add(new SearchOrder("year", Constants.ROP_ASC));
		List<UserLeave> leaveList = null;
		try {
			// 查剩余可用天数
			leaveList = queryAllBySearchFilter(filter);
			if (!CollectionUtils.isEmpty(leaveList)) {
				leftDays = BigDecimal.valueOf(leaveList.stream().mapToDouble(leave -> leave.getLeftDays().doubleValue()).sum()).setScale(2,
						BigDecimal.ROUND_HALF_UP);
			}
			// 本流程占用的天数，也算在可用天数中
			if (StringUtil.isNotBlank(flowEntId)) {
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEntId));
				filter.getRules().add(new SearchRule("valid", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
				List<SpecialAttendanceRecord> applyingList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(applyingList)) {
					leftDays = leftDays.add(applyingList.get(0).getDays());
				}
			}
			if (leftDays.compareTo(BigDecimal.ZERO) == 0) {
				logger.info("员工：" + user.getRealName() + "没有可用调休");
			} else {
				logger.info("员工：" + user.getRealName() + "剩余可用调休：" + leftDays.toPlainString() + "天");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return leftDays;
	}

	/**
	 * 获取可用婚假天数
	 *
	 * @param user
	 *            员工
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 * @param applyTime
	 *            申请时间
	 * @param leaveType
	 *            请假类型
	 * @param flowEntId
	 *            请假流程id
	 * @return
	 */
	@Override
	public BigDecimal getWeddingLeaveLeftDays(User user, Date startTime, Date endTime, Timestamp applyTime, int leaveType, String flowEntId) {
		BigDecimal leftDays = new BigDecimal(0);

		try {
			Parameter parameter = null;
			if (user.getMaritalStatus() == MaritalStatus.SINGLE.getCode()) {
				leftDays = new BigDecimal(Constants.WEDDING_LEAVE_DAYS_1);
				parameter = parameterService.readOneByProperty("paramkey", Constants.WEDDING_LEAVE_DAYS_KEY_1);
			} else {
				leftDays = new BigDecimal(Constants.WEDDING_LEAVE_DAYS_2);
				parameter = parameterService.readOneByProperty("paramkey", Constants.WEDDING_LEAVE_DAYS_KEY_2);
			}
			if (null != parameter && StringUtil.isNotBlank(parameter.getParamvalue())) {
				leftDays = new BigDecimal(parameter.getParamvalue());
			}
			// 本流程占用的天数，也算在可用天数中
			if (StringUtil.isNotBlank(flowEntId)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEntId));
				filter.getRules().add(new SearchRule("valid", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
				List<SpecialAttendanceRecord> applyingList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(applyingList)) {
					leftDays = leftDays.add(applyingList.get(0).getDays());
				}
			}
			if (leftDays.compareTo(BigDecimal.ZERO) == 0) {
				logger.info("员工：" + user.getRealName() + "没有可用婚假");
			} else {
				logger.info("员工：" + user.getRealName() + "剩余可用婚假：" + leftDays.toPlainString() + "天");
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return leftDays;
	}

	/**
	 * 计算请假天数
	 * 
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	@Override
	public BigDecimal getLeaveDays(Date startTime, Date endTime) {
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
		String[] amWorkStartTime = amWorkTime[0].split(":"); // [8, 30]
		String[] amWorkEndTime = amWorkTime[1].split(":"); // [11, 45]
		String[] pmWorkStartTime = pmWorkTime[0].split(":"); // [13, 15]
		String[] pmWorkEndTime = pmWorkTime[1].split(":"); // [18, 0]

		// 上下班的时分折算成一天的分钟
		int amWorkStart = Integer.parseInt(amWorkStartTime[0]) * 60 + Integer.parseInt(amWorkStartTime[1]);
		int amWorkEnd = Integer.parseInt(amWorkEndTime[0]) * 60 + Integer.parseInt(amWorkEndTime[1]);
		int pmWorkStart = Integer.parseInt(pmWorkStartTime[0]) * 60 + Integer.parseInt(pmWorkStartTime[1]);
		int pmWorkEnd = Integer.parseInt(pmWorkEndTime[0]) * 60 + Integer.parseInt(pmWorkEndTime[1]);
		// 一个工作日的工作分钟数
		int totalWorkTime = amWorkEnd - amWorkStart + pmWorkEnd - pmWorkStart;

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		// 请假时间的时分折算成一天的分钟
		int leaveTimeStart = start.get(Calendar.HOUR_OF_DAY) * 60 + start.get(Calendar.MINUTE);
		int leaveTimeEnd = end.get(Calendar.HOUR_OF_DAY) * 60 + end.get(Calendar.MINUTE);
		// 请假时间随上下班时间调整
		if (leaveTimeStart <= amWorkStart) {
			leaveTimeStart = amWorkStart;
		}
		if (leaveTimeEnd <= amWorkStart) {
			leaveTimeEnd = amWorkStart;
		}
		if (leaveTimeStart >= pmWorkEnd) {
			leaveTimeStart = pmWorkEnd;
		}
		if (leaveTimeEnd >= pmWorkEnd) {
			leaveTimeEnd = pmWorkEnd;
		}

		BigDecimal workDays = null;
		int diffDays = DateUtil.getDiffDays(DateUtil.getDateStartDateTime(end.getTime()), DateUtil.getDateStartDateTime(start.getTime()));
		int diffMins = leaveTimeEnd - leaveTimeStart;
		if (diffDays == 0 || diffMins >= 0) {
			// 同一工作日，或 跨工作日且结束时间的时分在开始时间的时分之后
			// 如：2020-08-18 08:30:00 - 2020-08-18 18:00:00
			// 2020-08-18 08:30:00 - 2020-08-21 18:00:00
			if (leaveTimeStart <= amWorkEnd && pmWorkStart <= leaveTimeEnd) {
				// __8_|_11__13_|_18__
				diffMins -= pmWorkStart - amWorkEnd;
			} else if (leaveTimeStart <= amWorkEnd && amWorkEnd <= leaveTimeEnd && leaveTimeEnd <= pmWorkStart) {
				// __8_|_11_|_13__18__
				diffMins -= leaveTimeEnd - amWorkEnd;
			} else if (amWorkEnd <= leaveTimeStart && leaveTimeStart <= pmWorkStart && leaveTimeEnd <= pmWorkStart) {
				// __8__11_|_|_13__18__
				diffMins -= leaveTimeEnd - leaveTimeStart;
			} else if (amWorkEnd <= leaveTimeStart && leaveTimeStart <= pmWorkStart && pmWorkStart <= leaveTimeEnd) {
				// __8__11_|_13_|_18__
				diffMins -= pmWorkStart - leaveTimeStart;
			}
			workDays = new BigDecimal(diffDays).add(new BigDecimal(diffMins).divide(new BigDecimal(totalWorkTime), 2, BigDecimal.ROUND_HALF_UP));
		} else if (diffDays > 0 && diffMins < 0) {
			// 跨工作日，且结束时间的时分在开始时间的时分之前
			// 如：2020-08-18 13:15:00 - 2020-08-21 11:45:00
			diffDays--;
			diffMins = 0;
			// 结束时间
			if (amWorkStart <= leaveTimeEnd && leaveTimeEnd <= amWorkEnd) {
				// __8_|_11__13__18__
				diffMins += leaveTimeEnd - amWorkStart;
			} else if (amWorkEnd <= leaveTimeEnd && leaveTimeEnd <= pmWorkStart) {
				// __8__11_|_13__18__
				diffMins += amWorkEnd - amWorkStart;
			} else if (pmWorkStart <= leaveTimeEnd && leaveTimeEnd <= pmWorkEnd) {
				// __8__11__13_|_18__
				diffMins += (amWorkEnd - amWorkStart) + (leaveTimeEnd - pmWorkStart);
			}
			workDays = new BigDecimal(diffDays).add(new BigDecimal(diffMins).divide(new BigDecimal(totalWorkTime), 2, BigDecimal.ROUND_HALF_UP));

			diffMins = 0;
			// 开始时间
			if (amWorkStart <= leaveTimeStart && leaveTimeStart <= amWorkEnd) {
				// __8_|_11__13__18__
				diffMins += (amWorkEnd - leaveTimeStart) + (pmWorkEnd - pmWorkStart);
			} else if (amWorkEnd <= leaveTimeStart && leaveTimeStart <= pmWorkStart) {
				// __8__11_|_13__18__
				diffMins += pmWorkEnd - pmWorkStart;
			} else if (pmWorkStart <= leaveTimeStart && leaveTimeStart <= pmWorkEnd) {
				// __8__11__13_|_18__
				diffMins += pmWorkEnd - leaveTimeStart;
			}
			workDays = workDays.add(new BigDecimal(diffMins).divide(new BigDecimal(totalWorkTime), 2, BigDecimal.ROUND_HALF_UP));
		}
		return workDays;
	}

	@Override
	public BigDecimal getLeaveDaysByTimeShot(Date startTime, Date endTime) {
		//判断开始时间是上午还是下午
		GregorianCalendar gre = new GregorianCalendar();
		gre.setTime(startTime);
		int startType = gre.get(GregorianCalendar.AM_PM);
		gre.setTime(endTime);
		int endType = gre.get(GregorianCalendar.AM_PM);
		BigDecimal days = getLeaveDays(startTime,endTime);
		days = days.setScale(0, BigDecimal.ROUND_DOWN);
		if(startType == 0 && endType == 0){
			//都是上午时间 +0.5天
			days = days.add(new BigDecimal(0.5));
		}else if(startType == 0 && endType == 1){
			//开始时间：上午  结束时间：下午
		}else if(startType == 1 && endType == 0){
			//开始时间：下午 结束时间：上午
		}else{
			//开始时间：下午 结束时间：下午
			days = days.add(new BigDecimal(0.5));
		}
		return days;
	}

}
