package com.dahantc.erp.vo.invoice.service;

import com.dahantc.erp.vo.invoice.entity.Invoice;

public class InvoiceExtendDto extends Invoice {

	private static final long serialVersionUID = 6672459828341531763L;

	private String deptId;

	private String ossUserId;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

}
