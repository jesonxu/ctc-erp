package com.dahantc.erp.dto.modifyPrice;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 跳转充值页面响应填充数据
 */
public class ToChargeRecordRespDto implements Serializable {

	private static final long serialVersionUID = -1380343149945332103L;

	private String supplierId;

	private String supplierName;

	private String productId;

	private String productName;

	private int productTypeInt;

	private String productType;
	// 付款方式
	private String settleType;
	// 酬金模式
	private String rewardType;

	private BigDecimal price;

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public String getRewardType() {
		return rewardType;
	}

	public void setRewardType(String rewardType) {
		this.rewardType = rewardType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getProductTypeInt() {
		return productTypeInt;
	}

	public void setProductTypeInt(int productTypeInt) {
		this.productTypeInt = productTypeInt;
	}
}
