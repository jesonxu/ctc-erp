package com.dahantc.erp.vo.dsOrderDetail.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.dsOrderDetail.entity.DsOrderDetail;

public interface IDsOrderDetailService {
	DsOrderDetail read(Serializable id) throws ServiceException;

	boolean save(DsOrderDetail entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(DsOrderDetail enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<DsOrderDetail> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<DsOrderDetail> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<DsOrderDetail> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<DsOrderDetail> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<DsOrderDetail> objs) throws ServiceException;
}
