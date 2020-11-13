package com.dahantc.erp.dto.modifyPrice;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

/**
 * 充值流程申请请求参数
 * 
 * @author wangyang
 *
 */
public class ChargeRecordReqDto implements Serializable {

	private static final long serialVersionUID = 7419838406606414023L;

	/**
	 * 流程id
	 */
	@NotBlank(message = "流程id不能为空")
	private String flowId;

	/**
	 * 供应商id
	 */
	@NotBlank(message = "供应商id不能为空")
	private String supplierId;

	/**
	 * 产品id
	 */
	@NotBlank(message = "产品id不能为空")
	private String productId;

	/**
	 * 标签值-json
	 */
	@NotBlank(message = "标签值不能为空")
	private String labelJsonVal;

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

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getLabelJsonVal() {
		return labelJsonVal;
	}

	public void setLabelJsonVal(String labelJsonVal) {
		this.labelJsonVal = labelJsonVal;
	}

}
