package com.dahantc.erp.dto.dsOutDepot;

import java.math.BigDecimal;

public class DsSaveOutDepotDto {

	/**
	 * 主键id
	 */
	private String id;
	
	/**
	 * 出库批次号
	 */
	private String outDepotCode;
	
	/**
	 * 出库合计金额
	 */
	private BigDecimal outDepotTotal;
	
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
	 * 出库日期
	 */
	private String outTime;
	
	/**
	 * 出库删除状态 0：删除 1：未删除
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
	 * 客户id
	 */
	private String customerId;

	/**
	 * 客户名称
	 */
	private String customerName;
	
	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 用户名称
	 */
	private String userName;
	
	/**
	 * 出库详情
	 */
	private String DsOutDepotDetials;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDsOutDepotDetials() {
		return DsOutDepotDetials;
	}

	public void setDsOutDepotDetials(String dsOutDepotDetials) {
		DsOutDepotDetials = dsOutDepotDetials;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "DsSaveOutDepotDto [id=" + id + ", outDepotCode=" + outDepotCode + ", outDepotTotal=" + outDepotTotal
				+ ", otherCost=" + otherCost + ", verifyStatus=" + verifyStatus + ", createrId=" + createrId
				+ ", createrName=" + createrName + ", wtime=" + wtime + ", outTime=" + outTime + ", isDelete="
				+ isDelete + ", remark=" + remark + ", updateId=" + updateId + ", updateName=" + updateName
				+ ", customerId=" + customerId + ", customerName=" + customerName + ", userId=" + userId + ", userName="
				+ userName + ", DsOutDepotDetials=" + DsOutDepotDetials + "]";
	}
	
}
