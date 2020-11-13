package com.dahantc.erp.vo.tj.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.tj.entity.CustomerProductTj;

public interface ICustomerProductTjService {
	CustomerProductTj read(Serializable id) throws ServiceException;

	boolean save(CustomerProductTj entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CustomerProductTj enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<CustomerProductTj> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<CustomerProductTj> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CustomerProductTj> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<CustomerProductTj> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<CustomerProductTj> objs) throws ServiceException;
}
