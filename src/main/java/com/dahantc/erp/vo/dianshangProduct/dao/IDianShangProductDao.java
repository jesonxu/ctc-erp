package com.dahantc.erp.vo.dianshangProduct.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.dianshangProduct.entity.DianShangProduct;

public interface IDianShangProductDao {
	DianShangProduct read(Serializable id) throws DaoException;

	boolean save(DianShangProduct entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(DianShangProduct enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<DianShangProduct> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<DianShangProduct> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<DianShangProduct> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<DianShangProduct> objs) throws DaoException;

	boolean saveByBatch(List<DianShangProduct> objs) throws DaoException;

	boolean deleteByBatch(List<DianShangProduct> objs) throws DaoException;
	
	List<DianShangProduct> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
