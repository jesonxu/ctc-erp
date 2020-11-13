package com.dahantc.erp.vo.dsOrderDetail.entity;

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
@Table(name = "erp_ds_order_detail")
@DynamicUpdate(true)
public class DsOrderDetail implements Serializable {

	private static final long serialVersionUID = 1973691432891902935L;

	@Id
	@Column(name = "orderdetailid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String orderDetailId;

	/**
	 * 订单编号
	 */
	@Column(name = "orderid", columnDefinition = "varchar(32) COMMENT '订单编号'")
	private String orderId;

	/**
	 * 订单类型，销售订单，采购订单
	 */
	@Column(name = "ordertype", columnDefinition = "int(2) default 0 COMMENT '订单类型'")
	private int orderType;

	/**
	 * 商品名
	 */
	@Column(name = "productname", columnDefinition = "varchar(255) COMMENT '商品名'")
	private String productName;

	/**
	 * 商品id
	 */
	@Column(name = "productid", columnDefinition = "varchar(32) COMMENT '商品id'")
	private String productId;

	/**
	 * 规格
	 */
	@Column(name = "format", columnDefinition = "varchar(255) COMMENT '规格'")
	private String format;

	/**
	 * 单位
	 */
	@Column(name = "unit", columnDefinition = "varchar(32) COMMENT '单位'")
	private String unit;

	/**
	 * 数量
	 */
	@Column(name = "amount", columnDefinition = "int(11) COMMENT '数量'")
	private int amount;

	/**
	 * 销售单价
	 */
	@Column(name = "price", columnDefinition = "Decimal(19,4) COMMENT '单价'")
	private BigDecimal price;

	/**
	 * 销售总额 = 销售单价 * 数量
	 */
	@Column(name = "total", columnDefinition = "Decimal(19,2) COMMENT '总额'")
	private BigDecimal total;

	/**
	 * 物流费
	 */
	@Column(name = "logisticscost", columnDefinition = "Decimal(19,2) COMMENT '物流费'")
	private BigDecimal logisticsCost;

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
	 * 备注
	 */
	@Column(name = "remark", columnDefinition = "varchar(255) COMMENT '备注'")
	private String remark;

	/**
	 * 创建日期
	 */
	@Column(name = "wtime", columnDefinition = "DATETIME COMMENT '创建日期'")
	private Date wtime;

	public String getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(String orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
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

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public BigDecimal getLogisticsCost() {
		return logisticsCost;
	}

	public void setLogisticsCost(BigDecimal logisticsFee) {
		this.logisticsCost = logisticsFee;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
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

	@Override
	public String toString() {
		return "DsOrderDetail{" + "orderDetailId='" + orderDetailId + '\'' + ", orderId='" + orderId + '\'' + ", orderType=" + orderType + ", productName='"
				+ productName + '\'' + ", productId='" + productId + '\'' + ", format='" + format + '\'' + ", unit='" + unit + '\'' + ", amount=" + amount
				+ ", price=" + price + ", total=" + total + ", logisticsCost=" + logisticsCost + ", supplierId='" + supplierId + '\'' + ", supplierName='"
				+ supplierName + '\'' + ", remark='" + remark + '\'' + ", wtime=" + wtime + '}';
	}
}
