package com.dahantc.erp.vo.accountbalance.entity;

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
@Table(name = "erp_account_balance")
@DynamicUpdate(true)
public class AccountBalance implements Serializable {

	private static final long serialVersionUID = -987687078745414932L;

	@Id
	@Column(name = "accountbalanceid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountBalanceId;

	@Column(name = "account", length = 255)
	private String account;

	@Column(name = "accountbalance", precision = 19, scale = 4)
	private BigDecimal accountBalance;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getAccountBalanceId() {
		return accountBalanceId;
	}

	public void setAccountBalanceId(String accountBalanceId) {
		this.accountBalanceId = accountBalanceId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
