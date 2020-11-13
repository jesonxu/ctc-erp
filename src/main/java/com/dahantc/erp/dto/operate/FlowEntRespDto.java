package com.dahantc.erp.dto.operate;

import java.io.Serializable;

public class FlowEntRespDto implements Serializable {

	private static final long serialVersionUID = -7595813406013594985L;
	private String id;
	private String productId;
	private String flowTitle;
	private String flowStatus;
	private String nodeName;
	private String applyTime;
	private String deptName;
	private String realName;

	// 是否可操作（通过、驳回）
	private boolean canOperat;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFlowTitle() {
		return flowTitle;
	}

	public void setFlowTitle(String flowTitle) {
		this.flowTitle = flowTitle;
	}

	public String getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public boolean isCanOperat() {
		return canOperat;
	}

	public void setCanOperat(boolean canOperat) {
		this.canOperat = canOperat;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
}
