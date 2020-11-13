package com.dahantc.erp.vo.flowNode.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;

public interface IFlowNodeService {
	FlowNode read(Serializable id) throws ServiceException;

	boolean save(FlowNode entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(FlowNode entity) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<FlowNode> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<FlowNode> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<FlowNode> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<FlowNode> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	List<Object> findBySql(String sql,Object[] params) throws ServiceException;

	boolean saveByBatch(List<FlowNode> objs) throws ServiceException;

	boolean deleteByBatch(List<FlowNode> objs) throws ServiceException;

	/**
	 * 根据id集合查询流程节点
	 * @param ids id集合
	 * @return 流程节点信息
	 */
	List<FlowNode> findFlowNodeByIds(List<String> ids);

}
