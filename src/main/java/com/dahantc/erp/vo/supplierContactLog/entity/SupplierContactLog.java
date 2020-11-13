package com.dahantc.erp.vo.supplierContactLog.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_supplier_contactlog")
@DynamicUpdate(true)
public class SupplierContactLog implements Serializable {

	private static final long serialVersionUID = 7294621764901923245L;

	@Id
	@Column(name = "erpsuppliercontactlogid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String erpSupplierContactLogId;

	@Column(name = "recordtime")
	private Timestamp recordTime = new Timestamp(System.currentTimeMillis());

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	// 供应商/客户id
	@Column(name = "supplierid", length = 32)
	private String supplierId;

	@Column(name = "contacts", length = 255)
	private String contacts;

	@Column(name = "contactsform", length = 255)
	private String contactsForm;

	@Column(name = "content", length = 2500)
	private String content;

	@Column(name = "result", length = 255)
	private String result;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getErpSupplierContactLogId() {
		return erpSupplierContactLogId;
	}

	public Timestamp getRecordTime() {
		return recordTime;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public String getContacts() {
		return contacts;
	}

	public String getContactsForm() {
		return contactsForm;
	}

	public String getContent() {
		return content;
	}

	public String getResult() {
		return result;
	}

	public void setErpSupplierContactLogId(String erpSupplierContactLogId) {
		this.erpSupplierContactLogId = erpSupplierContactLogId;
	}

	public void setRecordTime(Timestamp recordTime) {
		this.recordTime = recordTime;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public void setContactsForm(String contactsForm) {
		this.contactsForm = contactsForm;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
