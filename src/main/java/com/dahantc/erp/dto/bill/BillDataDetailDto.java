package com.dahantc.erp.dto.bill;

import java.io.Serializable;
import java.util.List;

/**
 * 单个账单对应的数据详情
 */
public class BillDataDetailDto implements Serializable {

	private static final long serialVersionUID = -8052967035055089262L;

	private String productId;

	private String productName;

	private int productType;

	private String billMonth;

	// 该产品每天的发送量详情
	List<DateDetail> dateDetailList;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public List<DateDetail> getDateDetailList() {
		return dateDetailList;
	}

	public void setDateDetailList(List<DateDetail> dateDetailList) {
		this.dateDetailList = dateDetailList;
	}
}
