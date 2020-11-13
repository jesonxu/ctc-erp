package com.dahantc.erp.controller.customerSettlement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.flow.ReadFlowEntRspDto;
import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntWithOpt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/customerSettlement")
public class CustomerSettlementAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(CustomerSettlementAction.class);

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IRoleService roleService;

	/**
	 * 根据产品ID获取结算流程数量和时间标题 有哪些年、月
	 *
	 * @param productId
	 *            产品id
	 * @param flowType
	 *            流程类型，2对账，3发票，4销账
	 * @return 结算时间
	 */
	@RequestMapping("/getSettlementTime")
	public String getSettlementTime(String productId, String flowType) {
		logger.info("查询客户产品的结算时间标题和未处理流程数开始，productId:" + productId + "，flowType：" + flowType);
		// 存放年-月份的流程数据
		Map<String, List<ToQueryMonthRespDto>> yearAndmonth = new LinkedHashMap<>();
		// 是否显示发起按钮
		boolean buttonBody = false;
		try {
			// 默认是对账的流程
			int flowtype = FlowType.BILL.ordinal();
			if (StringUtils.isNotBlank(flowType) && StringUtils.isNumeric(flowType)) {
				Optional<FlowType> flowTypeOpt = FlowType.getEnumsByCode(Integer.parseInt(flowType));
				if (flowTypeOpt.isPresent()) {
					flowtype = flowTypeOpt.get().ordinal();
				} else {
					logger.info("错误的流程类型，flowType：" + flowType);
					return "";
				}
			}
			if (StringUtils.isNotBlank(productId)) {
				// 查询当前用户在每个客户的每个产品等待处理的运营/结算流程数（结果不会为null）
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				// 过滤出指定客户产品的结算流程
				int finalFlowtype = flowtype;
				flowCount = flowCount.stream().filter(c -> c.getEntityType() == EntityType.CUSTOMER.ordinal() && StringUtils.equals(productId, c.getProductId())
						&& c.getFlowType() == finalFlowtype).collect(Collectors.toList());
				CustomerProduct product = customerProductService.read(productId);
				// 年未处理流程数
				Map<String, Long> yearFlowCount = new HashMap<>();
				if (product != null) {
					// 点击了产品才显示流程发起按钮
					buttonBody = true;
					Customer customer = customerService.read(product.getCustomerId());
					// 获取从客户创建时间到现在的年月集合 {年 -> [每个月]}
					yearAndmonth = DateUtil.getMonthBetweenDate(customer.getWtime(), new Date());

					if (!flowCount.isEmpty() && yearAndmonth != null && !yearAndmonth.isEmpty()) {
						// 获取每年的流程数统计数据 {年 -> 数据统计对象}
						Map<Integer, IntSummaryStatistics> map = flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
						// 将 每年数据统计数据中的总数 放到 年未处理流程数Map，得到 {年 -> 当年未处理流程总数}
						map.entrySet().stream().forEachOrdered(entry -> yearFlowCount.put(entry.getKey() + "", entry.getValue().getSum()));
						// 将未处理流程按年分组，得到 {年 -> 当年有未处理流程的月份}
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream().collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 每年的集合 [年 -> [每个月]]
						Set<Map.Entry<String, List<ToQueryMonthRespDto>>> set = yearAndmonth.entrySet();
						// 遍历每年
						for (Map.Entry<String, List<ToQueryMonthRespDto>> entry : set) {
							// 当年有未处理流程，当年有月份有未处理流程
							if (yearFlowCount.get(entry.getKey()) != null && yearFlowCount.get(entry.getKey()) > 0 && StringUtils.isNumeric(entry.getKey())
									&& monthCountMap.get(Integer.valueOf(entry.getKey())) != null) {
								// 获得每个月的未处理流程数 {每个月 -> 当月未处理流程数}
								Map<Integer, Integer> monthFlowCount = monthCountMap.get(Integer.valueOf(entry.getKey())).stream()
										.collect(Collectors.toMap(FlowEntDealCount::getMonth, FlowEntDealCount::getFlowEntCount));
								// 把 每个月的未处理流程数 放到 每月里
								List<ToQueryMonthRespDto> dtoList = entry.getValue().stream().map(dto -> {
									if (dto == null) {
										return null;
									}
									Integer value = monthFlowCount.get(dto.getMonth());
									if (value != null) {
										dto.setFlowEntCount(value.longValue());
									} else {
										dto.setFlowEntCount(0l);
									}
									return dto;
								}).collect(Collectors.toList());
								yearAndmonth.replace(entry.getKey(), dtoList);
							}
						}
					}
				}
				request.setAttribute("yearFlowCount", yearFlowCount);
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("productId", productId);
			request.setAttribute("yearAndmonth", yearAndmonth);
			request.setAttribute("buttonBody", buttonBody);
			request.setAttribute("flowType", flowtype);
		} catch (ServiceException e) {
			logger.error("查询客户结算时间标题和未处理流程数异常", e);
			return "";
		}
		return "/views/customerSettlement/customerSettlementTime";
	}

	/**
	 * 根据客户ID获取所有产品的结算流程数量和时间标题 有哪些年、月
	 *
	 * @param customerId
	 *            客户id，为空时查权限下所有客户的所有产品
	 * @param flowType
	 *            流程类型，2对账，3发票，4销账
	 * @return 结算时间
	 */
	@RequestMapping("/getAllSettlementTime")
	public String getAllSettlementTime(String customerId, String flowType) {
		logger.info("查询客户的结算时间和数量开始，客户id：" + customerId + "，flowType：" + flowType);
		long _start = System.currentTimeMillis();
		// 存放年-月份的流程数据
		Map<String, List<ToQueryMonthRespDto>> yearAndmonth = new LinkedHashMap<>();
		try {
			// 默认是对账的流程
			int flowtype = FlowType.BILL.ordinal();
			if (StringUtils.isNotBlank(flowType)) {
				try {
					Optional<FlowType> flowTypeOpt = FlowType.getEnumsByCode(Integer.parseInt(flowType));
					if (flowTypeOpt.isPresent()) {
						flowtype = flowTypeOpt.get().ordinal();
					}
				} catch (Exception e) {
					logger.error("错误的流程类型，flowType：" + flowType);
					return "";
				}
			}
			OnlineUser onlineUser = getOnlineUserAndOnther();
			// 结算的最早时间
			Timestamp startTime = null;
			if (StringUtils.isNotBlank(customerId)) {
				// 有客户id，查询客户，获取客户的产品的最早开始时间
				Customer customer = customerService.read(customerId);
				if (customer != null) {
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerId));
					searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
					List<CustomerProduct> resultList = customerProductService.queryAllByFilter(searchFilter);
					if (resultList != null && resultList.size() > 0) {
						startTime = resultList.get(0).getWtime();
					} else {
						startTime = customer.getWtime();
					}
				}
			} else {
				// 没有客户id，获取流程的最早时间
				// List<FlowEnt> flowEnts =
				// flowEntService.queryFlowEntByDate(onlineUser,
				// EntityType.CUSTOMER.ordinal(), flowtype, "", "", "");
				Role role = roleService.read(onlineUser.getRoleId());
				FlowEnt flowEnt = flowEntService.queryEarliestFlowEntByRole(onlineUser.getUser(), role, EntityType.CUSTOMER.ordinal(), flowtype);
				if (flowEnt != null) {
					startTime = flowEnt.getWtime();
				}
			}
			if (startTime != null) {
				// 获取从开始时间到现在的年每个月集合
				yearAndmonth = DateUtil.getMonthBetweenDate(startTime, new Date());
				// 获取当前用户的 所有未处理流程
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(onlineUser.getRoleId(), onlineUser.getUser().getOssUserId());
				// 获取当前用户的所有客户或指定客户
				List<Customer> customers = customerService.readCustomers(onlineUser, "", customerId, "", "");
				List<String> customerIds = new ArrayList<String>();
				if (customers != null && !customers.isEmpty()) {
					customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				}
				if (flowCount != null && !flowCount.isEmpty()) {
					// 过滤出属于用户下的客户的流程
					List<String> finalCustomerIds = customerIds;
					int finalFlowType = flowtype;
					flowCount = flowCount.stream().filter(flow -> flow.getFlowType() == finalFlowType && flow.getEntityType() == EntityType.CUSTOMER.ordinal()
							&& finalCustomerIds.contains(flow.getSupplierId())).collect(Collectors.toList());
					// 年-年未处理流程数
					Map<String, Long> yearFlowCount = new HashMap<>();
					if (yearAndmonth != null && !yearAndmonth.isEmpty()) {
						// 统计年-流程数量
						flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)))
								.forEach((year, yearCount) -> yearFlowCount.put(year + "", yearCount.getSum()));

						// 年-每个月未处理流程数
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream().collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 遍历 年-每个月未处理流程数 Map
						Set<Map.Entry<String, List<ToQueryMonthRespDto>>> set = yearAndmonth.entrySet();
						for (Map.Entry<String, List<ToQueryMonthRespDto>> entry : set) {
							// 年份
							String year = entry.getKey();
							// 年未处理流程数
							Long yearCount = yearFlowCount.get(year);
							// 年未处理流程数
							if (yearCount != null && yearCount > 0 && StringUtils.isNumeric(year) && monthCountMap.get(Integer.valueOf(year)) != null) {
								// 某年的每个月未处理流程数
								Map<Integer, Integer> monthFlowCount = monthCountMap.get(Integer.valueOf(year)).stream()
										.collect(Collectors.groupingBy(FlowEntDealCount::getMonth, Collectors.summingInt(FlowEntDealCount::getFlowEntCount)));
								// 年-月 流程信息
								List<ToQueryMonthRespDto> dtoList = entry.getValue().stream().map(dto -> {
									if (dto == null) {
										return null;
									}
									// 月流程数
									Integer monthCount = monthFlowCount.get(dto.getMonth());
									if (monthCount != null) {
										dto.setFlowEntCount(monthCount.longValue());
									} else {
										dto.setFlowEntCount(0l);
									}
									return dto;
								}).collect(Collectors.toList());
								yearAndmonth.replace(year, dtoList);
							}
						}
					}
					request.setAttribute("yearFlowCount", yearFlowCount);
				}
				request.setAttribute("yearAndmonth", yearAndmonth);
				request.setAttribute("supplierId", customerId);
			}
			request.setAttribute("flowType", flowtype);
		} catch (Exception e) {
			logger.info("根据客户ID查询所有结算记录异常", e);
			return "";
		}
		logger.info("查询客户结算的时间和数量结束，耗时：" + (System.currentTimeMillis() - _start));
		return "/views/customerSettlement/customerSettlementTime";
	}

	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> buildAllSettlementByDate(String date, String customerId) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份查询所有结算记录开始，月份：" + date);
		long _start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();
			List<FlowEnt> flowList = null;

			flowList = flowEntService.queryFlowEntByDate(onlineUser, EntityType.CUSTOMER.ordinal(), FlowType.FINANCE.ordinal(), date, customerId, "");

			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success(resultList);
			}
			flowList.sort((o1, o2) -> {
				return o2.getWtime().compareTo(o1.getWtime());
			});

			for (FlowEnt ent : flowList) {
				ReadFlowEntRspDto rsp = new ReadFlowEntRspDto();
				rsp.setFlowId(ent.getFlowId());
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setNodeId(ent.getNodeId());
				rsp.setOssUserId(ent.getOssUserId());
				rsp.setId(ent.getId());
				rsp.setWtime(DateUtil.convert(ent.getWtime(), DateUtil.format5) + "号 " + Constants.WEEK_NAME[DateUtil.getDayOfWeek(ent.getWtime())]);
				rsp.setOwnMonth(DateUtil.convert(ent.getWtime(), DateUtil.format4));
				rsp.setProductId(ent.getProductId());
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
					if (flowNode.getRoleId().contains(roleId)
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
			logger.info("按月份查询所有结算记录结束，耗时：" + (System.currentTimeMillis() - _start));
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}

	/**
	 * 分页获取流程标题
	 *
	 * @param date
	 *            月份
	 * @param customerId
	 *            指定客户id，为空时查数据权限下的所有客户
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页大小
	 * @param flowType
	 *            流程类型，2对账，3发票，4销账
	 * @return
	 */
	@RequestMapping("/getAllSettlementByPage")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> getAllSettlementByPage(String date, String customerId, int page, int pageSize, String flowType) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份分页查询结算记录开始，月份：" + date + "，page：" + page + "，pageSize：" + pageSize);
		long start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();
			// 默认查对账的流程
			int flowtype = FlowType.BILL.ordinal();
			if (StringUtils.isNotBlank(flowType) && StringUtils.isNumeric(flowType)) {
				Optional<FlowType> flowTypeOpt = FlowType.getEnumsByCode(Integer.parseInt(flowType));
				if (flowTypeOpt.isPresent()) {
					flowtype = flowTypeOpt.get().ordinal();
				}
			}
			// 获取当前用户的当前角色的所有待处理和处理过的流程
			// List<FlowEnt> flowList =
			// flowEntService.queryFlowEntByDate(onlineUser,
			// EntityType.CUSTOMER.ordinal(), flowtype, date, customerId, "");
			PageResult<FlowEntWithOpt> flowPageInfo = flowEntService.queryFlowEntByPageSql(onlineUser, EntityType.CUSTOMER.ordinal(), flowtype, date,
					customerId, null, pageSize, page, null);
			if (null == flowPageInfo || flowPageInfo.getData() == null || flowPageInfo.getData().isEmpty()) {
				logger.info("按月份分页查询结算记录结束，未查询到流程");
				return BaseResponse.success("0", resultList);
			}
			List<FlowEntWithOpt> flowList = flowPageInfo.getData();
			/*
			 * if (null == flowList || flowList.isEmpty()) {
			 * logger.info("按月份分页查询结算记录结束，未查询到流程"); return
			 * BaseResponse.success("0", resultList); }
			 */
			//flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));
			// 用户名称
			Map<String, String> userNames = userService.findUserNameByIds(flowList.stream().map(FlowEntWithOpt::getOssUserId).collect(Collectors.toList()));
			// 流程名称
			Map<String, String> flowNames = erpFlowService.findFlowNameByIds(flowList.stream().map(FlowEntWithOpt::getId).collect(Collectors.toList()));
			// 流程节点
			Map<String, List<FlowNode>> flowNodeInfos = flowNodeService
					.findFlowNodeByIds(flowList.stream().map(FlowEntWithOpt::getNodeId).collect(Collectors.toList())).stream()
					.collect(Collectors.groupingBy(FlowNode::getNodeId));
			// 封装返回结果
			for (FlowEntWithOpt ent : flowList) {
				ReadFlowEntRspDto rsp = new ReadFlowEntRspDto();
				rsp.setFlowId(ent.getFlowId());
				rsp.setFlowTitle(ent.getFlowTitle());
				rsp.setFlowStatus(FlowStatus.values()[ent.getFlowStatus()].getDesc());
				rsp.setNodeId(ent.getNodeId());
				rsp.setOssUserId(ent.getOssUserId());
				rsp.setId(ent.getId());
				rsp.setWtime(DateUtil.convert(ent.getWtime(), DateUtil.format1));
				rsp.setOwnMonth(DateUtil.convert(ent.getWtime(), DateUtil.format4));
				rsp.setProductId(ent.getProductId());
				rsp.setUserName(userNames.get(ent.getOssUserId()));
				rsp.setFlowName(flowNames.get(ent.getFlowId()));
				List<FlowNode> nodeList = flowNodeInfos.get(ent.getNodeId());
				if (nodeList != null && !nodeList.isEmpty()) {
					FlowNode flowNode = nodeList.get(0);
					rsp.setNodeName(flowNode.getNodeName());
					String userId = onlineUser.getUser().getOssUserId();
					boolean canOpt = flowNode.getRoleId().contains(roleId) && (flowNode.getNodeIndex() != 0 || userId.equals(ent.getOssUserId()))
							&& ent.getFlowStatus() != FlowStatus.FILED.ordinal() && ent.getFlowStatus() != FlowStatus.CANCLE.ordinal();
					rsp.setCanOperat(canOpt);
				}
				resultList.add(rsp);
			}
			flowNames.clear();
			/*
			 * resultList.sort((o1, o2) -> { int temp1 = o1.isCanOperat() ? 1 :
			 * 0; int temp2 = o2.isCanOperat() ? 1 : 0; return temp2 - temp1;
			 * });
			 */
			logger.info("按月份分页查询结算记录结束，耗时：" + (System.currentTimeMillis() - start));
			/*
			 * int pages = (int) (Math.ceil(1.0 * resultList.size() /
			 * pageSize)); // 向上 if (page > pages) { return
			 * BaseResponse.success(pages + "", new ArrayList<>()); } int
			 * pageStart = (page - 1) * pageSize; int pageEnd = Math.min(page *
			 * pageSize, resultList.size());
			 */
			return BaseResponse.success(flowPageInfo.getTotalPages() + "", resultList);
		} catch (Exception e) {
			logger.error("按月份分页查询结算记录异常", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}
}
