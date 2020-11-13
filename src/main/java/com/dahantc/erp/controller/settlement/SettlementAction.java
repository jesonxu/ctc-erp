package com.dahantc.erp.controller.settlement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntWithOpt;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.flow.ReadFlowEntDto;
import com.dahantc.erp.dto.flow.ReadFlowEntRspDto;
import com.dahantc.erp.dto.modifyPrice.ToQueryMonthRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.entity.FlowEntDealCount;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/settlement")
public class SettlementAction extends BaseAction {

	private static final Logger logger = LogManager.getLogger(SettlementAction.class);

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IRoleService roleService;

	@PostMapping("/readFlowEnt")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> readFlowEnt(@Valid ReadFlowEntDto req) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		try {
			String roleId = onlineUser.getRoleId();
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();

			List<FlowEnt> flowList = flowEntService.queryFlowEntByDate(onlineUser, Integer.parseInt(req.getEntityType()), FlowType.FINANCE.ordinal(),
					req.getDate(), "", req.getProductId());

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
				rsp.setProductId(ent.getProductId());
				rsp.setId(ent.getId());
				rsp.setWtime(DateUtil.convert(ent.getWtime(), DateUtil.format5) + "号 " + Constants.WEEK_NAME[DateUtil.getDayOfWeek(ent.getWtime())]);
				rsp.setOwnMonth(DateUtil.convert(ent.getWtime(), DateUtil.format4));
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
			return BaseResponse.success(resultList);
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}

	/**
	 * 根据产品ID获取结算流程数量和时间标题 有哪些年、月
	 *
	 * @param productId
	 *            产品id
	 * @return 结算时间
	 */
	@RequestMapping("/getSettlementTime")
	public String getSettlementTime(String productId) {
		logger.info("查询供应商结算时间标题和未处理流程数开始，productId:" + productId);
		// 从产品创建时间到现在的年月集合
		Map<String, List<ToQueryMonthRespDto>> yearAndmonth = null;
		// 是否显示发起按钮
		boolean buttonBody = false;
		try {
			if (StringUtils.isNotBlank(productId)) {
				// 查询当前用户在每个供应商的每个产品等待处理的运营/结算流程数（结果不会为null）
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(getOnlineUserAndOnther().getRoleId(), getOnlineUser().getOssUserId());
				// 过滤出指定供应商产品的流程
				flowCount = flowCount.stream().filter(c -> c.getEntityType() == EntityType.SUPPLIER.ordinal() && c.getFlowType() == FlowType.FINANCE.ordinal()
						&& StringUtils.equals(productId, c.getProductId())).collect(Collectors.toList());
				Product product = productService.read(productId);
				// 年未处理流程数
				Map<String, Long> yearFlowCount = new HashMap<>();
				if (product != null) {
					// 点击了产品才显示流程发起按钮
					buttonBody = true;
					// 获取从产品创建时间到现在的年月集合 {年 -> [每个月]}
					yearAndmonth = DateUtil.getMonthBetweenDate(product.getWtime(), new Date());
					if (!flowCount.isEmpty() && yearAndmonth != null && !yearAndmonth.isEmpty()) {
						// 获取每年的流程数统计数据 {年 -> 数据统计对象}
						Map<Integer, IntSummaryStatistics> map = flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
						// 将 每年数据统计数据中的总数 放到 年未处理流程数Map，得到 {年 -> 当年未处理流程总数}
						map.entrySet().stream().forEachOrdered(entry -> yearFlowCount.put(entry.getKey() + "", entry.getValue().getSum()));
						// 将未处理流程按年分组，得到 {年 -> 当年有未处理流程的月份}
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream().collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 每年的集合 [年 -> [每个月]]
						Set<Entry<String, List<ToQueryMonthRespDto>>> set = yearAndmonth.entrySet();
						// 遍历每年
						for (Entry<String, List<ToQueryMonthRespDto>> entry : set) {
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
		} catch (ServiceException e) {
			logger.info("查询供应商结算时间标题和未处理流程数异常", e);
			return "";
		}

		return "/views/settlement/settlementTime";
	}

	/**
	 * 根据产品ID
	 *
	 * @return 结算时间
	 */
	@RequestMapping("/getSettlementDetail")
	public String getSettlementDetail(@Valid ReadFlowEntDto req) {
		BaseResponse<List<ReadFlowEntRspDto>> flowEntResult = readFlowEnt(req);
		request.setAttribute("flowEnts", flowEntResult.getData());
		return "/views/settlement/settlementDetail";
	}

	/**
	 * 根据供应商ID查询所有结算记录
	 *
	 * @param supplierId
	 *            供应商id
	 * @return 结算记录
	 */
	@RequestMapping("/getAllSettlement")
	public String getAllSettlement(String supplierId) {
		logger.info("查询供应商结算的时间和数量开始，供应商id：" + supplierId);
		long startTimeMills = System.currentTimeMillis();
		// 年-每个月未处理流程数
		Map<String, List<ToQueryMonthRespDto>> yearAndmonth = null;
		boolean buttonBody = false;
		String entityType = request.getParameter("entityType");
		int entitytype = EntityType.SUPPLIER.ordinal();
		try {
			// 如果是电商的供应商，点击供应商就要显示流程发起按钮
			if (StringUtil.isNotBlank(entityType)) {
				if (StringUtils.isNumeric(entityType)) {
					Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entityType));
					if (entityTypeOpt.isPresent()) {
						buttonBody = EntityType.SUPPLIER_DS == entityTypeOpt.get();
						entitytype = entityTypeOpt.get().ordinal();
					} else {
						logger.info("错误的实体类型：" + entityType);
						return "";
					}
				} else {
					logger.info("错误的实体类型：" + entityType);
					return "";
				}
			}
			OnlineUser onlineUser = getOnlineUserAndOnther();
			Timestamp startTime = null; // 结算的最早时间
			// 有供应商id，查询供应商，获取供应商的产品的最早开始时间
			if (StringUtils.isNotBlank(supplierId)) {
				Supplier supplier = supplierService.read(supplierId);
				if (supplier != null) {
					SearchFilter searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, supplierId));
					searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_ASC));
					List<Product> resultList = productService.queryAllBySearchFilter(searchFilter);
					if (resultList != null && resultList.size() > 0) {
						startTime = resultList.get(0).getWtime();
					} else {
						startTime = supplier.getWtime();
					}
				}
			} else { // 没有供应商id，获取流程的最早时间
				List<FlowEnt> flowEnts = flowEntService.queryFlowEntByDate(onlineUser, entitytype, FlowType.FINANCE.ordinal(), "", "", "");
				if (flowEnts != null && flowEnts.size() > 0) {
					startTime = flowEnts.get(0).getWtime();
				}
			}

