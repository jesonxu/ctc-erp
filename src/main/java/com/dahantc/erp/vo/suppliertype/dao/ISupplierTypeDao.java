package com.dahantc.erp.vo.suppliertype.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;

public interface ISupplierTypeDao {
	SupplierType read(Serializable id) throws DaoException;

	boolean save(SupplierType entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(SupplierType enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<SupplierType> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<SupplierType> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<SupplierType> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<SupplierType> objs) throws DaoException;

	boolean saveByBatch(List<SupplierType> objs) throws DaoException;

	boolean deleteByBatch(List<SupplierType> objs) throws DaoException;

	List<SupplierType> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
