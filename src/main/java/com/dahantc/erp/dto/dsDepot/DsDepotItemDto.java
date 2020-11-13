package com.dahantc.erp.dto.dsDepot;

import java.math.BigDecimal;

public class DsDepotItemDto {
	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 采购id
	 */
	private String depotHeadId;
	
	/**
	 * 商品名
	 */
	private String productName;
	
	/**
	 * 供应商id
	 */
	private String supplierId;

	/**
	 * 供应商名称
	 */
	private String supplierName;
	
	/**
	 * 商品id
	 */
	private String productId;
	
	/**
	 * 商品类别
	 */
	private String productType;

	/**
	 * 商品规格
	 */
	private String format;
	
	/**
	 * 数量
	 */
	private int amount;

	/**
	 * 单价
	 */
	private BigDecimal price;

	/**
	 * 总额 = 单价 * 数量
	 */
	private BigDecimal total;
	
	/**
	 * 采购删除状态 0：删除 1：未删除
	 */
	private int isDelete;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 是否是样品 0：是 1：否
	 */
	private int isSample;
	
	/**
	 * 库存类别
	 */
	private String depotType;
	
	/**
	 * 有效日期
	 */
	private String validTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepotHeadId() {
		return depotHeadId;
	}

	public void setDepotHeadId(String depotHeadId) {
		this.depotHeadId = depotHeadId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getIsSample() {
		return isSample;
	}

	public void setIsSample(int isSample) {
		this.isSample = isSample;
	}

	public String getDepotType() {
		return depotType;
	}

	public void setDepotType(String depotType) {
		this.depotType = depotType;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	@Override
	public String toString() {
		return "DsdepotItemDto [id=" + id + ", depotHeadId=" + depotHeadId + ", productName=" + productName
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", productId=" + productId
				+ ", productType=" + productType + ", format=" + format + ", amount=" + amount + ", price=" + price
				+ ", total=" + total + ", isDelete=" + isDelete + ", remark=" + remark + ", isSample=" + isSample
				+ ", depotType=" + depotType + ", validTime=" + validTime + "]";
	}
	
}
