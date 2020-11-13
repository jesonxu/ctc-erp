package com.dahantc.erp.vo.supplierContactsHistory.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.supplierContactsHistory.entity.SupplierContactsHistory;

public interface ISupplierContactsHistoryDao {
	SupplierContactsHistory read(Serializable id) throws DaoException;

	boolean save(SupplierContactsHistory entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SupplierContactsHistory enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SupplierContactsHistory> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<SupplierContactsHistory> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SupplierContactsHistory> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<SupplierContactsHistory> objs) throws DaoException;

	boolean saveByBatch(List<SupplierContactsHistory> objs) throws DaoException;

	boolean deleteByBatch(List<SupplierContactsHistory> objs) throws DaoException;

	List<SupplierContactsHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
