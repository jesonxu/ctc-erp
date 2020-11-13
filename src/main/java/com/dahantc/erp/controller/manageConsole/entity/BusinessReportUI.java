package com.dahantc.erp.controller.manageConsole.entity;

public class BusinessReportUI {
	
	private String year;
	
	// 总收入
	private double incomeTotal;
	
	// 总成本
	private double costTotal;
	
	// 总毛利
	private double grossTotal;
	
	// 毛利率
	private String grossProfit;
	
	// 缴纳税金
	private double taxes;
	
	// 净利润
	private double netProfit;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public double getIncomeTotal() {
		return incomeTotal;
	}

	public void setIncomeTotal(double incomeTotal) {
		this.incomeTotal = incomeTotal;
	}

	public double getCostTotal() {
		return costTotal;
	}

	public void setCostTotal(double costTotal) {
		this.costTotal = costTotal;
	}

	public double getGrossTotal() {
		return grossTotal;
	}

	public void setGrossTotal(double grossTotal) {
		this.grossTotal = grossTotal;
	}

	public String getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(String grossProfit) {
		this.grossProfit = grossProfit;
	}

	public double getTaxes() {
		return taxes;
	}

	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}

	public double getNetProfit() {
		return netProfit;
	}

	public void setNetProfit(double netProfit) {
		this.netProfit = netProfit;
	}
	
}
