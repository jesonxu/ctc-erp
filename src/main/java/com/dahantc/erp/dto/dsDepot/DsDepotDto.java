package com.dahantc.erp.dto.dsDepot;

import java.math.BigDecimal;

public class DsDepotDto {

	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 供应商名称
	 */
	private String supplierName;
	
	/**
	 * 采购批次号
	 */
	private String depotCode;
	
	/**
	 * 商品名
	 */
	private String productName;
	
	/**
	 * 采购合计金额
	 */
	private BigDecimal depotCost;
	
	/**
	 * 其他金额
	 */
	private BigDecimal otherCost;
	
	/**
	 * 采购日期
	 */
	private String buyTime;
	
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
	 * 入库详情表主键id
	 */
	private String depotItemId;

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
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getDepotCode() {
		return depotCode;
	}

	public void setDepotCode(String depotCode) {
		this.depotCode = depotCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getDepotCost() {
		return depotCost;
	}

	public void setDepotCost(BigDecimal depotCost) {
		this.depotCost = depotCost;
	}

	public String getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(String buyTime) {
		this.buyTime = buyTime;
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

	public String getDepotItemId() {
		return depotItemId;
	}

	public void setDepotItemId(String depotItemId) {
		this.depotItemId = depotItemId;
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

	@Override
	public String toString() {
		return "DsDepotDto [id=" + id + ", supplierName=" + supplierName + ", depotCode=" + depotCode + ", productName="
				+ productName + ", depotCost=" + depotCost + ", otherCost=" + otherCost + ", buyTime=" + buyTime
				+ ", createrName=" + createrName + ", verifyStatus=" + verifyStatus + ", remark=" + remark
				+ ", depotItemId=" + depotItemId + ", amount=" + amount + ", price=" + price + ", isSample=" + isSample
				+ "]";
	}
	
}
