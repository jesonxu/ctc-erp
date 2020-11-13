package com.dahantc.erp.vo.supplier.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.supplier.entity.Supplier;

public interface ISupplierDao {
	Supplier read(Serializable id) throws DaoException;

	boolean save(Supplier entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Supplier enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Supplier> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Supplier> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Supplier> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<Supplier> objs) throws DaoException;

	boolean saveByBatch(List<Supplier> objs) throws DaoException;

	boolean deleteByBatch(List<Supplier> objs) throws DaoException;

	List<Supplier> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
