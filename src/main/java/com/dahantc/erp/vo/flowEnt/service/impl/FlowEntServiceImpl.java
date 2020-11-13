package com.dahantc.erp.vo.flowEnt.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.enums.FlowType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.flow.FlowEntForMobileDto;
import com.dahantc.erp.dto.flow.SubFlowCount;
import com.dahantc.erp.dto.flow.UserFlowInfoDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowAssociateType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.dao.IFlowEntDao;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntWithOpt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("flowEntService")
public class FlowEntServiceImpl implements IFlowEntService {
	private static Logger logger = LogManager.getLogger(FlowEntServiceImpl.class);

	@Autowired
	private IFlowEntDao flowEntDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Override
	public FlowEnt read(Serializable id) throws ServiceException {
		try {
			return flowEntDao.read(id);
		} catch (Exception e) {
			logger.error("读取流程信息表失败", e);
			throw new ServiceException("读取流程信息表失败", e);
		}
	}

	@Override
	public boolean save(FlowEnt entity) throws ServiceException {
		try {
			return flowEntDao.save(entity);
		} catch (Exception e) {
			logger.error("保存流程信息表失败", e);
			throw new ServiceException("保存流程信息表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<FlowEnt> objs) throws ServiceException {
		try {
			return flowEntDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return flowEntDao.delete(id);
		} catch (Exception e) {
			logger.error("删除流程信息表失败", e);
			throw new ServiceException("删除流程信息表失败", e);
		}
	}

	@Override
	public boolean update(FlowEnt enterprise) throws ServiceException {
		try {
			return flowEntDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新流程信息表失败", e);
			throw new ServiceException("更新流程信息表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return flowEntDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询流程信息表数量失败", e);
			throw new ServiceException("查询流程信息表数量失败", e);
		}
	}

	@Override
	public PageResult<FlowEnt> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return flowEntDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询流程信息表分页信息失败", e);
			throw new ServiceException("查询流程信息表分页信息失败", e);
		}
	}

