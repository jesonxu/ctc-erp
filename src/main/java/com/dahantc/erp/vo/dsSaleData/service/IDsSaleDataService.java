package com.dahantc.erp.vo.dsSaleData.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsSaleData.entity.DsSaleData;

public interface IDsSaleDataService {
	DsSaleData read(Serializable id) throws ServiceException;

	boolean save(DsSaleData entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsSaleData enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsSaleData> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsSaleData> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsSaleData> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsSaleData> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsSaleData> objs) throws ServiceException;
}
