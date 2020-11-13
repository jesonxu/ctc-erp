package com.dahantc.erp.vo.flowEnt.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.flow.FlowEntForMobileDto;
import com.dahantc.erp.dto.flow.SubFlowCount;
import com.dahantc.erp.dto.flow.UserFlowInfoDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntWithOpt;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.user.entity.User;

public interface IFlowEntService {
	FlowEnt read(Serializable id) throws ServiceException;

	boolean save(FlowEnt entity) throws ServiceException;

	boolean delete(Serializable id) throws ServiceException;

	boolean update(FlowEnt enterprise) throws ServiceException;

	int getCount(SearchFilter filter) throws ServiceException;

	PageResult<FlowEnt> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException;

	List<FlowEnt> findByFilter(int size, int start, SearchFilter filter) throws ServiceException;

	List<FlowEnt> queryAllBySearchFilter(SearchFilter filter) throws ServiceException;

	List<FlowEnt> findByhql(final String hql, final Map<String, Object> params, int maxCount) throws ServiceException;

	boolean saveByBatch(List<FlowEnt> objs) throws ServiceException;

	FlowEnt buildFlowEnt(String title, String flowId, int flowType, String ossUserId, String productId, String supplierId, String viewerRoleId, int entityType)
			throws ServiceException;

	List<FlowEntDealCount> queryFlowEntDealCount(String roleId, String ossUserId) throws ServiceException;

	boolean existSameFlow(String flowId, String productId, String flowMsg) throws ServiceException;

	/**
	 * 根据条件查询 已处理和未处理流程对应的主体id （客户id 或 供应商id）
	 *
	 * @param roleId
	 *            角色id
	 * @param ossUserId
	 *            客户id
	 * @param entityType
	 *            实体类型
	 * @return List<String> 实体的id
	 */
	List<String> queryFlowEntityId(String roleId, String ossUserId, EntityType entityType);

	/**
	 * 根据主体的id（客户 | 供应商 ），角色权限 查询对应的未处理的流程数量
	 *
	 * @param subjId
	 *            主体id
	 * @param userDeptId
	 *            销售部门Id
	 * @param roleId
	 *            角色id
	 * @param entityType
	 *            流程类型
	 * @param userId
	 *            当前用户ID
	 * @return 未处理流程数量
	 */
	List<SubFlowCount> queryFlowCountBySub(List<String> subjId, String userDeptId, String roleId, EntityType entityType, String userId);

	/**
	 * 按月份查询当前角色待处理和已处理的流程
	 * 
	 * @param onlineUser
	 *            当前用户
	 * @param entityType
	 *            主体类型（供应商/客户）
	 * @param flowType
	 *            流程类型（运营/结算）
	 * @param date
	 *            月份yyyy-MM
	 * @param entityId
	 *            主体id（供应商id/客户id）
	 * @param productId
	 *            产品id
	 * @return
	 */
	List<FlowEnt> queryFlowEntByDate(OnlineUser onlineUser, int entityType, int flowType, String date, String entityId, String productId);

	List<FlowEnt> queryFlowEntByDate2(OnlineUser onlineUser, Integer entityType, Integer flowType, String searchStartDate, String date, String entityId,
			String productId, String keyWords, String flowId, boolean queryAll);

	PageResult<FlowEnt> queryFlowEntByPage(OnlineUser onlineUser, Integer entityType, Integer flowType, String date, String entityId, String productId,
			String keyWords, int pageSize, int page);

	PageResult<FlowEnt> queryFlowEntByPage2(OnlineUser onlineUser, Integer entityType, Integer flowType, String startDate, String date, String entityId,
			String productId, String keyWords, int pageSize, int page, boolean queryAll);

	/**
	 * 获取用户的角色的待处理的流程实体id
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param roleId
	 *            指定角色
	 * @return 流程实体id列表
	 */
	public List<String> queryFlowEntByRole(OnlineUser onlineUser, String roleId);

	/**
	 * 按权限查主体id
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param entityType
	 *            主体类型，0供应商，1客户，2电商供应商，为空则查全部
	 * @return
	 */
	public Set<String> getEntityIdSetByEntityType(OnlineUser onlineUser, Integer entityType);

	/**
	 * 获取用户待处理的流程信息（所有 包含供应商、客户的信息）
	 *
	 * @param user
	 *            用户信息
	 * @param roleId
	 *            角色id
	 * @return 用户待处理流程信息
	 */
	List<UserFlowInfoDto> queryUserFlowInfo(User user, String roleId);

	/**
	 * 根据id 批量获取流程信息
	 * 
	 * @param ids
	 *            id集合
	 * @return 流程节点信息
	 */
	List<FlowEnt> queryFlowEntByIds(List<String> ids);

	/**
	 * 分页查询流程信息（通过SQL进行查询 包含是否能编辑信息）
	 *
	 * @param onlineUser
	 *            登录用户
	 * @param entityType
	 *            类型
	 * @param flowType
	 *            流程类型
	 * @param date
	 *            时间
	 * @param entityId
	 *            主体ID
	 * @param productId
	 *            产品ID
	 * @param pageSize
	 *            分页大小
	 * @param page
	 *            当前页
	 * @param keywords
	 *            关键词
	 * @return 流程信息
	 */
	PageResult<FlowEntWithOpt> queryFlowEntByPageSql(OnlineUser onlineUser, Integer entityType, Integer flowType, String date, String entityId,
			String productId, Integer pageSize, Integer page, String keywords);

	/**
	 * 统计用户未处理的流程数
	 * 
	 * @param user
	 *            用户
	 * @param role
	 *            角色id
	 * @return 未处理的数量
	 */
	Integer countUnprocessFlowEntByRole(User user, Role role);

	/**
	 * 查询最早的流程信息
	 *
	 * @param user
	 *            用户
	 * @param role
	 *            角色
	 * @param entityType
	 *            类型
	 * @param flowType
	 *            流程类型
	 * @return 未处理的数量
	 */
	FlowEnt queryEarliestFlowEntByRole(User user, Role role, Integer entityType, Integer flowType);

	/**
	 * 分页查询流程信息（移动端 不能和原来的公用一个方法）
	 * 
	 * @param onlineUser
	 *            登录用户
	 * @param page
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @param searchMonth
	 *            月份
	 * @param searchContent
	 *            内容（关键字）
	 * @return 流程信息
	 */
	PageResult<FlowEntForMobileDto> queryPageForMobile(OnlineUser onlineUser, Integer page, Integer pageSize, String searchMonth, String searchContent);

	/**
	 * 按用户id和角色获取可以看到的流程实体对应的主体ID
	 *
	 * @param roleId
	 *            当前角色
	 * @param ossUserId
	 *            用户id
	 * @param entityType
	 *            主体类型
	 * @return 流程实体id集合
	 */
	Set<String> queryEntityIdFromFlowEnt(String roleId, String ossUserId, Integer entityType);

	/**
	 * 查询超时流程
	 * @return
	 */
	List<FlowEnt> queryTimeOutFlow();

	/**
	 * 判断是账单开票流程还是无账单开票流程 true账单开票流程 false无账单开票流程
	 *
	 * @param flowMsg
	 *            流程内容
	 * @param flowClass
	 *            流程处理类
	 * @return
	 */
	String hasBillOrNot(String flowMsg, String flowClass) throws Exception;

}
