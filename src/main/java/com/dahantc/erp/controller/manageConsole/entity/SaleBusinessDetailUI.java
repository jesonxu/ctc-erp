package com.dahantc.erp.controller.manageConsole.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SaleBusinessDetailUI implements Serializable {

	private static final long serialVersionUID = 4715177806739626646L;

	private String regionName;

	private String parentDeptName;

	private String deptName;

	private String saleName;

	private BigDecimal sumReceivables;

	private BigDecimal sumGrossProfit;

	private BigDecimal sumIncome;

	// 每个月的数据 MM -> [收入，毛利，收款]
	private Map<String, BigDecimal[]> monthDataMap;

	public SaleBusinessDetailUI() {
		this.regionName = "-";
		this.parentDeptName = "-";
		this.deptName = "-";
		this.saleName = "-";
		this.monthDataMap = new HashMap<>();
		this.sumReceivables = new BigDecimal(0);
		this.sumGrossProfit = new BigDecimal(0);
		this.sumIncome = new BigDecimal(0);
	}

	public void addSumReceivables(BigDecimal receivables) {
		setSumReceivables(this.sumReceivables.add(receivables));
	}

	public void addSumGrossProfit(BigDecimal grossProfit) {
		setSumGrossProfit(this.sumGrossProfit.add(grossProfit));
	}

	public void addSumIncome(BigDecimal income) {
		setSumIncome(this.sumIncome.add(income));
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getParentDeptName() {
		return parentDeptName;
	}

	public void setParentDeptName(String parentDeptName) {
		this.parentDeptName = parentDeptName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public BigDecimal getSumReceivables() {
		return sumReceivables;
	}

	public void setSumReceivables(BigDecimal sumReceivables) {
		this.sumReceivables = sumReceivables;
	}

	public BigDecimal getSumGrossProfit() {
		return sumGrossProfit;
	}

	public void setSumGrossProfit(BigDecimal sumGrossProfit) {
		this.sumGrossProfit = sumGrossProfit;
	}

	public BigDecimal getSumIncome() {
		return sumIncome;
	}

	public void setSumIncome(BigDecimal sumIncome) {
		this.sumIncome = sumIncome;
	}

	public Map<String, BigDecimal[]> getMonths() {
		return monthDataMap;
	}

	public void setMonths(Map<String, BigDecimal[]> monthDataMap) {
		this.monthDataMap = monthDataMap;
	}

	// 获取某个月的数据 [收入，毛利，收款]
	public BigDecimal[] getMonthData(String month) {
		return this.monthDataMap.getOrDefault(month, new BigDecimal[] {new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)});
	}

	// 存放某个月的数据 [收入，毛利，收款]
	public void setMonthData(String month, BigDecimal[] data) {
		this.monthDataMap.put(month, data);
	}
}
