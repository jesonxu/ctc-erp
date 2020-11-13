package com.dahantc.erp.dto.saleStatistics;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.dahantc.erp.vo.productBills.entity.ProductBills;

public class SaleStatisticsRspDto implements Serializable {

	private static final long serialVersionUID = 3717053546065652309L;

	public SaleStatisticsRspDto() {

	}

	public SaleStatisticsRspDto(ProductBills bill) {
		this.customerCount = bill.getSupplierCount();
		this.platformCount = bill.getPlatformCount();
		this.receivables = bill.getReceivables().setScale(2).toString();
		this.actualReceivables = bill.getActualReceivables().setScale(2).toString();
		this.payables = bill.getPayables().setScale(2).toString();
		this.actualPayables = bill.getActualPayables().setScale(2).toString();
		this.cost = bill.getCost().setScale(2).toString();
	}

	/**
	 * 时间类型，0-周，1-月，2-季
	 */
	private int dateType;

	/**
	 * 时间
	 */
	private String date;

	/**
	 * 部门名称
	 */
	private String deptName;

	/**
	 * 客户成功数
	 */
	private long customerCount;

	/**
	 * 发送量
	 */
	private long platformCount;

	/**
	 * 应收金额
	 */
	private String receivables;

	/**
	 * 实收金额
	 */
	private String actualReceivables;

	/**
	 * 应付金额
	 */
	private String payables;

	/**
	 * 实付金额
	 */
	private String actualPayables;

	/**
	 * 成本
	 */
	private String cost;

	/**
	 * 利润
	 */
	private String profit;

	/**
	 * 新增客户数
	 */
	private int newCusCount;

	/**
	 * 新增客户发送量
	 */
	private long newCusSendCount;

	public long getPlatformCount() {
		return platformCount;
	}

	public void setPlatformCount(long platformCount) {
		this.platformCount = platformCount;
	}

	public String getReceivables() {
		return receivables;
	}

	public void setReceivables(String receivables) {
		this.receivables = receivables;
	}

	public String getActualReceivables() {
		return actualReceivables;
	}

	public void setActualReceivables(String actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getPayables() {
		return payables;
	}

	public void setPayables(String payables) {
		this.payables = payables;
	}

	public String getActualPayables() {
		return actualPayables;
	}

	public void setActualPayables(String actualPayables) {
		this.actualPayables = actualPayables;
	}

	public long getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(long customerCount) {
		this.customerCount = customerCount;
	}

	public int getNewCusCount() {
		return newCusCount;
	}

	public long getNewCusSendCount() {
		return newCusSendCount;
	}

	public void setNewCusCount(int newCusCount) {
		this.newCusCount = newCusCount;
	}

	public void setNewCusSendCount(long newCusSendCount) {
		this.newCusSendCount = newCusSendCount;
	}

	public int getDateType() {
		return dateType;
	}

	public String getDate() {
		return date;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDateType(int dateType) {
		this.dateType = dateType;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public void addCustomerCount(long customerCount) {
		this.customerCount += customerCount;

	}

	public void addPlatformCount(long platformCount) {
		this.platformCount += platformCount;
	}

	public void addReceivables(String receivables) {
		if (StringUtils.isBlank(receivables)) {
			receivables = "0";
		}
		if (StringUtils.isBlank(this.receivables)) {
			this.receivables = "0";
		}
		this.receivables = String.format("%.2f", new BigDecimal(this.receivables).add(new BigDecimal(receivables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addActualReceivables(String actualReceivables) {
		if (StringUtils.isBlank(actualReceivables)) {
			actualReceivables = "0";
		}
		if (StringUtils.isBlank(this.actualReceivables)) {
			this.actualReceivables = "0";
		}
		this.actualReceivables = String.format("%.2f",
				new BigDecimal(this.actualReceivables).add(new BigDecimal(actualReceivables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addPayables(String payables) {
		if (StringUtils.isBlank(payables)) {
			payables = "0";
		}
		if (StringUtils.isBlank(this.payables)) {
			this.payables = "0";
		}
		this.payables = String.format("%.2f", new BigDecimal(this.payables).add(new BigDecimal(payables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addActualPayables(String actualPayables) {
		if (StringUtils.isBlank(actualPayables)) {
			actualPayables = "0";
		}
		if (StringUtils.isBlank(this.actualPayables)) {
			this.actualPayables = "0";
		}
		this.actualPayables = String.format("%.2f",
				new BigDecimal(this.actualPayables).add(new BigDecimal(actualPayables)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addCost(String cost) {
		if (StringUtils.isBlank(cost)) {
			cost = "0";
		}
		if (StringUtils.isBlank(this.cost)) {
			this.cost = "0";
		}
		this.cost = String.format("%.2f", new BigDecimal(this.cost).add(new BigDecimal(cost)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addProfit(String profit) {
		if (StringUtils.isBlank(profit)) {
			profit = "0";
		}
		if (StringUtils.isBlank(this.profit)) {
			this.profit = "0";
		}
		this.profit = String.format("%.2f", new BigDecimal(this.profit).add(new BigDecimal(profit)).setScale(2, BigDecimal.ROUND_CEILING));
	}

	public void addNewCusCount(int newCusCount) {
		this.newCusCount += newCusCount;
	}

	public void addNewCusSendCount(long newCusSendCount) {
		this.newCusSendCount += newCusSendCount;
	}

}
