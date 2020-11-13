package com.dahantc.erp.vo.flowNode.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_flow_node")
@DynamicUpdate(true)
public class FlowNode implements Serializable {

	private static final long serialVersionUID = -5824785972073263022L;

	@Id
	@Column(name = "nodeid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String nodeId;

	@Column(name = "nodename", length = 255)
	private String nodeName;

	@Column(name = "roleid", length = 255)
	private String roleId;

	@Column(name = "flowid", length = 32)
	private String flowId;

	@Column(name = "nextnodeid", length = 32)
	private String nextNodeId;

	@Column(name = "nodeindex", columnDefinition = "int default 0")
	private int nodeIndex;

	@Column(name = "nodepermission", length = 255)
	private String nodePermission;

	/** 展示标签id集合，逗号分隔*/
	@Column(name = "viewlabelids", length = 1000)
	private String viewLabelIds;
	
	/** 可编辑标签id集合，逗号分隔*/
	@Column(name = "editlabelids", length = 1000)
	private String editLabelIds;
	
	/** 必要标签id集合，逗号分隔*/
	@Column(name = "mustlabelids", length = 1000)
	private String mustLabelIds;

	/** 流程阈值（JSON字符串 标签id 对应 viewLabelIds 里面id
	 * 存储对象参考 {@link com.dahantc.erp.dto.flow.FlowThresholdDto}
	 * */
	@Column(name = "flowthreshold", length = 2000)
	private String flowThreshold;

	/**
	 * 阈值脚本文件路径
	 */
	@Column(name = "thresholdfile")
	private String thresholdFile;

	/**
	 * 处理期限（单位小时，0表示不限）
	 */
	@Column(name = "duetime", columnDefinition = "int default 0")
	private int dueTime = 0;

	public String getNodeId() {
		return nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getRoleId() {
		return roleId;
	}

	public String getFlowId() {
		return flowId;
	}

	public String getNextNodeId() {
		return nextNodeId;
	}

	public String getNodePermission() {
		return nodePermission;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public void setNextNodeId(String nextNodeId) {
		this.nextNodeId = nextNodeId;
	}

	public void setNodePermission(String nodePermission) {
		this.nodePermission = nodePermission;
	}

	public int getNodeIndex() {
		return nodeIndex;
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

	public String getFlowThreshold() {
		return flowThreshold;
	}

	public void setFlowThreshold(String flowThreshold) {
		this.flowThreshold = flowThreshold;
	}

	public String getThresholdFile() {
		return thresholdFile;
	}

	public void setThresholdFile(String thresholdFile) {
		this.thresholdFile = thresholdFile;
	}

	public int getDueTime() {
		return dueTime;
	}

	public void setDueTime(int dueTime) {
		this.dueTime = dueTime;
	}
}
