package com.dahantc.erp.vo.supplierStatistics.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.supplierStatistics.entity.SupplierStatistics;

public interface ISupplierStatisticsService {

	SupplierStatistics read(Serializable id) throws ServiceException;

	boolean save(SupplierStatistics entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(SupplierStatistics enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<SupplierStatistics> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<SupplierStatistics> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<SupplierStatistics> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<SupplierStatistics> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<SupplierStatistics> objs) throws ServiceException;
}
