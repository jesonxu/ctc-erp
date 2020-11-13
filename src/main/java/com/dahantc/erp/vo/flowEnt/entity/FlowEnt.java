package com.dahantc.erp.vo.flowEnt.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.enums.PlatformType;
import com.dahantc.erp.util.StringUtil;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;

@Entity
@Table(name = "erp_flow_ent")
@DynamicUpdate(true)
public class FlowEnt implements Serializable {

	private static final long serialVersionUID = 7105662623395120372L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	/* 所属流程id */
	@Column(name = "flowid", length = 32)
	private String flowId;

	/* 所属流程类型 */
	@Column(name = "flowtype", columnDefinition = "int default 0")
	private int flowType;

	/* 标题 */
	@Column(name = "flowtitle", length = 255)
	private String flowTitle;

	/* 发起人 */
	@Column(name = "ossuserid", length = 32)
	private String ossUserId;

	/* 供应商id、客户id */
	@Column(name = "supplierid", length = 32)
	private String supplierId;

	/* 产品id */
	@Column(name = "productid", length = 32)
	private String productId;

	@Column(name = "foreignid", length = 32)
	private String foreignId;

	/* json格式的流程内容 */
	@Column(name = "flowmsg", columnDefinition = "TEXT")
	private String flowMsg;

	/* 当前节点 */
	@Column(name = "nodeid", length = 32)
	private String nodeId;

	/* 流程状态 */
	@Column(name = "flowstatus", columnDefinition = "int default 0")
	private int flowStatus = FlowStatus.NOT_AUDIT.ordinal();

	/* 备注 */
	@Column(name = "remark", length = 255)
	private String remark;

	/* 发起时间 */
	@Column(name = "wtime")
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	@Column(name = "entitytype", columnDefinition = "int default 0")
	private int entityType = EntityType.SUPPLIER.ordinal();

	@Column(name = "viewerroleid", length = 1000)
	private String viewerRoleId;

	@Column(name = "deptid", length = 32)
	private String deptId;

	@Column(name = "platform", columnDefinition = "int default 0")
	private int platform = PlatformType.PC.ordinal();

	/**
	 * 从flowMsg字段抽取账单信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ProductBillsJSONObject> getProductBillsListFromJSON() throws Exception {
		List<ProductBillsJSONObject> productBillsList = new ArrayList<>();
		JSONObject flowMsgJson = JSON.parseObject(flowMsg);
		String billLabel = flowMsgJson.getString(Constants.BILL_INFO_KEY);
		if (StringUtil.isNotBlank(billLabel)) {
			JSONArray billInfoList = JSON.parseArray(billLabel);
			Optional.ofNullable(billInfoList).orElse(new JSONArray()).forEach(billJSONObject -> {
				ProductBillsJSONObject productBills = JSON.parseObject(JSON.toJSONString(billJSONObject), ProductBillsJSONObject.class);
				productBillsList.add(productBills);
			});
		}
		return productBillsList;
	}

	public String getId() {
		return id;
	}

	public String getFlowId() {
		return flowId;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public String getFlowMsg() {
		return flowMsg;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public void setFlowMsg(String flowMsg) {
		this.flowMsg = flowMsg;
	}

	public String getForeignId() {
		return foreignId;
	}

	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}

	public int getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(int flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public String getFlowTitle() {
		return flowTitle;
	}

	public void setFlowTitle(String flowTitle) {
		this.flowTitle = flowTitle;
	}

	public int getFlowType() {
		return flowType;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public String getViewerRoleId() {
		return viewerRoleId;
	}

	public void setViewerRoleId(String viewerRoleId) {
		this.viewerRoleId = viewerRoleId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}
}
