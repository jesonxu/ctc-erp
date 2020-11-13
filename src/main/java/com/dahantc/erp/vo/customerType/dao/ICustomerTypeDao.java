package com.dahantc.erp.vo.customerType.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.customerType.entity.CustomerType;

public interface ICustomerTypeDao {
	CustomerType read(Serializable id) throws DaoException;

	boolean save(CustomerType entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(CustomerType enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<CustomerType> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<CustomerType> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<CustomerType> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<CustomerType> objs) throws DaoException;

	boolean saveByBatch(List<CustomerType> objs) throws DaoException;

	boolean deleteByBatch(List<CustomerType> objs) throws DaoException;
	
	List<CustomerType> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	CustomerType readOneByProperty(String property, Object value) throws DaoException;
}
