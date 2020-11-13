package com.dahantc.erp.controller.flow;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.FlowTaskManager;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.flow.AddFlowDto;
import com.dahantc.erp.dto.flow.DealRecordDto;
import com.dahantc.erp.dto.flow.ErpFlowUI;
import com.dahantc.erp.dto.flow.FlowThresholdDto;
import com.dahantc.erp.dto.flow.ReadFlowDetailRespDto;
import com.dahantc.erp.dto.flow.ReadFlowEntDto;
import com.dahantc.erp.dto.flow.ReadFlowEntRspDto;
import com.dahantc.erp.dto.flow.ReadFlowLabelRspDto;
import com.dahantc.erp.dto.flow.ReadFlowNodeRspDto;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.BindType;
import com.dahantc.erp.enums.ChargeType;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowAssociateType;
import com.dahantc.erp.enums.FlowLabelType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.PriceType;
import com.dahantc.erp.enums.RoleType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dianshangProduct.entity.DianShangProduct;
import com.dahantc.erp.vo.dianshangProduct.service.IDianShangProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/flow")
public class FlowAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(FlowAction.class);
	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private FlowTaskManager flowtaskManaget;

	@Autowired
	private IProductService productService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IDianShangProductService dsProductService;

	@Autowired
	private IProductTypeService productTypeService;

	private int pageSize = 15;

	private int nowPage = 1;

	@RequestMapping("toFlow")
	public String toFlow() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		return "/views/flow/flow";
	}

	@RequestMapping("toEditFlow")
	public String toEditFlow() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		String flowId = request.getParameter("flowId");
		if (flowId != null) {
			try {
				ErpFlow flow = erpFlowService.read(flowId);
				request.setAttribute("flow", flow);
			} catch (ServiceException e) {
				logger.info(e.getMessage(), e);
			}
		}
		List<String> bindTypeList = new ArrayList<>();
		for (BindType bindType : BindType.values()) {
			bindTypeList.add(bindType.getDesc());
		}
		Map<Integer, String> associateTypes = new HashMap<>();
		for (FlowAssociateType associateType : FlowAssociateType.values()) {
			associateTypes.put(associateType.getCode(), associateType.getDesc());
		}
		request.setAttribute("bindTypeList", bindTypeList);
		request.setAttribute("associateTypes", associateTypes);
		request.setAttribute("methodtype", "edit");
		request.setAttribute("title", "修改流程");
		return "/views/flow/updateFlow";
	}

	@RequestMapping("toAddFlow")
	public String toAddFlow() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		List<String> bindTypeList = new ArrayList<>();
		for (BindType bindType : BindType.values()) {
			bindTypeList.add(bindType.getDesc());
		}
		request.setAttribute("bindTypeList", bindTypeList);
		request.setAttribute("methodtype", "add");
		Map<Integer, String> associateTypes = new HashMap<>();
		for (FlowAssociateType associateType : FlowAssociateType.values()) {
			associateTypes.put(associateType.getCode(), associateType.getDesc());
		}
		request.setAttribute("associateTypes", associateTypes);
		request.setAttribute("title", "添加流程");
		return "/views/flow/updateFlow";
	}

	@RequestMapping("toAddNode")
	public String toAddNode() {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		request.setAttribute("roles", "");
		return "/views/flow/addNode";
	}

	@RequestMapping("readFlowLabel")
	@ResponseBody
	public BaseResponse<PageResult<ReadFlowLabelRspDto>> readFlowLabel(@RequestParam String id) {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		if (StringUtils.isBlank(id)) {
			return BaseResponse.success(new PageResult<>(new ArrayList<>(), 0));
		}

		try {
			List<FlowLabel> list = getFlowLabelList(id);
			List<ReadFlowLabelRspDto> uilist = new ArrayList<>();
			for (FlowLabel label : list) {
				ReadFlowLabelRspDto dto = new ReadFlowLabelRspDto(label);
				uilist.add(dto);
			}
			return BaseResponse.success(new PageResult<>(uilist, uilist.size()));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询节点标签信息失败");
	}

	@RequestMapping("/readFlowNode")
	@ResponseBody
	public BaseResponse<PageResult<ReadFlowNodeRspDto>> readFlowNode(@RequestParam String id) {
		User user = getOnlineUser();
		if (null == user) {
			return null;
		}
		if (StringUtils.isBlank(id)) {
			return BaseResponse.success(new PageResult<>(new ArrayList<>(), 0));
		}
		Map<String, FlowLabel> map = new HashMap<>();
		Map<String, Role> roleMap = new HashMap<>();
		try {
			List<ReadFlowNodeRspDto> uilist = new ArrayList<>();

			// 角色
			List<Role> dataList = roleService.queryAllBySearchFilter(new SearchFilter());
			for (Role role : dataList) {
				roleMap.put(role.getRoleid(), role);
			}
			// 标签
			List<FlowLabel> list = getFlowLabelList(id);
			for (FlowLabel label : list) {
				map.put(label.getId(), label);
			}

			// 节点
			List<FlowNode> nodeList = getFlowNodeList(id);
			for (FlowNode node : nodeList) {
				ReadFlowNodeRspDto dto = new ReadFlowNodeRspDto();
				dto.setId(node.getNodeId());
				dto.setName(node.getNodeName());
				List<String> roleName = new ArrayList<>();
				List<String> roleIds = new ArrayList<>();
				for (String roId : node.getRoleId().split(",")) {
					if (roleMap.containsKey(roId)) {
						roleName.add(roleMap.get(roId).getRolename());
						roleIds.add(roId);
					}
				}
				dto.setRoleId(String.join(",", roleIds));
				dto.setRole(String.join(",", roleName));
				dto.setDueTime(node.getDueTime() + "");
				List<String> viewLabel = new ArrayList<>();
				List<String> viewLabelId = new ArrayList<>();
				for (String labId : node.getViewLabelIds().split(",")) {
					if (map.containsKey(labId)) {
						viewLabel.add(map.get(labId).getName());
						viewLabelId.add(labId);
					}
				}
				dto.setViewLabel(String.join(",", viewLabel));
				dto.setViewLabelId(String.join(",", viewLabelId));

				List<String> editLabel = new ArrayList<>();
				List<String> editLabelId = new ArrayList<>();
				for (String labId : node.getEditLabelIds().split(",")) {
					if (map.containsKey(labId)) {
						editLabel.add(map.get(labId).getName());
						editLabelId.add(labId);
					}
				}
				dto.setEditLabel(String.join(",", editLabel));
				dto.setEditLabelId(String.join(",", editLabelId));

				List<String> mustLabel = new ArrayList<>();
				List<String> mustLabelId = new ArrayList<>();
				for (String labId : node.getMustLabelIds().split(",")) {
					if (map.containsKey(labId)) {
						mustLabel.add(map.get(labId).getName());
						mustLabelId.add(labId);
					}
				}
				dto.setMustLabel(String.join(",", mustLabel));
				dto.setMustLabelId(String.join(",", mustLabelId));

				// 节点阈值
				String flowThreshold = node.getFlowThreshold();
				if (StringUtil.isNotBlank(flowThreshold)) {
					List<FlowThresholdDto> thresholds = JSONArray.parseArray(flowThreshold, FlowThresholdDto.class);
					List<String> parameterIds = thresholds.stream().map(FlowThresholdDto::getThresholdValue).collect(Collectors.toList());
					SearchFilter parameterFilter = new SearchFilter();
					parameterFilter.getRules().add(new SearchRule("entityid", Constants.ROP_IN, parameterIds));
					List<Parameter> parameters = parameterService.findAllByCriteria(parameterFilter);
					if (parameters != null && !parameters.isEmpty()) {
						Map<String, String> parameterInfos = parameters.stream()
								.peek(parameter -> parameter.setParamvalue(parameter.getParamkey() + "(" + parameter.getParamvalue() + ")"))
								.collect(Collectors.toMap(Parameter::getEntityid, Parameter::getParamvalue));
						StringBuffer thresholdInfos = new StringBuffer();
						thresholds.forEach(flowThresholdDto -> {
							String labelId = flowThresholdDto.getLabelId();
							if (StringUtil.isNotBlank(labelId)) {
								FlowLabel label = map.get(labelId);
								if (label != null) {
									parameterInfos.get(flowThresholdDto.getThresholdValue());
									thresholdInfos.append("[").append(label.getName()).append(" ")
											.append(StringUtil.getMatchName(flowThresholdDto.getRelationship())).append(" ")
											.append(parameterInfos.get(flowThresholdDto.getThresholdValue())).append("]");
								}
							}
						});
						dto.setThresholdInfos(thresholdInfos.toString());
						dto.setThresholds(thresholds);
					}
				}
				// 阈值脚本文件信息
				String thresholdFile = node.getThresholdFile();
				if (StringUtil.isNotBlank(thresholdFile)) {
					JSONObject fileInfo = JSON.parseObject(thresholdFile);
					dto.setThresholdFileName(fileInfo.getString("fileName"));
					dto.setThresholdFile(thresholdFile);
				}
				uilist.add(dto);
			}
			return BaseResponse.success(new PageResult<>(uilist, uilist.size()));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询节点标签信息失败");
	}

	@PostMapping("/addFlow")
	@ResponseBody
	public BaseResponse<String> addFlow(@Valid @RequestBody AddFlowDto req) {
		logger.info("添加流程开始");
		long _start = System.currentTimeMillis();
		User user = getOnlineUser();
		if (null == user) {
			logger.info("用户未登录，添加失败");
			return BaseResponse.noLogin("请先登录");
		}
		try {
			req.setCreatorId(user.getOssUserId());
			erpFlowService.addFlow(req);
			logger.info("流程添加成功，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success("添加流程信息成功");
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("添加流程信息失败");
	}

	@PostMapping("/editFlow")
	@ResponseBody
	public BaseResponse<String> editFlow(@Valid @RequestBody AddFlowDto req) {
		logger.info("修改流程开始");
		long _start = System.currentTimeMillis();
		User user = getOnlineUser();
		if (null == user) {
			logger.info("用户未登录，修改失败");
			return BaseResponse.noLogin("请先登录");
		}
		if (StringUtils.isBlank(req.getFlowId())) {
			logger.info("流程ID为空，修改失败");
			return BaseResponse.error("流程ID不能为空");
		}
		try {
			ErpFlow flow = erpFlowService.read(req.getFlowId());
			if (null == flow) {
				logger.info("根据ID未查询到流程，修改失败");
				return BaseResponse.error("根据ID未查询到流程");
			}
			erpFlowService.editFlow(req);
			logger.info("流程修改成功，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success("修改流程信息成功");
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("修改流程信息失败");
	}

	@RequestMapping("/enableFlow")
	@ResponseBody
	public BaseResponse<String> enableFlow(@RequestParam String flowId) {
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.noLogin("请先登录");
		}
		if (StringUtils.isBlank(flowId)) {
			return BaseResponse.error("流程ID不能为空");
		}
		try {
			ErpFlow flow = erpFlowService.read(flowId);
			if (null == flow) {
				return BaseResponse.error("根据ID未查询到流程");
			}
			String msg = "";
			if (flow.getStatus() == EntityStatus.NORMAL.ordinal()) {
				flow.setStatus(EntityStatus.DELETED.ordinal());
				msg = "禁用";
			} else {
				flow.setStatus(EntityStatus.NORMAL.ordinal());
				msg = "激活";
			}
			erpFlowService.update(flow);
			return BaseResponse.success("流程" + msg + "成功");
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("禁用/激活流程失败");
	}

	/**
	 * 分页查询流程信息
	 *
	 * @return 流程分页
	 */
	@RequestMapping(value = "/readPages")
	@ResponseBody
	public BaseResponse<PageResult<ErpFlowUI>> readPages() {
		PageResult<ErpFlowUI> flows = new PageResult<ErpFlowUI>();
		logger.info("查询流程开始");
		try {
			long _start = System.currentTimeMillis();
			if (StringUtils.isNotBlank(request.getParameter("limit"))) {
				pageSize = Integer.parseInt(request.getParameter("limit"));
			}
			if (StringUtils.isNotBlank(request.getParameter("page"))) {
				nowPage = Integer.parseInt(request.getParameter("page"));
			}
			String flowName = request.getParameter("flowName");
			String creatorid = request.getParameter("creatorid");
			String startTime = request.getParameter("date");
			String endTime = request.getParameter("endDate");
			String status = request.getParameter("status");
			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(flowName)) {
				filter.getRules().add(new SearchRule("flowName", Constants.ROP_CN, flowName));
			}
			if (StringUtils.isNotBlank(creatorid)) {
				filter.getRules().add(new SearchRule("creatorid", Constants.ROP_EQ, creatorid));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (StringUtils.isBlank(startTime)) {
				startTime = sdf.format(new Date()) + " 00:00:00";
			}
			if (StringUtils.isBlank(endTime)) {
				endTime = sdf.format(new Date()) + " 23:59:59";
			}
			if (StringUtils.isNotBlank(status)) {
				String[] states = status.split(",");
				if (states.length == 1) {
					filter.getRules().add(new SearchRule("status", Constants.ROP_EQ, Integer.parseInt(status)));
				} else {
					filter.getOrRules().add(new SearchRule[] { new SearchRule("status", Constants.ROP_EQ, Integer.parseInt(states[0])),
							new SearchRule("status", Constants.ROP_EQ, Integer.parseInt(states[1])) });
				}
			}
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert2(startTime)));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.convert2(endTime)));
			filter.getOrders().add(new SearchOrder("wtime", "desc"));
			PageResult<ErpFlow> page = erpFlowService.queryByPages(pageSize, nowPage, filter);
			flows = new PageResult<ErpFlowUI>(buildErpFlowUI(page.getData()), page.getCurrentPage(), page.getTotalPages(), page.getCount());
			logger.info("查询流程结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.info("查询流程异常", e.getMessage());
			flows.setData(new ArrayList<ErpFlowUI>());
		}
		return BaseResponse.success(flows);
	}

	@PostMapping("/readSelfFlowEnt")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> readSelfFlowEnt(@Valid ReadFlowEntDto req) {
		User user = getOnlineUser();
		if (null == user) {
			return BaseResponse.noLogin("请先登录");
		}
		try {
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.getNextMonthFirst(req.getDate())));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert2(req.getDate() + "-01 00:00:00")));
			List<FlowEnt> flowList = flowEntService.queryAllBySearchFilter(filter);
			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success(resultList);
			}
			flowList.sort((o1, o2) -> {
				return o1.getWtime().compareTo(o2.getWtime());
			});

			for (FlowEnt ent : flowList) {
				ReadFlowEntRspDto rsp = new ReadFlowEntRspDto();
				rsp.setFlowId(ent.getFlowId());
				rsp.setFlowTitle(rsp.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setNodeId(ent.getNodeId());
				rsp.setOssUserId(ent.getOssUserId());
				rsp.setId(ent.getId());
				User tempUser = userService.read(ent.getOssUserId());
				if (null != tempUser) {
					rsp.setUserName(tempUser.getRealName());
				}
				ErpFlow erpFlow = erpFlowService.read(ent.getFlowId());
				if (null != erpFlow) {
					rsp.setFlowName(erpFlow.getFlowName());
				}
				FlowNode flowNode = flowNodeService.read(ent.getNodeId());
				if (null != flowNode) {
					rsp.setNodeName(flowNode.getNodeName());
				}
				resultList.add(rsp);
			}

			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}

	@PostMapping("/readFlowEnt")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> readFlowEnt(@Valid ReadFlowEntDto req) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		Map<String, Object> queryParam = new HashMap<>();
		try {
			String ossUserId = onlineUser.getUser().getOssUserId();
			String roleId = onlineUser.getRoleId();
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();
			SearchFilter nodefilter = new SearchFilter();
			nodefilter.getRules().add(new SearchRule("roleId", Constants.ROP_CN, roleId));
			List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(nodefilter);
			List<String> nodeIdList = new ArrayList<>();
			StringBuffer flowHql = new StringBuffer("FROM FlowEnt where flowType=" + FlowType.OPERATE.ordinal() + " and productId=:productId and");
			queryParam.put("productId", req.getProductId());
			flowHql.append(" ((( ");
			for (FlowNode flowNode : nodeList) {
				if (flowNode.getNodeIndex() == 0) {
					flowHql.append(" (nodeId ='" + flowNode.getNodeId() + "' and ossUserId ='").append(ossUserId).append("') or ");
				} else {
					nodeIdList.add(flowNode.getNodeId());
				}
			}
			flowHql.append(" nodeId in :nodeId)  ");
			queryParam.put("nodeId", nodeIdList);
			flowHql.append(" and wtime < :endTime and wtime >= :startTime)  or id in :id)");
			Timestamp endTime = new Timestamp(DateUtil.getNextMonthFirst(req.getDate()).getTime());
			Date startTime = new Timestamp(DateUtil.convert2(req.getDate() + "-01 00:00:00").getTime());
			queryParam.put("endTime", endTime);
			queryParam.put("startTime", startTime);
			SearchFilter logfilter = new SearchFilter();
			logfilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, ossUserId));
			logfilter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, DateUtil.getNextMonthFirst(req.getDate())));
			logfilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert2(req.getDate() + "-01 00:00:00")));
			List<FlowLog> log = flowLogService.queryAllBySearchFilter(logfilter);
			List<String> entId = log.stream().map(FlowLog::getFlowEntId).collect(Collectors.toList());
			queryParam.put("id", entId);
			List<FlowEnt> flowList = flowEntService.findByhql(flowHql.toString(), queryParam, 0);
			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success(resultList);
			}
			flowList.sort((o1, o2) -> {
				return o1.getWtime().compareTo(o2.getWtime());
			});

			for (FlowEnt ent : flowList) {
				ReadFlowEntRspDto rsp = new ReadFlowEntRspDto();
				rsp.setFlowId(ent.getFlowId());
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setNodeId(ent.getNodeId());
				rsp.setOssUserId(ent.getOssUserId());
				rsp.setId(ent.getId());
				User tempUser = userService.read(ent.getOssUserId());
				if (null != tempUser) {
					rsp.setUserName(tempUser.getRealName());
				}
				ErpFlow erpFlow = erpFlowService.read(ent.getFlowId());
				if (null != erpFlow) {
					rsp.setFlowName(erpFlow.getFlowName());
				}
				FlowNode flowNode = flowNodeService.read(ent.getNodeId());
				if (null != flowNode) {
					rsp.setNodeName(flowNode.getNodeName());
				}
				resultList.add(rsp);
			}
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}

	/**
	 * 查询流程详细信息
	 *
	 * @param id
	 *            flowEnt表id
	 * @return
	 */
	@RequestMapping("/flowDetail")
	@ResponseBody
	public BaseResponse<ReadFlowDetailRespDto> readFlowDetail(@RequestParam String id) {
		logger.info("获取流程处理详情开始，流程实体id：" + id);
		if (StringUtils.isBlank(id)) {
			return BaseResponse.error("流程实体id不能为空");
		}
		ReadFlowDetailRespDto respDto = new ReadFlowDetailRespDto();
		Map<String, User> userMap = new HashMap<String, User>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("请先登录");
			}
			FlowEnt flowEnt = flowEntService.read(id);
			if (flowEnt == null) {
				return BaseResponse.error("流程实体信息错误");
			}
			ErpFlow flow = erpFlowService.read(flowEnt.getFlowId());
			respDto.setFlowStatus(flowEnt.getFlowStatus());
			respDto.setFlowId(flowEnt.getFlowId());
			respDto.setFlowClass(flow.getFlowClass());
			respDto.setFlowEntId(flowEnt.getId());
			respDto.setProductId(flowEnt.getProductId());
			respDto.setSupplierId(flowEnt.getSupplierId());
			respDto.setAssociateType(flow.getAssociateType());
			int entityType = flowEnt.getEntityType();
			respDto.setEntityType(entityType);

			FlowNode fNode = flowNodeService.read(flowEnt.getNodeId());

			// 判断是不是能取消, 1.申请人是自己, 2.未归档(未审核, 审核未通过), 3.节点不在第一个
			if (StringUtils.equals(getOnlineUser().getOssUserId(), flowEnt.getOssUserId())
					&& (flowEnt.getFlowStatus() == FlowStatus.NOT_AUDIT.ordinal() || flowEnt.getFlowStatus() == FlowStatus.NO_PASS.ordinal())
					&& (fNode != null && fNode.getNodeIndex() != 0)) {
				respDto.setCanRevoke(true);
			} else {
				respDto.setCanRevoke(false);
			}

			if (FlowAssociateType.USER.getCode() == flow.getAssociateType()) {
				// 员工流程 不需要供应商、客户、产品
				respDto.setProductName("");
				respDto.setSupplierName("");
			} else if (EntityType.SUPPLIER.ordinal() == entityType) {
				Supplier supplier = supplierService.read(flowEnt.getSupplierId());
				if (supplier != null) {
					respDto.setSupplierName(supplier.getCompanyName());
					Product product = null;
					if (StringUtil.isNotBlank(flowEnt.getProductId())) {
						product = productService.read(flowEnt.getProductId());
					}
					respDto.setProductName(product != null ? product.getProductName() : "未知");
				}
			} else if (EntityType.CUSTOMER.ordinal() == entityType) {
				Customer customer = customerService.read(flowEnt.getSupplierId());
				if (customer != null) {
					respDto.setSupplierName(customer.getCompanyName());
					CustomerProduct product = null;
					if (StringUtil.isNotBlank(flowEnt.getProductId())) {
						product = customerProductService.read(flowEnt.getProductId());
					}
					respDto.setProductName(product != null ? product.getProductName() : "未知");
				}
			} else if (EntityType.SUPPLIER_DS.ordinal() == entityType) {
				Supplier supplier = supplierService.read(flowEnt.getSupplierId());
				if (supplier != null) {
					respDto.setSupplierName(supplier.getCompanyName());
					DianShangProduct product = null;
					if (StringUtil.isNotBlank(flowEnt.getProductId())) {
						product = dsProductService.read(flowEnt.getProductId());
					}
					respDto.setProductName(product != null ? product.getProductname() : "未知");
				}
			}
			respDto.setApplyTime(flowEnt.getWtime());
			String flowMsg = flowEnt.getFlowMsg();
			if (StringUtil.isNotBlank(flowMsg)) {
				JSONObject json = JSONObject.parseObject(flowMsg, Feature.OrderedField);
				if (json != null) {
					for (String key : json.keySet()) {
						// 账单流程平台数据
						if (Constants.FLOW_BASE_DATA_KEY.equals(key)) {
							JSONObject baseData = json.getJSONObject(key);
							if (baseData != null) {
								for (String _key : baseData.keySet()) {
									respDto.getBaseDataMap().put(_key, baseData.getString(_key));
								}
							}
						} else { // 流程标签的值
							respDto.getLabelValueMap().put(key, json.getString(key));
						}
					}
				}
			}

			User user = userService.read(flowEnt.getOssUserId());
			if (user != null) {
				respDto.setOssUserId(user.getOssUserId());
				respDto.setOssUserName(user.getRealName());
			}
			// 流程创建人
			/*
			 * User user = userMap.get(flowEnt.getOssUserId()); if (user ==
			 * null) { user = userService.read(flowEnt.getOssUserId()); } if
			 * (user != null) { respDto.setOssUserId(user.getOssUserId());
			 * respDto.setOssUserName(user.getRealName());
			 * userMap.put(flowEnt.getOssUserId(), user); }
			 */
			respDto.setFlowTitle(flowEnt.getFlowTitle());
			// 设置流程处理记录
			respDto.setRecord(getFlowLogList(flowEnt.getId()));
			// 获取流程的所有标签
			List<FlowLabel> labelList = getFlowLabelList(flowEnt.getFlowId());
			int nodeIndex = -1;
			// 流程当前节点的可处理角色
			String roleId = "";
			// 当前用户的角色
			String userRoleId = onlineUser.getRoleId();
			if (labelList != null && labelList.size() > 0) {
				// 当前用户的角色可以处理的节点
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowEnt.getFlowId()));
				filter.getRules().add(new SearchRule("roleId", Constants.ROP_CN, userRoleId));
				filter.getOrders().add(new SearchOrder("nodeIndex", Constants.ROP_DESC));
				List<FlowNode> roleNodes = flowNodeService.queryAllBySearchFilter(filter);

				// 当前用户的角色的所有展示标签
				String viewLabelIds = "";
				Set<String> viewLabelSet = new HashSet<String>();
				for (FlowNode roleNode : roleNodes) {
					viewLabelSet.addAll(Arrays.asList(roleNode.getViewLabelIds().split(",")));
				}
				viewLabelIds = String.join(",", viewLabelSet);

				// 当前用户的角色的流程展示标签
				if (StringUtil.isNotBlank(viewLabelIds)) {
					for (FlowLabel flowLabel : labelList) {
						if (viewLabelIds.contains(flowLabel.getId())) {
							respDto.getLabelList().add(flowLabel);
						}
					}
				}
				// 允许查看此流程的角色中有当前用户的角色，则展示所有标签
				if (StringUtil.isNotBlank(flowEnt.getViewerRoleId()) && flowEnt.getViewerRoleId().contains(userRoleId)) {
					respDto.setLabelList(labelList);
				}
				if (flowEnt.getOssUserId().equals(onlineUser.getUser().getOssUserId())) {
					respDto.setLabelList(labelList);
				}
				// 流程当前节点
				FlowNode flowNode = flowNodeService.read(flowEnt.getNodeId());

				if (flowNode != null) {
					// 流程可编辑标签
					respDto.setEditLabelIds(flowNode.getEditLabelIds());
					// 流程必要标签
					respDto.setMustLabelIds(flowNode.getMustLabelIds());
					// 本节点可以处理的角色
					roleId = flowNode.getRoleId();
					// 本节点的序号
					nodeIndex = flowNode.getNodeIndex();
					respDto.setNodeIndex(nodeIndex);
					String nodeId = flowNode.getNodeId();
					respDto.setNodeId(nodeId);
				}
			}

			// 流程可以审核（未归档、未取消）
			if (flowEnt.getFlowStatus() != FlowStatus.FILED.ordinal() && flowEnt.getFlowStatus() != FlowStatus.CANCLE.ordinal()) {
				// 员工流程，在员工中心处理，只要自己任意一个角色能处理该流程，则可以处理
				if (flow.getFlowType() == FlowType.EMPLOYEE.getCode()) {
					// 用户角色列表
					List<Role> userRoleList = roleService.findUserAllRole(onlineUser.getUser().getOssUserId());
					for (Role role : userRoleList) {
						// 起始节点&&自己发起 | 审核节点&&自己有处理角色
						if ((nodeIndex == 0 && onlineUser.getUser().getOssUserId().equals(flowEnt.getOssUserId())) || (nodeIndex != 0 && roleId.contains(role.getRoleid()))) {
							respDto.setCanOperat(true);
							logger.info("员工流程：flowEntId");
							break;
						}
					}
				} else {
					// 普通流程，在各自工作台，必须用相应角色才可以处理
					if (roleId.contains(onlineUser.getRoleId()) && (nodeIndex != 0 || onlineUser.getUser().getOssUserId().equals(flowEnt.getOssUserId()))) {
						respDto.setCanOperat(true);
					}
				}

				if (nodeIndex == 0) {
					// 发起人节点，直接展示发起人的名字
					respDto.setDealUserName(respDto.getOssUserName());
				} else {
					// 非发起人节点，展示待处理的角色名
					respDto.setDealRoleName("无可处理此流程的人，请联系管理员");
					// 获取实际能处理该节点的角色和用户
					Map<String, List<String>> dealRoleAndUser = userService.getDealRoleAndUser(user, roleId);
					String[] roleIds = roleId.split(",");
					for (String roleid : roleIds) {
						List<String> dealUsers = dealRoleAndUser.getOrDefault(roleid, new ArrayList<>());
						// 找到第1个有用户的角色，只取前两个用户
						if (dealUsers.size() == 0) {
							continue;
						} else if (dealUsers.size() <= 2) {
							Role role = roleService.read(roleid);
							respDto.setDealRoleName(role.getRolename() + "(" + String.join(",", dealUsers) + ");……");
						} else {
							Role role = roleService.read(roleid);
							respDto.setDealRoleName(
									role.getRolename() + "(" + String.join(",", dealUsers.stream().limit(2).collect(Collectors.toList())) + "…);……");
						}
						break;
					}
				}
			}
			logger.info("获取流程处理详情结束");
		} catch (Exception e) {
			logger.error("获取流程处理详情异常，流程实体id：" + id, e);
		} finally {
			if (userMap != null) {
				userMap.clear();
				userMap = null;
			}
		}
		return BaseResponse.success(respDto);
	}

	/**
	 * 查询流程处理记录（log表）
	 * 
	 * @param flowEntId
	 *            流程实体id
	 * @return
	 */
	private List<DealRecordDto> getFlowLogList(String flowEntId) {
		List<DealRecordDto> details = new ArrayList<DealRecordDto>();
		List<FlowLog> list = null;
		Map<String, Role> roleMap = new HashMap<String, Role>();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEntId));
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
			list = flowLogService.queryAllBySearchFilter(filter);
			if (list != null && list.size() > 0) {
				Map<String, String> userNameMap = userService.findUserNameByIds(list.stream().map(FlowLog::getOssUserId).collect(Collectors.toList()));
				for (FlowLog flowLog : list) {
					DealRecordDto dto = new DealRecordDto();
					dto.setDealTime(DateUtil.convert(flowLog.getWtime(), DateUtil.format3));
					Optional<AuditResult> auOptional = AuditResult.getEnumsByCode(flowLog.getAuditResult());
					if (auOptional.isPresent()) {
						dto.setAuditResult(auOptional.get().getMsg());
					}
					try {
						// 本次的处理人
						/*
						 * User tempUser = userMap.get(flowLog.getOssUserId());
						 * if (tempUser == null) { tempUser =
						 * userService.read(flowLog.getOssUserId()); } if
						 * (tempUser != null) {
						 * dto.setDealPerson(tempUser.getRealName());
						 * userMap.put(flowLog.getOssUserId(), tempUser); }
						 */
						dto.setDealPerson(userNameMap.get(flowLog.getOssUserId()));
						// 本次的角色
						dto.setDealRole("未知");
						String nodeId = flowLog.getNodeId();
						if (StringUtils.isNotBlank(nodeId)) {
							Role tempRole = roleMap.get(nodeId);
							if (tempRole == null) {
								FlowNode node = flowNodeService.read(nodeId);
								if (node != null) {
									String roleId = node.getRoleId().split(",")[0];
									tempRole = roleService.read(roleId);
								}
							}
							if (tempRole != null) {
								dto.setDealRole(tempRole.getRolename());
								roleMap.put(nodeId, tempRole);
							}
						}
					} catch (ServiceException e) {
						logger.error("", e);
					}
					dto.setRemark(flowLog.getRemark());
					dto.setFlowMsg(flowLog.getFlowMsg());
					details.add(dto);
				}
			}
		} catch (Exception e) {
			logger.error("获取流程处理信息异常", e);
		} finally {
			if (list != null) {
				list.clear();
				list = null;
			}
		}
		return details;
	}

	/**
	 * 根据流程id获取流程的所有标签
	 * 
	 * @param flowId
	 *            流程id
	 * @return
	 */
	private List<FlowLabel> getFlowLabelList(String flowId) {
		List<FlowLabel> details = new ArrayList<FlowLabel>();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
			filter.getOrders().add(new SearchOrder("position", Constants.ROP_ASC));
			details = flowLabelService.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("获取流程标签信息异常", e);
		}
		return details;
	}

	/**
	 * 根据流程id获取流程的所有节点
	 * 
	 * @param flowId
	 *            流程id
	 * @return
	 */
	private List<FlowNode> getFlowNodeList(String flowId) {
		List<FlowNode> details = new ArrayList<>();
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
			filter.getOrders().add(new SearchOrder("nodeIndex", Constants.ROP_ASC));
			details = flowNodeService.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("获取流程节点信息异常", e);
		}
		return details;
	}

	/**
	 * 封装流程UI对象
	 *
	 * @param flows
	 *            带封装的流程
	 * @return 封装好的流程
	 */
	private List<ErpFlowUI> buildErpFlowUI(List<ErpFlow> flows) {
		List<ErpFlowUI> flowUIs = new ArrayList<ErpFlowUI>();
		try {
			if (flows != null && flows.size() > 0) {
				ErpFlowUI flowUI;
				for (ErpFlow flow : flows) {
					flowUI = new ErpFlowUI();
					BeanUtils.copyProperties(flow, flowUI);
					User user = userService.read(flow.getCreatorid());
					if (user != null) {
						flowUI.setCreatorRealName(user.getRealName());
					}
					FlowNode startNode = flowNodeService.read(flow.getStartNodeId());
					if (startNode != null) {
						flowUI.setStartNodeName(startNode.getNodeName());
					}
					Optional<FlowType> flowType = FlowType.getEnumsByCode(flow.getFlowType());
					if (flowType.isPresent()) {
						flowUI.setFlowTypeDesc(flowType.get().getDesc());
					}
					flowUI.setwTime(DateUtil.convert(flow.getWtime(), DateUtil.format2));
					flowUIs.add(flowUI);
				}
			}
		} catch (ServiceException e) {
			logger.info("流程封装异常", e.getMessage());
		}

		return flowUIs;
	}

	/**
	 * 获取标签类型下拉框
	 */
	@RequestMapping("/getFlowLabelType")
	@ResponseBody
	public String getFlowLabelType() {
		long _start = System.currentTimeMillis();
		JSONArray types = new JSONArray();
		String[] typeDescs = FlowLabelType.getDescs();
		for (int i = 0; i < typeDescs.length; i++) {
			JSONObject json = new JSONObject();
			json.put("value", i);
			json.put("name", typeDescs[i]);
			if (i == 11) {
				String defaultValue = "";
				for (PriceType pt : PriceType.values()) {
					defaultValue += ("," + pt.getCode() + ":" + pt.getMsg());
				}
				if (defaultValue.startsWith(",")) {
					defaultValue = defaultValue.substring(1);
				}
				json.put("defaultValue", defaultValue);
			} else if (i == 12) {
				String defaultValue = "";
				for (ChargeType pt : ChargeType.values()) {
					defaultValue += ("," + pt.getCode() + ":" + pt.getMsg());
				}
				if (defaultValue.startsWith(",")) {
					defaultValue = defaultValue.substring(1);
				}
				json.put("defaultValue", defaultValue);
			}
			types.add(json);
		}
		logger.info("获取标签类型下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return types.toJSONString();
	}

	/**
	 * 获取流程类型下拉框
	 */
	@RequestMapping("/getFlowType")
	@ResponseBody
	public String getFlowType() {
		long _start = System.currentTimeMillis();
		JSONArray types = new JSONArray();
		String[] typeDescs = FlowType.getDescs();
		for (int i = 0; i < typeDescs.length; i++) {
			JSONObject json = new JSONObject();
			json.put("value", i);
			json.put("name", typeDescs[i]);
			types.add(json);
		}
		logger.info("获取流程类型下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return types.toJSONString();
	}

	/**
	 * 获取流程类别下拉框
	 */
	@RequestMapping("/getFlowClass")
	@ResponseBody
	public String getFlowClass() {
		long _start = System.currentTimeMillis();
		JSONArray classes = new JSONArray();
		Map<String, BaseFlowTask> flowClasses = flowtaskManaget.getFlowTasks();
		for (Entry<String, BaseFlowTask> e : flowClasses.entrySet()) {
			JSONObject json = new JSONObject();
			json.put("value", e.getValue().getFlowClass());
			json.put("name", e.getValue().getFlowName());
			classes.add(json);
		}
		logger.info("获取流程类别下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return classes.toJSONString();
	}

	/**
	 * 按条件获取能发起的流程
	 */
	@RequestMapping("/getFlowByType")
	@ResponseBody
	public String getFlowByType() {
		// entityType：0通信供应商，1通信销售，2电商供应商
		String entityType = request.getParameter("entityType");
		// flowType：0供应商，1销售
		String flowType = request.getParameter("flowType");
		// 供应商、客户产品id，电商不用传此参数
		String productId = request.getParameter("productId");
		String customerId = request.getParameter("customerId");
		JSONArray flowArray = new JSONArray();
		if (StringUtils.isAnyBlank(entityType, flowType)) {
			logger.info("必要参数不能为空，entityType：" + entityType + "，flowType：" + flowType);
			return flowArray.toJSONString();
		}
		String roleId = getOnlineUserAndOnther().getRoleId();
		String hql = "select f From ErpFlow f LEFT JOIN FlowNode n on f.startNodeId = n.nodeId WHERE f.associateType <> :associateType and n.roleId like :roleId and f.status = :status and f.flowType = :flowType";

		Map<String, Object> params = new HashMap<>();
		params.put("associateType", FlowAssociateType.USER.ordinal());
		List<ErpFlow> flowList;
		// 默认值
		int productType = -1;
		try {
			// 主体类型决定是供应商产品还是客户产品，电商供应商不用查产品
			Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entityType));
			if (!entityTypeOpt.isPresent()) {
				logger.info("错误的实体类型：" + entityType);
				return flowArray.toJSONString();
			} else if (EntityType.SUPPLIER == entityTypeOpt.get() && StringUtil.isBlank(productId)) {
				logger.info("产品id为空");
				return flowArray.toJSONString();
			}
			if (EntityType.SUPPLIER == entityTypeOpt.get()) {
				Product product = productService.read(productId);
				if (product == null) {
					logger.info("产品不存在，productId：" + productId);
					return flowArray.toJSONString();
				}
				productType = product.getProductType();
			} else if (EntityType.CUSTOMER == entityTypeOpt.get() && StringUtils.isNotBlank(productId)) {
				CustomerProduct customerProduct = customerProductService.read(productId);
				if (customerProduct == null) {
					logger.info("产品不存在，productId：" + productId);
					return flowArray.toJSONString();
				}
				productType = customerProduct.getProductType();
			} // else 电商供应商不用查产品
			if (productType == productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS)) {
				// 国际短信产品，不展示销售调价流程、销售账单流程
				hql += " and f.flowName != :flowName";
				hql += " and f.flowClass != :flowClass";
				params.put("flowName", Constants.CUSTOMER_BILL_FLOW_NAME);
				params.put("flowClass", Constants.ADJUST_PRICE_FLOW_CLASS);
			} else if (productType != -1) {
				// 非国际短信产品，不展示销售国际调价流程、销售国际账单流程
				hql += " and f.flowName != :flowName";
				hql += " and f.flowClass != :flowClass";
				params.put("flowName", Constants.CUSTOMER_INTER_BILL_FLOW_NAME);
				params.put("flowClass", Constants.INTER_ADJUST_PRICE_FLOW_CLASS);
			} // else 电商不用查产品
				// 流程类型：运营/结算（对账、发票、销账）
				// 分别查询可以在客户或者产品申请的流程
			Customer customer = null;
			if (StringUtils.isNotBlank(customerId)) {
				customer = customerService.read(customerId);
				if (customer == null || StringUtils.isNotBlank(customer.getOssuserId())) {
					return null;
				}
				hql += " and f.bindType = :bindType";
				params.put("bindType", BindType.ENTITY.ordinal());
			} else {
				hql += " and f.bindType = :bindType";
				params.put("bindType", BindType.PRODUCT.ordinal());
			}
			Optional<FlowType> FlowTypeOpt = FlowType.getEnumsByCode(Integer.parseInt(flowType));
			if (FlowTypeOpt.isPresent()) {
				params.put("flowType", FlowTypeOpt.get().ordinal());
			} else {
				logger.info("错误的流程类型：" + flowType);
				return flowArray.toJSONString();
			}
			params.put("roleId", "%" + roleId + "%");
			params.put("status", EntityStatus.NORMAL.ordinal());
			flowList = erpFlowService.findByhql(hql, params, 0);
			if (flowList != null && !flowList.isEmpty()) {
				for (ErpFlow flow : flowList) {
					if (flow.getFlowClass() == Constants.APPLY_CUSTOMER_FLOW_CLASS) {
						if (StringUtils.isNotBlank(customer.getOssuserId())) {
							continue;
						}
					}
					JSONObject flowobj = new JSONObject();
					flowobj.put("flowName", flow.getFlowName());
					flowobj.put("flowId", flow.getFlowId());
					flowArray.add(flowobj);
				}
				return flowArray.toJSONString();
			}
		} catch (Exception e) {
			logger.info("根据流程类型获取流程异常", e);
		}
		return flowArray.toJSONString();
	}

	/**
	 * 一次查询多个流程的详细信息
	 *
	 * @param ids
	 *            逗号分隔的flowEnt表id
	 * @return
	 */
	@RequestMapping("/getFlowDetailByPage")
	@ResponseBody
	public BaseResponse<Map<String, ReadFlowDetailRespDto>> getFlowDetailByPage(String ids) {
		logger.info("分页获取流程处理详情开始，流程实体ids：" + ids);
		if (StringUtils.isBlank(ids)) {
			return BaseResponse.error("流程id不能为空");
		}
		ids = ids.endsWith(",") ? ids.substring(0, ids.length() - 1) : ids;
		String[] flowEntIds = ids.split(",");
		Map<String, ReadFlowDetailRespDto> resultList = new HashMap<String, ReadFlowDetailRespDto>();
		// Map<String, User> userMap = new HashMap<String, User>();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("请先登录");
			}
			// 一次查询出所有的流程信息（因为一次的加载量都不大，不用担心消耗过多内存）
			List<FlowEnt> flowEntList = flowEntService.queryFlowEntByIds(Arrays.asList(flowEntIds));
			if (flowEntList == null || flowEntList.isEmpty()) {
				return BaseResponse.error("流程实体信息错误");
			}
			// 流程节点信息
			Map<String, FlowEnt> flowEntInfos = new HashMap<>();
			flowEntList.forEach(ent -> flowEntInfos.put(ent.getId(), ent));

			// 流程信息
			List<ErpFlow> flowList = erpFlowService.findFlowByIds(flowEntList.stream().map(FlowEnt::getFlowId).collect(Collectors.toList()));
			Map<String, ErpFlow> flowInfos = new HashMap<>();
			if (flowList != null && !flowList.isEmpty()) {
				flowList.forEach(flow -> flowInfos.put(flow.getFlowId(), flow));
				flowList.clear();
				flowList = null;
			}

			// 下一个节点信息
			List<FlowNode> nextNodeList = flowNodeService.findFlowNodeByIds(flowEntList.stream().map(FlowEnt::getNodeId).collect(Collectors.toList()));
			Map<String, FlowNode> nextNodeInfos = new HashMap<>();
			if (nextNodeList != null && !nextNodeList.isEmpty()) {
				nextNodeList.forEach(nextNode -> nextNodeInfos.put(nextNode.getNodeId(), nextNode));
				nextNodeList.clear();
				nextNodeList = null;
			}
			// 用户信息
			Map<String, User> userInfos = userService.findUserByIds(flowEntList.stream().map(FlowEnt::getOssUserId).collect(Collectors.toList()));

			flowEntList.clear();
			flowEntList = null;

			for (String id : flowEntIds) {
				FlowEnt flowEnt = flowEntInfos.get(id);
				if (flowEnt == null) {
					return BaseResponse.error("流程实体信息错误");
				}
				ReadFlowDetailRespDto respDto = new ReadFlowDetailRespDto();
				ErpFlow flow = flowInfos.get(flowEnt.getFlowId());
				respDto.setFlowStatus(flowEnt.getFlowStatus());
				respDto.setFlowId(flowEnt.getFlowId());
				respDto.setFlowClass(flow == null ? "" : flow.getFlowClass());
				respDto.setFlowEntId(flowEnt.getId());
				respDto.setProductId(flowEnt.getProductId());
				respDto.setSupplierId(flowEnt.getSupplierId());
				int entityType = flowEnt.getEntityType();
				respDto.setEntityType(entityType);

				// 判断是不是能取消, 1.申请人是自己, 2.未归档(未审核, 审核未通过), 3.节点不在第一个
				if (StringUtils.equals(getOnlineUser().getOssUserId(), flowEnt.getOssUserId())
						&& (flowEnt.getFlowStatus() == FlowStatus.NOT_AUDIT.ordinal() || flowEnt.getFlowStatus() == FlowStatus.NO_PASS.ordinal())
						&& (nextNodeInfos.get(flowEnt.getNodeId()) != null && nextNodeInfos.get(flowEnt.getNodeId()).getNodeIndex() != 0)) {
					respDto.setCanRevoke(true);
				} else {
					respDto.setCanRevoke(false);
				}
				if (EntityType.SUPPLIER.ordinal() == entityType) {
					// 供应商
					Supplier supplier = StringUtil.isBlank(flowEnt.getSupplierId()) ? null : supplierService.read(flowEnt.getSupplierId());
					respDto.setSupplierName(supplier != null ? supplier.getCompanyName() : "未知");

					Product product = StringUtil.isBlank(flowEnt.getProductId()) ? null : productService.read(flowEnt.getProductId());
					respDto.setProductName(product != null ? product.getProductName() : "未知");
				} else if (EntityType.CUSTOMER.ordinal() == entityType) {
					// 客户
					Customer customer = StringUtil.isBlank(flowEnt.getSupplierId()) ? null : customerService.read(flowEnt.getSupplierId());
					respDto.setSupplierName(customer != null ? customer.getCompanyName() : "未知");

					CustomerProduct product = StringUtil.isBlank(flowEnt.getProductId()) ? null : customerProductService.read(flowEnt.getProductId());
					respDto.setProductName(product != null ? product.getProductName() : "未知");
				} else if (EntityType.SUPPLIER_DS.ordinal() == entityType) {
					// 电商供应商
					Supplier supplier = StringUtil.isBlank(flowEnt.getSupplierId()) ? null : supplierService.read(flowEnt.getSupplierId());
					respDto.setSupplierName(supplier != null ? supplier.getCompanyName() : "未知");

					DianShangProduct product = StringUtil.isBlank(flowEnt.getProductId()) ? null : dsProductService.read(flowEnt.getProductId());
					respDto.setProductName(product != null ? product.getProductname() : "未知");
				}
				respDto.setApplyTime(flowEnt.getWtime());
				String flowMsg = flowEnt.getFlowMsg();
				if (StringUtil.isNotBlank(flowMsg)) {
					JSONObject json = JSONObject.parseObject(flowMsg, Feature.OrderedField);
					if (json != null) {
						for (String key : json.keySet()) {
							// 账单流程平台数据
							if (Constants.FLOW_BASE_DATA_KEY.equals(key)) {
								JSONObject baseData = json.getJSONObject(key);
								if (baseData != null) {
									for (String _key : baseData.keySet()) {
										respDto.getBaseDataMap().put(_key, baseData.getString(_key));
									}
								}
							} else { // 流程标签的值
								respDto.getLabelValueMap().put(key, json.getString(key));
							}
						}
					}
				}

				// 流程创建人
				// User user = userMap.get(flowEnt.getOssUserId());
				// if (user == null) {
				// user = userService.read(flowEnt.getOssUserId());
				// }
				User user = userInfos.get(flowEnt.getOssUserId());
				if (user != null) {
					respDto.setOssUserId(flowEnt.getOssUserId());
					respDto.setOssUserName(user.getRealName());
					// userMap.put(flowEnt.getOssUserId(), user);
				}
				respDto.setFlowTitle(flowEnt.getFlowTitle());
				// 设置流程处理记录
				respDto.setRecord(getFlowLogList(flowEnt.getId()));
				// 获取流程的所有标签
				List<FlowLabel> labelList = getFlowLabelList(flowEnt.getFlowId());
				int nodeIndex = -1;
				// 流程当前节点的可处理角色
				String roleId = "";
				// 当前用户的角色
				String userRoleId = onlineUser.getRoleId();
				if (labelList != null && labelList.size() > 0) {
					// 当前用户的角色可以处理的节点
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowEnt.getFlowId()));
					filter.getRules().add(new SearchRule("roleId", Constants.ROP_CN, userRoleId));
					filter.getOrders().add(new SearchOrder("nodeIndex", Constants.ROP_DESC));
					List<FlowNode> roleNodes = flowNodeService.queryAllBySearchFilter(filter);

					// 当前用户的角色的所有展示标签
					String viewLabelIds = "";
					Set<String> viewLabelSet = new HashSet<String>();
					for (FlowNode roleNode : roleNodes) {
						viewLabelSet.addAll(Arrays.asList(roleNode.getViewLabelIds().split(",")));
					}
					viewLabelIds = String.join(",", viewLabelSet);

					// 当前用户的角色的流程展示标签
					if (StringUtil.isNotBlank(viewLabelIds)) {
						for (FlowLabel flowLabel : labelList) {
							if (viewLabelIds.contains(flowLabel.getId())) {
								respDto.getLabelList().add(flowLabel);
							}
						}
					}
					// 允许查看此流程的角色中有当前用户的角色，则展示所有标签
					if (StringUtil.isNotBlank(flowEnt.getViewerRoleId()) && flowEnt.getViewerRoleId().contains(userRoleId)) {
						respDto.setLabelList(labelList);
					}

					// 流程当前节点
					FlowNode flowNode = nextNodeInfos.get(flowEnt.getNodeId());
					if (flowNode != null) {
						// 流程可编辑标签
						respDto.setEditLabelIds(flowNode.getEditLabelIds());
						// 流程必要标签
						respDto.setMustLabelIds(flowNode.getMustLabelIds());
						// 本节点可以处理的角色
						roleId = flowNode.getRoleId();
						// 本节点的序号
						nodeIndex = flowNode.getNodeIndex();
						respDto.setNodeIndex(nodeIndex);
						String nodeId = flowNode.getNodeId();
						respDto.setNodeId(nodeId);
					}
				}

				// 流程可以审核（未归档、未取消）
				if (flowEnt.getFlowStatus() != FlowStatus.FILED.ordinal() && flowEnt.getFlowStatus() != FlowStatus.CANCLE.ordinal()) {
					if (roleId.contains(onlineUser.getRoleId()) && (nodeIndex != 0 || onlineUser.getUser().getOssUserId().equals(flowEnt.getOssUserId()))) {
						respDto.setCanOperat(true);
					}
					if (nodeIndex == 0) {
						// 发起人节点，直接展示发起人的名字
						respDto.setDealUserName(respDto.getOssUserName());
					} else {
						// 非发起人节点，展示待处理的角色名
						respDto.setDealRoleName("无可处理此流程的人，请联系管理员");
						// 获取实际能处理该节点的角色和用户
						Map<String, List<String>> dealRoleAndUser = userService.getDealRoleAndUser(user, roleId);
						String[] roleIds = roleId.split(",");
						for (String roleid : roleIds) {
							List<String> dealUsers = dealRoleAndUser.getOrDefault(roleid, new ArrayList<>());
							// 找到第1个有用户的角色，只取前两个用户
							if (dealUsers.size() == 0) {
								continue;
							} else if (dealUsers.size() <= 2) {
								Role role = roleService.read(roleid);
								respDto.setDealRoleName(role.getRolename() + "(" + String.join(",", dealUsers) + ");……");
							} else {
								Role role = roleService.read(roleid);
								respDto.setDealRoleName(
										role.getRolename() + "(" + String.join(",", dealUsers.stream().limit(2).collect(Collectors.toList())) + "…);……");
							}
							break;
						}
					}
				}
				resultList.put(respDto.getFlowEntId(), respDto);
			}
			logger.info("分页获取流程处理详情结束");
		} catch (Exception e) {
			logger.error("分页获取流程处理详情异常，流程实体ids：" + ids, e);
		} /*
			 * finally { if (userMap != null) { userMap.clear(); userMap = null;
			 * } }
			 */
		return BaseResponse.success(resultList);
	}

	/**
	 * 获取流程某个节点之前的全部节点的信息，用于选择驳回到哪个节点
	 * 
	 * @param flowId
	 *            流程设计
	 * @param nodeIndex
	 *            某个节点的序号
	 * @return
	 */
	@RequestMapping("getFlowNodeBefore")
	@ResponseBody
	public BaseResponse<JSONArray> getFlowNodeBefore(String flowId, String nodeIndex) {
		JSONArray result = new JSONArray();
		List<FlowNode> details = new ArrayList<>();
		if (StringUtils.isNotBlank(nodeIndex)) {
			try {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_LT, Integer.parseInt(nodeIndex)));
				filter.getOrders().add(new SearchOrder("nodeIndex", Constants.ROP_ASC));
				details = flowNodeService.queryAllBySearchFilter(filter);
				if (details != null && !details.isEmpty()) {
					for (FlowNode node : details) {
						JSONObject item = new JSONObject();
						item.put("nodeId", node.getNodeId());
						item.put("nodeName", node.getNodeName());
						String roleIds = node.getRoleId();
						String roleNames = "未知角色";
						if (StringUtils.isNotBlank(roleIds)) {
							filter = new SearchFilter();
							filter.getRules().add(new SearchRule("roleid", Constants.ROP_IN, Arrays.asList(roleIds.split(","))));
							List<Role> roles = roleService.queryAllBySearchFilter(filter);
							if (roles != null && !roles.isEmpty()) {
								roleNames = roles.stream().map(Role::getRolename).collect(Collectors.joining(","));
							}
						}
						item.put("roleName", roleNames);
						result.add(item);
					}
				}
			} catch (Exception e) {
				logger.error("获取流程节点信息异常", e);
			}
		}
		return BaseResponse.success(result);
	}

	/**
	 * 获取可查看此流程的角色下拉框
	 */
	@RequestMapping("/getViewerRoles")
	@ResponseBody
	public String getViewerRoles() {
		long _start = System.currentTimeMillis();
		JSONArray roleList = new JSONArray();
		List<String> viewerRoleIdList = new ArrayList<>();
		try {
			// 获取流程里设计的可查看角色，如果是新建流程，id为空
			String flowId = request.getParameter("id");
			if (StringUtil.isNotBlank(flowId)) {
				try {
					ErpFlow flow = erpFlowService.read(flowId);
					if (flow != null) {
						String viewRoleIds = flow.getViewerRoleId();
						if (StringUtil.isNotBlank(viewRoleIds)) {
							viewerRoleIdList = Arrays.asList(viewRoleIds.split(","));
						}
					}
				} catch (Exception e) {
					logger.info("未找到流程，flowId：" + flowId);
				}
			}
			SearchFilter filter = new SearchFilter();
			// 去除“超级管理员”角色
			filter.getRules().add(new SearchRule("rolename", Constants.ROP_NE, RoleType.ADMIN.getDesc()));
			List<Role> dataList = roleService.queryAllBySearchFilter(filter);
			for (Role role : dataList) {
				JSONObject json = new JSONObject();
				json.put("value", role.getRoleid());
				json.put("name", role.getRolename());
				json.put("selected", viewerRoleIdList.contains(role.getRoleid()) ? "selected" : "");
				json.put("disabled", "");
				roleList.add(json);
			}
			logger.info("获取角色下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return roleList.toJSONString();
	}

	/**
	 * 获取当前用户每个角色的待处理流程数
	 *
	 * @return {角色Id：待处理流程数}
	 */
	@ResponseBody
	@RequestMapping("/queryRoleFlowEntCount")
	public JSONObject queryRoleFlowEntCount() {
		logger.info("获取当前用户每个角色的待处理流程数开始");
		long _start = System.currentTimeMillis();
		JSONObject result = new JSONObject();
		try {
			OnlineUser user = getOnlineUserAndOnther();
			if (user == null) {
				return result;
			}
			// 获取当前登录用户的所有角色
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getUser().getOssUserId()));
			List<RoleRelation> roleRelationList = roleRelationService.queryAllBySearchFilter(filter);
			List<String> roleIdList = new ArrayList<>();
			if (!ListUtils.isEmpty(roleRelationList)) {
				roleIdList = roleRelationList.stream().map(RoleRelation::getRoleId).collect(Collectors.toList());
			}
			// 用户角色信息
			List<Role> roleList = roleService.queryRoleByIds(roleIdList);
			for (Role role : roleList) {
				Integer unprocessFlowCount = flowEntService.countUnprocessFlowEntByRole(user.getUser(), role);
				result.put(role.getRoleid(), unprocessFlowCount);
			}
			// 获取每个角色的待处理流程id列表
			/*
			 * List<String> flowEntIds = null; for (String roleId : roleIdList)
			 * { flowEntIds = flowEntService.queryFlowEntByRole(user, roleId);
			 * if (!ListUtils.isEmpty(flowEntIds)) { result.put(roleId,
			 * flowEntIds.size()); } else { // 为0也要传到前台，用来清除气泡
			 * result.put(roleId, 0); } }
			 */
			logger.info("获取当前用户每个角色的待处理流程数结束，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("获取当前用户每个角色的待处理流程数异常", e);
		}
		return result;
	}

	/**
	 * 无可处理此流程的人，请联系管理员
	 *
	 * @Return 联系管理员成功
	 */
	@RequestMapping("/linkedAdmin")
	@ResponseBody
	public JSONObject LinkedAdmin(){
		//没有流程，联系管理员
		JSONObject result = new JSONObject();
		//获取管理员账号
		User admin = userService.findAdmin();
		//发送信息给管理员账号
//		MsgCenterDto msgCenterDto = new MsgCenterDto();
//		msgCenterDto.setWtime(new Date());
//		msgCenterDto.setInfotype(3);
//
//		MsgDetailDto msgDetailDto = new MsgDetailDto();
//		msgDetailDto.setMessageId(msgCenterDto.getMessageid());
//		msgDetailDto.
		//msgDetailDto.set
		return result;
	}
}
