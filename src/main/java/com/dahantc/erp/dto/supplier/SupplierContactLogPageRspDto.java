package com.dahantc.erp.dto.supplier;

import java.io.Serializable;
import java.util.Date;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;

public class SupplierContactLogPageRspDto implements Serializable {

	private static final long serialVersionUID = -2419715842434863575L;

	private String supplierId;

	private String recordTime;

	private String contacts;

	private String contactsForm;

	private String content;

	private String result;

	public SupplierContactLogPageRspDto(SupplierContactLog log) {
		this.supplierId = log.getSupplierId();
		this.contacts = log.getContacts();
		this.contactsForm = log.getContactsForm();
		this.content = log.getContent();
		this.result = log.getResult();
		Date time = new Date(log.getRecordTime().getTime());
		// 获取星期
		String weekStr =  Constants.WEEK_NAME[DateUtil.getDayOfWeek(log.getRecordTime())];
		String dayStr = DateUtil.convert(time,DateUtil.format5) + " 日 ";
		this.recordTime = dayStr + weekStr;
		//this.recordTime = DateUtil.convert(log.getRecordTime(), DateUtil.format1) + " " + Constants.WEEK_NAME[DateUtil.getDayOfWeek(log.getRecordTime())];
	}

	public String getSupplierId() {
		return supplierId;
	}

	public String getRecordTime() {
		return recordTime;
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

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
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

}
