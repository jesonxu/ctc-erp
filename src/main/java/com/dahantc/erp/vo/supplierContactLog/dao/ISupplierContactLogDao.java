package com.dahantc.erp.vo.supplierContactLog.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;

public interface ISupplierContactLogDao {
	SupplierContactLog read(Serializable id) throws DaoException;

	boolean save(SupplierContactLog entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SupplierContactLog enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SupplierContactLog> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<SupplierContactLog> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SupplierContactLog> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<SupplierContactLog> objs) throws DaoException;

	boolean saveByBatch(List<SupplierContactLog> objs) throws DaoException;

	boolean deleteByBatch(List<SupplierContactLog> objs) throws DaoException;

	List<SupplierContactLog> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
