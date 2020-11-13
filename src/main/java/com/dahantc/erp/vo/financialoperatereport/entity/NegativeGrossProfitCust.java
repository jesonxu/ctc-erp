package com.dahantc.erp.vo.financialoperatereport.entity;

import java.math.BigDecimal;

public class NegativeGrossProfitCust {

	private String customerName;

	private BigDecimal receive;

	private BigDecimal grossProfit;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getReceive() {
		return receive;
	}

	public void setReceive(BigDecimal receive) {
		this.receive = receive;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
	}

}
