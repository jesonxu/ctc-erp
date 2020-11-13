package com.dahantc.erp.vo.flowLog.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;

public interface IFlowLogService {
	FlowLog read(Serializable id) throws ServiceException;

	boolean save(FlowLog entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(FlowLog enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<FlowLog> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;
	
	List<FlowLog> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<FlowLog> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
	
	List<FlowLog> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;
	
	boolean saveByBatch(List<FlowLog> objs) throws ServiceException;
}
