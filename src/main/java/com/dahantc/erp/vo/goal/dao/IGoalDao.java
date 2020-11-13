package com.dahantc.erp.vo.goal.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.goal.entity.Goal;

public interface IGoalDao {
	Goal read(Serializable id) throws DaoException;

	boolean save(Goal entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Goal enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Goal> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<Goal> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Goal> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<Goal> objs) throws DaoException;

	boolean saveByBatch(List<Goal> objs) throws DaoException;

	boolean deleteByBatch(List<Goal> objs) throws DaoException;
	
	List<Goal> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
