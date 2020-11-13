package com.dahantc.erp.controller.dsMatchOrder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.resourceConsole.ResourceConsoleAction;
import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.dto.operate.FlowEntRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("dsMatchOrder")
public class DsMatchOrderAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(ResourceConsoleAction.class);

	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IRoleRelationService roleRelationService;
	
	@Autowired
	private IUserService userService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IErpFlowService erpFlowService;

	/**
	 * 跳转电商配单工作台
	 */
	@RequestMapping("/toMatchOrderConsole")
	public String toMatchOrderConsole() {
		try {
			// 读取 用户页面的权限
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
		} catch (Exception e) {
			logger.error("获取用户页面权限的时候异常", e);
		}
		return "/views/console/matchOrderConsoleDs";
	}

	/**
	 * 查询所有运营流程数量和时间标题
	 */
	@RequestMapping("/getAllOperate")
	public String getAllOperate() {
		logger.info("查询待配单的时间和数量开始");
		long _start = System.currentTimeMillis();
		Map<String, List<ToQueryMonthRespDto>> timeMap = new LinkedHashMap<>();
		boolean empty = true;
		boolean buttonBody = false;
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 运营的最早时间
			Timestamp startTime = null;
			// 获取订单流程的最早时间
			List<FlowEnt> flowEnts = queryFlowEntByDate(onlineUser,"");
			if (flowEnts != null && flowEnts.size() > 0) {
				startTime = flowEnts.get(0).getWtime();
			}

			if (startTime != null) {
				// 获取从开始时间到现在的年每个月集合
				timeMap = DateUtil.getMonthBetweenDate(startTime, new Date());
				// 获取当前用户角色的所有未处理流程
				List<FlowEntDealCount> flowCount = flowEntService
						.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				if (flowCount != null && !flowCount.isEmpty()) {
						// 过滤出用户所有客户的流程
						flowCount = flowCount.stream()
								.filter(flow -> flow.getFlowType() == FlowType.OPERATE.ordinal()
										&& flow.getEntityType() == EntityType.CUSTOMER.ordinal() )
								.collect(Collectors.toList());
					// 年-年未处理流程数
					Map<String, Long> yearFlowCount = new HashMap<>();
					if (timeMap != null && !timeMap.isEmpty()) {
						// 统计年-流程数量
						flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear,
										Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)))
								.forEach((year, yearCount) -> yearFlowCount.put(year + "", yearCount.getSum()));
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 统计时间（月份-流程数量）
						Set<Map.Entry<String, List<ToQueryMonthRespDto>>> yearMonthTime = timeMap.entrySet();
						for (Map.Entry<String, List<ToQueryMonthRespDto>> yearMonthInfo : yearMonthTime) {
							// 年份
							String year = yearMonthInfo.getKey();
							// 年未处理流程数
							Long yearCount = yearFlowCount.get(year);
							if (yearCount != null && yearCount > 0 && StringUtils.isNumeric(year)
									&& monthCountMap.get(Integer.valueOf(year)) != null) {
								Map<Integer, Integer> monthFlowCount = monthCountMap.get(Integer.valueOf(year)).stream()
										.collect(Collectors.groupingBy(FlowEntDealCount::getMonth,
												Collectors.summingInt(FlowEntDealCount::getFlowEntCount)));
								List<ToQueryMonthRespDto> dtoList = yearMonthInfo.getValue().stream().map(dto -> {
									if (dto == null) {
										return null;
									}
									// 月流程数
									Integer value = monthFlowCount.get(dto.getMonth());
									if (value != null) {
										dto.setFlowEntCount(value.longValue());
									} else {
										dto.setFlowEntCount(0l);
									}
									return dto;
								}).collect(Collectors.toList());
								timeMap.replace(yearMonthInfo.getKey(), dtoList);
							}
						}
					}
					request.setAttribute("yearFlowCount", yearFlowCount);
				}
				if (timeMap != null && !timeMap.isEmpty()) {
					empty = false;
					request.setAttribute("timeMap", timeMap);
				}
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("empty", empty);
			request.setAttribute("buttonBody", buttonBody);
		} catch (ServiceException e) {
			logger.error("查询供应商运营的时间和数量异常", e);
			return "";
		}
		logger.info("查询待配单的时间和数量结束，耗时：" + (System.currentTimeMillis() - _start));
		return "/views/orderOperateDs/orderOperateDsInfo";
	}

	/**
	 * 按月份查询指定供应商/客户的所有运营记录
	 *
	 * @param date
	 *            月份
	 * @return
	 */
	@RequestMapping("/getAllOperateByDate")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> getAllOperateByDate(@RequestParam String date) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份查询所有运营记录开始，月份：" + date);
		long _start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<FlowEntRespDto> resultList = new ArrayList<>(); // 最终的结果
			List<FlowEnt> flowList = queryFlowEntByDate(onlineUser, date);
			if (null == flowList || flowList.isEmpty()) {
				logger.info("未查询到流程：" + date);
				return BaseResponse.success(resultList);
			}
			logger.info("按月份查询到运营流程数：" + flowList.size());
			flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));
			// 封装返回结果
			for (FlowEnt ent : flowList) {
				FlowEntRespDto rsp = new FlowEntRespDto();
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setId(ent.getId());
				rsp.setProductId(ent.getProductId());
				FlowNode flowNode = flowNodeService.read(ent.getNodeId());
				if (null != flowNode) {
					rsp.setNodeName(flowNode.getNodeName());
					// 当前角色可处理
					if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
							&& (flowNode.getNodeIndex() != 0
									|| onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
						rsp.setCanOperat(true);
					}
				}
				resultList.add(rsp);
			}
			resultList.sort((o1, o2) -> {
				int temp1 = o1.isCanOperat() ? 1 : 0;
				int temp2 = o2.isCanOperat() ? 1 : 0;
				return temp2 - temp1;
			});
			logger.info("按月份查询所有运营记录结束，耗时:" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.info("按月份查询所有运营记录异常", e);
		}
		return BaseResponse.error("按月份查询所有运营记录失败");
	}

	public List<FlowEnt> queryFlowEntByDate(OnlineUser onlineUser, String date) {
		List<FlowEnt> newFlowEnt = new ArrayList<>();
		Map<String, String> flowIdAndClassMap = new HashMap<>();
		try {
			List<FlowEnt> flowEnts = flowEntService.queryFlowEntByDate(onlineUser, EntityType.CUSTOMER.ordinal(),
					FlowType.OPERATE.ordinal(), date, "", "");
			if (CollectionUtils.isEmpty(flowEnts)) {
				return newFlowEnt;
			}
			for (FlowEnt flowEnt : flowEnts) {
				String flowClass = null;
				if (flowIdAndClassMap.containsKey(flowEnt.getFlowId())) {
					flowClass = flowIdAndClassMap.get(flowEnt.getFlowId());
				} else {
					ErpFlow flow = erpFlowService.read(flowEnt.getFlowId());
					flowIdAndClassMap.put(flowEnt.getFlowId(), flow.getFlowClass());
					flowClass = flow.getFlowClass();
				}
				if (StringUtils.equals(flowClass, Constants.DS_ORDER_FLOW_CLASS)) {
					newFlowEnt.add(flowEnt);
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			return newFlowEnt;
		} finally {
			flowIdAndClassMap.clear();
		}
		return newFlowEnt;
	}

	/**
	 * 分页查询流程标题
	 *
	 * @param date
	 *            展开的月份
	 * @param page
	 *            第几页
	 * @param pageSize
	 *            每页大小
	 * @return
	 */
	@RequestMapping("/getAllOperateByPage")
	@ResponseBody
	public BaseResponse<List<FlowEntRespDto>> getAllOperateByPage(String date, int page, int pageSize) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份分页查询运营记录开始，月份：" + date + "，page：" + page + "，pageSize：" + pageSize);
		long start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			// 最终的结果
			List<FlowEntRespDto> resultList = new ArrayList<>();
			List<FlowEnt> flowList = queryFlowEntByDate(onlineUser, date);
			if (null == flowList || flowList.isEmpty()) {
				logger.info("未查询到流程：" + date);
				return BaseResponse.success("0", resultList);
			}
			logger.info("按月份查询到运营流程数：" + flowList.size());
			flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));
			List<FlowNode> nodeInfoList = flowNodeService.findFlowNodeByIds(flowList.stream()
					.map(FlowEnt::getNodeId).collect(Collectors.toList()));
			Map<String, FlowNode> nodeInfos = new HashMap<>();
			if (nodeInfoList != null && !nodeInfoList.isEmpty()){
				nodeInfoList.forEach(flowNode -> nodeInfos.put(flowNode.getNodeId(),flowNode));
				nodeInfoList.clear();
				nodeInfoList = null;
			}
			// 封装返回结果
			for (FlowEnt ent : flowList) {
				FlowEntRespDto rsp = new FlowEntRespDto();
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setId(ent.getId());
				rsp.setProductId(ent.getProductId());
				rsp.setApplyTime(DateUtil.convert(ent.getWtime(), DateUtil.format1));
				FlowNode flowNode =  nodeInfos.get(ent.getNodeId());
				if (flowNode != null) {
					rsp.setNodeName(flowNode.getNodeName());
					// 当前角色可处理
					if (StringUtils.isNotBlank(flowNode.getRoleId()) && flowNode.getRoleId().contains(roleId)
							&& (flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal()) {
						rsp.setCanOperat(true);
					}
				}
				resultList.add(rsp);
			}
			resultList.sort((o1, o2) -> {
				int temp1 = o1.isCanOperat() ? 1 : 0;
				int temp2 = o2.isCanOperat() ? 1 : 0;
				return temp2 - temp1;
			});
			logger.info("按月份分页查询运营记录结束，耗时：" + (System.currentTimeMillis() - start));
			int pages = (int) (Math.ceil(1.0 * resultList.size() / pageSize));
			if (page > pages) {
				return BaseResponse.success(pages + "", new ArrayList<>());
			}
			int pageStart = (page - 1) * pageSize;
			int pageEnd = Math.min(page * pageSize, resultList.size());
			return BaseResponse.success(pages + "", resultList.subList(pageStart, pageEnd));
		} catch (Exception e) {
			logger.info("按月份分页查询运营记录异常", e);
		}
		return BaseResponse.error("按月份查询所有运营记录失败");
	}

	/**
	 * 跳转电商配单工作台查询商品
	 */
	@RequestMapping("/toQueryDsProduct")
	public String toQueryDsProduct() {
		String product = request.getParameter("productName");
		String id = request.getParameter("id");
		request.setAttribute("productName", product);
		request.setAttribute("id", id);
		return "/views/matchOrderDs/matchProduct";
	}
	
	/**
	 * 获取配单员下拉框
	 */
	@RequestMapping("/getSelectRole")
	@ResponseBody
	public BaseResponse<List<User>> getSelectRole() {
		long _start = System.currentTimeMillis();
		List<User> userList = new ArrayList<>();
		try {
			SearchFilter filter = new SearchFilter();
			// 查询“电商配单员”角色
			filter.getRules().add(new SearchRule("rolename", Constants.ROP_EQ, Constants.ROLE_NAME_MATCH_ORDER));
			List<Role> dataList = roleService.queryAllBySearchFilter(filter);
			SearchFilter roleRelationFilter = new SearchFilter();
			// 查询“电商配单员”角色
			roleRelationFilter.getRules().add(new SearchRule("roleId", Constants.ROP_EQ, dataList.get(0).getRoleid()));
			List<RoleRelation> roleRelationList = roleRelationService.queryAllBySearchFilter(roleRelationFilter);
			for (RoleRelation roleRelation : roleRelationList) {
				User user = userService.read(roleRelation.getOssUserId());
				if (user != null) {
					userList.add(user);
				}
			}
			logger.info("获取角色下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
		}
		return BaseResponse.success(userList);
	}

}
