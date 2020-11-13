package com.dahantc.erp.controller.manageConsole.entity;

public class ProductBusinessUI {

	// 月份
	private String month;

	// 产品类型
	private String productType;

	// 总成功数
	private long totalSuccessCount;

	// 计费数
	private long successCount;

	// 平均销售单价
	private double salePrice;

	// 权益收入
	private double equityIncome;

	// 预付结余
	private double balance;

	// 欠款
	private double arrears;

	// 平均成本单价
	private double costPrice;

	// 成本
	private double cost;

	// 毛利
	private double gross;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public long getTotalSuccessCount() {
		return totalSuccessCount;
	}

	public void setTotalSuccessCount(long totalSuccessCount) {
		this.totalSuccessCount = totalSuccessCount;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public double getEquityIncome() {
		return equityIncome;
	}

	public void setEquityIncome(double equityIncome) {
		this.equityIncome = equityIncome;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getArrears() {
		return arrears;
	}

	public void setArrears(double arrears) {
		this.arrears = arrears;
	}

	public double getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(double costPrice) {
		this.costPrice = costPrice;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getGross() {
		return gross;
	}

	public void setGross(double gross) {
		this.gross = gross;
	}
}
