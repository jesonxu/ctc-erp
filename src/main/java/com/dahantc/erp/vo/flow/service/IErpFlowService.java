package com.dahantc.erp.vo.flow.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.flow.AddFlowDto;
import com.dahantc.erp.dto.flow.FlowDetail;
import com.dahantc.erp.vo.flow.entity.ErpFlow;

public interface IErpFlowService {
	ErpFlow read(Serializable id) throws ServiceException;

	boolean save(ErpFlow entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(ErpFlow enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<ErpFlow> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<ErpFlow> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<ErpFlow> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<ErpFlow> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<ErpFlow> objs) throws ServiceException;

	boolean addFlow(AddFlowDto req) throws ServiceException;

	boolean editFlow(AddFlowDto req) throws ServiceException;

	/**
	 * 根据流程的Id 查询流程的名称
	 * @param flowIds 流程id
	 * @return id - 名称
	 */
	Map<String,String> findFlowNameByIds(List<String> flowIds);

	List<String> checkFlowViewer(OnlineUser onlineUser, List<String> flowIdList);

	/**
	 * 根据ID集合 查询流程信息
	 * @param flowIds 流程id 集合
	 * @return 流程信息
	 */
	List<ErpFlow> findFlowByIds(List<String> flowIds);

	/**
	 * 获取用户可以申请的所有流程
	 *
	 * @param user 当前登录用户
	 * @return 流程信息
	 */
	List<ErpFlow> queryUserAllFlow(OnlineUser user);

	/**
	 * 根据流程id 查询流程信息
	 *
	 * @param flowId 流程id
	 * @return 流程详情
	 */
	FlowDetail queryFlowDetailById(String flowId);
}
