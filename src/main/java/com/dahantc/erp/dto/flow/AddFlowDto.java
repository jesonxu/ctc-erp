package com.dahantc.erp.dto.flow;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AddFlowDto implements Serializable {

	private static final long serialVersionUID = -7006294313002663849L;

	private String flowId;

	@NotBlank(message = "流程名称不能为空")
	private String flowName;

	@NotBlank(message = "流程实现类不能为空")
	private String flowClass;

	@NotNull(message = "流程类型不能为空")
	private int flowType;

	@NotEmpty(message = "节点不能为空")
	private List<FlowNodeDto> nodeList;

	private List<FlowLabelDto> labelList;

	private String creatorId;

	private String viewerRoleId;
	
	@NotNull(message = "绑定类型不能为空")
	private int bindType;

	@NotNull(message = "流程关联类型不能为空")
	private int associateType ;

	public int getBindType() {
		return bindType;
	}

	public void setBindType(int bindType) {
		this.bindType = bindType;
	}

	public String getFlowName() {
		return flowName;
	}

	public int getFlowType() {
		return flowType;
	}

	public List<FlowNodeDto> getNodeList() {
		return nodeList;
	}

	public List<FlowLabelDto> getLabelList() {
		return labelList;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public void setNodeList(List<FlowNodeDto> nodeList) {
		this.nodeList = nodeList;
	}

	public void setLabelList(List<FlowLabelDto> labelList) {
		this.labelList = labelList;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getFlowClass() {
		return flowClass;
	}

	public void setFlowClass(String flowClass) {
		this.flowClass = flowClass;
	}

	public String getViewerRoleId() {
		return viewerRoleId;
	}

	public void setViewerRoleId(String viewerRoleId) {
		this.viewerRoleId = viewerRoleId;
	}

	public int getAssociateType() {
		return associateType;
	}

	public void setAssociateType(int associateType) {
		this.associateType = associateType;
	}
}
