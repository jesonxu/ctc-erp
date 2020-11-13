package com.dahantc.erp.vo.dsOutDepotDetail.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_ds_out_depot_detail")
@DynamicUpdate(true)
public class DsOutDepotDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7197475474206945013L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", columnDefinition = "varchar(32) COMMENT '主键id'")
	private String id;
	
	/**
	 * 出库id
	 */
	@Column(name = "outdepotid", columnDefinition = "varchar(32) COMMENT '出库id'")
	private String outDepotId;
	
	/**
	 * 商品名
	 */
	@Column(name = "productname", columnDefinition = "varchar(255) COMMENT '商品名'")
	private String productName;
	
	/**
	 * 供应商id
	 */
	@Column(name = "supplierid", columnDefinition = "varchar(32) COMMENT '供应商id'")
	private String supplierId;

	/**
	 * 供应商名称
	 */
	@Column(name = "suppliername", columnDefinition = "varchar(255) COMMENT '供应商名称'")
	private String supplierName;
	
	/**
	 * 商品id
	 */
	@Column(name = "productid", columnDefinition = "varchar(32) COMMENT '商品id'")
	private String productId;
	
	/**
	 * 商品类别
	 */
	@Column(name = "producttype", columnDefinition = "varchar(32) COMMENT '商品类别'")
	private String productType;

	/**
	 * 商品规格
	 */
	@Column(name = "format", columnDefinition = "varchar(255) COMMENT '商品id'")
	private String format;
	
	/**
	 * 数量
	 */
	@Column(name = "amount", columnDefinition = "int(11) COMMENT '数量'")
	private int amount;

	/**
	 * 单价
	 */
	@Column(name = "price", columnDefinition = "Decimal(19,2) COMMENT '单价'")
	private BigDecimal price;

	/**
	 * 总额 = 销售单价 * 数量
	 */
	@Column(name = "total", columnDefinition = "Decimal(19,2) COMMENT '总额'")
	private BigDecimal total;

	/**
	 * 商品入库id
	 */
	@Column(name = "depotitemid", columnDefinition = "varchar(32) COMMENT '商品入库id'")
	private String depotItemId;
	
	/**
	 * 商品初始库存
	 */
	@Column(name = "depotnumber", columnDefinition = "int(11) COMMENT '初始库存'")
	private String depotNumber;
	
	/**
	 * 出库删除状态 0：删除 1：未删除
	 */
	@Column(name = "isdelete", columnDefinition = "int(2) default 1 COMMENT '出库删除状态'")
	private int isDelete;
	
	/**
	 * 备注
	 */
	@Column(name = "remark", columnDefinition = "varchar(64) COMMENT '备注'")
	private String remark;
	
	/**
	 * 是否是样品 0：是 1：否
	 */
	@Column(name = "issample", columnDefinition = "int(2) default 1 COMMENT '是否是样品'")
	private int isSample;
	
	/**
	 * 库存类别
	 */
	@Column(name = "depottype", columnDefinition = "varchar(32) COMMENT '库存类别'")
	private String depotType;
	
	/**
	 * 有效日期
	 */
	@Column(name = "validtime", columnDefinition = "DATE COMMENT '有效日期'")
	private Date validTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOutDepotId() {
		return outDepotId;
	}

	public void setOutDepotId(String outDepotId) {
		this.outDepotId = outDepotId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDepotItemId() {
		return depotItemId;
	}

	public void setDepotItemId(String depotItemId) {
		this.depotItemId = depotItemId;
	}

	public String getDepotNumber() {
		return depotNumber;
	}

	public void setDepotNumber(String depotNumber) {
		this.depotNumber = depotNumber;
	}

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	@Override
	public String toString() {
		return "DsOutDepotDetail [id=" + id + ", outDepotId=" + outDepotId + ", productName=" + productName
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", productId=" + productId
				+ ", productType=" + productType + ", format=" + format + ", amount=" + amount + ", price=" + price
				+ ", total=" + total + ", depotItemId=" + depotItemId + ", depotNumber=" + depotNumber + ", isDelete="
				+ isDelete + ", remark=" + remark + ", isSample=" + isSample + ", depotType=" + depotType
				+ ", validTime=" + validTime + "]";
	}
	
}
