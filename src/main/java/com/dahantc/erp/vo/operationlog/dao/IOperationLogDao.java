package com.dahantc.erp.vo.operationlog.dao;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.operationlog.entity.OperationLog;

public interface IOperationLogDao {
	OperationLog read(Serializable id) throws DaoException;

	boolean save(OperationLog entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(OperationLog enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<OperationLog> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;

	List<OperationLog> queryAllBySearchFilter(SearchFilter filter) throws DaoException;

	boolean updateByBatch(List<OperationLog> objs) throws DaoException;

	boolean saveByBatch(List<OperationLog> objs) throws DaoException;

	boolean deleteByBatch(List<OperationLog> objs) throws DaoException;
}
