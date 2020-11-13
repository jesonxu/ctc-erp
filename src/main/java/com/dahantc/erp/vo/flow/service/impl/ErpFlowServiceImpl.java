package com.dahantc.erp.vo.flow.service.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.FlowType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.FlowTaskManager;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.flow.AddFlowDto;
import com.dahantc.erp.dto.flow.FlowDetail;
import com.dahantc.erp.dto.flow.FlowNodeDto;
import com.dahantc.erp.dto.flow.FlowThresholdDto;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.flowtask.service.CommonFlowTask;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.flow.dao.IErpFlowDao;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;

@Service("erpFlowService")
public class ErpFlowServiceImpl implements IErpFlowService {
	private static Logger logger = LogManager.getLogger(ErpFlowServiceImpl.class);

	@Autowired
	private IErpFlowDao erpFlowDao;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private CommonFlowTask commonFlowTask;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Override
	public ErpFlow read(Serializable id) throws ServiceException {
		try {
			return erpFlowDao.read(id);
		} catch (Exception e) {
			logger.error("读取流程表失败", e);
			throw new ServiceException("读取流程表失败", e);
		}
	}

	@Override
	public boolean save(ErpFlow entity) throws ServiceException {
		try {
			return erpFlowDao.save(entity);
		} catch (Exception e) {
			logger.error("保存流程表失败", e);
			throw new ServiceException("保存流程表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<ErpFlow> objs) throws ServiceException {
		try {
			return erpFlowDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return erpFlowDao.delete(id);
		} catch (Exception e) {
			logger.error("删除流程表失败", e);
			throw new ServiceException("删除流程表失败", e);
		}
	}

	@Override
	public boolean update(ErpFlow enterprise) throws ServiceException {
		try {
			return erpFlowDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新流程表失败", e);
			throw new ServiceException("更新流程表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return erpFlowDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询流程表数量失败", e);
			throw new ServiceException("查询流程表数量失败", e);
		}
	}

	@Override
	public PageResult<ErpFlow> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return erpFlowDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询流程表分页信息失败", e);
			throw new ServiceException("查询流程表分页信息失败", e);
		}
	}

