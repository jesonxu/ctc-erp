package com.dahantc.erp.dto.flow;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.dahantc.erp.dto.BaseDto;

public class ReadFlowEntDto extends BaseDto implements Serializable {

	private static final long serialVersionUID = 5424642771121192633L;
	@NotBlank(message = "产品id不能为空")
	private String productId;

	private int flowType;

	@NotBlank(message = "查询日期不能为空")
	private String date;

	private String flowStatus;

	private String entityType;

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public int getFlowType() {
		return flowType;
	}

	public String getFlowStatus() {
		return flowStatus;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

}
