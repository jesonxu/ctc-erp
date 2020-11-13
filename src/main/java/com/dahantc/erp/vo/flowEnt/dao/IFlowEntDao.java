package com.dahantc.erp.vo.flowEnt.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;

public interface IFlowEntDao {
	FlowEnt read(Serializable id) throws DaoException;

	boolean save(FlowEnt entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(FlowEnt enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<FlowEnt> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<FlowEnt> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<FlowEnt> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<FlowEnt> objs) throws DaoException;

	boolean saveByBatch(List<FlowEnt> objs) throws DaoException;

	boolean deleteByBatch(List<FlowEnt> objs) throws DaoException;
	
	List<FlowEnt> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;
}
