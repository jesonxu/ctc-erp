package com.dahantc.erp.vo.flow.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.BindType;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.FlowAssociateType;
import com.dahantc.erp.enums.FlowType;

@Entity
@Table(name = "erp_flow")
@DynamicUpdate(true)
public class ErpFlow implements Serializable {

	private static final long serialVersionUID = 1264036043479517618L;

	@Id
	@Column(name = "flowid", length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String flowId;

	@Column(name = "flowname", length = 255)
	private String flowName;

	@Column(name = "startnodeid", length = 32)
	private String startNodeId;

	@Column(name = "flowtype", columnDefinition = "int default 0")
	private int flowType = FlowType.OPERATE.ordinal();

	@Column(name = "extention", length = 255)
	private String extention;

	@Column(name = "creatorid", length = 32)
	private String creatorid;

	@Column(name = "wtime")
	private Timestamp wtime;

	@Column(name = "status", columnDefinition = "int default 1")
	private int status = EntityStatus.NORMAL.ordinal();

	@Column(name = "flowclass", length = 32)
	private String flowClass;

	@Column(name = "viewerroleid", length = 1000)
	private String viewerRoleId;

	@Column(name = "bindtype", columnDefinition = "int default 0")
	private int bindType = BindType.PRODUCT.ordinal();

	/**
	 * {@link FlowAssociateType} 流程关联的类型（现在对移动端起作用）
	 */
	@Column(name = "associatetype", columnDefinition = "int default 0")
	private Integer associateType = FlowAssociateType.CUSTOMER.getCode();

	public int getBindType() {
		return bindType;
	}

	public void setBindType(int bindType) {
		this.bindType = bindType;
	}

	public String getFlowId() {
		return flowId;
	}

	public String getFlowName() {
		return flowName;
	}

	public String getStartNodeId() {
		return startNodeId;
	}

	public int getFlowType() {
		return flowType;
	}

	public String getExtention() {
		return extention;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public void setStartNodeId(String startNodeId) {
		this.startNodeId = startNodeId;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
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

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFlowClass() {
		return flowClass;
	}

	public void setFlowClass(String flowClass) {
		this.flowClass = flowClass;
	}

	public String getViewerRoleId() {
		return viewerRoleId;
	}

	public void setViewerRoleId(String viewerRoleId) {
		this.viewerRoleId = viewerRoleId;
	}

	public Integer getAssociateType() {
		return associateType;
	}

	public void setAssociateType(Integer associateType) {
		this.associateType = associateType;
	}
}
