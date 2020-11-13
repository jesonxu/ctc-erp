package com.dahantc.erp.dto.flow;

import com.dahantc.erp.vo.flow.entity.ErpFlow;

public class ErpFlowUI extends ErpFlow {

	private static final long serialVersionUID = 6211730166750288448L;

	private String flowId;

	private String flowName;

	private String startNodeId;

	private String startNodeName;

	private int flowType;

	private String flowTypeDesc;

	private String extention;

	private String creatorid;

	private String creatorRealName;

	private String wTime;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getStartNodeId() {
		return startNodeId;
	}

	public void setStartNodeId(String startNodeId) {
		this.startNodeId = startNodeId;
	}

	public String getStartNodeName() {
		return startNodeName;
	}

	public void setStartNodeName(String startNodeName) {
		this.startNodeName = startNodeName;
	}

	public int getFlowType() {
		return flowType;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public String getFlowTypeDesc() {
		return flowTypeDesc;
	}

	public void setFlowTypeDesc(String flowTypeDesc) {
		this.flowTypeDesc = flowTypeDesc;
	}

	public String getExtention() {
		return extention;
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

	public String getCreatorid() {
		return creatorid;
	}

	public void setCreatorid(String creatorid) {
		this.creatorid = creatorid;
	}

	public String getCreatorRealName() {
		return creatorRealName;
	}

	public void setCreatorRealName(String creatorRealName) {
		this.creatorRealName = creatorRealName;
	}

	public String getwTime() {
		return wTime;
	}

	public void setwTime(String wTime) {
		this.wTime = wTime;
	}

}