	@Override
	public List<ErpFlow> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return erpFlowDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询流程表失败", e);
			throw new ServiceException("查询流程表失败", e);
		}
	}

	@Override
	public List<ErpFlow> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return erpFlowDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询流程表失败", e);
			throw new ServiceException("查询流程表失败", e);
		}
	}

	@Override
	public List<ErpFlow> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return erpFlowDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询流程表失败", e);
			throw new ServiceException("查询流程表失败", e);
		}
	}

	@Override
	public boolean addFlow(AddFlowDto req) throws ServiceException {
		try {
			ErpFlow flow = new ErpFlow();
			flow.setFlowName(req.getFlowName());
			flow.setFlowType(req.getFlowType());
			flow.setWtime(new Timestamp(System.currentTimeMillis()));
			flow.setFlowClass(req.getFlowClass());
			flow.setCreatorid(req.getCreatorId());
			flow.setViewerRoleId(req.getViewerRoleId());
			flow.setBindType(req.getBindType());
			flow.setAssociateType(req.getAssociateType());
			this.save(flow);
			StringBuffer labelSql = new StringBuffer("insert into erp_flow_label (id, flowid, name, defaultvalue, type,position) values ");
			if (null != req.getLabelList() && !req.getLabelList().isEmpty()) {
				List<Object> values = new ArrayList<Object>();
				List<Type> typeValue = new ArrayList<>();
				int paramSize = 0;
				for (int i = 0; i < req.getLabelList().size(); i++) {
					FlowLabel label = req.getLabelList().get(i).getFlowLabel();
					label.setFlowId(flow.getFlowId());
					this.packageLabelParams(label, values, typeValue);
					// 参数个数
					if (i == 0) {
						paramSize = values.size();
					}
					// 拼接sql
					this.buildParamsSQL(paramSize, labelSql);
					if (i < req.getLabelList().size() - 1) {
						labelSql.append(",");
					}
				}
				baseDao.executeSqlUpdte(labelSql.toString(), values.toArray(), typeValue.toArray(new Type[typeValue.size()]));
			}

			req.getNodeList().sort((o1, o2) -> {
				return o2.getNodeIndex() - o1.getNodeIndex();
			});
			String nextNodeId = "";
			for (FlowNodeDto nodeDto : req.getNodeList()) {
				FlowNode node = nodeDto.getFlowNode();
				node.setFlowId(flow.getFlowId());
				node.setNextNodeId(nextNodeId);
				flowNodeService.save(node);
				nextNodeId = node.getNodeId();
			}
			flow.setStartNodeId(nextNodeId);
			this.update(flow);
			return true;
		} catch (Exception e) {
			logger.error("新建流程失败", e);
			throw new ServiceException("新建流程失败", e);
		}
	}

	@Override
	public boolean editFlow(AddFlowDto req) throws ServiceException {
		try {
			ErpFlow flow = this.read(req.getFlowId());
			flow.setFlowName(req.getFlowName());
			flow.setFlowType(req.getFlowType());
			flow.setFlowClass(req.getFlowClass());
			flow.setViewerRoleId(req.getViewerRoleId());
			flow.setBindType(req.getBindType());
			flow.setAssociateType(req.getAssociateType());
			// 更新之前的node的flowid为空，保留node待查询node的角色名
			String nodeDel = "UPDATE erp_flow_node set flowid='', nodename=concat('已删除-',nodename) where flowid='" + req.getFlowId().trim() + "'";
			baseDao.executeUpdateSQL(nodeDel);
			req.getNodeList().sort((o1, o2) -> o2.getNodeIndex() - o1.getNodeIndex());
			String nextNodeId = "";
			for (FlowNodeDto nodeDto : req.getNodeList()) {
				FlowNode oldNode = flowNodeService.read(nodeDto.getNodeId());
				if (oldNode != null) {
					// 更新旧节点
					oldNode.setNodeName(nodeDto.getNodeName());
					oldNode.setRoleId(nodeDto.getRoleId());
					oldNode.setFlowId(flow.getFlowId());
					oldNode.setNextNodeId(nextNodeId);
					oldNode.setNodeIndex(nodeDto.getNodeIndex());
					if (StringUtil.isNotBlank(nodeDto.getDueTime())) {
						oldNode.setDueTime(Integer.parseInt(nodeDto.getDueTime()));
					}
					oldNode.setViewLabelIds(nodeDto.getViewLabelIds());
					oldNode.setEditLabelIds(nodeDto.getEditLabelIds());
					oldNode.setMustLabelIds(nodeDto.getMustLabelIds());
					oldNode.setFlowThreshold("");
					List<FlowThresholdDto> flowThresholds = nodeDto.getFlowThresholds();
					if (flowThresholds != null && !flowThresholds.isEmpty()) {
						flowThresholds = flowThresholds.stream().filter(flowThresholdDto -> StringUtil.isNotBlank(flowThresholdDto.getLabelId()))
								.collect(Collectors.toList());
						if (!flowThresholds.isEmpty()) {
							// 将流程阈值转换为JSON对象放入数据库
							oldNode.setFlowThreshold(JSON.toJSONString(flowThresholds));
						}
					}
					oldNode.setThresholdFile(nodeDto.getThresholdFile());
					flowNodeService.update(oldNode);
					nextNodeId = oldNode.getNodeId();
				} else {
					// 新增节点
					FlowNode node = nodeDto.getFlowNode();
					node.setFlowId(flow.getFlowId());
					node.setNextNodeId(nextNodeId);
					flowNodeService.save(node);
					nextNodeId = node.getNodeId();
				}
			}
			flow.setStartNodeId(nextNodeId);
			this.update(flow);

			String labelDel = "DELETE from erp_flow_label where flowid='" + req.getFlowId().trim() + "'";
			baseDao.executeUpdateSQL(labelDel);

			StringBuffer labelSql = new StringBuffer("insert into erp_flow_label (id, flowid, name, defaultvalue, type,position) values ");
			if (null != req.getLabelList() && !req.getLabelList().isEmpty()) {
				List<Object> values = new ArrayList<Object>();
				List<Type> typeValue = new ArrayList<>();
				int paramSize = 0;
				for (int i = 0; i < req.getLabelList().size(); i++) {
					FlowLabel label = req.getLabelList().get(i).getFlowLabel();
					label.setFlowId(flow.getFlowId());
					this.packageLabelParams(label, values, typeValue);
					// 参数个数
					if (i == 0) {
						paramSize = values.size();
					}
					// 拼接sql
					this.buildParamsSQL(paramSize, labelSql);
					if (i < req.getLabelList().size() - 1) {
						labelSql.append(",");
					}
				}
				baseDao.executeSqlUpdte(labelSql.toString(), values.toArray(), typeValue.toArray(new Type[typeValue.size()]));
			}
			// 修改流程时，需要把当前这个流程对应的还未归档的流程改到提交者处，重新开始流程
			/*
			 * String updateSql = "UPDATE erp_flow_ent set nodeid ='" +
			 * nextNodeId + "',remark='流程重新设计，提交者重新发起' where flowid ='" +
			 * flow.getFlowId() + "' and (flowstatus = " +
			 * FlowStatus.NOT_AUDIT.ordinal() + " or flowstatus = " +
			 * FlowStatus.NO_PASS.ordinal() + ")";
			 * baseDao.executeUpdateSQL(updateSql); this.subCashFlow(flow);
			 */
			return true;
		} catch (Exception e) {
			logger.error("修改流程失败", e);
			throw new ServiceException("修改流程失败", e);
		}
	}

	public void subCashFlow(ErpFlow flow) {
		try {
			String flowClass = flow.getFlowClass();
			BaseFlowTask task = flowTaskManager.getFlowTasks(flowClass);
			if (task == null) {
				task = commonFlowTask;
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flow.getFlowId()));
			filter.getOrRules().add(new SearchRule[] { new SearchRule("flowStatus", Constants.ROP_EQ, FlowStatus.NOT_AUDIT.ordinal()),
					new SearchRule("flowStatus", Constants.ROP_EQ, FlowStatus.NO_PASS.ordinal()) });
			List<FlowEnt> ents = flowEntService.queryAllBySearchFilter(filter);
			if (ents != null && !ents.isEmpty()) {
				for (FlowEnt ent : ents) {
					task.flowMsgModify(AuditResult.CANCLE.getCode(), ent);
				}
			}
		} catch (ServiceException e) {
			logger.error("修改流程失败", e);
		}
	}

	private void buildParamsSQL(int paramsSize, StringBuffer insertsql) {
		insertsql.append("(");
		for (int i = 0; i < paramsSize; i++) {
			insertsql.append("?");
			if (i < paramsSize - 1) {
				insertsql.append(",");
			}
		}
		insertsql.append(")");
	}

	private void packageLabelParams(FlowLabel out, List<Object> flowLabel_values, List<Type> typeValue) {
		flowLabel_values.add(out.getId());
		typeValue.add(StandardBasicTypes.STRING);
		flowLabel_values.add(out.getFlowId());
		typeValue.add(StandardBasicTypes.STRING);
		flowLabel_values.add(out.getName());
		typeValue.add(StandardBasicTypes.STRING);
		flowLabel_values.add(out.getDefaultValue());
		typeValue.add(StandardBasicTypes.STRING);
		flowLabel_values.add(out.getType());
		typeValue.add(StandardBasicTypes.INTEGER);
		flowLabel_values.add(out.getPosition());
		typeValue.add(StandardBasicTypes.INTEGER);
	}

	/**
	 * 根据流程的Id 查询流程的名称
	 * 
	 * @param flowIds
	 *            流程id
	 * @return id - 名称
	 */
	@Override
	public Map<String, String> findFlowNameByIds(List<String> flowIds) {
		Map<String, String> flowNames = new HashMap<>();
		if (flowIds == null || flowIds.isEmpty()) {
			return flowNames;
		}
		String hql = "select flowId, flowName from ErpFlow where flowId in(:ids)";
		Map<String, Object> params = new HashMap<>();
		params.put("ids", new ArrayList<>(new HashSet<>(flowIds)));
		List<Object> results = null;
		try {
			results = baseDao.findByhql(hql, params, Integer.MAX_VALUE);
		} catch (BaseException e) {
			logger.error("根据Id 查询流程名称异常", e);
		}
		if (results != null && !results.isEmpty()) {
			results.forEach(r -> {
				if (r.getClass().isArray()) {
					Object[] row = (Object[]) r;
					if (row.length >= 2) {
						flowNames.put(String.valueOf(row[0]), String.valueOf(row[1]));
					}
				}
			});
		}
		return flowNames;
	}

	/**
	 * 查询流程是否过角色
	 *
	 * @param onlineUser
	 *            当前用户
	 * @param flowIdList
	 *            流程设计id
	 *
	 * @return 过用户当前角色的流程设计id
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<String> checkFlowViewer(OnlineUser onlineUser, List<String> flowIdList) {
		if (null == onlineUser || CollectionUtils.isEmpty(flowIdList)) {
			return null;
		}
		try {
			Role role = roleService.read(onlineUser.getRoleId());
			// 数据权限
			int dataPermission = role.getDataPermission();
			if (dataPermission != DataPermission.Flow.ordinal()) {
				return flowIdList;
			} else {
				// 流程权限，判断流程是否过角色
				if (!CollectionUtils.isEmpty(flowIdList)) {
					flowIdList = flowIdList.stream().map(flowId -> "'" + flowId + "'").collect(Collectors.toList());
					String flowIds = String.join(",", flowIdList);
					String sql = "select distinct f.flowid from erp_flow f left join erp_flow_node fn on f.flowid = fn.flowid" + " where f.flowid in ("
							+ flowIds + ") and fn.roleid like '%" + onlineUser.getRoleId() + "%' order by f.flowname, fn.nodeindex";
					List<Object> result = (List<Object>) baseDao.selectSQL(sql);
					if (!CollectionUtils.isEmpty(result)) {
						return result.stream().map(String::valueOf).collect(Collectors.toList());
					}
				}
			}
		} catch (Exception e) {
			logger.error("检查流程是否过角色异常，角色Id：" + onlineUser.getRoleId(), e);
		}
		return null;
	}

	@Override
	public List<ErpFlow> findFlowByIds(List<String> flowIds) {
		List<ErpFlow> flowList = new ArrayList<>(flowIds.size());
		if (flowIds == null || flowIds.isEmpty()) {
			return flowList;
		}
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("flowId", Constants.ROP_IN, new ArrayList<>(new HashSet<>(flowIds))));
		List<ErpFlow> flows = null;
		try {
			flows = queryAllBySearchFilter(searchFilter);
		} catch (ServiceException e) {
			logger.error("根据id 查询流程信息异常", e);
		}
		if (flows != null && !flows.isEmpty()) {
			flowList.addAll(flows);
		}
		return flowList;
	}

	/**
	 * 获取用户可以申请的所有流程
	 *
	 * @param user
	 *            当前登录用户
	 * @return 流程信息
	 */
	@Override
	public List<ErpFlow> queryUserAllFlow(OnlineUser user) {
		try {
			String roleId = user.getRoleId();
			String hql = "select f From ErpFlow f INNER JOIN FlowNode n on f.startNodeId = n.nodeId WHERE (n.roleId like :roleId or f.flowType = :flowType) and f.status = :status";
			Map<String, Object> params = new HashMap<>();
			params.put("roleId", "%" + roleId + "%");
			params.put("flowType", FlowType.EMPLOYEE.getCode());
			params.put("status", EntityStatus.NORMAL.ordinal());
			return this.findByhql(hql, params, Integer.MAX_VALUE);
		} catch (Exception e) {
			logger.info("根据流程类型获取流程异常", e);
		}
		return null;
	}

	/**
	 * 根据流程id 查询流程信息
	 *
	 * @param flowId
	 *            流程id
	 * @return 流程详情
	 */
	@Override
	public FlowDetail queryFlowDetailById(String flowId) {
		try {
			ErpFlow flow = this.read(flowId);
			if (flow == null) {
				logger.info("移动端【流程申请】查询流程信息未能查找到流程");
				return null;
			}
			FlowDetail flowDetail = new FlowDetail();
			BeanUtils.copyProperties(flow, flowDetail);
			SearchFilter labelFilter = new SearchFilter();
			labelFilter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
			labelFilter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
			List<FlowLabel> flowLabels = flowLabelService.queryAllBySearchFilter(labelFilter);
			flowDetail.setLabels(flowLabels);
			// 流程开始节点
			String startNode = flow.getStartNodeId();
			if (StringUtil.isNotBlank(startNode)) {
				FlowNode flowNode = flowNodeService.read(startNode);
				if (flowNode != null) {
					String mustLabelIds = flowNode.getMustLabelIds();
					if (StringUtil.isNotBlank(mustLabelIds)) {
						flowDetail.setMustLabels(new HashSet<>(Arrays.asList(mustLabelIds.split(","))));
					}
					String editLabelIds = flowNode.getEditLabelIds();
					if (StringUtil.isNotBlank(editLabelIds)) {
						flowDetail.setEditLabels(new HashSet<>(Arrays.asList(editLabelIds.split(","))));
					}
				}
			}
			return flowDetail;
		} catch (ServiceException e) {
			logger.error("根据流程ID查询流程信息异常", e);
		}
		return null;
	}
}
