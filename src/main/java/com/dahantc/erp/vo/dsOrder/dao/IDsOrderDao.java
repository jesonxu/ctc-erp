package com.dahantc.erp.vo.dsOrder.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;

public interface IDsOrderDao {
	DsOrder read(Serializable id) throws DaoException;

	boolean save(DsOrder entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DsOrder enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DsOrder> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DsOrder> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DsOrder> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DsOrder> objs) throws DaoException;

	boolean saveByBatch(List<DsOrder> objs) throws DaoException;

	boolean deleteByBatch(List<DsOrder> objs) throws DaoException;
	
	List<DsOrder> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
