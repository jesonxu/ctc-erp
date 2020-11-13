package com.dahantc.erp.dto.dsSaleData;

import java.math.BigDecimal;

public class DsSaleDataDto {
	//部门
	private String department;
	//销售名称
	private String saleName;
	//客户数
	private int CustomerCount;
	//新客户数
	private int newCustomerCount;
	//老客户数
	private int oldCustomerCount;
	//新增日志数
	private int newLogCount;
	//签单数
	private int orderNo;
	//签单金额
	private BigDecimal orderTotalPrice;
	//成本
	private BigDecimal totalCost;
	//累计客户毛利
	private BigDecimal grossProfit;
	//毛利率
	private BigDecimal grossProfitRate;
	//客户回款
	private BigDecimal returnMoney;
	//业绩目标
	private int performanceGoal;
	//利润目标
	private int profitGoal;
	//业绩完成率
	private BigDecimal performanceGoalRate;
	//毛利完成率
	private BigDecimal profitGoalRate;

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public int getCustomerCount() {
		return CustomerCount;
	}

	public void setCustomerCount(int customerCount) {
		CustomerCount = customerCount;
	}

	public int getNewCustomerCount() {
		return newCustomerCount;
	}

	public void setNewCustomerCount(int newCustomerCount) {
		this.newCustomerCount = newCustomerCount;
	}

	public int getOldCustomerCount() {
		return oldCustomerCount;
	}

	public void setOldCustomerCount(int oldCustomerCount) {
		this.oldCustomerCount = oldCustomerCount;
	}

	public int getNewLogCount() {
		return newLogCount;
	}

	public void setNewLogCount(int newLogcount) {
		this.newLogCount = newLogcount;
	}

	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public BigDecimal getOrderTotalPrice() {
		return orderTotalPrice;
	}

	public void setOrderTotalPrice(BigDecimal orderTotalPrice) {
		this.orderTotalPrice = orderTotalPrice;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

	public BigDecimal getGrossProfitRate() {
		return grossProfitRate;
	}

	public void setGrossProfitRate(BigDecimal grossProfitRate) {
		this.grossProfitRate = grossProfitRate;
	}

	public BigDecimal getReturnMoney() {
		return returnMoney;
	}

	public void setReturnMoney(BigDecimal returnMoney) {
		this.returnMoney = returnMoney;
	}

	public int getPerformanceGoal() {
		return performanceGoal;
	}

	public void setPerformanceGoal(int performanceGoal) {
		this.performanceGoal = performanceGoal;
	}

	public int getProfitGoal() {
		return profitGoal;
	}

	public void setProfitGoal(int profitGoal) {
		this.profitGoal = profitGoal;
	}

	public BigDecimal getPerformanceGoalRate() {
		return performanceGoalRate;
	}

	public void setPerformanceGoalRate(BigDecimal performanceGoalRate) {
		this.performanceGoalRate = performanceGoalRate;
	}

	public BigDecimal getProfitGoalRate() {
		return profitGoalRate;
	}

	public void setProfitGoalRate(BigDecimal profitGoalRate) {
		this.profitGoalRate = profitGoalRate;
	}

	@Override
	public String toString() {
		return "DsSaleDataDto [department=" + department + ", saleName=" + saleName + ", CustomerCount=" + CustomerCount
				+ ", newCustomerCount=" + newCustomerCount + ", oldCustomerCount=" + oldCustomerCount + ", newLogcount="
				+ newLogCount + ", orderNo=" + orderNo + ", orderTotalPrice=" + orderTotalPrice + ", totalCost="
				+ totalCost + ", grossProfit=" + grossProfit + ", grossProfitRate=" + grossProfitRate + ", returnMoney="
				+ returnMoney + ", performanceGoal=" + performanceGoal + ", profitGoal=" + profitGoal
				+ ", performanceGoalRate=" + performanceGoalRate + ", profitGoalRate=" + profitGoalRate + "]";
	}
	
}
