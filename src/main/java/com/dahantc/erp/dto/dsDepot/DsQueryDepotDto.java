package com.dahantc.erp.dto.dsDepot;

import javax.validation.constraints.NotBlank;

public class DsQueryDepotDto {
	
	/**
	 * 采购批次号
	 */
	private String depotCode;
	
	/**
	 * 采购商品名称
	 */
	private String productName;
	
	/**
	 * 审核状态 0：待审核 1：审核通过 2;审核不通过
	 */
	private String verifyStatus;
	
	/**
	 * 采购供应商名称
	 */
	private String supplierId;
	
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

	public String getDepotCode() {
		return depotCode;
	}

	public void setDepotCode(String depotCode) {
		this.depotCode = depotCode;
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

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
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

	@Override
	public String toString() {
		return "DsQueryDepotDto [depotCode=" + depotCode + ", productName=" + productName + ", verifyStatus="
				+ verifyStatus + ", supplierId=" + supplierId + ", createName=" + createName + ", startTime="
				+ startTime + ", endTime=" + endTime + ", limit=" + limit + ", page=" + page + "]";
	}

}
