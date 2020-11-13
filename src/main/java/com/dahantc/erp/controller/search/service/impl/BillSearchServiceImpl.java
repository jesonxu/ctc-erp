package com.dahantc.erp.controller.search.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.dahantc.erp.dto.search.BillSearchRespDto;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;

@Service
public class BillSearchServiceImpl extends ISearchService implements InitializingBean {

	private static Logger logger = LogManager.getLogger(BillSearchServiceImpl.class);

	@Autowired
	private IProductBillsService billsService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private ICustomerProductService productService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Override
	public Integer getSearchType() {
		return SearchType.BILL.getCode();
	}

	@Override
	public String getSearchTypeName() {
		return SearchType.BILL.getDesc();
	}

	@Override
	public BaseResponse<PageResult<Object>> search(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, int pageSize,
			int nowPage) {
		logger.info("搜索中心-账单搜索开始，关键词：" + searchContent + "，月份：" + searchStartDate + " ~ " + searchDate + "，当前页：" + nowPage + "，页大小：" + pageSize);
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchStartDate, searchDate, searchContent);
			if (filter != null) {
				PageResult<ProductBills> productBillsPageResult = billsService.queryByPages(pageSize, nowPage, filter);
				logger.info("搜索中心-账单搜索结束，结果条数：" + productBillsPageResult.getCount());
				return BaseResponse.success(new PageResult<>(buildBillUI(productBillsPageResult.getData()), productBillsPageResult.getCount()));
			}
		} catch (Exception e) {
			logger.error("搜索中心-账单搜索异常", e);
		}
		return BaseResponse.success(new PageResult<>());
	}

	private List<BillSearchRespDto> buildBillUI(List<ProductBills> data) {
		List<BillSearchRespDto> result = new ArrayList<>();
		Map<String, String> custMap = null;
		Map<String, String> supplierMap = null;
		Map<String, String> productMap = null;
		try {
			if (data != null && !data.isEmpty()) {
				custMap = new HashMap<>();
				supplierMap = new HashMap<>();
				productMap = new HashMap<>();
				for (ProductBills bills : data) {
					BillSearchRespDto dto = new BillSearchRespDto();
					BeanUtils.copyProperties(bills, dto);
					int entityType = bills.getEntityType();
					dto.setEntityType(EntityType.getEntityType(entityType));
					if (EntityType.CUSTOMER.getCode() == entityType) {
						dto.setEntityName(optCustomerName(custMap, bills.getEntityId()));
					} else if (EntityType.SUPPLIER.getCode() == entityType) {
						dto.setEntityName(optSupplierName(supplierMap, bills.getEntityId()));
					}
					String productId = bills.getProductId();
					if (!productMap.containsKey(productId)) {
						try {
							CustomerProduct product = productService.read(productId);
							if (product != null) {
								productMap.put(productId, product.getProductName());
							}
						} catch (ServiceException e) {
							logger.error("查询id为" + productId + "的产品信息失败", e);
						}
					}
					String productName = productMap.get(productId);
					dto.setProductName(StringUtils.isBlank(productName) ? "" : productName);
					Timestamp finalPayTime = bills.getFinalPayTime();
					dto.setFinalPayTime(finalPayTime == null ? "" : DateUtil.convert(finalPayTime, DateUtil.format2));
					Timestamp finalReceiveTime = bills.getFinalReceiveTime();
					dto.setFinalReceiveTime(finalReceiveTime == null ? "" : DateUtil.convert(finalReceiveTime, DateUtil.format2));
					result.add(dto);
				}
			}
		} finally {
			if (custMap != null && !custMap.isEmpty()) {
				custMap.clear();
				custMap = null;
			}
			if (supplierMap != null && !supplierMap.isEmpty()) {
				supplierMap.clear();
				supplierMap = null;
			}
			if (productMap != null && !productMap.isEmpty()) {
				productMap.clear();
				productMap = null;
			}
		}
		return result;
	}

	@Override
	protected String[] getExportTitle() {
		return new String[] { "账单编号", "名称", "主体类型", "产品名称", "供应商成本", "平台成功数", "应收金额", "实收金额", "应付金额", "实付金额", "已开票金额", "成本", "平均成本单价", "毛利润", "支付截止日期",
				"收款截止日期" };
	}

	@Override
	protected List<String[]> getExportData(OnlineUser onlineUser, String searchContent, String searchStartDate, String searchDate, String flowId) {
		logger.info("搜索中心-获取账单导出数据开始，关键词：" + searchContent + "，月份：" + searchDate);
		List<String[]> dataList = new ArrayList<>();
		int selectSize = 1000;
		try {
			SearchFilter filter = buildSearchFilter(onlineUser, searchStartDate, searchDate, searchContent);
			if (filter != null) {
				int count = billsService.getCount(filter);
				if (count > 0) {
					int i = count % selectSize == 0 ? count / selectSize : (count / selectSize + 1);
					for (int j = 1; j <= i; j++) {
						if (i == j) {
							selectSize = count - (i - 1) * selectSize;
						}
						List<ProductBills> productBills = billsService.findByFilter(selectSize, (j - 1) * selectSize, filter);
						dataList.addAll(buildExportData(productBills, getExportTitle()));
					}
				}
			}
			logger.info("搜索中心-获取账单导出数据结束，结果条数：" + dataList.size());
		} catch (Exception e) {
			logger.error("搜索中心-获取账单导出数据异常", e);
		}
		return dataList;
	}

	private List<String[]> buildExportData(List<ProductBills> productBills, String[] exportTitle) {
		List<String[]> dataList = new ArrayList<>();
		Map<String, String> custMap = null;
		Map<String, String> supplierMap = null;
		Map<String, String> productMap = null;
		try {
			if (productBills != null && !productBills.isEmpty()) {
				custMap = new HashMap<>();
				supplierMap = new HashMap<>();
				productMap = new HashMap<>();
				for (ProductBills bills : productBills) {
					String[] data = new String[exportTitle.length];
					data[0] = bills.getBillNumber();
					int entityType = bills.getEntityType();
					data[2] = EntityType.getEntityType(entityType);
					if (EntityType.CUSTOMER.getCode() == entityType) {
						data[1] = optCustomerName(custMap, bills.getEntityId());
					} else if (EntityType.SUPPLIER.getCode() == entityType) {
						data[1] = optSupplierName(supplierMap, bills.getEntityId());
					}
					String productId = bills.getProductId();
					if (!productMap.containsKey(productId)) {
						try {
							CustomerProduct product = productService.read(productId);
							if (product != null) {
								productMap.put(productId, product.getProductName());
							}
						} catch (ServiceException e) {
							logger.error("查询id为" + productId + "的产品信息失败", e);
						}
					}
					String productName = productMap.get(productId);
					data[3] = StringUtils.isBlank(productName) ? "" : productName;
					data[4] = bills.getSupplierCount() + "";
					data[5] = bills.getPlatformCount() + "";
					BigDecimal receivables = bills.getReceivables();
					data[6] = receivables == null ? "" : receivables.toString();
					BigDecimal actualReceivables = bills.getActualReceivables();
					data[7] = actualReceivables == null ? "" : actualReceivables.toString();
					BigDecimal payables = bills.getPayables();
					data[8] = payables == null ? "" : payables.toString();
					BigDecimal actualPayables = bills.getActualPayables();
					data[9] = actualPayables == null ? "" : actualPayables.toString();
					BigDecimal actualInvoiceAmount = bills.getActualInvoiceAmount();
					data[10] = actualInvoiceAmount == null ? "" : actualInvoiceAmount.toString();
					BigDecimal cost = bills.getCost();
					data[11] = cost == null ? "" : cost.toString();
					BigDecimal unitPrice = bills.getUnitPrice();
					data[12] = unitPrice == null ? "" : unitPrice.toString();
					BigDecimal grossProfit = bills.getGrossProfit();
					data[13] = grossProfit == null ? "" : grossProfit.toString();
					Timestamp finalPayTime = bills.getFinalPayTime();
					data[14] = (finalPayTime == null ? "" : DateUtil.convert(finalPayTime, DateUtil.format2));
					Timestamp finalReceiveTime = bills.getFinalReceiveTime();
					data[15] = (finalReceiveTime == null ? "" : DateUtil.convert(finalReceiveTime, DateUtil.format2));
					dataList.add(data);
				}
			}
		} finally {
			if (custMap != null && !custMap.isEmpty()) {
				custMap.clear();
				custMap = null;
			}
			if (supplierMap != null && !supplierMap.isEmpty()) {
				supplierMap.clear();
				supplierMap = null;
			}
			if (productMap != null && !productMap.isEmpty()) {
				productMap.clear();
				productMap = null;
			}
		}
		return dataList;
	}

	private SearchFilter buildSearchFilter(OnlineUser onlineUser, String searchStartDate, String searchDate, CharSequence searchContent)
			throws ServiceException {
		// 账单流程
		List<String> flowName = Arrays.asList(Constants.BILL_FLOW_NAME, Constants.CUSTOMER_BILL_FLOW_NAME, Constants.INTER_BILL_FLOW_NAME,
				Constants.CUSTOMER_INTER_BILL_FLOW_NAME);
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("flowName", Constants.ROP_IN, flowName));
		List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(searchFilter);
		if (!CollectionUtils.isEmpty(flowList)) {
			List<String> flowIdList = flowList.stream().map(ErpFlow::getFlowId).collect(Collectors.toList());
			flowIdList = erpFlowService.checkFlowViewer(onlineUser, flowIdList);
			if (CollectionUtils.isEmpty(flowIdList)) {
				logger.info("账单流程不过用户当前角色，不能搜索账单");
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
			searchFilter.getRules().add(new SearchRule("billNumber", Constants.ROP_CN, searchContent));
		}
		searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
		return searchFilter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		SearchHandler.getInstance().registerSearchService(getSearchType(), this);
	}
}
