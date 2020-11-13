package com.dahantc.erp.vo.invoiceHistory.entity;

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
@Table(name = "erp_invoice_information_history")
@DynamicUpdate(true)
public class InvoiceInformationHistory implements Serializable{

	private static final long serialVersionUID = -987687078745414932L;
	
	@Id
	@Column(name = "invoicehistoryid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String invoiceHistoryId;
	
	@Id
	@Column(name = "invoiceid", length = 32)
	private String invoiceId;

	@Column(name = "companyname", length = 255)
	private String companyName;
	
	@Column(name = "taxnumber", length = 255)
	private String taxNumber;
	
	@Column(name = "companyaddress", length = 255)
	private String companyAddress;
	
	@Column(name = "phone", length = 255)
	private String phone;
	
	@Column(name = "accountbank", length = 255)
	private String accountBank;
	
	@Column(name = "bankaccount", length = 255)
	private String bankAccount;
	
	@Column(name = "invoicetype", columnDefinition = "int default 1")
	private int invoiceType = InvoiceType.OtherInvoice.ordinal();
	
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());
	
	
	public String getInvoiceHistoryId() {
		return invoiceHistoryId;
	}

	public void setInvoiceHistoryId(String invoiceHistoryId) {
		this.invoiceHistoryId = invoiceHistoryId;
	}

	/**
	 * supplierId 或 customerId
	 */
	@Column(name = "basicsid", length = 255)
	private String basicsId;

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
