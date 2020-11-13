package com.dahantc.erp.vo.financialoperatereport.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.financialoperatereport.IFinancialOperateReportService;
import com.dahantc.erp.vo.financialoperatereport.entity.GrossProfitRateOverview;
import com.dahantc.erp.vo.financialoperatereport.entity.GrossProfitRateOverview.GrossProfitRateSection;
import com.dahantc.erp.vo.financialoperatereport.entity.MainCustResult;
import com.dahantc.erp.vo.financialoperatereport.entity.NegativeGrossProfitCust;
import com.dahantc.erp.vo.financialoperatereport.entity.SectionMainCust;

@Service("financialOperateReportService")
public class FinancialOperateReportServiceImpl implements IFinancialOperateReportService {

	private static final Logger logger = LogManager.getLogger(FinancialOperateReportServiceImpl.class);

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private ICustomerService customerService;

	// 各个部分毛利率的总览
	@Override
	public List<GrossProfitRateOverview> getEverySectionRateOverview(Date startDate, Date endDate, String productType) {
		List<GrossProfitRateOverview> result = new ArrayList<>();
		try {
			String sql = "SELECT SUM(receivables) AS receivables, SUM(grossprofit) AS grossprofit, SUM(grossprofit) / SUM(receivables) AS grossprofitrate"
					+ " FROM erp_customer_statistics WHERE statsyearmonth >= :startDate AND statsyearmonth < :endDate"
					+ (StringUtils.isNotBlank(productType) ? " AND productType IN :productType" : "")
					+ " GROUP BY statsyearmonth, customerid HAVING receivables <> 0";
			
			Map<String, Object> params = new HashMap<>();
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			if (StringUtils.isNotBlank(productType)) {
				params.put("productType", Arrays.asList(productType.split(",")));
			}
			
			List<Object[]> list = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(list)) {
				// 结果分区间
				GrossProfitRateSection[] values = GrossProfitRateSection.values();
				for (GrossProfitRateSection section : values) {
					GrossProfitRateOverview overview = new GrossProfitRateOverview();
					overview.setGrossProfitSection(section);
					result.add(overview);
				}

				BigDecimal totalReceivabels = BigDecimal.ZERO;
				BigDecimal totalGrossProfit = BigDecimal.ZERO;

				BigDecimal totalSmallCustReceivables = BigDecimal.ZERO;
				BigDecimal totalSmallCustGrossProfit = BigDecimal.ZERO;

				// 收入 毛利 区间总计
				for (Object[] arr : list) {
					BigDecimal receivables = new BigDecimal(((Number) arr[0]).doubleValue());
					BigDecimal grossprofit = new BigDecimal(((Number) arr[1]).doubleValue());
					BigDecimal grossprofitrate = new BigDecimal(((Number) arr[2]).doubleValue());

					totalReceivabels = totalReceivabels.add(receivables);
					totalGrossProfit = totalGrossProfit.add(grossprofit);

					if (grossprofit.doubleValue() - 100 <= 0) { // 零星客户
						totalSmallCustReceivables = totalSmallCustReceivables.add(receivables);
						totalSmallCustGrossProfit = totalSmallCustGrossProfit.add(grossprofit);
						continue;
					}

					int sectionIndex = GrossProfitRateSection.getGrossProfitRateSection(grossprofitrate).ordinal();
					GrossProfitRateOverview overview = result.get(sectionIndex);

					overview.setCustCount(overview.getCustCount() + 1);
					overview.setGrossProfit(overview.getGrossProfit().add(grossprofit));
					overview.setReceive(overview.getReceive().add(receivables));
				}

				// 零星客户
				BigDecimal totalSmallGrossProfitRate = totalSmallCustReceivables.signum() == 0 ? BigDecimal.ZERO
						: totalSmallCustGrossProfit.divide(totalSmallCustReceivables, 4, BigDecimal.ROUND_HALF_UP);

				int sectionIndex = GrossProfitRateSection.getGrossProfitRateSection(totalSmallGrossProfitRate).ordinal();
				GrossProfitRateOverview view = result.get(sectionIndex);

				view.setCustCount(view.getCustCount() + 1);
				view.setGrossProfit(view.getGrossProfit().add(totalSmallCustGrossProfit));
				view.setReceive(view.getReceive().add(totalSmallCustReceivables));

				// 计算 区间收入占比 区间毛利占比
				for (GrossProfitRateOverview overview : result) {
					overview.setSectionGrossProfitRate(overview.getGrossProfit().divide(totalGrossProfit, 4, BigDecimal.ROUND_HALF_UP));
					overview.setSectionReceiveRate(overview.getReceive().divide(totalReceivabels, 4, BigDecimal.ROUND_HALF_UP));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	// 各个比例阶段的代表用户 >= 10 、 <= 30、 80%，毛利倒叙排列
	@Override
	public MainCustResult<List<SectionMainCust>> getSectionMainCusts(Date startDate, Date endDate, GrossProfitRateSection section, String productType) {
		List<SectionMainCust> result = new ArrayList<>();
		try {

			String sql = "SELECT customerid, SUM(receivables) AS receivables, SUM(grossprofit) AS grossprofit, SUM(grossprofit) / SUM(receivables) AS grossprofitrate FROM erp_customer_statistics"
					+ " WHERE statsyearmonth >= :startDate AND statsyearmonth < :endDate"
					+ (StringUtils.isNotBlank(productType) ? " AND productType IN :productType" : "")
					+ " GROUP BY statsyearmonth, customerid HAVING receivables <> 0";
			
			Map<String, Object> params = new HashMap<>();
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			if (StringUtils.isNotBlank(productType)) {
				params.put("productType", Arrays.asList(productType.split(",")));
			}

			if (section.getStart() != null) {
				sql += " AND grossprofitrate >= :startRate";
				params.put("startRate", section.getStart());
			}
			if (section.getEnd() != null) {
				sql += " AND grossprofitrate < :endRate";
				params.put("endRate", section.getEnd());
			}
			sql += " ORDER BY grossprofit DESC";

			List<Object[]> list = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(list)) {
				List<String> custIdList = new ArrayList<>();

				// 计算全部的应收
				BigDecimal totalReceive = BigDecimal.ZERO;
				BigDecimal totalGrossProfit = BigDecimal.ZERO;
				for (Object[] arr : list) {
					totalReceive = totalReceive.add(new BigDecimal(((Number) arr[1]).doubleValue()));
					totalGrossProfit = totalGrossProfit.add(new BigDecimal(((Number) arr[2]).doubleValue()));
					custIdList.add(arr[0] == null ? "" : (String) arr[0]);
				}

				BigDecimal mainTotalReceive = totalReceive.multiply(new BigDecimal(0.8)).setScale(2, BigDecimal.ROUND_HALF_UP); // 80%

				// 查询客户名称
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, custIdList));
				List<Customer> custList = customerService.queryAllBySearchFilter(searchFilter);
				Map<String, String> custNameMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(custList)) {
					for (Customer customer : custList) {
						custNameMap.put(customer.getCustomerId(), customer.getCompanyName());
					}
				}

				// 封装数据 10 >= 、 <= 30 、80%
				BigDecimal currentTotalReceive = BigDecimal.ZERO;
				BigDecimal currentTotalGrossProfit = BigDecimal.ZERO;
				for (Object object : list) {
					Object[] arr = (Object[]) object;
					String custId = arr[0] == null ? "" : (String) arr[0];
					BigDecimal receivables = new BigDecimal(((Number) arr[1]).doubleValue());
					BigDecimal grossprofit = new BigDecimal(((Number) arr[2]).doubleValue());
					BigDecimal grossprofitrate = new BigDecimal(((Number) arr[3]).doubleValue());

					currentTotalReceive = currentTotalReceive.add(receivables);
					currentTotalGrossProfit = currentTotalGrossProfit.add(grossprofit);

					SectionMainCust sectionMainCust = new SectionMainCust();
					sectionMainCust.setCustomerName(custNameMap.get(custId));
					sectionMainCust.setReceive(receivables);
					sectionMainCust.setGrossProfit(grossprofit);
					sectionMainCust.setGrossProfitRate(grossprofitrate.setScale(4, BigDecimal.ROUND_HALF_UP));

					result.add(sectionMainCust);

					if ((currentTotalReceive.subtract(mainTotalReceive).signum() > 0 && result.size() >= 10)
							|| (currentTotalReceive.subtract(mainTotalReceive).signum() <= 0 && result.size() >= 30)) { // 10~30之间
						break;
					}
				}

				if (result.size() > 0) {
					// 合计
					SectionMainCust sectionMainCust = new SectionMainCust();
					sectionMainCust.setCustomerName("合计");
					sectionMainCust.setGrossProfit(currentTotalGrossProfit);
					sectionMainCust.setReceive(currentTotalReceive);
					sectionMainCust.setGrossProfitRate(currentTotalGrossProfit.divide(currentTotalReceive, 4, BigDecimal.ROUND_HALF_UP));
					result.add(sectionMainCust);

					SectionMainCust rate = new SectionMainCust();
					rate.setCustomerName("对应区间占比");
					rate.setGrossProfit(currentTotalGrossProfit.divide(totalGrossProfit, 4, BigDecimal.ROUND_HALF_UP));
					rate.setReceive(currentTotalReceive.divide(totalReceive, 4, BigDecimal.ROUND_HALF_UP));
					rate.setGrossProfitRate(BigDecimal.ZERO);
					result.add(rate);
				}
				return new MainCustResult<>(result, list.size());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return new MainCustResult<>(result, 0);
	}

	// 毛利为负，但业绩靠前的客户
	@Override
	public MainCustResult<List<NegativeGrossProfitCust>> getNegativeGrossProfitCusts(Date startDate, Date endDate, String productType) {
		List<NegativeGrossProfitCust> result = new ArrayList<>();
		try {
			String sql = "SELECT customerid, SUM(receivables) AS receivables, SUM(grossprofit) AS grossprofit, SUM(grossprofit) / SUM(receivables) AS grossprofitrate FROM erp_customer_statistics"
					+ " WHERE statsyearmonth >= :startDate AND statsyearmonth < :endDate"
					+ (StringUtils.isNotBlank(productType) ? " AND productType IN :productType" : "")
					+ " GROUP BY statsyearmonth, customerid HAVING receivables <> 0 AND grossprofitrate < 0 ORDER BY grossprofit ASC";
			
			Map<String, Object> params = new HashMap<>();
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			if (StringUtils.isNotBlank(productType)) {
				params.put("productType", Arrays.asList(productType.split(",")));
			}
			
			List<Object[]> list = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(list)) {
				List<String> custIdList = new ArrayList<>();

				// 计算全部的应收
				BigDecimal totalReceive = BigDecimal.ZERO;
				for (Object[] arr : list) {
					totalReceive = totalReceive.add(new BigDecimal(((Number) arr[1]).doubleValue()));
					custIdList.add(arr[0] == null ? "" : (String) arr[0]);
				}

				BigDecimal mainTotalReceive = totalReceive.multiply(new BigDecimal(0.8)).setScale(2, BigDecimal.ROUND_HALF_UP); // 80%

				// 查询客户名称
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("customerId", Constants.ROP_IN, custIdList));
				List<Customer> custList = customerService.queryAllBySearchFilter(searchFilter);
				Map<String, String> custNameMap = new HashMap<>();
				if (!CollectionUtils.isEmpty(custList)) {
					for (Customer customer : custList) {
						custNameMap.put(customer.getCustomerId(), customer.getCompanyName());
					}
				}

				// 封装数据 10 >= 、 <= 30 、80%
				BigDecimal currentTotalReceive = BigDecimal.ZERO;
				BigDecimal currentTotalGrossProfit = BigDecimal.ZERO;
				for (Object object : list) {
					Object[] arr = (Object[]) object;
					String custId = arr[0] == null ? "" : (String) arr[0];
					BigDecimal receivables = new BigDecimal(((Number) arr[1]).doubleValue());
					BigDecimal grossprofit = new BigDecimal(((Number) arr[2]).doubleValue());

					currentTotalReceive = currentTotalReceive.add(receivables);
					currentTotalGrossProfit = currentTotalGrossProfit.add(grossprofit);

					NegativeGrossProfitCust negativeGrossProfitCust = new NegativeGrossProfitCust();
					negativeGrossProfitCust.setCustomerName(custNameMap.get(custId));
					negativeGrossProfitCust.setReceive(receivables);
					negativeGrossProfitCust.setGrossProfit(grossprofit);

					result.add(negativeGrossProfitCust);

					if ((currentTotalReceive.subtract(mainTotalReceive).signum() > 0 && result.size() >= 10)
							|| (currentTotalReceive.subtract(mainTotalReceive).signum() <= 0 && result.size() >= 30)) { // 10~30之间
						break;
					}
				}

				if (result.size() > 0) {
					// 合计
					NegativeGrossProfitCust negativeGrossProfitCust = new NegativeGrossProfitCust();
					negativeGrossProfitCust.setCustomerName("合计");
					negativeGrossProfitCust.setGrossProfit(currentTotalGrossProfit);
					negativeGrossProfitCust.setReceive(currentTotalReceive);

					result.add(negativeGrossProfitCust);
				}
				return new MainCustResult<>(result, list.size());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return new MainCustResult<>(result, 0);
	}

	// 查询零星客户总数
	@Override
	public BaseResponse<Integer> getSmallCustCount(Date startDate, Date endDate, String productType) {
		try {
			String sql = "SELECT COUNT(tmp.customerid) FROM (SELECT customerid FROM erp_customer_statistics"
					+ " WHERE statsyearmonth >= :startDate AND statsyearmonth < :endDate"
					+ (StringUtils.isNotBlank(productType) ? " AND productType IN :productType" : "")
					+ " GROUP BY statsyearmonth, customerid HAVING SUM(receivables) <> 0 AND SUM(grossprofit) <= 100) AS tmp";
			
			Map<String, Object> params = new HashMap<>();
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			if (StringUtils.isNotBlank(productType)) {
				params.put("productType", Arrays.asList(productType.split(",")));
			}
			
			List<Number> list = baseDao.selectSQL(sql, params);
			if (!CollectionUtils.isEmpty(list)) {
				return BaseResponse.success(list.get(0).intValue());
			}
			return null;
		} catch (Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error(-1);
	}

}
