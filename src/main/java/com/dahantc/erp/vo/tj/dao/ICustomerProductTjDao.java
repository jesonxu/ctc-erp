package com.dahantc.erp.vo.tj.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.tj.entity.CustomerProductTj;

public interface ICustomerProductTjDao {
	CustomerProductTj read(Serializable id) throws DaoException;

	boolean save(CustomerProductTj entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(CustomerProductTj enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<CustomerProductTj> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<CustomerProductTj> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<CustomerProductTj> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<CustomerProductTj> objs) throws DaoException;

	boolean saveByBatch(List<CustomerProductTj> objs) throws DaoException;

	boolean deleteByBatch(List<CustomerProductTj> objs) throws DaoException;
	
	List<CustomerProductTj> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
