package com.dahantc.erp.vo.customer.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.customer.entity.Customer;

public interface ICustomerDao {
	Customer read(Serializable id) throws DaoException;

	boolean save(Customer entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(Customer enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<Customer> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<Customer> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<Customer> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<Customer> objs) throws DaoException;

	boolean saveByBatch(List<Customer> objs) throws DaoException;

	boolean deleteByBatch(List<Customer> objs) throws DaoException;
	
	List<Customer> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	/**
	 * 通过HQL进行分页查询
	 * 
	 * @param hql
	 *            hql
	 * @param countHql
	 *            计数Hql
	 * @param params
	 *            参数
	 * @param page
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @return 分页数据
	 */
	PageResult<Customer> findPageByHql(final String hql, String countHql,final Map<String, Object> params,Integer page,Integer pageSize) throws DaoException;
}
