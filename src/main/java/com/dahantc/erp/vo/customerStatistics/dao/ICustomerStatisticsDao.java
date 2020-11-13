package com.dahantc.erp.vo.customerStatistics.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.customerStatistics.entity.CustomerStatistics;

public interface ICustomerStatisticsDao {
	CustomerStatistics read(Serializable id) throws DaoException;

	boolean save(CustomerStatistics entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(CustomerStatistics enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<CustomerStatistics> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<CustomerStatistics> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<CustomerStatistics> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<CustomerStatistics> objs) throws DaoException;

	boolean saveByBatch(List<CustomerStatistics> objs) throws DaoException;

	boolean deleteByBatch(List<CustomerStatistics> objs) throws DaoException;
	
	List<CustomerStatistics> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
