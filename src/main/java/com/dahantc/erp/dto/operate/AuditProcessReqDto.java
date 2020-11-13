package com.dahantc.erp.dto.operate;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 审核流程请求参数
 * 
 * @author wangyang
 *
 */
public class AuditProcessReqDto implements Serializable {

	private static final long serialVersionUID = -3883379925913092050L;

	// 流程实体id
	@NotBlank(message = "流程实体id不能为空")
	private String flowEntId;

	// 当前审核的节点id，审核节点id与流程实体当前的节点id不一致时，说明流程已经被审核过了，本次审核失败
	private String nodeId;

	// 流程标签值集合
	@NotNull(message = "流程标签值集合不能为空")
	private String labelValueMap;
	// 流程基础信息集合
	@NotNull(message = "流程基础信息不能为空")
	private String baseDataMap;
	/**
	 * 操作类型 1-审核通过 2-审核驳回 3-保存4-取消6-撤销
	 */
	private int operateType;

	/**
	 * 存放审核过程中的处理意见等...
	 */
	private String remark;

	// 驳回到第几节点，起始0
	private String rejectToNode;

	private int platform = 0;

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getLabelValueMap() {
		return labelValueMap;
	}

	public void setLabelValueMap(String labelValueMap) {
		this.labelValueMap = labelValueMap;
	}

	public String getBaseDataMap() {
		return baseDataMap;
	}

	public void setBaseDataMap(String baseDataMap) {
		this.baseDataMap = baseDataMap;
	}

	public int getOperateType() {
		return operateType;
	}

	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRejectToNode() {
		return rejectToNode;
	}

	public void setRejectToNode(String rejectToNode) {
		this.rejectToNode = rejectToNode;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}
}
