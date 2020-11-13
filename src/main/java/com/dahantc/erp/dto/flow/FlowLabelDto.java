package com.dahantc.erp.dto.flow;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;

public class FlowLabelDto implements Serializable {

	private static final long serialVersionUID = -8002149118516311569L;

	@NotBlank(message = "标签id不能为空")
	private String id;
	
	@NotBlank(message = "标签名称不能为空")
	private String name;

	private String defaultValue;

	@NotNull(message = "标签值类型不能为空")
	private int type;

	@NotNull(message = "标签位置不能为空")
	private int position;

	public FlowLabel getFlowLabel() {
		FlowLabel label = new FlowLabel();
		label.setId(id);
		label.setName(name);
		label.setType(type);
		label.setPosition(position);
		label.setDefaultValue(defaultValue);
		return label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getPosition() {
		return position;
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

	public void setPosition(int position) {
		this.position = position;
	}

}
