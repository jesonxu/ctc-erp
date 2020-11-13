package com.dahantc.erp.vo.supplierStatistics.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.supplierStatistics.entity.SupplierStatistics;

public interface ISupplierStatisticsDao {

	SupplierStatistics read(Serializable id) throws DaoException;

	boolean save(SupplierStatistics entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SupplierStatistics enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SupplierStatistics> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<SupplierStatistics> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SupplierStatistics> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<SupplierStatistics> objs) throws DaoException;

	boolean saveByBatch(List<SupplierStatistics> objs) throws DaoException;

	boolean deleteByBatch(List<SupplierStatistics> objs) throws DaoException;
	
	List<SupplierStatistics> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
