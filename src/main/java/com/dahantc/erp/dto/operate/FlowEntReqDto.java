package com.dahantc.erp.dto.operate;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class FlowEntReqDto implements Serializable {
	private static final long serialVersionUID = 5860369401929726012L;

	@NotBlank(message = "产品id不能为空")
	private String productId;

	@NotBlank(message = "查询日期不能为空")
	private String date;

	private String entityType;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}
