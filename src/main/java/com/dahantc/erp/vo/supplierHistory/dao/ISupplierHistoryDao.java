package com.dahantc.erp.vo.supplierHistory.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.supplierHistory.entity.SupplierHistory;

public interface ISupplierHistoryDao {
	SupplierHistory read(Serializable id) throws DaoException;

	boolean save(SupplierHistory entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SupplierHistory enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SupplierHistory> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<SupplierHistory> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SupplierHistory> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<SupplierHistory> objs) throws DaoException;

	boolean saveByBatch(List<SupplierHistory> objs) throws DaoException;

	boolean deleteByBatch(List<SupplierHistory> objs) throws DaoException;

	List<SupplierHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
