package com.dahantc.erp.dto.dsOutDepot;

import javax.validation.constraints.NotBlank;

public class DsQueryOutDepotDto {
	
	/**
	 * 出库批次号
	 */
	private String outDepotCode;
	
	/**
	 * 出库商品名称
	 */
	private String productName;
	
	/**
	 * 审核状态 0：待审核 1：审核通过 2;审核不通过
	 */
	private String verifyStatus;
	
	/**
	 * 客户id
	 */
	private String customerId;
	
	/**
	 * 客户id
	 */
	private String userId;
	
	/**
	 * 创建人名称
	 */
	private String createName;
	
	/**
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 结束时间
	 */
	private String endTime;
	
	@NotBlank(message = "每页条数不能为空")
	private String limit;
	
	@NotBlank(message = "当前页不能为空")
	private String page;

	
	
	public String getOutDepotCode() {
		return outDepotCode;
	}

	public void setOutDepotCode(String outDepotCode) {
		this.outDepotCode = outDepotCode;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getVerifyStatus() {
		return verifyStatus;
	}

	public void setVerifyStatus(String verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "DsQueryOutDepotDto [outDepotCode=" + outDepotCode + ", productName=" + productName + ", verifyStatus="
				+ verifyStatus + ", customerId=" + customerId + ", userId=" + userId + ", createName=" + createName
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", limit=" + limit + ", page=" + page + "]";
	}

}
