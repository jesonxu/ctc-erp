package com.dahantc.erp.vo.dsBuyOrder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "erp_ds_buy_order")
@DynamicUpdate(true)
public class DsBuyOrder implements Serializable {

	private static final long serialVersionUID = 5217712318510327919L;

	/**
	 * 采购订单编号
	 */
	@Id
	@Column(name = "buyorderid", columnDefinition = "varchar(32) COMMENT '采购订单编号'")
	private String buyOrderId;

	/**
	 * 框架合同编号
	 */
	@Column(name = "contractno", columnDefinition = "varchar(32) COMMENT '框架合同编号'")
	private String contractNo;

	/**
	 * 销售订单编号，本采购单的产品，是对该销售订单中产品的拆分
	 */
	@Column(name = "orderid", columnDefinition = "varchar(32) COMMENT '订单编号'")
	private String orderId;

	/**
	 * 供应商id
	 */
	@Column(name = "supplierid", columnDefinition = "varchar(32) COMMENT '供应商id'")
	private String supplierId;

	/**
	 * 供应商名称
	 */
	@Column(name = "suppliername", columnDefinition = "varchar(64) COMMENT '供应商名称'")
	private String supplierName;

	/**
	 * 配送地址
	 */
	@Column(name = "sendaddress", columnDefinition = "varchar(64) COMMENT '配送地址'")
	private String sendAddress;

	/**
	 * 联系人
	 */
	@Column(name = "contactperson", columnDefinition = "varchar(32) COMMENT '联系人'")
	private String contactPerson;

	/**
	 * 联系电话
	 */
	@Column(name = "mobile", columnDefinition = "int(11) COMMENT '联系电话'")
	private int mobile;

	/**
	 * 流水号
	 */
	@Column(name = "serialno", columnDefinition = "int(2) COMMENT '流水号'")
	private int serialNo;

	/**
	 * 产品约定
	 */
	@Column(name = "productpromise", columnDefinition = "varchar(128) COMMENT '产品约定'")
	private String productPromise;

	/**
	 * 包装约定
	 */
	@Column(name = "packagepromise", columnDefinition = "varchar(128) COMMENT '质量约定'")
	private String packagePromise;

	/**
	 * 物流约定
	 */
	@Column(name = "logisticspromise", columnDefinition = "varchar(128) COMMENT '物流约定'")
	private String logisticsPromise;

	/**
	 * 备注
	 */
	@Column(name = "remark", columnDefinition = "varchar(128) COMMENT '备注'")
	private String remark;
	
	/**
	 * pdf文件地址
	 */
	@Column(name = "pdfpath", columnDefinition = "varchar(128) COMMENT 'pdf文件地址'")
	private String pdfPath;

	/**
	 * 采购日期
	 */
	@Column(name = "wtime", columnDefinition = "DATETIME COMMENT '采购日期'")
	private Date wtime;

	public String getBuyOrderId() {
		return buyOrderId;
	}

	public void setBuyOrderId(String buyOrderId) {
		this.buyOrderId = buyOrderId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getProductPromise() {
		return productPromise;
	}

	public void setProductPromise(String productPromise) {
		this.productPromise = productPromise;
	}

	public String getPackagePromise() {
		return packagePromise;
	}

	public void setPackagePromise(String packagePromise) {
		this.packagePromise = packagePromise;
	}

	public String getLogisticsPromise() {
		return logisticsPromise;
	}

	public void setLogisticsPromise(String logisticsPromise) {
		this.logisticsPromise = logisticsPromise;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	@Override
	public String toString() {
		return "DsBuyOrder [buyOrderId=" + buyOrderId + ", contractNo=" + contractNo + ", orderId=" + orderId
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", sendAddress=" + sendAddress
				+ ", contactPerson=" + contactPerson + ", mobile=" + mobile + ", serialNo=" + serialNo
				+ ", productPromise=" + productPromise + ", packagePromise=" + packagePromise + ", logisticsPromise="
				+ logisticsPromise + ", remark=" + remark + ", pdfPath=" + pdfPath + ", wtime=" + wtime + "]";
	}

}
