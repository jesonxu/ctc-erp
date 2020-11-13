package com.dahantc.erp.dto.operate;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
/**
 * 
 * @author wangyang
 *
 */
public class ApplyProcessReqDto implements Serializable {

	private static final long serialVersionUID = -3883379925913092050L;

	/**
	 * 流程id
	 */
	@NotBlank(message = "流程id不能为空")
	private String flowId;

	/**
	 * 供应商id
	 */
	private String supplierId;

	/**
	 * 产品id
	 */
//	@NotBlank(message = "产品id不能为空")
	private String productId;

	/**
	 * 标签值-json
	 */
	@NotBlank(message = "标签值不能为空")
	private String flowMsg;

	// 实体类型
	private String entityType;

	// 平台，0桌面端，1移动端
	private int platform = 0;

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

	public String getFlowMsg() {
		return flowMsg;
	}

	public void setFlowMsg(String flowMsg) {
		this.flowMsg = flowMsg;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}
}
