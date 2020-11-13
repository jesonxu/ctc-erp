package com.dahantc.erp.vo.goal.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.goal.entity.Goal;

public interface IGoalService {
	Goal read(Serializable id) throws ServiceException;

	boolean save(Goal entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(Goal enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<Goal> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<Goal> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<Goal> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<Goal> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<Goal> objs) throws ServiceException;

	JSONObject querySaleGoal(Date startDate, Date endDate, OnlineUser onlineUser);
}
