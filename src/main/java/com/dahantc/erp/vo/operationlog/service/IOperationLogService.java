package com.dahantc.erp.vo.operationlog.service;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.operationlog.entity.OperationLog;

public interface IOperationLogService {
	OperationLog read(Serializable id) throws ServiceException;

	boolean save(OperationLog entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(OperationLog enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<OperationLog> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<OperationLog> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;
}
