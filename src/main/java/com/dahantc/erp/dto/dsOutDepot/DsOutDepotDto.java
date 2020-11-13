package com.dahantc.erp.dto.dsOutDepot;

import java.math.BigDecimal;

public class DsOutDepotDto {

	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 客户名称
	 */
	private String customerName;
	
	/**
	 * 销售名称
	 */
	private String userName;
	
	/**
	 * 出库批次号
	 */
	private String outDepotCode;
	
	/**
	 * 商品名
	 */
	private String productName;
	
	/**
	 * 采购合计金额
	 */
	private BigDecimal outDepotTotal;
	
	/**
	 * 其他金额
	 */
	private BigDecimal otherCost;
	
	/**
	 * 采购日期
	 */
	private String outTime;
	
	/**
	 * 创建人id
	 */
	private String createrName;
	
	/**
	 * 审核状态 0：待审核 1：审核通过 2;审核不通过
	 */
	private int verifyStatus;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 出库详情表主键id
	 */
	private String outDepotDetialId;

	/**
	 * 数量
	 */
	private int amount;

	/**
	 * 销售单价
	 */
	private BigDecimal price;

	/**
	 * 是否是样品 0：是 1：否
	 */
	private int isSample;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public BigDecimal getOtherCost() {
		return otherCost;
	}

	public void setOtherCost(BigDecimal otherCost) {
		this.otherCost = otherCost;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getIsSample() {
		return isSample;
	}

	public void setIsSample(int isSample) {
		this.isSample = isSample;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOutDepotCode() {
		return outDepotCode;
	}

	public void setOutDepotCode(String outDepotCode) {
		this.outDepotCode = outDepotCode;
	}

	public BigDecimal getOutDepotTotal() {
		return outDepotTotal;
	}

	public void setOutDepotTotal(BigDecimal outDepotTotal) {
		this.outDepotTotal = outDepotTotal;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getOutDepotDetialId() {
		return outDepotDetialId;
	}

	public void setOutDepotDetialId(String outDepotDetialId) {
		this.outDepotDetialId = outDepotDetialId;
	}

	@Override
	public String toString() {
		return "DsOutDepotDto [id=" + id + ", customerName=" + customerName + ", userName=" + userName
				+ ", outDepotCode=" + outDepotCode + ", productName=" + productName + ", outDepotTotal=" + outDepotTotal
				+ ", otherCost=" + otherCost + ", outTime=" + outTime + ", createrName=" + createrName
				+ ", verifyStatus=" + verifyStatus + ", remark=" + remark + ", outDepotDetialId=" + outDepotDetialId
				+ ", amount=" + amount + ", price=" + price + ", isSample=" + isSample + "]";
	}
	
}
