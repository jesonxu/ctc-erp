package com.dahantc.erp.dto.flow;

import java.io.Serializable;

/**
 * 流程阈值信息
 * 
 * @author 8520
 */
public class FlowThresholdDto implements Serializable {

	private static final long serialVersionUID = 462750987833708604L;

	/**
	 * 标签id(多个逗号分隔)
	 */
	private String labelId;

	/**
	 * 关系
	 */
	private String relationship;

	/**
	 * 阈值
	 */
	private String thresholdValue;

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getThresholdValue() {
		return thresholdValue;
	}

	public void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	@Override
	public String toString() {
		return "FlowThresholdDto{" + "labelIds='" + labelId + '\'' + ", relationship='" + relationship + '\'' + ", thresholdValue='" + thresholdValue + '\''
				+ '}';
	}
}
