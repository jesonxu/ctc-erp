package com.dahantc.erp.vo.flowLog.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dahantc.erp.enums.PlatformType;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "erp_flow_log")
@DynamicUpdate(true)
public class FlowLog implements Serializable {

	private static final long serialVersionUID = -3491508035430892883L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column(name = "flowid", length = 32)
	private String flowId;

	@Column(name = "flowentid", length = 32)
	private String flowEntId;

	@Column(name = "nodeid", length = 32)
	private String nodeId;

	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	/** 审核状态 */
	@Column(name = "auditresult", columnDefinition = "int default 0")
	private int auditResult;

	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	/* 流程内容修改的部分 */
	@Column(name = "flowmsg", columnDefinition = "TEXT")
	private String flowMsg;

	/** 备注 */
	@Column(name = "remark", length = 2000)
	private String remark;

	@Column(name = "roleid", length = 32)
	private String roleId;

	// 桌面端or移动端审核
	@Column(name = "platform", columnDefinition = "int default 0")
	private int platform = PlatformType.PC.ordinal();

	public String getId() {
		return id;
	}

	public String getFlowId() {
		return flowId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public int getAuditResult() {
		return auditResult;
	}

	public String getRemark() {
		return remark;
	}

	public void setAuditResult(int auditResult) {
		this.auditResult = auditResult;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getFlowMsg() {
		return flowMsg;
	}

	public void setFlowMsg(String flowMsg) {
		this.flowMsg = flowMsg;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}
}
