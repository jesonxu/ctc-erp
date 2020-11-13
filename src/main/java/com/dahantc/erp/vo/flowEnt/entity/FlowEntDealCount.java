package com.dahantc.erp.vo.flowEnt.entity;

/** 
 * @version: 
 * @Description: 未处理流程统计
 * @author: 8513
 * @date: 2019年8月22日 下午2:32:24
 */
public class FlowEntDealCount {
	
	private String supplierId;
	
	private String productId;
	
	private int flowType;

	private int entityType;

	private int year;
	
	private int month;
	
	private int flowEntCount;

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getFlowType() {
		return flowType;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getFlowEntCount() {
		return flowEntCount;
	}

	public void setFlowEntCount(int flowEntCount) {
		this.flowEntCount = flowEntCount;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}
}
