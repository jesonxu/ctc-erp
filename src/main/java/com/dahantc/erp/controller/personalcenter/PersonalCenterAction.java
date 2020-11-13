package com.dahantc.erp.controller.personalcenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.flow.ReadFlowEntRspDto;
import com.dahantc.erp.dto.operate.FlowEntRespDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.FlowAssociateType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.IdentityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntWithOpt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping(value = "/personalCenter")
public class PersonalCenterAction extends BaseAction {

	public static final Logger logger = LoggerFactory.getLogger(PersonalCenterAction.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IDepartmentService departmentService;

	/** 跳转个人中心页面 */
	@RequestMapping("/toPersonalCenterPage")
	public String toPersonalCenterPage() {
		return "/views/personalcenter/personalCenter";
	}

	/** 跳转个人信息页面 */
	@RequestMapping("/toPersonalInfoPage")
	public String toPersonalInfoPage() {
		request.setAttribute("personalInfo", userService.queryPersonInfo(getOnlineUser()));
		return "/views/personalcenter/personalInfo";
	}

	/** 跳转申请流程页面 */
	@RequestMapping("/toApplyFlowPage")
	public String toApplyFlowPage() {
		return "/views/personalcenter/applyFlow";
	}

	/** 跳转流程审核页面 */
	@RequestMapping("/toFlowAuditPage")
	public String toExamineFlowPage() {
		return "/views/personalcenter/flowAudit";
	}

	/** 跳转我的消息页面 */
	@RequestMapping("/toMyMessagePage")
	public String toMyMessagePage() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return null;
		}
		return "/views/personalcenter/myMessage";
	}

	/** 跳转工作汇报页面 */
	@RequestMapping("/toUserReportPage")
	public String toUserReportPage() {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return null;
		}
		int isLeader = -1; // IdentityType
		try {
			Role role = roleService.read(onlineUser.getRoleId());
			// 角色权限是自己但是是领导的按部门权限处理
			if (role.getDataPermission() == DataPermission.Self.ordinal() && onlineUser.getUser().getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal()) {
				role.setDataPermission(DataPermission.Dept.ordinal());
			}
			if (role.getDataPermission() == DataPermission.Dept.ordinal() || role.getDataPermission() == DataPermission.Customize.ordinal()
					|| (role.getDataPermission() == DataPermission.All.ordinal()
							&& onlineUser.getUser().getIdentityType() == IdentityType.LEADER_IN_DEPT.ordinal())) {
				List<String> list = departmentService.getDeptIdsByPermission(onlineUser, role);
				if (!CollectionUtils.isEmpty(list) && list.size() > 1) {
					isLeader = 1;
				} else {
					isLeader = 0;
				}
			} else {
				isLeader = 0;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		request.setAttribute("isLeader", isLeader);
		return "/views/personalcenter/userReport";
	}

	/** 查询个人流程 */
	@RequestMapping("/getPersonalFlow")
	@ResponseBody
	public BaseResponse<JSONArray> getPersonalFlow() {
		User user = getOnlineUser();
		if (user == null){
			return BaseResponse.error("请先登录");
		}
		JSONArray flowArray = new JSONArray();
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("associateType", Constants.ROP_EQ, FlowAssociateType.USER.ordinal()));
			searchFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(searchFilter);
			if (!CollectionUtils.isEmpty(flowList)) {
				for (ErpFlow flow : flowList) {
					JSONObject flowobj = new JSONObject();
					flowobj.put("flowName", flow.getFlowName());
					flowobj.put("flowId", flow.getFlowId());
					flowArray.add(flowobj);
				}
			}
			return BaseResponse.success(flowArray);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(flowArray);
	}

	/** 查询个人历史流程 */
	@RequestMapping("/queryHistoryFlowEnts")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> queryHistoryFlowEnts() {
		List<ReadFlowEntRspDto> result = new ArrayList<>();
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("associateType", Constants.ROP_EQ, FlowAssociateType.USER.ordinal()));
			searchFilter.getRules().add(new SearchRule("status", Constants.ROP_EQ, EntityStatus.NORMAL.ordinal()));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(searchFilter);
			List<String> flowIdList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(flowList)) {
				for (ErpFlow flow : flowList) {
					flowIdList.add(flow.getFlowId());
				}
			} else {
				return BaseResponse.success(result);
			}

			searchFilter.getRules().clear();
			searchFilter.getRules().add(new SearchRule("flowId", Constants.ROP_IN, flowIdList));
			searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, getOnlineUser().getOssUserId()));
			// 查询一年之内的流程
			Calendar calender = Calendar.getInstance();
			calender.setTime(new Date());
			calender.add(Calendar.YEAR, -1);
			searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, calender.getTime()));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<FlowEnt> flowEnts = flowEntService.queryAllBySearchFilter(searchFilter);

			if (!CollectionUtils.isEmpty(flowEnts)) {

				// 流程节点
				Map<String, List<FlowNode>> flowNodeInfos = flowNodeService
						.findFlowNodeByIds(flowEnts.stream().map(FlowEnt::getNodeId).collect(Collectors.toList())).stream()
						.collect(Collectors.groupingBy(FlowNode::getNodeId));

				String realName = getOnlineUser().getRealName();
				for (FlowEnt ent : flowEnts) {
					ReadFlowEntRspDto dto = new ReadFlowEntRspDto();
					dto.setId(ent.getId());
					dto.setFlowId(ent.getFlowId());
					dto.setFlowTitle(ent.getFlowTitle());
					dto.setOssUserId(ent.getOssUserId());
					dto.setUserName(realName);
					dto.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
					dto.setNodeId(ent.getNodeId());
					dto.setWtime(DateUtil.convert(ent.getWtime(), DateUtil.format2));
					List<FlowNode> nodeList = flowNodeInfos.get(ent.getNodeId());
					if (!CollectionUtils.isEmpty(nodeList)) {
						FlowNode flowNode = nodeList.get(0);
						dto.setNodeName(flowNode.getNodeName());
					}
					result.add(dto);
				}
			}
			return BaseResponse.success(result);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(result);
	}

	@RequestMapping("/getPersonalFlowEntByPage")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> getPersonalFlowEntByPage(int page, int pageSize) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}

		logger.info("查询员工流程，用户：" + onlineUser.getUser().getRealName() + "：page：" + page + "，pageSize：" + pageSize);
		long startTime = System.currentTimeMillis();

		// 存放所有角色查出的流程 {flowEntId -> dto}
		Map<String, FlowEntRespDto> resultMap = new HashMap<>();

		// 用户所有角色
		List<Role> roleList = roleService.findUserAllRole(onlineUser.getUser().getOssUserId());
		// 角色流程数最多的那个角色的流程总页数
		int maxPage = 0;

		if (CollectionUtils.isEmpty(roleList)) {
			logger.info("用户没有角色");
			return BaseResponse.success(maxPage + "", new ArrayList<>(resultMap.values()));
		}

		// 流程申请人部门id -> 部门名
		Map<String, String> deptNameMap = new HashMap<>();
		// 流程申请人id -> 姓名
		Map<String, String> realNameMap = new HashMap<>();

		try {
			// 查每个角色能看到的流程
			for (Role role : roleList) {
				String roleId = role.getRoleid();
				onlineUser.setRoleId(roleId);
				// 分页查当前角色的流程
				PageResult<FlowEntWithOpt> roleFlow = flowEntService.queryFlowEntByPageSql(onlineUser, null, FlowType.EMPLOYEE.ordinal(), null, null, null,
						pageSize, page, null);
				if (null == roleFlow || roleFlow.getData() == null || roleFlow.getData().isEmpty()) {
					logger.info("用户角色无流程，ossUserId：" + onlineUser.getUser().getOssUserId() + "，roleId：" + roleId);
					continue;
				}
				if (roleFlow.getTotalPages() > maxPage) {
					maxPage = roleFlow.getTotalPages();
				}
				List<FlowEntWithOpt> flowList = roleFlow.getData();
				logger.info("用户角色：" + roleId + "，总页：" + roleFlow.getTotalPages() + "，分页：" + page + "，当页流程数：" + flowList.size());
				List<FlowNode> flowNodeList = flowNodeService.findFlowNodeByIds(flowList.stream().map(FlowEntWithOpt::getNodeId).collect(Collectors.toList()));
				Map<String, FlowNode> flowNodeInfos = new HashMap<>();
				if (flowNodeList != null) {
					flowNodeList.forEach(node -> flowNodeInfos.put(node.getNodeId(), node));
					flowNodeList.clear();
				}

				// 封装返回结果
				for (FlowEntWithOpt ent : flowList) {
					FlowEntRespDto rsp = new FlowEntRespDto();
					rsp.setFlowTitle(ent.getFlowTitle());
					rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
					rsp.setId(ent.getId());
					rsp.setProductId(ent.getProductId());
					rsp.setApplyTime(DateUtil.convert(ent.getWtime(), DateUtil.format1));
					// 申请人
					if (ent.getOssUserId() != null) {
						if (realNameMap.containsKey(ent.getOssUserId())) {
							rsp.setRealName(realNameMap.get(ent.getOssUserId()));
						} else {
							User user = userService.read(ent.getOssUserId());
							if (null != user) {
								rsp.setRealName(user.getRealName());
								realNameMap.put(user.getOssUserId(), user.getRealName());
							} else {
								rsp.setRealName("未知");
							}
						}
					} else {
						rsp.setRealName("未知");
					}
					// 申请人部门
					if (ent.getDeptId() != null) {
						if (deptNameMap.containsKey(ent.getDeptId())) {
							rsp.setDeptName(deptNameMap.get(ent.getDeptId()));
						} else {
							Department dept = departmentService.read(ent.getDeptId());
							if (null != dept) {
								rsp.setDeptName(dept.getDeptname());
								deptNameMap.put(dept.getDeptid(), dept.getDeptname());
							} else {
								rsp.setDeptName("未知");
							}
						}
					} else {
						rsp.setDeptName("未知");
					}
					FlowNode flowNode = flowNodeInfos.get(ent.getNodeId());
					if (flowNode != null) {
						rsp.setNodeName(flowNode.getNodeName());
						// 当前角色可处理
						if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
								&& (flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
								&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
							rsp.setCanOperat(true);
						}
					}
					if (!resultMap.containsKey(ent.getId()) || rsp.isCanOperat()) {
						resultMap.put(ent.getId(), rsp);
					}
				}
			}
			logger.info("查询员工流程，耗时：" + (System.currentTimeMillis() - startTime));
			List<FlowEntRespDto> dtoList = new ArrayList<>(resultMap.values());
			dtoList.sort((dto1, dto2) -> {
				return -BooleanUtils.compare(dto1.isCanOperat(), dto2.isCanOperat());
			});
			return BaseResponse.success(maxPage + "", dtoList);
		} catch (Exception e) {
			logger.info("查询员工流程异常", e);
		}
		return BaseResponse.error("查询员工流程失败");
	}

}
