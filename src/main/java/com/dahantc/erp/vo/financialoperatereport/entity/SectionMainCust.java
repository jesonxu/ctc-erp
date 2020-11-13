package com.dahantc.erp.vo.financialoperatereport.entity;

import java.math.BigDecimal;

public class SectionMainCust extends NegativeGrossProfitCust {

	private BigDecimal grossProfitRate;

	public BigDecimal getGrossProfitRate() {
		return grossProfitRate;
	}

	public void setGrossProfitRate(BigDecimal grossProfitRate) {
		this.grossProfitRate = grossProfitRate;
	}

}
