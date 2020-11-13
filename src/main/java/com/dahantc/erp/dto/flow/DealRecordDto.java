package com.dahantc.erp.dto.flow;

import java.io.Serializable;

/**
 * 流程处理记录
 * 
 * @author wangyang
 *
 */
public class DealRecordDto implements Serializable {

	private static final long serialVersionUID = -4629712913012130031L;

	// 处理人
	private String dealPerson;

	// 处理角色
	private String dealRole;

	// 处理时间
	private String dealTime;

	// 处理结果(通过，驳回....)
	private String auditResult;

	// 备注
	private String remark;

	// 流程内容修改的部分
	private String flowMsg;

	public String getDealPerson() {
		return dealPerson;
	}

	public void setDealPerson(String dealPerson) {
		this.dealPerson = dealPerson;
	}

	public String getDealRole() {
		return dealRole;
	}

	public void setDealRole(String dealRole) {
		this.dealRole = dealRole;
	}

	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}

	public String getAuditResult() {
		return auditResult;
	}

	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFlowMsg() {
		return flowMsg;
	}

	public void setFlowMsg(String flowMsg) {
		this.flowMsg = flowMsg;
	}
}
