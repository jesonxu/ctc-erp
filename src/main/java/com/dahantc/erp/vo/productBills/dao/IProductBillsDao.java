package com.dahantc.erp.vo.productBills.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.productBills.entity.ProductBills;

public interface IProductBillsDao {
	ProductBills read(Serializable id) throws DaoException;

	boolean save(ProductBills entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(ProductBills enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<ProductBills> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<ProductBills> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<ProductBills> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<ProductBills> objs) throws DaoException;

	boolean saveByBatch(List<ProductBills> objs) throws DaoException;

	boolean deleteByBatch(List<ProductBills> objs) throws DaoException;

	List<ProductBills> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
