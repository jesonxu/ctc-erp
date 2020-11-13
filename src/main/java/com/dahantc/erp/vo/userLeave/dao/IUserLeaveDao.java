package com.dahantc.erp.vo.userLeave.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.userLeave.entity.UserLeave;

public interface IUserLeaveDao {
	UserLeave read(Serializable id) throws DaoException;

	boolean save(UserLeave entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(UserLeave enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<UserLeave> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<UserLeave> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<UserLeave> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<UserLeave> objs) throws DaoException;

	boolean saveByBatch(List<UserLeave> objs) throws DaoException;

	boolean deleteByBatch(List<UserLeave> objs) throws DaoException;
	
	List<UserLeave> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	UserLeave readOneByProperty(String property, Object value) throws DaoException;
}
