package com.dahantc.erp.controller.manageConsole.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class SaleReceivablesDetailUI implements Serializable {

	private static final long serialVersionUID = 2437664217543695717L;

	private String deptName;

	private String salerName;

	private BigDecimal receivables;

	private BigDecimal actualreceivables;

	private String actualReceivablesPercent;

	public SaleReceivablesDetailUI(String deptName, String salerName, BigDecimal receivables, BigDecimal actualreceivables) {
		this.deptName = deptName;
		this.salerName = salerName;
		this.receivables = receivables;
		this.actualreceivables = actualreceivables;
	}

	public void calculatePercent() {
		if (receivables.compareTo(new BigDecimal(0)) == 0) {
			actualReceivablesPercent = "--";
		} else {
			actualReceivablesPercent = String.format("%.2f", actualreceivables.divide(receivables, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)))
					+ "%";
		}
	}

	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}

	public String getDeptName() {
		return deptName;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public BigDecimal getActualreceivables() {
		return actualreceivables;
	}

	public String getActualReceivablesPercent() {
		return actualReceivablesPercent;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public void setActualreceivables(BigDecimal actualreceivables) {
		this.actualreceivables = actualreceivables;
	}

	public void setActualReceivablesPercent(String actualReceivablesPercent) {
		this.actualReceivablesPercent = actualReceivablesPercent;
	}

}