	@Override
	public List<FlowEnt> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return flowEntDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询流程信息表失败", e);
			throw new ServiceException("查询流程信息表失败", e);
		}
	}

	@Override
	public List<FlowEnt> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return flowEntDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询流程信息表失败", e);
			throw new ServiceException("查询流程信息表失败", e);
		}
	}

	@Override
	public List<FlowEnt> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return flowEntDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询流程信息表失败", e);
			throw new ServiceException("查询流程信息表失败", e);
		}
	}

	/**
	 * 创建流程实体
	 */
	@Override
	public FlowEnt buildFlowEnt(String title, String flowId, int flowType, String ossUserId, String productId, String supplierId, String viewerRoleId,
			int entityType) throws ServiceException {
		FlowEnt flowEnt = new FlowEnt();
		flowEnt.setFlowTitle(title);
		flowEnt.setFlowId(flowId);
		flowEnt.setFlowType(flowType);
		flowEnt.setOssUserId(ossUserId);
		flowEnt.setSupplierId(supplierId);
		flowEnt.setProductId(productId);
		flowEnt.setViewerRoleId(viewerRoleId);
		flowEnt.setEntityType(entityType);
		flowEnt.setWtime(new Timestamp(System.currentTimeMillis()));

		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
		filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
		List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
		if (nodeList != null && nodeList.size() > 0) {
			FlowNode flowNode = nodeList.get(0);
			if (flowNode != null) { // 不要跳过发起人节点
				flowEnt.setNodeId(flowNode.getNodeId());
			}
		}
		return flowEnt;
	}

	/**
	 * 查询当前用户在每个客户的每个产品等待处理的运营/结算流程数
	 *
	 * @param roleId
	 *            角色Id
	 * @param ossUserId
	 *            用户id
	 * @return 每个客户每个产品等待处理的运营/结算流程数
	 * @throws ServiceException
	 */
	@Override
	public List<FlowEntDealCount> queryFlowEntDealCount(String roleId, String ossUserId) throws ServiceException {
		try {
			if (StringUtils.isNoneBlank(roleId, ossUserId)) {
				String hql = "select ent.entityType, ent.flowType, ent.supplierId, ent.productId, year(ent.wtime), month(ent.wtime),count(ent.id) "
						+ " from FlowEnt ent left join FlowNode node on ent.nodeId = node.nodeId " + " where ent.flowStatus in (" + FlowStatus.NO_PASS.ordinal()
						+ ", " + FlowStatus.NOT_AUDIT.ordinal() + ") and node.roleId like :roleId and (node.nodeIndex != 0 or ent.ossUserId = :ossUserId) "
						+ " group by ent.entityType, ent.flowType, ent.supplierId, ent.productId, year(ent.wtime), month(ent.wtime)";
				Map<String, Object> params = new HashMap<>();
				params.put("roleId", "%" + roleId + "%");
				params.put("ossUserId", ossUserId);
				List<Object> list = baseDao.findByhql(hql, params, 0);
				return list.stream().map(obj -> {
					FlowEntDealCount dealCount = new FlowEntDealCount();
					Object[] objArray = (Object[]) obj;
					dealCount.setEntityType(((Number) objArray[0]).intValue());
					dealCount.setFlowType(((Number) objArray[1]).intValue());
					dealCount.setSupplierId((String) objArray[2]);
					dealCount.setProductId((String) objArray[3]);
					dealCount.setYear(((Number) objArray[4]).intValue());
					dealCount.setMonth(((Number) objArray[5]).intValue());
					dealCount.setFlowEntCount(((Number) objArray[6]).intValue());
					return dealCount;
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("查询待处理流程异常：", e, "roleId=" + roleId, "ossUserId=" + ossUserId);
			throw new ServiceException("查询待处理流程异常：", e);
		}
		return new ArrayList<>();
	}

	@Override
	public boolean existSameFlow(String flowId, String productId, String flowMsg) throws ServiceException {
		boolean result = false;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
			filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
			if (StringUtil.isNotBlank(flowMsg)) {
				filter.getRules().add(new SearchRule("flowMsg", Constants.ROP_CN, flowMsg));
			}
			filter.getRules().add(new SearchRule("flowStatus", Constants.ROP_NE, FlowStatus.CANCLE.ordinal()));
			List<FlowEnt> list = queryAllBySearchFilter(filter);
			result = list != null && list.size() > 0;
		} catch (BaseException e) {
			logger.error("校验同样流程是否存在异常，flowId：" + flowId + "，productId：" + productId + "，flowMsg（包含）：" + flowMsg, e);
			throw new ServiceException("校验同样流程是否存在异常：", e);
		}
		return result;
	}

	/**
	 * 根据条件查询 已处理和未处理流程对应的主体id （客户id 或 供应商id）
	 *
	 * @param roleId
	 *            角色id
	 * @param ossUserId
	 *            客户id
	 * @param entityType
	 *            实体类型
	 * @return
	 */
	@Override
	public List<String> queryFlowEntityId(String roleId, String ossUserId, EntityType entityType) {
		// 查询所有待处理和已处理的流程实体id
		Set<String> flowEntIds = queryFlowEntId(roleId, ossUserId);
		if (flowEntIds.isEmpty()) {
			return null;
		}
		List<String> ids = new ArrayList<>(flowEntIds);
		// 根据流程实体id找到对应的实体id（供应商/客户）
		String hql = "select distinct supplierId from FlowEnt where id in (:ids)";
		Map<String, Object> params = new HashMap<>();
		params.put("ids", ids);
		if (entityType != null) {
			hql += " and entityType = :entityType";
			params.put("entityType", entityType.ordinal());
		}
		try {
			List<Object> list = baseDao.findByhql(hql, params, 0);
			return list.stream().map(String::valueOf).collect(Collectors.toList());
		} catch (BaseException e) {
			logger.error("查询流程对应的主体错误", e);
		}
		return null;
	}

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
	 * @return 未处理流程数量
	 */
	@Override
	public List<SubFlowCount> queryFlowCountBySub(List<String> subjId, String userDeptId, String roleId, EntityType entityType, String userId) {
		Map<String, Object> params = new HashMap<>();
		// 流程在归档后 nodeid 为空
		StringBuilder hql = new StringBuilder("SELECT distinct ent.supplierId, obj.deptId , COUNT(ent.id)"
				+ (EntityType.CUSTOMER.ordinal() == entityType.ordinal() ? ", obj.ossuserId" : ", obj.ossUserId")
				+ " FROM FlowEnt ent INNER JOIN FlowNode node ON ent.nodeId = node.nodeId ");
		if (EntityType.CUSTOMER.ordinal() == entityType.ordinal()) {
			hql.append(" inner join Customer obj on obj.customerId = ent.supplierId ");
		} else {
			hql.append(" inner join Supplier obj on obj.supplierId = ent.supplierId ");
		}
		hql.append("WHERE");
		if (!CollectionUtils.isEmpty(subjId)) {
			hql.append(" ent.supplierId IN (:subjId) and ");
			params.put("subjId", subjId);
		}
		if (StringUtils.isNotBlank(userDeptId)) {
			hql.append(" obj.deptId = :userDeptId and ");
			params.put("userDeptId", userDeptId);
		}
		hql.append(" (( node.nodeIndex = 0 and ent.ossUserId = :userId) or node.nodeIndex !=0) ");
		params.put("userId", userId);
		hql.append(" and node.roleId like :roleId AND ent.entityType = :entityType and ent.flowStatus in (");
		hql.append(FlowStatus.NOT_AUDIT.ordinal()).append(",").append(FlowStatus.NO_PASS.ordinal());
		hql.append(") GROUP BY ent.supplierId ");
		params.put("roleId", "%" + roleId + "%");
		params.put("entityType", entityType.ordinal());
		try {
			List<Object> list = baseDao.findByhql(hql.toString(), params, 0);
			if (list != null && !list.isEmpty()) {
				return list.stream().map(obj -> {
					Object[] objArray = (Object[]) obj;
					String subId = String.valueOf(objArray[0]);
					String deptId = String.valueOf(objArray[1]);
					Integer count = ((Number) objArray[2]).intValue();
					String ossUserId = (String) objArray[3];
					return new SubFlowCount(subId, deptId, count, ossUserId);
				}).collect(Collectors.toList());
			}
		} catch (BaseException e) {
			logger.error("主体对应未处理流程错误", e);
		}
		return null;
	}

	/**
	 * 按月份获取待处理和处理过的流程实体（无关键词）
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param entityType
	 *            主体类型（供应商/客户）
	 * @param flowType
	 *            流程类型（运营/结算）
	 * @param date
	 *            日期yyyy-MM
	 * @return 流程实体列表
	 */
	@Override
	public List<FlowEnt> queryFlowEntByDate(OnlineUser onlineUser, int entityType, int flowType, String date, String entityId, String productId) {
		return queryFlowEntByDate2(onlineUser, entityType, flowType, null, date, entityId, productId, null, null, false);
	}

	/**
	 * 按月份获取待处理和处理过的流程实体
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param entityType
	 *            主体类型（供应商/客户）
	 * @param flowType
	 *            流程类型（运营/结算）
	 * @param date
	 *            日期yyyy-MM
	 * @param keyWords
	 *            关键词
	 * @return 流程实体列表
	 */
	@Override
	public List<FlowEnt> queryFlowEntByDate2(OnlineUser onlineUser, Integer entityType, Integer flowType, String startDate, String date, String entityId,
			String productId, String keyWords, String flowId, boolean queryAll) {
		long _start = System.currentTimeMillis();
		logger.info("按月份获取待处理和处理过的流程实体开始，entityType：" + entityType + "，flowType：" + flowType + "，月份：" + date + "，entityId：" + entityId + "，productId："
				+ productId + "，keyWords：" + keyWords);
		try {
			SearchFilter filter = buildQueryFlowEntFilter(null, onlineUser, entityType, flowType, startDate, date, entityId, productId, keyWords, flowId,
					queryAll);
			if (null == filter) {
				return null;
			}
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<FlowEnt> flowList = queryAllBySearchFilter(filter);
			logger.info("按条件和数据权限过滤结束，获取到流程实体数：" + (ListUtils.isEmpty(flowList) ? 0 : flowList.size()));
			logger.info("按月份获取待处理和处理过的流程实体结束，耗时：" + (System.currentTimeMillis() - _start));
			return flowList;
		} catch (ServiceException e) {
			logger.error("按月份获取待处理和处理过的流程实体异常", e);
			return null;
		}
	}

	@Override
	public PageResult<FlowEnt> queryFlowEntByPage(OnlineUser onlineUser, Integer entityType, Integer flowType, String date, String entityId, String productId,
			String keyWords, int pageSize, int page) {
		return queryFlowEntByPage2(onlineUser, entityType, flowType, null, date, entityId, productId, keyWords, pageSize, page, false);
	}

	public PageResult<FlowEnt> queryFlowEntByPage2(OnlineUser onlineUser, Integer entityType, Integer flowType, String startDate, String date, String entityId,
			String productId, String keyWords, int pageSize, int page, boolean queryAll) {
		long _start = System.currentTimeMillis();
		logger.info("分页获取待处理和处理过的流程实体开始，entityType：" + entityType + "，flowType：" + flowType + "，月份：" + startDate + " ~ " + date + "，entityId：" + entityId
				+ "，productId：" + productId + "，keyWords：" + keyWords);
		try {
			SearchFilter filter = buildQueryFlowEntFilter(null, onlineUser, entityType, flowType, startDate, date, entityId, productId, keyWords, null,
					queryAll);
			if (null == filter) {
				return null;
			}
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			PageResult<FlowEnt> flowPage = queryByPages(pageSize, page, filter);
			logger.info("分页获取待处理和处理过的流程实体结束，获取到流程实体数：" + flowPage.getCount() + "，耗时：" + (System.currentTimeMillis() - _start));
			return flowPage;
		} catch (ServiceException e) {
			logger.error("分页获取待处理和处理过的流程实体异常", e);
			return null;
		}
	}

	/**
	 * 组装按权限查流程的filter
	 *
	 * @param filter
	 *            传入filter
	 * @param onlineUser
	 *            当前用户
	 * @param entityType
	 *            查询条件，主体类型，0供应商，1客户，2电商供应商，为空查全部
	 * @param flowType
	 *            查询条件，流程类型，0运营，1结算，2对账，3发票，4销账
	 * @param date
	 *            查询条件，月份yyyy-MM
	 * @param entityId
	 *            过滤条件，指定主体id
	 * @param productId
	 *            过滤条件，指定产品id
	 * @param keyWords
	 *            过滤条件，标题和内容关键词
	 * @return 组装好的filter
	 */
	private SearchFilter buildQueryFlowEntFilter(SearchFilter filter, OnlineUser onlineUser, Integer entityType, Integer flowType, String startDate,
			String date, String entityId, String productId, String keyWords, String flowId, boolean queryAll) {
		filter = null == filter ? new SearchFilter() : filter;
		// 根据用户id和角色id，查出所有能看到的流程实体id
		Set<String> flowEntIds = queryFlowEntId(onlineUser.getRoleId(), onlineUser.getUser().getOssUserId());
		if (CollectionUtils.isEmpty(flowEntIds)) {
			// 没有能看的流程
			logger.info("未查询到流程");
			return null;
		}
		logger.info("按用户和角色获取到流程数：" + flowEntIds.size());
		// 查询到的流程实体id最大范围，在这个范围内按条件过滤
		if (StringUtils.isNotBlank(flowId)) {
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
		}
		filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<String>(flowEntIds)));
		if (null != flowType) {
			filter.getRules().add(new SearchRule("flowType", Constants.ROP_EQ, flowType));
		} else {
			filter.getRules().add(new SearchRule("flowType", Constants.ROP_NE, FlowType.EMPLOYEE.getCode()));
		}
		if (null != entityType) {
			filter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, entityType));
		}
		if (StringUtils.isNotBlank(productId)) {
			// 有产品id过滤条件
			filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, productId));
		} else if (StringUtils.isNotBlank(entityId)) {
			// 有主体id（客户/供应商id）过滤条件
			filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, entityId));
		} else {
			// 无主体和产品过滤条件，按权限查所有主体id
			Set<String> entityIdSet = getEntityIdSetByEntityType(onlineUser, entityType);
			if (CollectionUtils.isEmpty(entityIdSet)) {
				logger.info("用户当前角色下无客户或供应商");
				return null;
			}

			if (queryAll) {

				SearchFilter flowFilter = new SearchFilter();
				flowFilter.getRules().add(new SearchRule("associateType", Constants.ROP_EQ, FlowAssociateType.USER.ordinal()));
				List<ErpFlow> flowList = null;
				try {
					flowList = erpFlowService.queryAllBySearchFilter(flowFilter);
				} catch (ServiceException e) {
					logger.error("", e);
				}
				List<String> flowIdList = new ArrayList<>();
				if (!CollectionUtils.isEmpty(flowList)) {
					flowList.forEach(flow -> {
						flowIdList.add(flow.getFlowId());
					});
				}

				if (!CollectionUtils.isEmpty(flowIdList)) {
					filter.getOrRules().add(new SearchRule[] { new SearchRule("supplierId", Constants.ROP_IN, new ArrayList<>(entityIdSet)),
							new SearchRule("flowId", Constants.ROP_IN, flowIdList) });
				} else {
					filter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, new ArrayList<>(entityIdSet)));
				}

			} else {
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, new ArrayList<>(entityIdSet)));
			}

		}
		// 月份
		Timestamp startTime = null;
		Timestamp endTime = null;
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(date)) {
			startDate = startDate.endsWith("-") ? startDate.substring(0, startDate.length() - 1) : startDate;
			date = date.endsWith("-") ? date.substring(0, date.length() - 1) : date;
			startTime = new Timestamp(DateUtil.convert2(startDate + "-01 00:00:00").getTime());
			endTime = new Timestamp(DateUtil.getNextMonthFirst(DateUtil.convert4(date)).getTime());
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endTime));
		} else {
			if (StringUtils.isNotBlank(startDate)) {
				date = startDate;
			}
			if (StringUtils.isNotBlank(date)) {
				date = date.endsWith("-") ? date.substring(0, date.length() - 1) : date;
				startTime = new Timestamp(DateUtil.convert2(date + "-01 00:00:00").getTime());
				endTime = new Timestamp(DateUtil.getNextMonthFirst(date).getTime());
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endTime));
			}
		}
		// 关键词过滤
		if (StringUtil.isNotBlank(keyWords)) {
			filter.getOrRules()
					.add(new SearchRule[] { new SearchRule("flowTitle", Constants.ROP_CN, keyWords), new SearchRule("flowMsg", Constants.ROP_CN, keyWords) });
		}
		return filter;
	}

	/**
	 * 按主体类型和数据权限获取主体id（客户id、供应商id）
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param entityType
	 *            主体类型，0供应商，1客户，2电商供应商
	 * @return
	 */
	@Override
	public Set<String> getEntityIdSetByEntityType(OnlineUser onlineUser, Integer entityType) {
		Set<String> entityIdSet = null;
		if (entityType == null) {
			// 不指定客户还是供应商，客户和供应商都查
			entityIdSet = new HashSet<>();
			List<Customer> customers = customerService.readCustomers(onlineUser, "", "", "", "");
			if (customers != null && !customers.isEmpty()) {
				List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				entityIdSet.addAll(customerIds);
			}
			List<Supplier> suppliers = supplierService.readSuppliers(onlineUser, "", "", "", "", SearchType.FLOW.ordinal());
			if (suppliers != null && !suppliers.isEmpty()) {
				List<String> supplierIds = suppliers.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
				entityIdSet.addAll(supplierIds);
			}
		} else if (EntityType.CUSTOMER.ordinal() == entityType) {
			// 根据数据权限查客户
			entityIdSet = new HashSet<>();
			List<Customer> customers = customerService.readCustomers(onlineUser, "", "", "", "");
			if (customers != null && !customers.isEmpty()) {
				List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				entityIdSet.addAll(customerIds);
			}
		} else if (EntityType.SUPPLIER.ordinal() == entityType || EntityType.SUPPLIER_DS.ordinal() == entityType) {
			// 根据数据权限查供应商
			entityIdSet = new HashSet<>();
			List<Supplier> suppliers = supplierService.readSuppliers(onlineUser, "", "", "", "", SearchType.FLOW.ordinal());
			if (suppliers != null && !suppliers.isEmpty()) {
				List<String> supplierIds = suppliers.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
				entityIdSet.addAll(supplierIds);
			}
		}
		return entityIdSet;
	}

	/**
	 * 按用户id和角色获取可以看到的流程实体id
	 *
	 * @param roleId
	 *            当前角色
	 * @param ossUserId
	 *            用户id
	 * @return 流程实体id集合
	 */
	private Set<String> queryFlowEntId(String roleId, String ossUserId) {
		logger.info("获取待处理和处理过的流程实体id开始，roleId：" + roleId + "，ossUserId：" + ossUserId);
		Set<String> flowIds = new HashSet<>();
		try {
			// 当前角色 待处理的/全程能看到的 流程实体id，从FLowEnt查（自己创建/自己的角色处理）
			String flowHql1 = "select ent.id from FlowEnt ent left join FlowNode node on ent.nodeId = node.nodeId "
					+ " where node.roleId like :roleId and (node.nodeIndex != 0 or ent.ossUserId = :ossUserId) or ent.viewerRoleId like :viewerRoleId";
			Map<String, Object> params = new HashMap<>();
			params.put("roleId", "%" + roleId + "%");
			params.put("ossUserId", ossUserId);
			params.put("viewerRoleId", "%" + roleId + "%");
			List<Object> flowList1 = baseDao.findByhql(flowHql1, params, 0);
			List<String> flowIds1 = flowList1.stream().map(String::valueOf).collect(Collectors.toList());

			// 当前角色 处理过的 流程实体id，从FlowLog查
			String flowHql2 = "select log.flowEntId from FlowLog log left join FlowNode node on log.nodeId = node.nodeId "
					+ " where (node.roleId like :roleId or log.ossUserId = :ossUserId)";
			params = new HashMap<>();
			params.put("roleId", "%" + roleId + "%");
			params.put("ossUserId", ossUserId);
			List<Object> flowList2 = baseDao.findByhql(flowHql2, params, 0);
			List<String> flowIds2 = flowList2.stream().map(String::valueOf).collect(Collectors.toList());

			// 去重，得到所有流程实体id
			flowIds.addAll(flowIds1);
			flowIds.addAll(flowIds2);
			logger.info("获取待处理和处理过的流程实体id结束，获取到流程实体数：" + flowIds.size());
			return flowIds;
		} catch (BaseException e) {
			logger.error("获取待处理和处理过的流程实体id异常，roleId：" + roleId + "，ossUserId：" + ossUserId, e);
		}
		return flowIds;
	}

	/**
	 * 获取用户的角色的待处理的流程实体id
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param roleId
	 *            指定角色
	 * @return 流程实体id列表
	 */
	@Override
	public List<String> queryFlowEntByRole(OnlineUser onlineUser, String roleId) {
		logger.info("获取用户的角色的待处理的流程实体id开始，登录名：" + onlineUser.getUser().getLoginName() + "，角色id：" + roleId);
		List<String> flowIds = new ArrayList<>();
		try {
			// 当前角色待处理的流程实体id，从FLowEnt查（自己创建/自己的角色处理）
			String flowHql1 = "select ent.id, ent.supplierId from FlowEnt ent left join FlowNode node on ent.nodeId = node.nodeId "
					+ " where node.roleId like :roleId and (node.nodeIndex != 0 or ent.ossUserId = :ossUserId)";
			Map<String, Object> params = new HashMap<>();
			params.put("roleId", "%" + roleId + "%");
			params.put("ossUserId", onlineUser.getUser().getOssUserId());
			List<Object[]> flowList = baseDao.findByhql(flowHql1, params, 0);
			// 从查询结果中，过滤出 指定角色的数据权限 下的供应商/客户的流程
			if (!ListUtils.isEmpty(flowList)) {
				// 查询结果 {[flowEntId, supplierId]}
				List<String[]> entityFlowList = flowList.stream().map(data -> new String[] { String.valueOf(data[0]), String.valueOf(data[1]) })
						.collect(Collectors.toList());
				// 当前用户在指定角色下能看到的所有客户
				List<Customer> customerList = customerService.readCustomersByRole(onlineUser, roleId, null, null, null, null);
				if (!ListUtils.isEmpty(customerList)) {
					List<String> customerIdList = customerList.stream().map(Customer::getCustomerId).collect(Collectors.toList());
					// 遍历每个待处理流程，过滤出是此角色的客户的流程
					for (String[] data : entityFlowList) {
						if (customerIdList.contains(data[1])) {
							flowIds.add(data[0]);
						}
					}
				}
				// 当前用户在指定角色下能看到的所有供应商
				List<Supplier> supplierList = supplierService.readSuppliersByRole(onlineUser, roleId, null, null, null, null, SearchType.FLOW.ordinal(), null);
				if (!ListUtils.isEmpty(supplierList)) {
					List<String> supplierIdList = supplierList.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
					// 遍历每个待处理流程，过滤出是此角色的供应商的流程
					for (String[] data : entityFlowList) {
						if (supplierIdList.contains(data[1])) {
							flowIds.add(data[0]);
						}
					}
				}
			}
			logger.info("获取用户的角色的待处理的流程实体id结束，角色id：" + roleId + "待处理流程数：" + flowIds.size());
		} catch (BaseException e) {
			logger.error("获取用户的角色的待处理的流程实体id异常", e);
		}
		return flowIds;
	}

	/**
	 * 获取用户待处理的流程信息（所有 包含供应商、客户的信息）
	 *
	 * @param user
	 *            用户信息
	 * @param roleId
	 *            角色id
	 * @return 用户待处理流程信息
	 */
	@Override
	public List<UserFlowInfoDto> queryUserFlowInfo(User user, String roleId) {
		try {
			if (user == null || StringUtil.isBlank(roleId)) {
				return null;
			}
			Role role = roleService.read(roleId);
			List<UserFlowInfoDto> flowInfos = new ArrayList<>();
			if (role != null) {
				int dataPermission = role.getDataPermission();
				StringBuilder hql = new StringBuilder(
						"SELECT ent.id,ent.flowTitle,f.flowName,f.flowType,node.nodeName," + " f.flowClass,node.nodeIndex ,ent.ossUserId" + " FROM FlowEnt ent"
								+ " INNER JOIN ErpFlow f ON ent.flowId = f.flowId " + " INNER JOIN FlowNode node ON ent.nodeId= node.nodeId ");
				Map<String, Object> params = new HashMap<>();
				if (dataPermission == DataPermission.Flow.ordinal()) {
					hql.append(" WHERE (node.nodeIndex != 0 OR ent.ossUserId = :userId) " + " AND node.roleId LIKE :roleId ");
					params.put("userId", user.getOssUserId());
					params.put("roleId", "%" + roleId + "%");
				} else if (dataPermission == DataPermission.All.ordinal()) {
					hql.append(" WHERE (node.nodeIndex != 0 OR ent.ossUserId = :userId) " + " AND node.roleId LIKE :roleId  ");
					params.put("userId", user.getOssUserId());
					params.put("roleId", "%" + roleId + "%");
				} else if (dataPermission == DataPermission.Customize.ordinal()) {
					String deptIds = role.getDeptIds();
					if (StringUtil.isBlank(deptIds)) {
						return null;
					}
					String[] deptIdarr = deptIds.split(",");

					hql.append(" INNER JOIN User u ON ent.ossUserId = u.ossUserId " + " WHERE (node.nodeIndex != 0 OR ent.ossUserId = :userId) "
							+ " AND node.roleId LIKE :roleId " + " AND u.deptId IN (:deptIds) ");
					params.put("userId", user.getOssUserId());
					params.put("roleId", "%" + roleId + "%");
					params.put("deptIds", new ArrayList<>(Arrays.asList(deptIdarr)));
				} else if (dataPermission == DataPermission.Dept.ordinal()) {
					List<String> deptIds = new ArrayList<>();
					deptIds.add(user.getDeptId());
					List<String> subDeptId = departmentService.getSubDeptIds(user.getDeptId());
					if (subDeptId != null && !subDeptId.isEmpty()) {
						deptIds.addAll(subDeptId);
					}
					// 获取用户的子部门
					hql.append(" INNER JOIN User u ON ent.ossUserId = u.ossUserId " + " WHERE (node.nodeIndex != 0 OR ent.ossUserId = :userId) "
							+ " AND node.roleId LIKE :roleId " + " AND u.deptId IN (:deptIds) ");
					params.put("userId", user.getOssUserId());
					params.put("roleId", "%" + roleId + "%");
					params.put("deptIds", deptIds);
				} else if (dataPermission == DataPermission.Self.ordinal()) {
					hql.append(" WHERE ent.ossUserId = :userId ");
					params.put("userId", user.getOssUserId());
				} else {
					return null;
				}
				hql.append(" and (ent.flowStatus = ").append(FlowStatus.NOT_AUDIT.ordinal()).append(" or ent.flowStatus = ")
						.append(FlowStatus.NO_PASS.ordinal()).append(") ");
				try {
					List<?> result = baseDao.findByhql(hql.toString(), params, Integer.MAX_VALUE);
					if (result != null && !result.isEmpty()) {
						flowInfos = result.stream().map(obj -> {
							if (obj.getClass().isArray()) {
								Object[] rowObj = (Object[]) obj;
								UserFlowInfoDto userFlowInfo = new UserFlowInfoDto();
								String id = String.valueOf(rowObj[0]);
								String flowTitle = String.valueOf(rowObj[1]);
								String flowName = String.valueOf(rowObj[2]);
								Integer flowType = Integer.parseInt(String.valueOf(rowObj[3]));
								String nodeName = String.valueOf(rowObj[4]);
								String flowClass = String.valueOf(rowObj[5]);
								Integer nodeIndex = Integer.parseInt(String.valueOf(rowObj[6]));
								String userId = String.valueOf(rowObj[7]);
								userFlowInfo.setEntId(id);
								userFlowInfo.setEntName(flowTitle);
								userFlowInfo.setEntUserId(userId);
								userFlowInfo.setNodeName(nodeName);
								userFlowInfo.setFlowClass(flowClass);
								userFlowInfo.setNodeIndex(nodeIndex);
								userFlowInfo.setFlowType(flowType);
								userFlowInfo.setFlowName(flowName);
								return userFlowInfo;
							}
							return null;
						}).collect(Collectors.toList());
					}
				} catch (BaseException e) {
					logger.error("HQL执行异常", e);
				}
			}
			return flowInfos;
		} catch (ServiceException e) {
			logger.error("数据查询异常", e);
		}
		return null;
	}

	/**
	 * 根据id 批量获取流程信息
	 * 
	 * @param ids
	 *            id集合
	 * @return 流程节点信息
	 */
	@Override
	public List<FlowEnt> queryFlowEntByIds(List<String> ids) {
		List<FlowEnt> flowEntList = new ArrayList<>();
		if (ids == null || ids.isEmpty()) {
			// 返回不为空的对象，尽量避免空指针异常
			return flowEntList;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(new HashSet<>(ids))));
		try {
			flowEntList = queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("根据ID批量查询流程节点信息异常", e);
		}
		return flowEntList;
	}

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
	 * @return 分页流程信息
	 */
	@Override
	public PageResult<FlowEntWithOpt> queryFlowEntByPageSql(OnlineUser onlineUser, Integer entityType, Integer flowType, String date, String entityId,
			String productId, Integer pageSize, Integer page, String keywords) {
		pageSize = (pageSize == null || pageSize <= 0) ? 5 : pageSize;
		page = (page == null || page < 0) ? 1 : page;
		long startTime = System.currentTimeMillis();
		logger.info("按月份获取流程信息开始，entityType：" + entityType + "，flowType：" + flowType + "，月份：" + date + "，entityId：" + entityId + "，productId：" + productId);
		PageResult<FlowEntWithOpt> result = new PageResult<>(null, 0);
		try {
			String roleId = onlineUser.getRoleId();
			String userId = onlineUser.getUser().getOssUserId();
			Role role = roleService.read(roleId);
			// 条件SQL
			StringBuilder whereSql = new StringBuilder();
			if (DataPermission.All.ordinal() == role.getDataPermission()) {
				// 全部权限（在可以看见的流程基础上不需要限制）
			} else if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
				// 部门权限
				Set<String> deptIds = new HashSet<>();
				deptIds.add(onlineUser.getUser().getDeptId());
				List<String> subDeptIds = departmentService.getSubDeptIds(onlineUser.getUser().getDeptId());
				if (!CollectionUtils.isEmpty(subDeptIds)) {
					deptIds.addAll(subDeptIds);
				}
				whereSql.append(" AND e.deptid IN ('").append(String.join("','", deptIds)).append("') ");
			} else if (DataPermission.Flow.ordinal() == role.getDataPermission()) {
				// 流程权限(只是查看可以看见的流程)
				// 已经限制过了
			} else if (DataPermission.Self.ordinal() == role.getDataPermission()) {
				// 自己权限
				whereSql.append(" AND e.ossuserid = '").append(userId).append("' ");
			} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
				// 自定义权限
				String defDeptIds = role.getDeptIds();
				if (StringUtil.isBlank(defDeptIds)) {
					return result;
				}
				List<String> deptIds = Arrays.asList(defDeptIds.split(","));
				whereSql.append(" AND e.deptid IN ('").append(String.join("','", deptIds)).append("') ");
			} else {
				return result;
			}
			// 条件
			if (null != flowType) {
				whereSql.append(" and e.flowtype = ").append(flowType);
			} else {
				whereSql.append(" and e.flowtype != ").append(FlowType.EMPLOYEE.getCode());
			}
			if (null != entityType) {
				whereSql.append(" and e.entitytype = ").append(entityType);
			}
			if (StringUtils.isNotBlank(productId)) {
				whereSql.append(" and e.productid = '").append(productId).append("' ");
			} else if (StringUtils.isNotBlank(entityId)) {
				whereSql.append(" and e.supplierid = '").append(entityId).append("' ");
			}

			// 月份
			if (StringUtils.isNotBlank(date)) {
				date = date.endsWith("-") ? date.substring(0, date.length() - 1) : date;
				String startDate = DateUtil.convert(DateUtil.convert2(date + "-01 00:00:00"), DateUtil.format2);
				String endDate = DateUtil.convert(DateUtil.getNextMonthFirst(date), DateUtil.format2);
				whereSql.append(" and e.wtime >='").append(startDate).append("' ");
				whereSql.append(" and e.wtime < '").append(endDate).append("' ");
			}
			// 关键词过滤
			if (StringUtil.isNotBlank(keywords)) {
				whereSql.append(" and e.flowtitle like '%").append(keywords).append("%' ");
			}

			StringBuilder countSql = new StringBuilder("select count(1)  FROM  erp_flow_ent e LEFT JOIN erp_flow_node n ON e.nodeid = n.nodeid ");
			countSql.append("WHERE (LOCATE('").append(roleId).append("',n.roleid) > 0 OR e.ossuserid = '").append(userId)
					.append("' OR e.id IN (SELECT DISTINCT fg.flowentid FROM erp_flow_log fg WHERE fg.ossuserid = '").append(userId).append("')")
					.append("OR LOCATE('").append(roleId).append("', e.viewerroleid) > 0) ");
			// 总数
			int total = 0;
			// 统计总数SQL
			logger.info("按月份获取流程SQL，查询总数：" + countSql.toString() + whereSql.toString());
			List<?> countList = baseDao.selectSQL(countSql.toString() + whereSql.toString());
			if (countList != null && !countList.isEmpty()) {
				Object countRow = countList.get(0);
				if (countRow instanceof Number) {
					Number rowInfo = (Number) countRow;
					total = rowInfo.intValue();
				}
			}
			result.setCount(total);
			result.setTotalPages((total % pageSize == 0) ? (total / pageSize) : (total / pageSize + 1));
			if (total == 0 || page > result.getTotalPages()) {
				// 总数为0的时候或者已经是最后一页，不用再查
				return result;
			}
			// 查询数据的SQL
			String dataSql = "SELECT e.id,e.flowid,e.flowmsg,e.flowstatus,e.foreignid,e.nodeid,"
					+ "e.ossuserid,e.remark,e.wtime,e.productid,e.supplierid,e.flowtitle,e.flowtype,e.entitytype,e.viewerroleid," + "((LOCATE('" + roleId
					+ "',n.roleid) > 0  )&& (( !ISNULL(n.nodeindex) && n.nodeindex != 0) || ('" + userId
					+ "' = e.ossuserid)) && (e.flowstatus != 1) && (e.flowstatus != 3)) AS canopt, e.deptid FROM  erp_flow_ent e LEFT JOIN erp_flow_node n ON e.nodeid = n.nodeid "
					+ "WHERE (LOCATE('" + roleId + "',n.roleid) > 0 OR e.ossuserid = '" + userId
					+ "' OR e.id IN (SELECT DISTINCT fg.flowentid FROM erp_flow_log fg WHERE fg.ossuserid = '" + userId + "')" + "OR LOCATE('" + roleId
					+ "', e.viewerroleid) > 0) " + whereSql + " ORDER BY ((LOCATE('" + roleId
					+ "',n.roleid) > 0  )&& (( !ISNULL(n.nodeindex) && n.nodeindex != 0) || ('" + userId
					+ "' = e.ossuserid)) && (e.flowstatus != 1) && (e.flowstatus != 3)) DESC ,e.wtime DESC LIMIT " + (page - 1) * pageSize + "," + pageSize;
			logger.info("按月份获取流程SQL，分页查询数据：" + dataSql);
			List<?> flowList = baseDao.selectSQL(dataSql);
			if (flowList != null && !flowList.isEmpty()) {
				List<FlowEntWithOpt> flowEntWithOptList = new ArrayList<>(flowList.size());
				for (Object row : flowList) {
					if (row.getClass().isArray()) {
						Object[] rowData = (Object[]) row;
						FlowEntWithOpt flowEntWithOpt = new FlowEntWithOpt();
						if (flowEntWithOpt.setDataInfo(rowData)) {
							flowEntWithOptList.add(flowEntWithOpt);
						}
					}
				}
				result.setData(flowEntWithOptList);
			}
			logger.info(
					"按条件和数据权限过滤结束，获取到流程实体数：" + (ListUtils.isEmpty(flowList) ? 0 : flowList.size()) + " 耗时：" + (System.currentTimeMillis() - startTime) + " ms");
		} catch (ServiceException e) {
			logger.error("按月份获取待处理和处理过的流程实体异常", e);
		} catch (BaseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 统计用户未处理的流程数
	 *
	 * @param user
	 *            用户
	 * @param role
	 *            角色
	 * @return 未处理的数量
	 */
	@Override
	public Integer countUnprocessFlowEntByRole(User user, Role role) {
		StringBuilder flowHql1 = new StringBuilder("select count(1) from FlowEnt ent left join FlowNode node"
				+ " on ent.nodeId = node.nodeId where node.roleId like :roleId and (node.nodeIndex != 0  or ent.ossUserId = :ossUserId) ");
		Map<String, Object> params = new HashMap<>();
		params.put("roleId", "%" + role.getRoleid() + "%");
		params.put("ossUserId", user.getOssUserId());
		int permission = role.getDataPermission();
		// 全部权限（在可以看见的流程基础上不需要限制）
		if (DataPermission.Dept.ordinal() == role.getDataPermission()) {
			// 部门权限
			Set<String> deptIds = new HashSet<>();
			deptIds.add(user.getDeptId());
			List<String> subDeptIds = departmentService.getSubDeptIds(user.getDeptId());
			if (!CollectionUtils.isEmpty(subDeptIds)) {
				deptIds.addAll(subDeptIds);
			}
			flowHql1.append(" AND ent.deptId IN (:deptId) ");
			params.put("deptId", deptIds);
		} else if (DataPermission.Self.ordinal() == role.getDataPermission()) {
			// 自己权限
			flowHql1.append(" AND ent.ossUserId = :userId ");
			params.put("userId", user.getOssUserId());
		} else if (DataPermission.Customize.ordinal() == role.getDataPermission()) {
			// 自定义权限
			String defDeptIds = role.getDeptIds();
			if (StringUtil.isBlank(defDeptIds)) {
				return 0;
			}
			List<String> deptIds = Arrays.asList(defDeptIds.split(","));
			flowHql1.append(" AND ent.deptId IN (:deptId) ");
			params.put("deptId", deptIds);
		} else if (permission != DataPermission.All.ordinal() && permission != DataPermission.Flow.ordinal()) {
			return 0;
		}
		try {
			List<Object> flowCountInfo = baseDao.findByhql(flowHql1.toString(), params, Integer.MAX_VALUE);
			if (flowCountInfo != null && !flowCountInfo.isEmpty()) {
				for (Object countInfo : flowCountInfo) {
					if (countInfo instanceof Number) {
						return ((Number) countInfo).intValue();
					}
				}
			}
		} catch (BaseException e) {
			logger.error("查询用户角色待处理流程数异常", e);
		}
		return 0;
	}

	/**
	 * 统计用户未处理的流程数
	 *
	 * @param user
	 *            用户
	 * @param role
	 *            角色
	 * @return 未处理的数量
	 */
	@Override
	public FlowEnt queryEarliestFlowEntByRole(User user, Role role, Integer entityType, Integer flowType) {
		if (user == null || role == null) {
			return null;
		}
		try {
			String roleId = role.getRoleid();
			String userId = user.getOssUserId();
			// 条件SQL
			StringBuilder whereHql = new StringBuilder();
			// 角色的数据权限
			int permission = role.getDataPermission();
			// 参数
			Map<String, Object> params = new HashMap<>();
			if (DataPermission.Dept.ordinal() == permission) {
				// 部门权限
				Set<String> deptIds = new HashSet<>();
				deptIds.add(user.getDeptId());
				List<String> subDeptIds = departmentService.getSubDeptIds(user.getDeptId());
				if (!CollectionUtils.isEmpty(subDeptIds)) {
					deptIds.addAll(subDeptIds);
				}
				whereHql.append(" AND e.deptId IN (:deptId) ");
				params.put("deptId", deptIds);
			} else if (DataPermission.Self.ordinal() == permission) {
				// 自己权限
				whereHql.append(" AND e.ossUserId = :userId ");
				params.put("userId", userId);
			} else if (DataPermission.Customize.ordinal() == permission) {
				// 自定义权限
				String defDeptIds = role.getDeptIds();
				if (StringUtil.isBlank(defDeptIds)) {
					return null;
				}
				List<String> deptIds = Arrays.asList(defDeptIds.split(","));
				whereHql.append(" AND e.deptId IN (:deptId) ");
				params.put("deptId", deptIds);
			} else if (DataPermission.All.ordinal() != permission && DataPermission.Flow.ordinal() != permission) {
				return null;
			}
			// 条件
			if (null != flowType) {
				whereHql.append(" and e.flowType = : flowType ");
				params.put("flowType", flowType);
			}
			if (null != entityType) {
				whereHql.append(" and e.entityType = :entityType ");
				params.put("entityType", entityType);
			}
			// 查询数据的SQL
			String dataHql = "SELECT e FROM FlowEnt e LEFT JOIN FlowNode n ON e.nodeId = n.nodeId  WHERE (LOCATE(:roleId ,n.roleId) > 0 OR e.ossUserId = :userId OR"
					+ " e.id IN (SELECT DISTINCT fg.flowEntId FROM FlowLog fg WHERE fg.ossUserId = :userId) OR LOCATE(:roleId, e.viewerRoleId) > 0) "
					+ whereHql.toString() + " ORDER BY e.wtime asc";
			params.put("roleId", roleId);
			params.put("userId", userId);
			logger.info("按月份获取流程Hql，分页查询数据：" + dataHql);
			List<FlowEnt> flowList = findByhql(dataHql, params, 1);
			if (flowList != null && !flowList.isEmpty()) {
				return flowList.get(0);
			}
		} catch (ServiceException e) {
			logger.error("根据用户查询流程信息", e);
		}
		return null;
	}

	/**
	 * 分页查询流程信息（移动端 不能和原来的公用一个方法）
	 *
	 * @param user
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
	@Override
	public PageResult<FlowEntForMobileDto> queryPageForMobile(OnlineUser user, Integer page, Integer pageSize, String searchMonth, String searchContent) {
		pageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
		page = (page == null || page < 0) ? 1 : page;
		long startTime = System.currentTimeMillis();
		PageResult<FlowEntForMobileDto> result = new PageResult<>(null, 0);
		try {
			String roleId = user.getRoleId();
			String userId = user.getUser().getOssUserId();
			Role role = roleService.read(roleId);
			// 条件SQL
			StringBuilder whereSql = new StringBuilder();
			int permission = role.getDataPermission();
			if (DataPermission.Dept.ordinal() == permission) {
				// 部门权限
				Set<String> deptIds = new HashSet<>();
				deptIds.add(user.getUser().getDeptId());
				List<String> subDeptIds = departmentService.getSubDeptIds(user.getUser().getDeptId());
				if (!CollectionUtils.isEmpty(subDeptIds)) {
					deptIds.addAll(subDeptIds);
				}
				whereSql.append(" AND e.deptid IN ('").append(String.join("','", deptIds)).append("') ");
			} else if (DataPermission.Self.ordinal() == permission) {
				// 自己权限
				whereSql.append(" AND e.ossuserid = '").append(userId).append("' ");
			} else if (DataPermission.Customize.ordinal() == permission) {
				// 自定义权限
				String defDeptIds = role.getDeptIds();
				if (StringUtil.isBlank(defDeptIds)) {
					return result;
				}
				whereSql.append(" AND e.deptid IN ('").append(String.join("','", defDeptIds.split(","))).append("') ");
			} else if (DataPermission.All.ordinal() != permission && DataPermission.Flow.ordinal() != permission) {
				// all 和 flow 不限制 ，但是不在系统管理的权限内 将 不允许查询 流程
				return result;
			}
			// 没有时间 就查询 所有
			if (StringUtil.isNotBlank(searchMonth)) {
				// searchMonth = DateUtil.convert(new Date(), DateUtil.format4);
				String startDate = searchMonth + "-" + "01 00:00:00";
				String endDate = DateUtil.convert(DateUtil.getNextMonthFirst(DateUtil.convert(searchMonth, DateUtil.format4)), DateUtil.format2);
				whereSql.append(" and e.wtime >= '").append(startDate).append("' ").append(" and e.wtime < '").append(endDate).append("' ");
			}

			// 关键词过滤
			if (StringUtil.isNotBlank(searchContent)) {
				whereSql.append(" and (s.companyname LIKE '%").append(searchContent).append("%' OR c.companyname LIKE '%").append(searchContent)
						.append("%' OR e.flowtitle like '%").append(searchContent).append("%') ");
			}

			String countSql = "select count(1)  FROM  erp_flow_ent e LEFT JOIN erp_flow_node n ON e.nodeid = n.nodeid "
					+ " LEFT JOIN erp_supplier s ON s.supplierid = e.supplierid  LEFT JOIN erp_customer c ON c.customerid = e.supplierid " + " WHERE (LOCATE('"
					+ roleId + "',n.roleid) > 0 OR e.ossuserid = '" + userId
					+ "' OR e.id IN (SELECT DISTINCT fg.flowentid FROM erp_flow_log fg WHERE fg.ossuserid = '" + userId + "')  OR LOCATE('" + roleId
					+ "', e.viewerroleid) > 0) " + whereSql.toString();
			// 总数
			int total = 0;
			// 统计总数SQL
			logger.info("按月份获取流程SQL，查询总数：" + countSql);
			List<?> countList = baseDao.selectSQL(countSql);
			if (countList != null && !countList.isEmpty()) {
				Object countRow = countList.get(0);
				if (countRow instanceof Number) {
					Number rowInfo = (Number) countRow;
					total = rowInfo.intValue();
				}
			}
			result.setCount(total);
			result.setTotalPages((total % pageSize == 0) ? (total / pageSize) : (total / pageSize + 1));
			if (total == 0 || page > result.getTotalPages()) {
				// 总数为0的时候或者已经是最后一页，不用再查
				return result;
			}
			// 查询数据的SQL
			String dataSql = "SELECT e.id,e.flowid,f.flowname,e.flowtitle,e.ossuserid,e.flowstatus,e.nodeid,n.nodename, e.wtime,e.productid," + " ( LOCATE('"
					+ roleId + "',n.roleid) > 0 && (( !ISNULL(n.nodeindex) && n.nodeindex != 0) || '" + userId
					+ "' = e.ossuserid) && e.flowstatus not in(1,3)) AS canopt,"
					+ " e.supplierid,e.entitytype,s.companyname as suppName,c.companyname as cName,e.flowtype " + " FROM  erp_flow_ent e "
					+ " LEFT JOIN erp_flow_node n ON e.nodeid = n.nodeid " + " LEFT JOIN erp_flow f ON e.flowid = f.flowid "
					+ " LEFT JOIN erp_supplier s ON s.supplierid = e.supplierid " + " LEFT JOIN erp_customer c ON c.customerid = e.supplierid "
					+ " WHERE (LOCATE('" + roleId + "',n.roleid) > 0 OR e.ossuserid = '" + userId
					+ "' OR e.id IN (SELECT DISTINCT fg.flowentid FROM erp_flow_log fg WHERE fg.ossuserid = '" + userId + "') OR LOCATE('" + roleId
					+ "', e.viewerroleid) > 0) " + whereSql + " ORDER BY (LOCATE('" + roleId
					+ "',n.roleid) > 0  && (( !ISNULL(n.nodeindex) && n.nodeindex != 0) || '" + userId + "' = e.ossuserid) && e.flowstatus not in (1,3)) DESC ,"
					+ " ( CASE e.flowstatus WHEN 0 THEN 0 WHEN 2 THEN 1 WHEN 1 THEN 2 ELSE 3 END ) ASC ,e.wtime DESC LIMIT " + (page - 1) * pageSize + ","
					+ pageSize;
			logger.info("按月份获取流程SQL，分页查询数据：" + dataSql);
			List<?> flowList = baseDao.selectSQL(dataSql);
			if (flowList != null && !flowList.isEmpty()) {
				List<FlowEntForMobileDto> flowEntForMobileDtos = new ArrayList<>(flowList.size());
				for (Object row : flowList) {
					if (row.getClass().isArray()) {
						Object[] rowData = (Object[]) row;
						FlowEntForMobileDto flowForMobile = new FlowEntForMobileDto();
						if (flowForMobile.setDataInfo(rowData)) {
							flowEntForMobileDtos.add(flowForMobile);
						}
					}
				}
				// 查询对应批次的详情数据
				List<String> userIds = flowEntForMobileDtos.stream().filter(flow -> StringUtil.isNotBlank(flow.getUserId())).map(FlowEntForMobileDto::getUserId)
						.distinct().collect(Collectors.toList());
				List<String> productIds = flowEntForMobileDtos.stream().filter(flow -> StringUtil.isNotBlank(flow.getProductId()))
						.map(FlowEntForMobileDto::getProductId).distinct().collect(Collectors.toList());
				Map<String, String> productNames = productService.findProductName(productIds);
				Map<String, String> customerProductNames = customerProductService.findProductName(productIds);

				if (!userIds.isEmpty()) {
					Map<String, String> userInfos = userService.findUserNameByIds(userIds);
					flowEntForMobileDtos.forEach(flowInfo -> {
						flowInfo.setUserName(userInfos.get(flowInfo.getUserId()));
						String productName = productNames.get(flowInfo.getProductId());
						String customerProductName = customerProductNames.get(flowInfo.getProductId());
						flowInfo.setProductName(StringUtil.isNotBlank(productName) ? productName : customerProductName);
					});
				}
				result.setData(flowEntForMobileDtos);
			}
			logger.info("查询流程：" + (flowList == null ? 0 : flowList.size()) + "条，耗时：" + (System.currentTimeMillis() - startTime) + " ms");
		} catch (ServiceException e) {
			logger.error("按月份获取待处理和处理过的流程实体异常", e);
		} catch (BaseException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 按用户id和角色获取可以看到的流程实体对应的主体ID
	 *
	 * @param roleId
	 *            当前角色
	 * @param userId
	 *            用户id
	 * @param entityType
	 *            主体类型
	 * @return 流程实体id集合
	 */
	@Override
	public Set<String> queryEntityIdFromFlowEnt(String roleId, String userId, Integer entityType) {
		logger.info("获取待处理和处理过的流程实体id开始，roleId：" + roleId + "，ossUserId：" + userId);
		Set<String> entityIds = new HashSet<>();
		try {
			// 当前角色 待处理的/全程能看到的 流程实体id，从FLowEnt查（自己创建/自己的角色处理）
			String flowHql1 = "select distinct ent.supplierId from FlowEnt ent left join FlowNode node on ent.nodeId = node.nodeId "
					+ " where node.roleId like :roleId and (node.nodeIndex != 0 or ent.ossUserId = :ossUserId) or ent.viewerRoleId like :viewerRoleId"
					+ " and ent.entityType = :entityType";
			Map<String, Object> params = new HashMap<>();
			params.put("roleId", "%" + roleId + "%");
			params.put("ossUserId", userId);
			params.put("viewerRoleId", "%" + roleId + "%");
			params.put("entityType", entityType);
			List<Object> flowEntityIds = baseDao.findByhql(flowHql1, params, 0);
			if (flowEntityIds != null && !flowEntityIds.isEmpty()) {
				// 去重，得到所有流程实体id
				entityIds.addAll(flowEntityIds.stream().map(String::valueOf).collect(Collectors.toSet()));
			}
			// 当前角色 处理过的 流程实体id，从FlowLog查
			String flowHql2 = "select distinct distinct ent.supplierId from FlowLog log left join FlowNode node on log.nodeId = node.nodeId "
					+ " inner join FlowEnt ent on log.flowEntId = ent.id where (node.roleId like :roleId or log.ossUserId = :ossUserId) "
					+ " and ent.entityType = :entityType";
			params = new HashMap<>();
			params.put("roleId", "%" + roleId + "%");
			params.put("ossUserId", userId);
			params.put("entityType", entityType);
			List<Object> logEntityIds = baseDao.findByhql(flowHql2, params, 0);
			if (logEntityIds != null && !logEntityIds.isEmpty()) {
				entityIds.addAll(logEntityIds.stream().map(String::valueOf).collect(Collectors.toSet()));
			}
			logger.info("获取待处理和处理过的流程实体id结束，获取到流程实体数：" + entityIds.size());
			return entityIds;
		} catch (BaseException e) {
			logger.error("获取待处理和处理过的流程实体id异常，roleId：" + roleId + "，ossUserId：" + userId, e);
		}
		return entityIds;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FlowEnt> queryTimeOutFlow() {
		logger.info("查询超时未处理流程开始");
		List<FlowEnt> entList = null;
		List<String> entIdList = new ArrayList<>();
		String sql = "select efe.id, efe.wtime, max(efl.wtime), efn.nodeindex, efn.duetime" +
				" from erp_flow_ent efe" +
				"   left join erp_flow_log efl on efe.id = efl.flowentid" +
				"   left join erp_flow_node efn on efe.nodeid = efn.nodeid" +
				" where efe.flowstatus in (" + FlowStatus.NOT_AUDIT.ordinal() + ", " + FlowStatus.NO_PASS.ordinal() + ")" +
				" and efn.duetime > 0 group by efe.id";
		try {
			List<Object[]> resultList = (List<Object[]>) baseDao.selectSQL(sql);
			if (CollectionUtils.isEmpty(resultList)) {
				logger.info("未找到超时未处理流程");
				return entList;
			}
			Date now = new Date();
			for (Object[] data : resultList) {
				// 0flowEntId，1流程创建时间，2上个节点处理时间，3当前节点位置，4当前节点处理时限（小时）
				Date startTime = null;
				Date endTime = null;
				int dueTime = ((Number)data[4]).intValue();
				int nodeIndex = ((Number)data[3]).intValue();
				if (0 == nodeIndex && null == data[2]) {
					// 流程刚发起，从发起时间开始算
					startTime = new Date(((Timestamp) data[1]).getTime());
				} else {
					// 流程审核过，从上个节点审核时间开始算
					startTime = new Date(((Timestamp) data[2]).getTime());
				}
				endTime = DateUtil.getIntervalHour(startTime, dueTime);
				if (now.after(endTime)) {
					entIdList.add((String) data[0]);
				}
			}
			if (!CollectionUtils.isEmpty(entIdList)) {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("id", Constants.ROP_IN, entIdList));
				entList = queryAllBySearchFilter(filter);
			}
			logger.info("查询到超时未处理流程数：" + (null == entList ? 0 : resultList.size()));
		} catch (BaseException e) {
			logger.error("查询超时未处理流程异常", e);
		}
		return entList;
	}

	/**
	 * 判断是账单开票流程 or 无账单开票流程
	 *
	 * @param flowMsg
	 *            流程内容
	 * @param flowClass
	 *            流程处理类
	 * @return
	 * @throws Exception
	 */
	@Override
	public String hasBillOrNot(String flowMsg, String flowClass) throws Exception {
		if (StringUtils.isBlank(flowMsg)) {
			logger.info("flowMsg为空，判断是否含有账单失败");
			return Constants.PARAM_BLANK;
		}
		if (StringUtils.isBlank(flowClass)) {
			logger.info("流程类别为空");
			return Constants.PARAM_BLANK;
		}
		if (!Constants.INVOICE_CLASS.equals(flowClass)) {
			logger.info("当前流程非发票流程");
			return Constants.ERROR_CLASS;
		}
		JSONObject flowMsgJson = JSON.parseObject(flowMsg);
		String billLabel = flowMsgJson.getString(Constants.BILL_INFO_KEY);
		if (StringUtil.isNotBlank(billLabel)) {
			return Constants.HAS_BILL;
		} else {
			return Constants.NO_BILL;
		}
	}
}
