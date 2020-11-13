package com.dahantc.erp.vo.customerHistory.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.customerHistory.entity.CustomerHistory;

public interface ICustomerHistoryService {
	CustomerHistory read(Serializable id) throws ServiceException;

	boolean save(CustomerHistory entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(CustomerHistory enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<CustomerHistory> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<CustomerHistory> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<CustomerHistory> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<CustomerHistory> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<CustomerHistory> objs) throws ServiceException;
}
