package com.dahantc.erp.dto.supplier;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;

public class AddSupContactLogReqDto implements Serializable {

	private static final long serialVersionUID = -96039210955996947L;

	@NotBlank(message = "供应商不能为空")
	private String supplierId;

	@NotBlank(message = "联系日期不能为空")
	private String recordtime;

	@NotBlank(message = "联系人不能为空")
	private String contacts;

	@NotBlank(message = "联系形式不能为空")
	private String contactsForm;

	@NotBlank(message = "工作内容不能为空")
	private String content;

	@NotBlank(message = "工作成果说明不能为空")
	private String result;

	public SupplierContactLog getSupContLog() {
		SupplierContactLog spLog = new SupplierContactLog();
		spLog.setSupplierId(supplierId);
		spLog.setContacts(contacts);
		spLog.setContactsForm(contactsForm);
		spLog.setContent(content);
		spLog.setResult(result);
		spLog.setRecordTime(new Timestamp(DateUtil.convert(recordtime, DateUtil.format1).getTime()));
		return spLog;
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

	public String getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

}
