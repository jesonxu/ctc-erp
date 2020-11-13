package com.dahantc.erp.vo.flowNode.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;

public interface IFlowNodeDao {
	FlowNode read(Serializable id) throws DaoException;

	boolean save(FlowNode entity) throws DaoException;

	boolean delete(Serializable id) throws DaoException;

	boolean update(FlowNode enterprise) throws DaoException;

	int getCountByCriteria(SearchFilter filter) throws DaoException;

	PageResult<FlowNode> findByPages(int pageSize, int currentPage, SearchFilter filter) throws DaoException;
	
	List<FlowNode> findByFilter(int size, int start, SearchFilter filter) throws DaoException;

	List<FlowNode> queryAllBySearchFilter(SearchFilter filter) throws DaoException;
	
	boolean updateByBatch(List<FlowNode> objs) throws DaoException;

	boolean saveByBatch(List<FlowNode> objs) throws DaoException;

	boolean deleteByBatch(List<FlowNode> objs) throws DaoException;
	
	List<FlowNode> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws DaoException;

	List<Object> findBySql(String sql,Object[] params) throws DaoException;
}
