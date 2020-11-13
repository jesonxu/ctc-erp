package com.dahantc.erp.vo.product.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.product.entity.Product;

public interface IProductDao {
	Product read(Serializable id) throws DaoException;

	boolean save(Product entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Product enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Product> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<Product> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<Product> objs) throws DaoException;

	boolean saveByBatch(List<Product> objs) throws DaoException;

	boolean deleteByBatch(List<Product> objs) throws DaoException;
}
