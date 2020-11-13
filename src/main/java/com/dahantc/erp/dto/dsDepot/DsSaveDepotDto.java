package com.dahantc.erp.dto.dsDepot;

import java.math.BigDecimal;

public class DsSaveDepotDto {

	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 采购批次号
	 */
	private String depotCode;
	
	/**
	 * 采购合计金额
	 */
	private BigDecimal depotCost;
	
	/**
	 * 其他金额
	 */
	private BigDecimal otherCost;
	
	/**
	 * 审核状态 0：待审核 1：审核通过 2;审核不通过
	 */
	private int verifyStatus;

	/**
	 * 创建人id
	 */
	private String createrId;
	
	/**
	 * 创建人名称
	 */
	private String createrName;
	
	/**
	 * 创建日期
	 */
	private String wtime;
	
	/**
	 * 采购日期
	 */
	private String buyTime;
	
	/**
	 * 采购删除状态 0：删除 1：未删除
	 */
	private int isDelete;
	
	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 更新人id
	 */
	private String updateId;
	
	/**
	 * 更新人名称
	 */
	private String updateName;
	
	/**
	 * 供应商id
	 */
	private String supplierId;

	/**
	 * 供应商名称
	 */
	private String supplierName;
	
	/**
	 * 入库详情
	 */
	private String DsDepotItems;

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

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getBuyTime() {
		return buyTime;
	}

	public void setBuyTime(String buyTime) {
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

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	public String getDsDepotItems() {
		return DsDepotItems;
	}

	public void setDsDepotItems(String dsDepotItems) {
		DsDepotItems = dsDepotItems;
	}

	@Override
	public String toString() {
		return "DsSaveDepotDto [id=" + id + ", depotCode=" + depotCode + ", depotCost=" + depotCost + ", otherCost="
				+ otherCost + ", verifyStatus=" + verifyStatus + ", createrId=" + createrId + ", createrName="
				+ createrName + ", wtime=" + wtime + ", buyTime=" + buyTime + ", isDelete=" + isDelete + ", remark="
				+ remark + ", updateId=" + updateId + ", updateName=" + updateName + ", supplierId=" + supplierId
				+ ", supplierName=" + supplierName + ", DsDepotItems=" + DsDepotItems + "]";
	}
	
}
