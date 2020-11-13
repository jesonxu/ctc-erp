package com.dahantc.erp.dto.flow;

import java.io.Serializable;
import java.util.List;

public class ReadFlowNodeRspDto implements Serializable {

	private static final long serialVersionUID = -5110818337394571918L;

	private String id;

	private String roleId;

	private String name;

	private String role;

	private String dueTime;

	private String viewLabel;

	private String viewLabelId;

	private String editLabel;

	private String editLabelId;

	private String mustLabel;

	private String mustLabelId;

	/**
	 * 阈值显示内容
	 */
	private String thresholdInfos;

	/**
	 * 节点阈值
	 */
	private List<FlowThresholdDto> thresholds;
	/**
	 * 阈值脚本文件名称
	 **/
	private String thresholdFileName;
	/**
	 * 阈值脚本信息 （JSON 含文件名称和路径）
	 **/
	private String thresholdFile;

	public String getId() {
		return id;
	}

	public String getRoleId() {
		return roleId;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public String getDueTime() {
		return dueTime;
	}

	public void setDueTime(String dueTime) {
		this.dueTime = dueTime;
	}

	public String getViewLabel() {
		return viewLabel;
	}

	public String getViewLabelId() {
		return viewLabelId;
	}

	public String getEditLabel() {
		return editLabel;
	}

	public String getEditLabelId() {
		return editLabelId;
	}

	public String getMustLabel() {
		return mustLabel;
	}

	public String getMustLabelId() {
		return mustLabelId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setViewLabel(String viewLabel) {
		this.viewLabel = viewLabel;
	}

	public void setViewLabelId(String viewLabelId) {
		this.viewLabelId = viewLabelId;
	}

	public void setEditLabel(String editLabel) {
		this.editLabel = editLabel;
	}

	public void setEditLabelId(String editLabelId) {
		this.editLabelId = editLabelId;
	}

	public void setMustLabel(String mustLabel) {
		this.mustLabel = mustLabel;
	}

	public void setMustLabelId(String mustLabelId) {
		this.mustLabelId = mustLabelId;
	}

	public List<FlowThresholdDto> getThresholds() {
		return thresholds;
	}

	public void setThresholds(List<FlowThresholdDto> thresholds) {
		this.thresholds = thresholds;
	}

	public String getThresholdInfos() {
		return thresholdInfos;
	}

	public void setThresholdInfos(String thresholdInfos) {
		this.thresholdInfos = thresholdInfos;
	}

	public String getThresholdFileName() {
		return thresholdFileName;
	}

	public void setThresholdFileName(String thresholdFileName) {
		this.thresholdFileName = thresholdFileName;
	}

	public String getThresholdFile() {
		return thresholdFile;
	}

	public void setThresholdFile(String thresholdFile) {
		this.thresholdFile = thresholdFile;
	}
}
