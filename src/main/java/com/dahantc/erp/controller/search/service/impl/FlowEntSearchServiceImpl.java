package com.dahantc.erp.controller.search.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.controller.search.service.ISearchService;
import com.dahantc.erp.controller.search.service.SearchHandler;
import com.dahantc.erp.dto.search.FlowEntSearchRespDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.FlowType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowLabel.service.impl.FlowLabelServiceImpl.LabelValue;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;

@Service
public class FlowEntSearchServiceImpl extends ISearchService implements InitializingBean {
	private static Logger logger = LogManager.getLogger(FlowEntSearchServiceImpl.class);
	@Autowired
	private IRoleService roleService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IFlowLabelService flowLabelService;

	@Autowired
	private IErpFlowService erpFlowService;

	private static final String[] fixedTitle = new String[] { "创建时间", "创建人", "流程类型", "流程标题", "流程状态", "当前节点", "接收日期", "当前状态" };

	private String[] exportTitle = null;

	@Override
	public Integer getSearchType() {
		return SearchType.FLOW.getCode();
	}

	@Override
	public String getSearchTypeName() {
		return SearchType.FLOW.getDesc();
	}

	@Override
	public BaseResponse<PageResult<Object>> search(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, int pageSize,
			int nowPage) {
		try {
			PageResult<FlowEnt> flowEntPageResult = flowEntService.queryFlowEntByPage2(onlineUser, null, null, searchStartDate, searchDate, null, null,
					searchContent, pageSize, nowPage, true);
			List<FlowLabel> flowLabelList = flowLabelService.queryAllBySearchFilter(null);
			Map<String, String> cacheFlowClassMap = erpFlowService.findByFilter(10000, 0, null).stream()
					.collect(Collectors.toMap(ErpFlow::getFlowId, ErpFlow::getFlowClass, (k1, k2) -> k2));
			Map<String, FlowLabel> cacheFlowLabelMap = flowLabelList.stream()
					.collect(Collectors.toMap(label -> label.getFlowId() + label.getName(), label -> label, (k1, k2) -> k2));
			return BaseResponse.success(
					new PageResult<>(buildFlowEnttUI(flowEntPageResult.getData(), cacheFlowLabelMap, cacheFlowClassMap), flowEntPageResult.getCount()));
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.success(new PageResult<Object>());
	}

	@Override
	protected String[] getExportTitle() {
		return exportTitle == null ? fixedTitle : exportTitle;
	}

	@Override
	protected List<String[]> getExportData(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, String flowId) {
		List<String[]> dataList = new ArrayList<>();
		try {
			List<FlowEnt> flowEnts = flowEntService.queryFlowEntByDate2(onlineUser, null, null, searchStartDate, searchDate, null, null, searchContent, flowId,
					true);
			List<FlowLabel> flowLabelList = flowLabelService.queryAllBySearchFilter(null);
			Map<String, String> cacheFlowClassMap = erpFlowService.findByFilter(10000, 0, null).stream()
					.collect(Collectors.toMap(ErpFlow::getFlowId, ErpFlow::getFlowClass, (k1, k2) -> k2));
			Map<String, FlowLabel> cacheFlowLabelMap = flowLabelList.stream()
					.collect(Collectors.toMap(label -> label.getFlowId() + label.getName(), label -> label, (k1, k2) -> k2));
			dataList.addAll(buildExportData(flowEnts, cacheFlowLabelMap, cacheFlowClassMap));
		} catch (Exception e) {
			logger.error("", e);
		}
		return dataList;
	}

	private List<String[]> buildExportData(List<FlowEnt> flowEnts, Map<String, FlowLabel> cacheFlowLabelMap, Map<String, String> cacheFlowClassMap) {
		List<String[]> dataList = new ArrayList<>();
		Map<String, String> userMap = null;
		Map<String, String> flowNodeMap = null;
		try {
			if (flowEnts != null && !flowEnts.isEmpty()) {
				userMap = new HashMap<>();
				flowNodeMap = new HashMap<>();

				List<String> asList = Arrays.asList(fixedTitle);
				asList = new ArrayList<>(asList);
				List<String> labelTitles = new ArrayList<>();
				Map<String, List<LabelValue>> labelValueMap = new HashMap<>();
				for (FlowEnt flowEnt : flowEnts) {
					List<LabelValue> allLabelValue = flowLabelService.getAllLabelValue(flowEnt.getFlowMsg(), cacheFlowClassMap.get(flowEnt.getFlowId()),
							flowEnt.getFlowId(), cacheFlowLabelMap);
					labelValueMap.put(flowEnt.getId(), allLabelValue);
					if (allLabelValue.size() > labelTitles.size()) {
						labelTitles = allLabelValue.stream().map(LabelValue::getKey).collect(Collectors.toList());
					}
				}
				asList.addAll(4, labelTitles);
				exportTitle = asList.toArray(new String[] {});
				for (FlowEnt flowEnt : flowEnts) {
					try {
						String[] data = new String[exportTitle.length];
						data[0] = DateUtil.convert(flowEnt.getWtime(), DateUtil.format2);
						data[1] = optUserName(userMap, flowEnt.getOssUserId());
						Optional<FlowType> flowType = FlowType.getEnumsByCode(flowEnt.getFlowType());
						String flowEntType = "";
						if (flowType.isPresent()) {
							flowEntType = flowType.get().getDesc();
						}
						data[2] = flowEntType;
						data[3] = flowEnt.getFlowTitle();

						Map<String, LabelValue> map = labelValueMap.get(flowEnt.getId()).stream()
								.collect(Collectors.toMap(LabelValue::getKey, labelValue -> labelValue, (k1, k2) -> k2));
						for (int i = 0; i < labelTitles.size(); i++) {
							if (map.containsKey(exportTitle[4 + i])) {
								data[4 + i] = map.get(exportTitle[4 + i]).getValue();
							}
						}

						Optional<FlowStatus> flowState = FlowStatus.getEnumsByOrdinal(flowEnt.getFlowStatus());
						String flowStatus = "";
						String nowStatus = "";
						if (flowState.isPresent()) {
							FlowStatus status = flowState.get();
							if (FlowStatus.CANCLE.getDesc().equals(status.getDesc())) {
								flowStatus = FlowStatus.CANCLE.getDesc();
							} else {
								flowStatus = "正常";
							}
							nowStatus = status.getDesc();
						}
						data[4 + labelTitles.size()] = flowStatus;
						data[5 + labelTitles.size()] = optFlowNode(flowNodeMap, flowEnt.getNodeId());
						data[6 + labelTitles.size()] = getReceiveTime(flowEnt.getId());
						data[7 + labelTitles.size()] = nowStatus;
						dataList.add(data);
					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}
		} finally {
			if (userMap != null && !userMap.isEmpty()) {
				userMap.clear();
				userMap = null;
			}
			if (flowNodeMap != null && !flowNodeMap.isEmpty()) {
				flowNodeMap.clear();
				flowNodeMap = null;
			}
			if (flowEnts != null && !flowEnts.isEmpty()) {
				flowEnts.clear();
				flowEnts = null;
			}
		}
		return dataList;
	}

	private List<FlowEntSearchRespDto> buildFlowEnttUI(List<FlowEnt> data, Map<String, FlowLabel> cacheFlowLabelMap, Map<String, String> cacheFlowClassMap) {
		List<FlowEntSearchRespDto> result = new ArrayList<>();
		Map<String, String> userMap = null;
		Map<String, String> flowNodeMap = null;
		try {
			if (data != null && !data.isEmpty()) {
				userMap = new HashMap<>();
				flowNodeMap = new HashMap<>();
				for (FlowEnt flowEnt : data) {
					FlowEntSearchRespDto dto = new FlowEntSearchRespDto();
					dto.setCreateUser(optUserName(userMap, flowEnt.getOssUserId()));
					int flowStatus = flowEnt.getFlowStatus();
					Optional<FlowStatus> flowState = FlowStatus.getEnumsByOrdinal(flowStatus);
					if (flowState.isPresent()) {
						FlowStatus status = flowState.get();
						if (FlowStatus.CANCLE.getDesc().equals(status.getDesc())) {
							dto.setFlowStatus(FlowStatus.CANCLE.getDesc());
						} else {
							dto.setFlowStatus("正常");
						}
						dto.setNowStatus(status.getDesc());
					}
					dto.setFlowTitle(flowEnt.getFlowTitle());
					dto.setLabelValue(flowLabelService.getAllLabelValue(flowEnt.getFlowMsg(), cacheFlowClassMap.get(flowEnt.getFlowId()), flowEnt.getFlowId(),
							cacheFlowLabelMap));
					Optional<FlowType> flowType = FlowType.getEnumsByCode(flowEnt.getFlowType());
					if (flowType.isPresent()) {
						dto.setFlowType(flowType.get().getDesc());
					}
					dto.setNodeName(optFlowNode(flowNodeMap, flowEnt.getNodeId()));
					dto.setReceiveTime(getReceiveTime(flowEnt.getId()));
					// 查询接收时间
					dto.setWtime(DateUtil.convert(flowEnt.getWtime(), DateUtil.format2));
					result.add(dto);
				}
			}
		} finally {
			if (userMap != null && !userMap.isEmpty()) {
				userMap.clear();
				userMap = null;
			}
			if (flowNodeMap != null && !flowNodeMap.isEmpty()) {
				flowNodeMap.clear();
				flowNodeMap = null;
			}
		}
		return result;
	}

	private String getReceiveTime(String id) {
		String time = "";
		if (StringUtils.isNotBlank(id)) {
			try {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, id));
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
				List<FlowLog> flowLogs = flowLogService.queryAllBySearchFilter(filter);
				if (flowLogs != null && !flowLogs.isEmpty()) {
					Timestamp wtime = flowLogs.get(0).getWtime();
					time = DateUtil.convert(wtime, DateUtil.format2);
				}
			} catch (ServiceException e) {
				logger.error("", e);
			}
		}
		return time;
	}

	@SuppressWarnings("unused")
	private SearchFilter buildSearchFilter(OnlineUser onlineUser, String searchDate, CharSequence searchContent) throws ServiceException {
		SearchFilter searchFilter = new SearchFilter();
		String deptId = onlineUser.getUser().getDeptId();
		Role role = roleService.read(onlineUser.getRoleId());
		// 数据权限
		int dataPermission = role.getDataPermission();
		if (dataPermission == DataPermission.Dept.ordinal()) {
			// 部门权限
			List<String> subDeptIds = new ArrayList<>();
			subDeptIds.add(deptId);
			subDeptIds.addAll(departmentService.getSubDeptIds(deptId));
			List<Customer> customers = customerService.readCustomers(onlineUser, String.join(",", subDeptIds), "", "", "");
			if (customers != null && !customers.isEmpty()) {
				List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, customerIds));
			} else {
				// 没有客户
				return null;
			}
		} else if (dataPermission == DataPermission.Self.ordinal()) {
			// 自己
			searchFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
		} else if (dataPermission == DataPermission.Customize.ordinal()) {
			// 自定义
			String deptIdInfo = role.getDeptIds();
			List<String> deptIds = new ArrayList<>();
			if (StringUtil.isNotBlank(deptIdInfo)) {
				deptIds.addAll(Arrays.asList(deptIdInfo.split(",")));
			}
			List<Customer> customers = customerService.readCustomers(onlineUser, String.join(",", deptIds), "", "", "");
			if (customers != null && !customers.isEmpty()) {
				List<String> customerIds = customers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
				searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, customerIds));
			} else {
				return null;
			}
		} else if (dataPermission == DataPermission.Flow.ordinal()) {
			// 流程权限
			// 查询待处理流程的相关供应商
			List<String> supplierIds = flowEntService.queryFlowEntityId(onlineUser.getRoleId(), onlineUser.getUser().getOssUserId(), null);
			if (ListUtils.isEmpty(supplierIds)) {
				return null;
			}
			searchFilter.getRules().add(new SearchRule("supplierId", Constants.ROP_IN, supplierIds));
		}
		String searchStartTime = "";
		String searchEndTime = "";
		if (StringUtils.isBlank(searchDate)) {
			Date thisMonthFirst = DateUtil.getThisMonthFirst();
			searchStartTime = DateUtil.convert(thisMonthFirst, DateUtil.format1) + " 00:00:00";
			searchEndTime = DateUtil.convert(DateUtil.getMonthEnd(new Date()), DateUtil.format1) + " 23:59:59";
		} else {
			Date convert = DateUtil.convert(searchDate, DateUtil.format4);
			Date thisMonthFirst = DateUtil.getThisMonthFirst(convert);
			searchStartTime = DateUtil.convert(thisMonthFirst, DateUtil.format1) + " 00:00:00";
			searchEndTime = DateUtil.convert(DateUtil.getMonthEnd(convert), DateUtil.format1) + " 23:59:59";
		}
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert(searchStartTime, DateUtil.format2)));
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.convert(searchEndTime, DateUtil.format2)));

		if (StringUtils.isNotBlank(searchContent)) {
			searchFilter.getRules().add(new SearchRule("flowTitle", Constants.ROP_CN, searchContent));
		}
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		return searchFilter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SearchHandler.getInstance().registerSearchService(getSearchType(), this);
	}
}
