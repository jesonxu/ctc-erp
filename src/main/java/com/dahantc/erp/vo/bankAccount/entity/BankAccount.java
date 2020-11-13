package com.dahantc.erp.vo.bankAccount.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.InvoiceType;

@Entity
@Table(name = "erp_bank_account")
@DynamicUpdate(true)
public class BankAccount implements Serializable {

	private static final long serialVersionUID = -987687078745414932L;

	@Id
	@Column(name = "bankaccountid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String bankAccountId;

	@Column(name = "accountname", length = 255)
	private String accountName;

	@Column(name = "accountbank", length = 255)
	private String accountBank;

	@Column(name = "bankaccount", length = 255)
	private String bankAccount;

	@Column(name = "invoicetype", columnDefinition = "int default 3")
	private int invoiceType = InvoiceType.OtherInvoice.ordinal();

	/**
	 * supplierId 或 customerId
	 */
	@Column(name = "basicsid", length = 32)
	private String basicsId;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "companyaddress", length = 100)
	private String companyAddress;

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountBank() {
		return accountBank;
	}

	public void setAccountBank(String accountBank) {
		this.accountBank = accountBank;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public int getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getBasicsId() {
		return basicsId;
	}

	public void setBasicsId(String basicsId) {
		this.basicsId = basicsId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
