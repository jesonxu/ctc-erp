package com.dahantc.erp.vo.goal.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.enums.GoalType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.goal.dao.IGoalDao;
import com.dahantc.erp.vo.goal.entity.Goal;
import com.dahantc.erp.vo.goal.service.IGoalService;

@Service("goalService")
public class GoalServiceImpl implements IGoalService {
	private static Logger logger = LogManager.getLogger(GoalServiceImpl.class);

	@Autowired
	private IGoalDao goalDao;

	@Override
	public Goal read(Serializable id) throws ServiceException {
		try {
			return goalDao.read(id);
		} catch (Exception e) {
			logger.error("读取销售业绩目标表失败", e);
			throw new ServiceException("读取销售业绩目标表失败", e);
		}
	}

	@Override
	public boolean save(Goal entity) throws ServiceException {
		try {
			return goalDao.save(entity);
		} catch (Exception e) {
			logger.error("保存销售业绩目标表失败", e);
			throw new ServiceException("保存销售业绩目标表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<Goal> objs) throws ServiceException {
		try {
			return goalDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return goalDao.delete(id);
		} catch (Exception e) {
			logger.error("删除销售业绩目标表失败", e);
			throw new ServiceException("删除销售业绩目标表失败", e);
		}
	}

	@Override
	public boolean update(Goal enterprise) throws ServiceException {
		try {
			return goalDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新销售业绩目标表失败", e);
			throw new ServiceException("更新销售业绩目标表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return goalDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询销售业绩目标表数量失败", e);
			throw new ServiceException("查询销售业绩目标表数量失败", e);
		}
	}

	@Override
	public PageResult<Goal> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return goalDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询销售业绩目标表分页信息失败", e);
			throw new ServiceException("查询销售业绩目标表分页信息失败", e);
		}
	}

	@Override
	public List<Goal> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return goalDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询销售业绩目标表失败", e);
			throw new ServiceException("查询销售业绩目标表失败", e);
		}
	}

	@Override
	public List<Goal> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return goalDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询销售业绩目标表失败", e);
			throw new ServiceException("查询销售业绩目标表失败", e);
		}
	}

	@Override
	public List<Goal> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return goalDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询销售业绩目标表失败", e);
			throw new ServiceException("查询销售业绩目标表失败", e);
		}
	}

	/**
	 * 查目标
	 * 
	 * @param startDate
	 *            开始时间 <=
	 * @param endDate
	 *            结束时间 <
	 * @param onlineUser
	 *            当前用户
	 * @return
	 */
	@Override
	public JSONObject querySaleGoal(Date startDate, Date endDate, OnlineUser onlineUser) {
		JSONObject goalInfo = new JSONObject();
		SearchFilter goalFilter = new SearchFilter();
		goalFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
		goalFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
		goalFilter.getRules().add(new SearchRule("goalType", Constants.ROP_EQ, GoalType.SelfMonth.ordinal()));
		goalFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		try {
			BigDecimal totalGoalReceivable = new BigDecimal(0);
			BigDecimal totalGoalGrossProfit = new BigDecimal(0);
			List<Goal> goalList = queryAllBySearchFilter(goalFilter);
			if (!CollectionUtils.isEmpty(goalList)) {
				for (Goal goal : goalList) {
					totalGoalGrossProfit = totalGoalGrossProfit.add(goal.getGrossProfit());
					totalGoalReceivable = totalGoalReceivable.add(goal.getReceivables());
				}
			}
			goalInfo.put("totalGoalReceivable", totalGoalReceivable.toPlainString());
			goalInfo.put("totalGoalGrossProfit", totalGoalGrossProfit.toPlainString());
			return goalInfo;
		} catch (ServiceException e) {
			logger.error("", e);
			return null;
		}
	}
}
