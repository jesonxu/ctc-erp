package com.dahantc.erp.dto.balanceaccount;

import java.math.BigDecimal;

public class BalanceInterestDto {

	/**
	 * 销售
	 */
	private String saleName;

	/**
	 * 销售ID
	 */
	private String saleId;

	/**
	 * 部门
	 */
	private String deptName;
	/**
	 * 部门ID
	 */
	private String deptId;

	/**
	 * 客户名称
	 */
	private String customerName;

	/**
	 * 客户ID
	 */
	private String customerId;

	/**
	 * 计息利率（平均）
	 */
	private BigDecimal interestRatio;

	/**
	 * 计息（总计）
	 */
	private BigDecimal interest;

	/**
	 * 账户余额（总计）
	 */
	private BigDecimal accountBalance;

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getInterestRatio() {
		return interestRatio;
	}

	public void setInterestRatio(BigDecimal interestRatio) {
		this.interestRatio = interestRatio;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}


	public String getSaleId() {
		return saleId;
	}

	public void setSaleId(String saleId) {
		this.saleId = saleId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BalanceInterestDto() {
	}
	// b.deptid,b.ossuserid,b.customerid,sum(b.accountbalance)," +
	//					"sum(b.interest)
	public boolean setObjectData(Object[] info) {
		if (info != null && info.length >= 5) {
			if (info[0] != null){
				this.deptId = String.valueOf(info[0]);
			}
			if (info[1] != null){
				this.saleId = String.valueOf(info[1]);
			}
			if (info[2] != null){
				this.customerId = String.valueOf(info[2]);
			}
			if (info[3] != null && info[3] instanceof Number) {
				this.accountBalance = BigDecimal.valueOf(((Number) info[3]).doubleValue());
			}
			if (info[4] != null && info[4] instanceof Number) {
				this.interest = BigDecimal.valueOf(((Number) info[4]).doubleValue());
			}
			return true;
		}
		return false;
	}
}