			if (startTime != null) {
				// 获取从开始时间到现在的年每个月集合
				yearAndmonth = DateUtil.getMonthBetweenDate(startTime, new Date());
				// 获取当前用户的 所有未处理流程
				List<FlowEntDealCount> flowCount = flowEntService.queryFlowEntDealCount(onlineUser.getRoleId(), onlineUser.getUser().getOssUserId());
				// 获取当前用户的所有供应商
				List<Supplier> suppliers = supplierService.readSuppliers(onlineUser, "", supplierId, "", "", SearchType.SUPPLIER.ordinal());
				List<String> supplierIds = new ArrayList<String>();
				if (suppliers != null && !suppliers.isEmpty()) {
					supplierIds = suppliers.stream().map(Supplier::getSupplierId).collect(Collectors.toList());
				}
				if (flowCount != null && !flowCount.isEmpty()) {
					if (StringUtils.isNotBlank(supplierId)) {
						// 过滤出指定供应商的结算流程
						flowCount = flowCount.stream()
								.filter(flow -> flow.getFlowType() == FlowType.FINANCE.ordinal() && StringUtils.equals(supplierId, flow.getSupplierId()))
								.collect(Collectors.toList());
					} else {
						// 过滤出用户所有供应商的结算流程
						List<String> finalSupplierIds = supplierIds;
						int finalEntitytype = entitytype;
						flowCount = flowCount.stream().filter(flow -> flow.getFlowType() == FlowType.FINANCE.ordinal()
								&& flow.getEntityType() == finalEntitytype && finalSupplierIds.contains(flow.getSupplierId())).collect(Collectors.toList());
					}
					// 年-年未处理流程数
					Map<String, Long> yearFlowCount = new HashMap<>();
					if (yearAndmonth != null && !yearAndmonth.isEmpty()) {
						// 年-年未处理流程统计数据
						Map<Integer, IntSummaryStatistics> map = flowCount.stream()
								.collect(Collectors.groupingBy(FlowEntDealCount::getYear, Collectors.summarizingInt(FlowEntDealCount::getFlowEntCount)));
						// 年-年未处理流程数赋值
						map.entrySet().stream().forEachOrdered(entry -> yearFlowCount.put(entry.getKey() + "", entry.getValue().getSum()));
						// 年-每个月未处理流程数
						Map<Integer, List<FlowEntDealCount>> monthCountMap = flowCount.stream().collect(Collectors.groupingBy(FlowEntDealCount::getYear));
						// 遍历 年-每个月未处理流程数 Map
						Set<Entry<String, List<ToQueryMonthRespDto>>> set = yearAndmonth.entrySet();
						for (Entry<String, List<ToQueryMonthRespDto>> entry : set) {
							// 年份
							String year = entry.getKey();
							// 年未处理流程数
							Long yearCount = yearFlowCount.get(year);
							// 年未处理流程数
							if (yearCount != null && yearCount > 0 && StringUtils.isNumeric(year) && monthCountMap.get(Integer.valueOf(year)) != null) {
								// 某年的每个月未处理流程数
								Map<Integer, Integer> monthFlowCount = monthCountMap.get(Integer.valueOf(entry.getKey())).stream()
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
			}
			String roleId = getOnlineUserAndOnther().getRoleId();
			request.setAttribute("pagePermission", roleService.getPagePermission(roleId));
			request.setAttribute("supplierId", supplierId);
			request.setAttribute("yearAndmonth", yearAndmonth);
			request.setAttribute("buttonBody", buttonBody);
		} catch (Exception e) {
			logger.info("查询供应商结算的时间和数量异常", e);
			return "";
		}
		logger.info("查询供应商结算的时间和数量结束，耗时：" + (System.currentTimeMillis() - startTimeMills));
		if (entitytype == EntityType.SUPPLIER_DS.ordinal()) {
			return "/views/settlementDs/settlementTime";
		}
		return "/views/settlement/settlementTime";
	}

	@RequestMapping("/getAllSettlementByDate")
	public String getAllSettlementByDate(@RequestParam String date, @RequestParam String supplierId) {
		BaseResponse<List<ReadFlowEntRspDto>> flowEntResult = buildAllSettlementByDate(date, supplierId);
		request.setAttribute("flowEnts", flowEntResult.getData());
		return "/views/settlement/settlementDetail";
	}

	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> buildAllSettlementByDate(String date, String supplierId) {
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

			flowList = flowEntService.queryFlowEntByDate(onlineUser, EntityType.SUPPLIER.ordinal(), FlowType.FINANCE.ordinal(), date, supplierId, "");
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

	@RequestMapping("/getSettlementByPage")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> getSettlementByPage(String date, String productId, int entityType, int page, int pageSize) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		String flowType = request.getParameter("flowType");
		int flowtype = FlowType.FINANCE.ordinal();
		if (StringUtils.isNotBlank(flowType)) {
			try {
				Optional<FlowType> flowTypeOpt = FlowType.getEnumsByCode(Integer.parseInt(flowType));
				if (flowTypeOpt.isPresent()) {
					flowtype = flowTypeOpt.get().ordinal();
				}
			} catch (Exception e) {
				logger.error("错误的流程类型，flowType：" + flowType);
				return BaseResponse.error("错误的流程类型：" + flowType);
			}
		}
		logger.info("按月份分页查询产品结算记录开始，月份：" + date + "，page：" + page + "，pageSize：" + pageSize);
		long _start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();
			// 查询点击产品的流程
			List<FlowEnt> flowList = flowEntService.queryFlowEntByDate(onlineUser, entityType, flowtype, date, "", productId);
			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success("0", resultList);
			}
			flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));

