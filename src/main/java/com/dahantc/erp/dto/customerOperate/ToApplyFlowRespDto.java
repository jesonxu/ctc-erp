package com.dahantc.erp.dto.customerOperate;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 跳转充值页面响应填充数据
 */
public class ToApplyFlowRespDto implements Serializable {

	private static final long serialVersionUID = -1380343149945332113L;

	private String customerId;

	private String customerName;

	private String productId;

	private String productName;

	// 产品类型
	private int productTypeInt;
	private String productType;

	// 账单类型
	private int billTypeInt;
	private String billType;

	// 计费周期
	private int billCycleInt;
	private String billCycle;

	// 结算方式
	private int settleTypeInt;
	private String settleType;

	private BigDecimal price;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillCycle() {
		return billCycle;
	}

	public void setBillCycle(String billCycle) {
		this.billCycle = billCycle;
	}

	public int getBillTypeInt() {
		return billTypeInt;
	}

	public void setBillTypeInt(int billTypeInt) {
		this.billTypeInt = billTypeInt;
	}

	public int getBillCycleInt() {
		return billCycleInt;
	}

	public void setBillCycleInt(int billCycleInt) {
		this.billCycleInt = billCycleInt;
	}

	public int getSettleTypeInt() {
		return settleTypeInt;
	}

	public void setSettleTypeInt(int settleTypeInt) {
		this.settleTypeInt = settleTypeInt;
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

	public int getProductTypeInt() {
		return productTypeInt;
	}

	public void setProductTypeInt(int productTypeInt) {
		this.productTypeInt = productTypeInt;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
