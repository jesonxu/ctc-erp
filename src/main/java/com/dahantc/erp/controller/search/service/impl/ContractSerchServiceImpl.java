package com.dahantc.erp.controller.search.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
import com.dahantc.erp.dto.search.ContractSearchRespDto;
import com.dahantc.erp.enums.ContractFlowStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.PayType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.contract.entity.Contract;
import com.dahantc.erp.vo.contract.service.IContractService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;

@Service
public class ContractSerchServiceImpl extends ISearchService implements InitializingBean {
	private static Logger logger = LogManager.getLogger(ContractSerchServiceImpl.class);

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IContractService contractService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IProductTypeService productTypeService;

	@Override
	public Integer getSearchType() {
		return SearchType.CONTRACT.getCode();
	}

	@Override
	public String getSearchTypeName() {
		return SearchType.CONTRACT.getDesc();
	}

	@Override
	public BaseResponse<PageResult<Object>> search(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, int pageSize,
			int nowPage) {
		logger.info("搜索中心-合同搜索开始，关键词：" + searchContent + "，申请时间：" + searchStartDate + " ~ " + searchDate + "，当前页：" + nowPage + "，页大小：" + pageSize);
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchStartDate, searchDate, searchContent);
			if (filter != null) {
				PageResult<Contract> saleContractTjPageResult = contractService.queryByPages(pageSize, nowPage, filter);
				logger.info("搜索中心-合同搜索结束，结果条数：" + saleContractTjPageResult.getCount());
				return BaseResponse.success(new PageResult<>(buildContracttUI(saleContractTjPageResult.getData()), saleContractTjPageResult.getCount()));
			}
		} catch (Exception e) {
			logger.error("搜索中心-合同搜索异常", e);
		}
		return BaseResponse.success(new PageResult<Object>());
	}

	@Override
	protected String[] getExportTitle() {
		return new String[] { "合同编号", "合同名称", "合同状态", "申请人", "申请人部门", "客户/供应商名称", "客户/供应商区域", "合同归属区域", "联系人", "联系方式", "联系地址", "合同类型", "产品类型", "付费方式", "月发送量",
				"合同金额", "单价", "项目负责人", "开始日期", "结束日期", "项目情况说明", "申请时间" };
	}

	@Override
	protected List<String[]> getExportData(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, String flowId) {
		logger.info("搜索中心-获取合同导出数据开始，关键词：" + searchContent + "，月份：" + searchDate);
		List<String[]> dataList = new ArrayList<>();
		int selectSize = 1000;
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchStartDate, searchDate, searchContent);
			if (filter != null) {
				int count = contractService.getCount(filter);
				if (count > 0) {
					int i = count % selectSize == 0 ? count / selectSize : (count / selectSize + 1);
					for (int j = 1; j <= i; j++) {
						if (i == j) {
							selectSize = count - (i - 1) * selectSize;
						}
						List<Contract> contracts = contractService.findByFilter(selectSize, (j - 1) * selectSize, filter);
						dataList.addAll(buildExportData(contracts, getExportTitle()));
					}
				}
			}
			logger.info("搜索中心-获取合同导出数据结束，结果条数：" + dataList.size());
		} catch (Exception e) {
			logger.error("搜索中心-获取合同导出数据异常", e);
		}
		return dataList;
	}

	private List<String[]> buildExportData(List<Contract> contracts, String[] title) {
		List<String[]> dataList = new ArrayList<>();
		Map<String, String> userMap = null;
		Map<String, String> deptMap = null;
		Map<Integer, String> regionMap = null;
		try {
			if (contracts != null && !contracts.isEmpty()) {
				userMap = new HashMap<>();
				deptMap = new HashMap<>();
				regionMap = new HashMap<>();
				for (Contract contract : contracts) {
					try {
						String[] data = new String[title.length];
						data[0] = contract.getContractId();
						data[1] = contract.getContactName();
						data[2] = getContractStatus(contract.getStatus());
						data[3] = optUserName(userMap, contract.getOssUserId());
						data[4] = optDeptName(deptMap, contract.getDeptId());
						data[5] = contract.getEntityName();
						data[6] = optRegion(regionMap, contract.getEntityRegion());
						data[7] = contract.getContractRegion();
						data[8] = contract.getContactName();
						data[9] = contract.getContactPhone();
						data[10] = contract.getAddress();
						data[11] = contract.getContractType();
						data[12] = productTypeService.getProductTypeNameByValue(contract.getProductType());
						data[13] = PayType.getPayType(contract.getSettleType());
						data[14] = contract.getMonthCount();
						data[15] = contract.getContractAmount();
						data[16] = contract.getPrice();
						data[17] = contract.getProjectLeader();
						Timestamp validityDateStart = contract.getValidityDateStart();
						data[18] = (validityDateStart == null ? "" : DateUtil.convert(validityDateStart, DateUtil.format2));
						Timestamp validityDateEnd = contract.getValidityDateEnd();
						data[19] = (validityDateEnd == null ? "" : DateUtil.convert(validityDateEnd, DateUtil.format2));
						data[20] = contract.getDescription();
						data[21] = DateUtil.convert(contract.getWtime(), DateUtil.format2);
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
			if (deptMap != null && !deptMap.isEmpty()) {
				deptMap.clear();
				deptMap = null;
			}
			if (regionMap != null && !regionMap.isEmpty()) {
				regionMap.clear();
				regionMap = null;
			}
		}
		return dataList;
	}

	private String getContractStatus(int status) {
		String contractStatus = "";
		Optional<ContractFlowStatus> stat = ContractFlowStatus.getEnumsByCode(status);
		if (stat.isPresent()) {
			contractStatus = stat.get().getMsg();
		}
		return contractStatus;
	}

	private List<ContractSearchRespDto> buildContracttUI(List<Contract> data) {
		List<ContractSearchRespDto> result = new ArrayList<>();
		Map<String, String> userMap = null;
		Map<String, String> deptMap = null;
		Map<Integer, String> regionMap = null;
		try {
			if (data != null && !data.isEmpty()) {
				userMap = new HashMap<>();
				deptMap = new HashMap<>();
				regionMap = new HashMap<>();
				for (Contract contract : data) {
					ContractSearchRespDto dto = new ContractSearchRespDto();
					BeanUtils.copyProperties(contract, dto);
					dto.setWtime(DateUtil.convert(contract.getWtime(), DateUtil.format2));
					Timestamp validityDateStart = contract.getValidityDateStart();
					dto.setValidityDateStart(validityDateStart == null ? "" : DateUtil.convert(validityDateStart, DateUtil.format2));
					Timestamp validityDateEnd = contract.getValidityDateEnd();
					dto.setValidityDateEnd(validityDateEnd == null ? "" : DateUtil.convert(validityDateEnd, DateUtil.format2));
					dto.setOssUserName(optUserName(userMap, contract.getOssUserId()));
					dto.setDeptName(optDeptName(deptMap, contract.getDeptId()));
					dto.setEntityRegion(optRegion(regionMap, contract.getEntityRegion()));
					Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(contract.getEntityType());
					entityTypeOpt.ifPresent(entityType -> dto.setEntityType(entityType.getMsg()));
					dto.setStatus(getContractStatus(contract.getStatus()));
					dto.setProductType(productTypeService.getProductTypeNameByValue(contract.getProductType()));
					dto.setSettleType(PayType.getPayType(contract.getSettleType()));
					result.add(dto);
				}
			}
		} finally {
			if (userMap != null && !userMap.isEmpty()) {
				userMap.clear();
				userMap = null;
			}
			if (deptMap != null && !deptMap.isEmpty()) {
				deptMap.clear();
				deptMap = null;
			}
			if (regionMap != null && !regionMap.isEmpty()) {
				regionMap.clear();
				regionMap = null;
			}
		}
		return result;
	}

	private SearchFilter buildSearchFilter(OnlineUser onlineUser, String searchStartDate, String searchDate, String searchContent) throws ServiceException {
		// 合同流程
		List<String> flowName = Arrays.asList(Constants.SALE_CONTRACT_FLOW_NAME);
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("flowName", Constants.ROP_IN, flowName));
		List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(searchFilter);
		if (!CollectionUtils.isEmpty(flowList)) {
			List<String> flowIdList = flowList.stream().map(ErpFlow::getFlowId).collect(Collectors.toList());
			flowIdList = erpFlowService.checkFlowViewer(onlineUser, flowIdList);
			if (CollectionUtils.isEmpty(flowIdList)) {
				logger.info("合同流程不过用户当前角色，不能搜索合同");
				return null;
			}
		}
		searchFilter = new SearchFilter();
		Set<String> entityIdSet = flowEntService.getEntityIdSetByEntityType(onlineUser, null);
		if (CollectionUtils.isEmpty(entityIdSet)) {
			logger.info("用户当前角色下没有客户和供应商");
			return null;
		}
		searchFilter.getRules().add(new SearchRule("entityId", Constants.ROP_IN, new ArrayList<String>(entityIdSet)));
		String searchStartTime = "";
		String searchEndTime = "";
		if (StringUtils.isNotBlank(searchStartDate) && StringUtils.isNotBlank(searchDate)) { // 都存在 
			searchStartTime = searchStartDate + "-01 00:00:00";
			searchEndTime = DateUtil.convert(DateUtil.getMonthEnd(DateUtil.convert4(searchDate)), DateUtil.format1) + " 23:59:59";
		} else if (StringUtils.isAllBlank(searchStartDate, searchDate)) { // 都不存在查询当前
			searchStartTime = DateUtil.convert(DateUtil.getThisMonthFirst(), DateUtil.format1) + " 00:00:00";
			searchEndTime = DateUtil.convert(DateUtil.getThisMonthFinal(new Date()), DateUtil.format1) + " 23:59:59";
		} else { // 存在一个查询这个月份的
			if (StringUtils.isBlank(searchDate)) {
				searchDate = searchStartDate;
			}
			searchStartTime = searchDate + "-01 00:00:00";
			searchEndTime = DateUtil.convert(DateUtil.getMonthEnd(DateUtil.convert4(searchDate)), DateUtil.format1) + " 23:59:59";
		}
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, DateUtil.convert(searchStartTime, DateUtil.format2)));
		searchFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, DateUtil.convert(searchEndTime, DateUtil.format2)));
		if (StringUtils.isNotBlank(searchContent)) {
			searchFilter.getRules().add(new SearchRule("contractName", Constants.ROP_CN, searchContent));
		}
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		return searchFilter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SearchHandler.getInstance().registerSearchService(getSearchType(), this);
	}
}
