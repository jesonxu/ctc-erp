package com.dahantc.erp.vo.dsDepotHead.entity;

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
@Table(name = "erp_ds_depot_head")
@DynamicUpdate(true)
public class DsDepotHead implements Serializable {

	private static final long serialVersionUID = -7216097953593597485L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", columnDefinition = "varchar(32) COMMENT '主键id'")
	private String id;

	/**
	 * 采购批次号
	 */
	@Column(name = "depotcode", columnDefinition = "varchar(32) COMMENT '采购批次号'")
	private String depotCode;
	
	/**
	 * 采购合计金额
	 */
	@Column(name = "depotcost", columnDefinition = "Decimal(19,2) COMMENT '采购合计金额'")
	private BigDecimal depotCost;
	
	/**
	 * 其他金额
	 */
	@Column(name = "othercost", columnDefinition = "Decimal(19,2) COMMENT '其他金额'")
	private BigDecimal otherCost;
	
	/**
	 * 审核状态 0：待审核 1：审核通过 2;审核不通过
	 */
	@Column(name = "verifystatus", columnDefinition = "int(2) default 0 COMMENT '审核状态'")
	private int verifyStatus;

	/**
	 * 创建人id
	 */
	@Column(name = "createrid", columnDefinition = "varchar(32) COMMENT '创建人id'")
	private String createrId;
	
	/**
	 * 创建人名称
	 */
	@Column(name = "creatername", columnDefinition = "varchar(32) COMMENT '创建人名称'")
	private String createrName;
	
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
	 * 商品名称
	 */
	@Column(name = "productname", columnDefinition = "varchar(255) COMMENT '商品名称'")
	private String productName;
	
	/**
	 * 创建日期
	 */
	@Column(name = "wtime", columnDefinition = "DATE COMMENT '创建日期'")
	private Date wtime;
	
	/**
	 * 采购日期
	 */
	@Column(name = "buytime", columnDefinition = "DATE COMMENT '采购日期'")
	private Date buyTime;
	
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
	 * 更新人id
	 */
	@Column(name = "updateid", columnDefinition = "varchar(32) COMMENT '更新人id'")
	private String updateId;
	
	/**
	 * 更新人名称
	 */
	@Column(name = "updatename", columnDefinition = "varchar(32) COMMENT '更新人名称'")
	private String updateName;
	
	/**
	 * 审核人id
	 */
	@Column(name = "auditid", columnDefinition = "varchar(32) COMMENT '审核人id'")
	private String auditId;
	
	/**
	 * 审核人名称
	 */
	@Column(name = "auditname", columnDefinition = "varchar(32) COMMENT '审核人名称'")
	private String auditName;
	
	/**
	 * 更新日期
	 */
	@Column(name = "updatetime", columnDefinition = "DATE COMMENT '更新日期'")
	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepotCode() {
		return depotCode;
	}

	public void setDepotCode(String depotCode) {
		this.depotCode = depotCode;
	}

	public BigDecimal getDepotCost() {
		return depotCost;
	}

	public void setDepotCost(BigDecimal depotCost) {
		this.depotCost = depotCost;
	}

	public BigDecimal getOtherCost() {
		return otherCost;
	}

	public void setOtherCost(BigDecimal otherCost) {
		this.otherCost = otherCost;
	}

	public int getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public String getCreaterId() {
		return createrId;
	}

	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
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

	public Date getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(Date buyTime) {
		this.buyTime = buyTime;
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

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getAuditId() {
		return auditId;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Override
	public String toString() {
		return "DsDepotHead [id=" + id + ", depotCode=" + depotCode + ", depotCost=" + depotCost + ", otherCost="
				+ otherCost + ", verifyStatus=" + verifyStatus + ", createrId=" + createrId + ", createrName="
				+ createrName + ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", productName="
				+ productName + ", wtime=" + wtime + ", buyTime=" + buyTime + ", isDelete=" + isDelete + ", remark="
				+ remark + ", updateId=" + updateId + ", updateName=" + updateName + ", auditId=" + auditId
				+ ", auditName=" + auditName + ", updateTime=" + updateTime + "]";
	}
	
}
