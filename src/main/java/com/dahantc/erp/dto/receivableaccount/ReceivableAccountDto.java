package com.dahantc.erp.dto.receivableaccount;

import java.math.BigDecimal;

public class ReceivableAccountDto {

	private String deptName;

	private String saleName;

	private String customerName;

	private String customerId;

	private BigDecimal[] receivables = new BigDecimal[12]; // 应收金额

	private BigDecimal[] invoiceds = new BigDecimal[12]; // 已开票金额

	private BigDecimal[] receiveds = new BigDecimal[12]; // 已收款金额

	private BigDecimal notInvoice = BigDecimal.ZERO; // 未开票金额

	private BigDecimal invoicedNotReceive = BigDecimal.ZERO; // 未开票金额

	private BigDecimal notReceive = BigDecimal.ZERO; // 未回款金额
	
	public boolean isShow() {
		for (BigDecimal receivable : receivables) {
			if (receivable != null && receivable.signum() > 0) {
				return true;
			}
		}
		return false;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BigDecimal[] getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal[] receivables) {
		this.receivables = receivables;
	}

	public BigDecimal[] getInvoiceds() {
		return invoiceds;
	}

	public void setInvoiceds(BigDecimal[] invoiceds) {
		this.invoiceds = invoiceds;
	}

	public BigDecimal[] getReceiveds() {
		return receiveds;
	}

	public void setReceiveds(BigDecimal[] receiveds) {
		this.receiveds = receiveds;
	}

	public BigDecimal getNotInvoice() {
		return notInvoice;
	}

	public void setNotInvoice(BigDecimal notInvoice) {
		this.notInvoice = notInvoice;
	}

	public BigDecimal getInvoicedNotReceive() {
		return invoicedNotReceive;
	}

	public void setInvoicedNotReceive(BigDecimal invoicedNotReceive) {
		this.invoicedNotReceive = invoicedNotReceive;
	}

	public BigDecimal getNotReceive() {
		return notReceive;
	}

	public void setNotReceive(BigDecimal notReceive) {
		this.notReceive = notReceive;
	}

}
