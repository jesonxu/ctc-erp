package com.dahantc.erp.vo.userLeave.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.LeaveType;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;

public interface IUserLeaveService {
	UserLeave read(Serializable id) throws ServiceException;

	boolean save(UserLeave entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(UserLeave enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<UserLeave> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<UserLeave> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<UserLeave> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<UserLeave> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean updateByBatch(List<UserLeave> objs) throws ServiceException;

	boolean saveByBatch(List<UserLeave> objs) throws ServiceException;

	boolean deleteByBatch(List<UserLeave> objs) throws ServiceException;

	UserLeave readOneByProperty(String property, Object value) throws ServiceException;

	/**
	 * 生成/重新计算员工在指定年的年假
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
	String buildAnnualLeave(OnlineUser onlineUser, String ossUserId, String year, Boolean all);

	/**
	 * 检查是否满足请假条件
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
	 * 	          申请时间
	 * @param days
	 *            前端计算的天数
	 * @return 结果，满足返回空，不满足返回原因
	 */
	String checkUserLeave(LeaveType leaveType, User user, Date startTime, Date endTime, Timestamp applyTime, BigDecimal days, String flowEntId);

	/**
	 * 查询员工的假期
	 * 
	 * @param leaveType
	 *            假期类型，为空时查全部
	 * @param user
	 *            员工
	 * @param startTime
	 *            请假开始时间
	 * @param endTime
	 *            请假结束时间
	 * @return
	 */
	List<UserLeave> readLeaveByUser(LeaveType leaveType, User user, Date startTime, Date endTime);

	BigDecimal getLeaveDays(Date startTime, Date endTime);

	BigDecimal getLeaveDaysByTimeShot(Date startTime, Date endTime);
	// 获取年假剩余天数
	BigDecimal getAnnualLeaveLeftDays(User user, Date startTime, Date endTime, Timestamp applyTime, int leaveType, String flowEntId);

	// 获取调休剩余天数
	BigDecimal getCompensatoryLeaveLeftDays(User user, Date startTime, Date endTime, Timestamp applyTime, int leaveType, String flowEntId);

	// 获取婚假剩余天数
	BigDecimal getWeddingLeaveLeftDays(User user, Date startTime, Date endTime, Timestamp applyTime, int leaveType, String flowEntId);

}
