package com.dahantc.erp.vo.dailyExpenseTj.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dailyExpenseTj.entity.Dailyexpenses;

public interface IDailyexpensesDao {
	Dailyexpenses read(Serializable id) throws DaoException;

	boolean save(Dailyexpenses entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Dailyexpenses enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Dailyexpenses> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<Dailyexpenses> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Dailyexpenses> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<Dailyexpenses> objs) throws DaoException;

	boolean saveByBatch(List<Dailyexpenses> objs) throws DaoException;

	boolean deleteByBatch(List<Dailyexpenses> objs) throws DaoException;
	
	List<Dailyexpenses> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
