package com.dahantc.erp.vo.msgCenter.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.dto.messageCenter.MsgDetailDto;
import com.dahantc.erp.enums.MessageType;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.customer.CustomerAction;
import com.dahantc.erp.dto.messageCenter.MsgCenterDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.msgCenter.dao.IMsgCenterDao;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import org.springframework.util.CollectionUtils;

@Service("MsgCenterService")
public class MsgCenterServiceImpl implements IMsgCenterService {

	public static final Logger logger = LoggerFactory.getLogger(CustomerAction.class);

	@Autowired
	public IMsgCenterDao msgCenterDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IMsgDetailService msgDetailService;

	@Autowired
	private IUserService userService;

	@Override
	public PageResult<MsgCenter> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return msgCenterDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询提示分页信息失败", e);
			throw new ServiceException("查询提示分页信息失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return msgCenterDao.getCountByDate(filter);
		} catch (Exception e) {
			logger.error("查询客户信息数量失败", e);
			throw new ServiceException("查询客户信息数量失败", e);
		}
	}

	@Override
	public List<MsgCenter> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return msgCenterDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询客户信息失败", e);
			throw new ServiceException("查询客户信息失败", e);
		}
	}

	@Override
	public boolean save(MsgCenter entity) throws ServiceException {
		try {
			return msgCenterDao.save(entity);
		} catch (Exception e) {
			logger.error("保存消息阅读计详情表失败", e);
			throw new ServiceException("保存消息阅读计详情表失败", e);
		}
	}

	/**
	 * 获取客户一个月内的警告日志数量
	 *
	 * @param customerIds
	 *            客户id
	 * @param month
	 *            时间
	 * @return 一个月内客户的警告信息数量
	 */
	@Override
	public Map<String, Integer> queryCustomerWarnCount(List<String> customerIds, int month) {
		Map<String, Integer> customerWarningInfo = new HashMap<>();
		if (customerIds == null || customerIds.isEmpty()) {
			return customerWarningInfo;
		}
		String hql = " select m.messagesourceid, count(1) from MsgCenter m where m.infotype =:infoType and m.messagesourceid in(:cId) and m.wtime >= :wTime group by m.messagesourceid ";
		Map<String, Object> params = new HashMap<>();
		params.put("infoType", MsgCenter.CUSTOMER_WARNING);
		params.put("cId", customerIds);
		params.put("wTime", new Timestamp(DateUtil.getMonthBefore(month)));
		List<Object> msgCountInfo = null;
		try {
			msgCountInfo = baseDao.findByhql(hql, params, Integer.MAX_VALUE);
		} catch (BaseException e) {
			logger.error("通过HQL查詢客户在一个月内的警告消息数量异常", e);
		}
		if (msgCountInfo != null && !msgCountInfo.isEmpty()) {
			for (Object row : msgCountInfo) {
				if (row.getClass().isArray()) {
					Object[] rowInfo = (Object[]) row;
					if (rowInfo.length >= 2) {
						String cId = String.valueOf(rowInfo[0]);
						int count = 0;
						if (rowInfo[1] instanceof Number) {
							count = ((Number) rowInfo[1]).intValue();
						}
						customerWarningInfo.put(cId, count);
					}
				}
			}
		}
		return customerWarningInfo;
	}

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
	@Override
	public PageResult<MsgCenterDto> queryMsgInfoByPage(OnlineUser user, Integer pageSize, Integer currentPage, String time, String userId, Integer msgType) {
		// 查询数据的HQL
		StringBuilder dataHql = new StringBuilder("select c.messageid,c.infotype,c.messagesourceid,c.messagedetail,c.wtime,d.state "
				+ "from MsgCenter c inner join MsgDetail d on c.messageid = d.messageid ");
		// 统计数量的HQL
		StringBuilder countHql = new StringBuilder("select count(1) from MsgCenter c inner join MsgDetail d on c.messageid = d.messageid ");
		// 条件语句
		StringBuilder whereHql = new StringBuilder(" where d.userid = :userId ");
		// 参数
		Map<String, Object> params = new HashMap<>();
		params.put("userId", user.getUser().getOssUserId());
		if (StringUtil.isNotBlank(userId)) {
			whereHql.append(" and c.ossUserId = :sourceUserId ");
			params.put("sourceUserId", userId);
		}
		String startTime = "";
		String endTime = "";
		if (StringUtil.isNotBlank(time)) {
			String[] timeInfo = time.split("~");
			if (timeInfo.length >= 2) {
				startTime = timeInfo[0];
				endTime = timeInfo[1];
			}
		}
		if (StringUtil.isNotBlank(startTime)) {
			whereHql.append(" and c.wtime >= :startTime ");
			params.put("startTime", DateUtil.convert(startTime, DateUtil.format1));
		}
		if (StringUtil.isNotBlank(endTime)) {
			whereHql.append(" and c.wtime <= :endTime ");
			params.put("endTime", DateUtil.convert(endTime + " 23:59:59", DateUtil.format2));
		}
		if (msgType != null) {
			whereHql.append(" and c.infotype = :infotype ");
			params.put("infotype", msgType);
		}
		try {
			String dataHqlFull = dataHql.append(whereHql).append(" order by d.state desc,c.wtime desc").toString();
			String countHqlFull = countHql.append(whereHql).toString();
			logger.info("分页查询消息数据HQL：{}", dataHqlFull);
			logger.info("分页查询消息数量HQL：{}", countHqlFull);
			PageResult<?> pageResult = baseDao.findByhql(dataHqlFull, countHqlFull, params, pageSize, currentPage);
			long count = pageResult.getCount();
			if (count == 0) {
				return new PageResult<>(null, 0);
			}
			List<?> datas = pageResult.getData();
			List<MsgCenterDto> msgInfos = new ArrayList<>(datas.size());
			for (Object object : datas) {
				if (object.getClass().isArray()) {
					Object[] row = (Object[]) object;
					MsgCenterDto msgCenterDto = new MsgCenterDto();
					msgCenterDto.setMessageid((row[0]).toString());
					msgCenterDto.setInfotype(Integer.parseInt((row[1].toString())));
					msgCenterDto.setMessagesourceid((row[2]).toString());
					msgCenterDto.setMessagedetail(row[3].toString());
					msgCenterDto.setWtime((Date) row[4]);
					msgCenterDto.setState(Integer.parseInt((row[5].toString())));
					msgInfos.add(msgCenterDto);
				}
			}
			return new PageResult<>(msgInfos, pageResult.getCurrentPage(), pageResult.getTotalPages(), count);
		} catch (BaseException e) {
			logger.error("分页查询消息异常", e);
		}
		return null;
	}

	@Override
	public boolean saveByBatch(List<MsgCenter> objs) throws ServiceException {
		try {
			return msgCenterDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 保存一条消息，并通知给相关用户
	 * @param user			流程发起人
	 * @param alarmUserIds	要通知的人
	 * @param msgDetail		消息内容
	 * @param sourceId		消息源表id
	 */
	@Override
	public void buildMessage(User user, List<String> alarmUserIds, String msgDetail, String sourceId) {
		// 消息
		MsgCenter msgCenter = new MsgCenter();
		msgCenter.setInfotype(MessageType.ANOMALOUS_FLOW.getCode());
		msgCenter.setOssUserId(user.getOssUserId());
		msgCenter.setWtime(new Date());
		msgCenter.setMessagesourceid(sourceId);
		msgCenter.setMessagedetail(msgDetail);
		boolean result = false;
		try {
			result = save(msgCenter);
		} catch (ServiceException e) {
			logger.error("保存异常流程信息异常", e);
		}
		if (!result) {
			return;
		}
		// 消息提醒给谁
		List<MsgDetail> msgDetails = new ArrayList<MsgDetail>();
		for (String userId : alarmUserIds) {
			MsgDetail msgDetailEntity = new MsgDetail();
			msgDetailEntity.setUserid(userId);
			msgDetailEntity.setState(MsgDetail.NOT_READ);
			msgDetailEntity.setWtime(new Date());
			msgDetailEntity.setMessageid(msgCenter.getMessageid());
			msgDetails.add(msgDetailEntity);
		}
		try {
			msgDetailService.saveByBatch(msgDetails);
		} catch (ServiceException e) {
			logger.error("保存异常流程信息给相关用户异常", e);
		}
	}

	@Override
	public List<MsgDetailDto> queryMsgAndDetail(String startDate, String endDate, String deptId, String userId) {
		String sql = "select emc.messageid, emc.infotype, emc.messagesourceid, emc.messagedetail, emc.wtime, emd.userid, eu.realname, eu.deptid, ed.deptname" +
				" from erp_message_center emc left join erp_message_detail emd on emc.messageid = emd.messageid" +
				" left join erp_user eu on emd.userid = eu.ossuserid left join erp_department ed on eu.deptid = ed.deptid" +
				" where emd.userid in (?) and emc.infotype in (?)";

		if (StringUtil.isNotBlank(startDate)) {
			sql += " and emc.wtime >= '" + startDate + " 00:00:00'";
			if (StringUtil.isNotBlank(endDate)) {
				sql += " and emc.wtime <= '" + endDate + " 23:59:59'";
			}
		}
		List<String> userIdList = new ArrayList<>();
		if (StringUtil.isNotBlank(userId)) {
			userIdList.add(userId);
		} else if (StringUtil.isNotBlank(deptId)) {
			// 只查部门领导
			Map<String, List<User>> leaderMap = userService.findAllDeptLeader(Arrays.asList(deptId));
			List<User> leaders = leaderMap.getOrDefault(deptId, new ArrayList<>());
			userIdList.addAll(leaders.stream().map(User::getOssUserId).collect(Collectors.toList()));
		}
		if (CollectionUtils.isEmpty(userIdList)) {
			logger.info("用户为空，userId：" + userId + "，deptId：" + deptId);
		}
		List<Object> params = new ArrayList<>();
		params.add(String.join(",", userIdList));
		params.add(MessageType.ANOMALOUS_FLOW.getCode());
		List<MsgDetailDto> resultList = new ArrayList<>();
		try {
			List<Object[]> result = baseDao.selectSQL(sql, params.toArray());
			// 0messageid，1infotype，2messagesourceid，3messagedetail，4wtime，5userid，6realname，7deptid，8deptname
			if (!CollectionUtils.isEmpty(result)) {
				for (Object[] data : result) {
					MsgDetailDto dto = new MsgDetailDto();
					dto.setMessageId(String.valueOf(data[0]));
					int infoType = ((Number) data[1]).intValue();
					dto.setInfoType(infoType);
					dto.setInfoTypeName(MessageType.getMessageType(infoType));
					dto.setMessageSourceId(String.valueOf(data[2]));
					dto.setMessageDetail(String.valueOf(data[3]));
					dto.setWtime(DateUtil.convert((Date) data[4], DateUtil.format1));
					dto.setOssUserId(String.valueOf(data[5]));
					dto.setRealName(String.valueOf(data[6]));
					dto.setDeptId(String.valueOf(data[7]));
					dto.setDeptName(String.valueOf(data[8]));
					resultList.add(dto);
				}
			}
		} catch (BaseException e) {
			logger.error("查询异常消息异常", e);
		}
		return resultList;
	}
}
