package com.dahantc.erp.dto.flow;

import java.io.Serializable;

public class ReadFlowEntRspDto implements Serializable {

	private static final long serialVersionUID = 4007677360641020450L;
	private String id;
	private String flowId;
	private String flowName;
	private String flowTitle;
	private String ossUserId;
	private String userName;
	private String flowStatus;
	private String nodeId;
	private String nodeName;
	private String wtime;
	// 当前记录属于哪个年月
	private String ownMonth;
	// 是否可操作（通过、驳回）
	private boolean canOperat;
	private String productId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFlowId() {
		return flowId;
	}

	public String getFlowName() {
		return flowName;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public String getUserName() {
		return userName;
	}

	public String getFlowStatus() {
		return flowStatus;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getWtime() {
		return wtime;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getFlowTitle() {
		return flowTitle;
	}

	public void setFlowTitle(String flowTitle) {
		this.flowTitle = flowTitle;
	}

	public boolean isCanOperat() {
		return canOperat;
	}

	public void setCanOperat(boolean canOperat) {
		this.canOperat = canOperat;
	}

	public String getOwnMonth() {
		return ownMonth;
	}

	public void setOwnMonth(String ownMonth) {
		this.ownMonth = ownMonth;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}
