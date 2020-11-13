package com.dahantc.erp.dto.flow;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;

public class FlowNodeDto implements Serializable {

	private static final long serialVersionUID = 4469725619566413275L;
	
	@NotBlank(message = "节点id不能为空")
	private String nodeId;
	
	@NotBlank(message = "节点名称不能为空")
	private String nodeName;

	@NotBlank(message = "节点操作人不能为空")
	private String roleId;

	@NotNull(message = "节点位置不能为空")
	private int nodeIndex;

	/** 处理期限，单位小时*/
	private String dueTime;

	/** 展示标签id集合，逗号分隔*/
	private String viewLabelIds;

	/** 可编辑标签id集合，逗号分隔 */
	private String editLabelIds;

	/** 必要标签id集合，逗号分隔 */
	private String mustLabelIds;

	/** 流程阈值 */
	private List<FlowThresholdDto> flowThresholds;

	/** 阈值脚本文件信息{"fileName":"","filePath":""} */
	private String thresholdFile;

	public FlowNode getFlowNode() {
		FlowNode node = new FlowNode();
		node.setNodeId(nodeId);
		node.setNodeName(nodeName);
		node.setRoleId(roleId);
		node.setNodeIndex(nodeIndex);
		if (StringUtil.isNotBlank(dueTime)) {
			node.setDueTime(Integer.parseInt(dueTime));
		}
		node.setViewLabelIds(viewLabelIds);
		node.setEditLabelIds(editLabelIds);
		node.setMustLabelIds(mustLabelIds);
		if (flowThresholds != null && !flowThresholds.isEmpty()) {
			flowThresholds = flowThresholds.stream()
					.filter(flowThresholdDto-> StringUtil.isNotBlank(flowThresholdDto.getLabelId()))
					.collect(Collectors.toList());
			if (!flowThresholds.isEmpty()) {
				// 将流程阈值转换为JSON对象放入数据库
				node.setFlowThreshold(JSON.toJSONString(flowThresholds));
			}
		}
		node.setThresholdFile(thresholdFile);
		return node;
	}

	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getRoleId() {
		return roleId;
	}

	public int getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public String getViewLabelIds() {
		return viewLabelIds;
	}

	public void setViewLabelIds(String viewLabelIds) {
		this.viewLabelIds = viewLabelIds;
	}

	public String getEditLabelIds() {
		return editLabelIds;
	}

	public void setEditLabelIds(String editLabelIds) {
		this.editLabelIds = editLabelIds;
	}

	public String getMustLabelIds() {
		return mustLabelIds;
	}

	public void setMustLabelIds(String mustLabelIds) {
		this.mustLabelIds = mustLabelIds;
	}

	public List<FlowThresholdDto> getFlowThresholds() {
		return flowThresholds;
	}

	public void setFlowThresholds(List<FlowThresholdDto> flowThresholds) {
		this.flowThresholds = flowThresholds;
	}

	public String getThresholdFile() {
		return thresholdFile;
	}

	public void setThresholdFile(String thresholdFile) {
		this.thresholdFile = thresholdFile;
	}

	public String getDueTime() {
		return dueTime;
	}

	public void setDueTime(String dueTime) {
		this.dueTime = dueTime;
	}
}
