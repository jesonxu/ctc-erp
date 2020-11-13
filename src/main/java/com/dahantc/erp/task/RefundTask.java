package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.RelateStatus;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.fsExpenseIncome.entity.FsExpenseIncome;
import com.dahantc.erp.vo.fsExpenseIncome.service.IFsExpenseIncomeService;

@Component("refundTask")
public class RefundTask {

	private static final Logger logger = LogManager.getLogger(RefundTask.class);

	@Autowired
	private IFsExpenseIncomeService fsExpenseIncomeService;

	@Autowired
	private IBaseDao baseDao;

	public void propertyTask() {
		try {
			logger.info("退款任务执行，开始。。。");
			long startTime = System.currentTimeMillis();
			List<FsExpenseIncome> allRefundData = getAllIncomeData(true);
			if (CollectionUtils.isEmpty(allRefundData)) {
				logger.info("退款任务执行，暂无数据");
			}
			List<FsExpenseIncome> allRemainData = getAllIncomeData(false);
			doRefund(allRefundData, allRemainData);
			logger.info("退款任务共耗时：" + (System.currentTimeMillis() - startTime));
		} catch (Throwable e) {
			logger.error("退款任务", e);
		}
	}

	private void doRefund(List<FsExpenseIncome> allRefundData, List<FsExpenseIncome> allRemainData) {
		try {
			if (CollectionUtils.isEmpty(allRemainData)) {
				logger.info("未能自动退款的记录，fsExpenseIncomeId：" + allRefundData.stream().map(FsExpenseIncome::getId).collect(Collectors.joining(","))
						+ "，请手动回退");
				return;
			}
			Map<String, List<FsExpenseIncome>> remainDataGroup = allRemainData.stream().filter(income -> StringUtil.isNotBlank(income.getDepict())).collect(Collectors.groupingBy(FsExpenseIncome::getDepict));
			// 优先匹配相同金额的
			for (FsExpenseIncome refundIncome : allRefundData) {
				List<FsExpenseIncome> list = remainDataGroup.get(refundIncome.getDepict());
				if (CollectionUtils.isEmpty(list)) {
					logger.info("未能自动退款的记录，fsExpenseIncomeId："
							+ allRefundData.stream().map(FsExpenseIncome::getId).collect(Collectors.joining(",")) + "，请手动回退");
					return;
				}
				int bestMatchIndex = -1;
				for (int i = 0; i < list.size(); i++) {
					FsExpenseIncome fsExpenseIncome = list.get(i);
					if (fsExpenseIncome.getRemainRelatedCost().add(refundIncome.getRemainRelatedCost()).signum() == 0) { // 只是剩余金额匹配
						bestMatchIndex = i;
						if (fsExpenseIncome.getCost().subtract(fsExpenseIncome.getRemainRelatedCost()).signum() == 0) {// 精确匹配
							break;
						}
					}
				}
				if (bestMatchIndex >= 0) {
					FsExpenseIncome bestMatchIncome = list.remove(bestMatchIndex);
					bestMatchIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
					bestMatchIncome.setRemainRelatedCost(BigDecimal.ZERO);
					refundIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
					refundIncome.setRemainRelatedCost(BigDecimal.ZERO);
					refundIncome.setCustomerId(bestMatchIncome.getCustomerId());
					refundIncome.setDeptName(bestMatchIncome.getDeptName());
					refundIncome.setDeptId(bestMatchIncome.getDeptId());
					refundIncome.setRemark((refundIncome.getRemark() == null ? "" : refundIncome.getRemark()) + "；退款【" + refundIncome.getCost().abs()
							+ "】元，其中收款记录fsExpenseIncomeId：" + bestMatchIncome.getId() + "【" + refundIncome.getCost().abs() + "】元");
					baseDao.updateByBatch(Arrays.asList(bestMatchIncome, refundIncome), false);
				} else { // 匹配多个到款信息
					BigDecimal refundCost = refundIncome.getRemainRelatedCost();
					List<FsExpenseIncome> incomeList = new ArrayList<>();
					if (!CollectionUtils.isEmpty(list)) {
						Iterator<FsExpenseIncome> iterator = list.iterator();
						List<String> remarks = new ArrayList<>();
						String totalRemark = "退款【" + refundIncome.getCost() + "】元，";
						while (iterator.hasNext()) {
							FsExpenseIncome remainIncome = iterator.next();
							refundCost = refundCost.add(remainIncome.getRemainRelatedCost());
							if (refundCost.signum() < 0) {
								remarks.add("其中收款记录fsExpenseIncomeId：" + remainIncome.getId() + "【" + remainIncome.getRemainRelatedCost() + "】元");
								remainIncome.setRemainRelatedCost(BigDecimal.ZERO);
								remainIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
								incomeList.add(remainIncome);
								iterator.remove();
							} else if (refundCost.add(remainIncome.getRemainRelatedCost()).signum() == 0) {
								remarks.add("其中收款记录fsExpenseIncomeId：" + remainIncome.getId() + "【" + remainIncome.getRemainRelatedCost() + "】元");
								remainIncome.setRemainRelatedCost(BigDecimal.ZERO);
								remainIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
								incomeList.add(remainIncome);
								iterator.remove();
								refundIncome.setRemainRelatedCost(BigDecimal.ZERO);
								refundIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
								refundIncome.setCustomerId(remainIncome.getCustomerId());
								refundIncome.setDeptId(remainIncome.getDeptId());
								refundIncome.setDeptName(remainIncome.getDeptName());
								refundIncome.setRemark(refundIncome.getRemark() + "；" + String.join("，", remarks));
								incomeList.add(refundIncome);
								break;
							} else {
								remarks.add("其中收款记录fsExpenseIncomeId：" + remainIncome.getId() + "【" + remainIncome.getRemainRelatedCost().subtract(refundCost)
										+ "】元");
								remainIncome.setRemainRelatedCost(refundCost);
								remainIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
								incomeList.add(remainIncome);
								refundIncome.setRemainRelatedCost(BigDecimal.ZERO);
								refundIncome.setRelateStatus(RelateStatus.RELATED.ordinal());
								refundIncome.setCustomerId(remainIncome.getCustomerId());
								refundIncome.setDeptId(remainIncome.getDeptId());
								refundIncome.setDeptName(remainIncome.getDeptName());
								refundIncome.setRemark(
										(refundIncome.getRemark() == null ? "" : refundIncome.getRemark()) + "；" + totalRemark + String.join("，", remarks));
								incomeList.add(refundIncome);
								break;
							}
						}
					}
					if (refundCost.signum() >= 0) { // 匹配上了
						baseDao.updateByBatch(incomeList, false);
					} else { // 没匹配上
						logger.info("未能自动退款的记录，fsExpenseIncomeId：" + refundIncome.getId() + "，请手动回退");
					}

				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	// 获取所有退款金额
	private List<FsExpenseIncome> getAllIncomeData(boolean refund) {
		try {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("isIncome", Constants.ROP_EQ, 0));
			searchFilter.getRules().add(new SearchRule("cost", refund ? Constants.ROP_LT : Constants.ROP_GT, BigDecimal.ZERO));
			searchFilter.getRules().add(new SearchRule("remainRelatedCost", Constants.ROP_NE, BigDecimal.ZERO));
			searchFilter.getOrders().add(new SearchOrder("operateTime", Constants.ROP_DESC));
			return fsExpenseIncomeService.queryAllBySearchFilter(searchFilter);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

}
