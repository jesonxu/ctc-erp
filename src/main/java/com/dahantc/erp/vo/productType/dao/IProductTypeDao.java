package com.dahantc.erp.vo.productType.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.productType.entity.ProductType;

public interface IProductTypeDao {
	ProductType read(Serializable id) throws DaoException;

	boolean save(ProductType entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(ProductType enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<ProductType> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<ProductType> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<ProductType> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<ProductType> objs) throws DaoException;

	boolean saveByBatch(List<ProductType> objs) throws DaoException;

	boolean deleteByBatch(List<ProductType> objs) throws DaoException;
	
	List<ProductType> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	ProductType readOneByProperty(String property, Object value) throws DaoException;

}
