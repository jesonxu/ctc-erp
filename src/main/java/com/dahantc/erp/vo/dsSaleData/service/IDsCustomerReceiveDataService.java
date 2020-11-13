package com.dahantc.erp.vo.dsSaleData.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsSaleData.entity.DsCustomerReceiveData;

public interface IDsCustomerReceiveDataService {
	DsCustomerReceiveData read(Serializable id) throws ServiceException;

	boolean save(DsCustomerReceiveData entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsCustomerReceiveData enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsCustomerReceiveData> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsCustomerReceiveData> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsCustomerReceiveData> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsCustomerReceiveData> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsCustomerReceiveData> objs) throws ServiceException;
}
