package com.dahantc.erp.vo.dsDepotItem.entity;

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
@Table(name = "erp_ds_depot_item")
@DynamicUpdate(true)
public class DsDepotItem implements Serializable {
	
	private static final long serialVersionUID = -3275877342351408144L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", columnDefinition = "varchar(32) COMMENT '主键id'")
	private String id;
	
	/**
	 * 采购id
	 */
	@Column(name = "depotheadid", columnDefinition = "varchar(32) COMMENT '采购id'")
	private String depotHeadId;
	
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
	 * 销售单价
	 */
	@Column(name = "price", columnDefinition = "Decimal(19,2) COMMENT '单价'")
	private BigDecimal price;

	/**
	 * 销售总额 = 销售单价 * 数量
	 */
	@Column(name = "total", columnDefinition = "Decimal(19,2) COMMENT '总额'")
	private BigDecimal total;
	
	/**
	 * 采购删除状态 0：删除 1：未删除
	 */
	@Column(name = "isdelete", columnDefinition = "int(2) default 1 COMMENT '采购删除状态'")
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

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
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

	@Override
	public String toString() {
		return "DsDepotItem [id=" + id + ", depotHeadId=" + depotHeadId + ", productName=" + productName
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", productId=" + productId
				+ ", productType=" + productType + ", format=" + format + ", amount=" + amount + ", price=" + price
				+ ", total=" + total + ", isDelete=" + isDelete + ", remark=" + remark + ", isSample=" + isSample
				+ ", depotType=" + depotType + ", validTime=" + validTime + "]";
	}
	
}
