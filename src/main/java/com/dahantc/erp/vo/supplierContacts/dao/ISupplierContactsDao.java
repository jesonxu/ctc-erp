package com.dahantc.erp.vo.supplierContacts.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;

public interface ISupplierContactsDao {
	SupplierContacts read(Serializable id) throws DaoException;

	boolean save(SupplierContacts entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SupplierContacts enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SupplierContacts> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<SupplierContacts> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SupplierContacts> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<SupplierContacts> objs) throws DaoException;

	boolean saveByBatch(List<SupplierContacts> objs) throws DaoException;

	boolean deleteByBatch(List<SupplierContacts> objs) throws DaoException;

	List<SupplierContacts> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
