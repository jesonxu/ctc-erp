package com.dahantc.erp.dto.flow;

import java.io.Serializable;

import com.dahantc.erp.enums.FlowLabelType;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;

public class ReadFlowLabelRspDto implements Serializable {

	private static final long serialVersionUID = -3222094428109536796L;

	private String id;

	private String flowId;

	private String name;

	private String defaultValue;

	private int type;

	private String typeName;

	public ReadFlowLabelRspDto() {
		
	}
	
	public ReadFlowLabelRspDto(FlowLabel label) {
		this.id = label.getId();
		this.flowId = label.getFlowId();
		this.name = label.getName();
		this.defaultValue = label.getDefaultValue();
		this.type = label.getType();
		this.typeName = FlowLabelType.getDescs()[this.type];
	}

	public String getId() {
		return id;
	}

	public String getFlowId() {
		return flowId;
	}

	public String getName() {
		return name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public int getType() {
		return type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
