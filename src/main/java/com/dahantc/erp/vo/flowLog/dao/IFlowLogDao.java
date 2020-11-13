package com.dahantc.erp.vo.flowLog.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;

public interface IFlowLogDao {
	FlowLog read(Serializable id) throws DaoException;

	boolean save(FlowLog entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(FlowLog enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<FlowLog> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<FlowLog> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<FlowLog> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<FlowLog> objs) throws DaoException;

	boolean saveByBatch(List<FlowLog> objs) throws DaoException;

	boolean deleteByBatch(List<FlowLog> objs) throws DaoException;
	
	List<FlowLog> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
