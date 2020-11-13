package com.dahantc.erp.dto.department;

import java.io.Serializable;

public class DeptInfo implements Serializable {

	private static final long serialVersionUID = 1028841849782178474L;

	private String id;

	private String name;

	private String pId;

	private String sequence;
	
	private String nodeType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	@Override
	public String toString() {
		return "DeptInfo [id=" + id + ", name=" + name + ", pId=" + pId + ", sequence=" + sequence + ", userId="
				+ nodeType + "]";
	}

}
