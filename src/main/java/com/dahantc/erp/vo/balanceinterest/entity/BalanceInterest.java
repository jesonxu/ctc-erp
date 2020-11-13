package com.dahantc.erp.vo.balanceinterest.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_balance_interest")
@DynamicUpdate(true)
public class BalanceInterest implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "balanceinterestid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String balanceInterestId;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "customerid", length = 32)
	private String customerId;

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	@Column(name = "deptid", length = 32)
	private String deptId;

	@Column(name = "accountbalance", precision = 19, scale = 2)
	private BigDecimal accountBalance;

	@Column(name = "interestratio", precision = 19, scale = 4)
	private BigDecimal interestRatio;

	@Column(name = "interest", precision = 19, scale = 2)
	private BigDecimal interest;

	public String getBalanceInterestId() {
		return balanceInterestId;
	}

	public void setBalanceInterestId(String balanceInterestId) {
		this.balanceInterestId = balanceInterestId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOssuserId() {
		return ossUserId;
	}

	public void setOssuserId(String ossuserId) {
		this.ossUserId = ossuserId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
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

}
