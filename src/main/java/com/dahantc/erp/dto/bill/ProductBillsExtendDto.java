package com.dahantc.erp.dto.bill;

import com.dahantc.erp.vo.productBills.entity.ProductBills;

public class ProductBillsExtendDto extends ProductBills {

	private static final long serialVersionUID = -6711757037228707467L;
	
	private String ossUserId;

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

}
