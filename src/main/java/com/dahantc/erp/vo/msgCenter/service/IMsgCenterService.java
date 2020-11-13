package com.dahantc.erp.vo.msgCenter.service;

import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.messageCenter.MsgCenterDto;
import com.dahantc.erp.dto.messageCenter.MsgDetailDto;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.user.entity.User;

public interface IMsgCenterService {

	PageResult<MsgCenter> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	List<MsgCenter> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	boolean save(MsgCenter entity) throws ServiceException;

	/**
	 * 获取客户一个月内的警告日志数量
	 * 
	 * @param customerIds
	 *            客户id
	 * @param month
	 *            时间
	 * @return 一个月内客户的警告信息数量
	 */
	Map<String, Integer> queryCustomerWarnCount(List<String> customerIds, int month);

	/**
	 * 分页查询用户消息信息
	 *
	 * @param user
	 *            当前用户
	 * @param pageSize
	 *            页大小
	 * @param currentPage
	 *            当前页
	 * @param time
	 *            时间（开始时间 结束时间）
	 * @param userId
	 *            员工ID
	 * @param msgType
	 *            消息类型
	 * @return PageResult<MsgCenterDto>
	 */
	PageResult<MsgCenterDto> queryMsgInfoByPage(OnlineUser user, Integer pageSize, Integer currentPage, String time, String userId, Integer msgType);

	boolean saveByBatch(List<MsgCenter> objs) throws ServiceException;

	void buildMessage(User user, List<String> alarmUserIds, String msgDetail, String sourceId);

	List<MsgDetailDto> queryMsgAndDetail(String startDate, String endDate, String deptId, String userIdList);
}