			for (FlowEnt ent : flowList) {
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
			logger.info("按月份分页查询产品结算记录结束，耗时：" + (System.currentTimeMillis() - _start));
			int pages = (int) (Math.ceil(1.0 * resultList.size() / pageSize)); // 向上
			if (page > pages) {
				return BaseResponse.success(pages + "", new ArrayList<>());
			}
			int pageStart = (page - 1) * pageSize;
			int pageEnd = Math.min(page * pageSize, resultList.size());
			return BaseResponse.success(pages + "", resultList.subList(pageStart, pageEnd));
		} catch (Exception e) {
			logger.error("按月份分页查询产品结算记录异常", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}



	@RequestMapping("/getAllSettlementByPage")
	@ResponseBody
	public BaseResponse<List<ReadFlowEntRspDto>> getAllSettlementByPage(String date, String supplierId, int page, int pageSize) {
		OnlineUser onlineUser = getOnlineUserAndOnther();
		if (null == onlineUser) {
			return BaseResponse.noLogin("请先登录");
		}
		logger.info("按月份分页查询结算记录开始，月份：" + date + "，page：" + page + "，pageSize：" + pageSize);
		long _start = System.currentTimeMillis();
		try {
			String roleId = onlineUser.getRoleId();
			List<ReadFlowEntRspDto> resultList = new ArrayList<>();
			PageResult<FlowEntWithOpt> flowPageInfo = flowEntService.queryFlowEntByPageSql(onlineUser, EntityType.SUPPLIER.ordinal(), FlowType.FINANCE.ordinal(), date, supplierId, null,pageSize,page,null);
			if (null == flowPageInfo || flowPageInfo.getData() == null || flowPageInfo.getData().isEmpty()) {
				logger.info("按月份分页查询结算记录结束，未查询到流程");
				return BaseResponse.success("0", resultList);
			}
			/*List<FlowEnt> flowList = flowEntService.queryFlowEntByDate(onlineUser, EntityType.SUPPLIER.ordinal(), FlowType.FINANCE.ordinal(), date, supplierId, "");
			if (null == flowList || flowList.isEmpty()) {
				return BaseResponse.success("0", resultList);
			}*/
			List<FlowEntWithOpt> flowList = flowPageInfo.getData();
			//flowList.sort((o1, o2) -> o2.getWtime().compareTo(o1.getWtime()));
			// 用户名称
			Map<String, String> userNames = userService.findUserNameByIds(flowList.stream().map(FlowEntWithOpt::getOssUserId).collect(Collectors.toList()));
			//流程名称
			Map<String, String> flowNames = erpFlowService.findFlowNameByIds(flowList.stream().map(FlowEntWithOpt::getId).collect(Collectors.toList()));
			// 流程节点
			Map<String, List<FlowNode>> flowNodeInfos = flowNodeService.findFlowNodeByIds(flowList.stream()
					.map(FlowEntWithOpt::getNodeId).collect(Collectors.toList()))
					.stream().collect(Collectors.groupingBy(FlowNode::getNodeId));
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
					boolean canOpt = flowNode.getRoleId().contains(roleId) &&
							(flowNode.getNodeIndex() != 0 || onlineUser.getUser().getOssUserId().equals(ent.getOssUserId())) &&
							ent.getFlowStatus() != FlowStatus.FILED.ordinal() &&
							ent.getFlowStatus() != FlowStatus.CANCLE.ordinal();
					rsp.setCanOperat(canOpt);
				}
				resultList.add(rsp);
			}
			/*resultList.sort((o1, o2) -> {
				int temp1 = o1.isCanOperat() ? 1 : 0;
				int temp2 = o2.isCanOperat() ? 1 : 0;
				return temp2 - temp1;
			});*/
			logger.info("按月份分页查询结算记录结束，耗时：" + (System.currentTimeMillis() - _start));
			/*int pages = (int) (Math.ceil(1.0 * resultList.size() / pageSize)); // 向上
			if (page > pages) {
				return BaseResponse.success(pages + "", new ArrayList<>());
			}*/
//			int pageStart = (page - 1) * pageSize;
//			int pageEnd = Math.min(page * pageSize, resultList.size());
			return BaseResponse.success(flowPageInfo.getTotalPages() + "", resultList);
		} catch (Exception e) {
			logger.error("按月份分页查询结算记录异常", e);
		}
		return BaseResponse.error("查询流程信息失败");
	}
}
