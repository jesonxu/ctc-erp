package com.dahantc.erp.vo.flowLabel.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.FlowLabelType;

@Entity
@Table(name = "erp_flow_label")
@DynamicUpdate(true)
public class FlowLabel implements Serializable {

	private static final long serialVersionUID = -4163923744889943057L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "flowid", length = 32)
	private String flowId;

	@Column(name = "name", length = 32)
	private String name;

	@Column(name = "defaultvalue", length = 1500)
	private String defaultValue;

	/** 数据类型 */
	@Column(name = "type")
	private int type = FlowLabelType.String.ordinal();

	@Column(name = "position", columnDefinition = "int default 0")
	private int position;

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

	public int getPosition() {
		return position;
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

	public void setPosition(int position) {
		this.position = position;
	}

}
