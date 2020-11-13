package com.dahantc.erp.dto.flow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.fasterxml.jackson.annotation.JsonFormat;


public class ReadFlowDetailRespDto implements Serializable {

	private static final long serialVersionUID = -8773016976452976236L;
	// 流程id
	private String flowId;
	// 流程实体id
	private String flowEntId;
	// 流程标题
	private String flowTitle;
	// 申请人id
	private String ossUserId;
	// 申请人名称
	private String ossUserName;
	// 对应的产品id
	private String productId;
	// 产品名称
	private String productName;
	// 供应商或客户id
	private String supplierId;
	// 公司名称
	private String supplierName;
	// 流程标签值集合
	private Map<String, String> labelValueMap = new HashMap<String, String>();
	// 流程基础信息集合
	private Map<String, String> baseDataMap = new HashMap<String, String>();
	// 申请时间
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date applyTime;

	// 流程展示标签集合
	private List<FlowLabel> labelList = new ArrayList<FlowLabel>();

	// 可编辑标签id集合，逗号分隔
	private String editLabelIds;

	// 必要标签id集合，逗号分隔
	private String mustLabelIds;

	// 是否可操作（通过、驳回）
	private boolean canOperat = false;

	// 当前节点id
	private String nodeId;

	// 当前节点index(为0是将驳回按钮改为取消)
	private int nodeIndex;

	// 流程各个节点的处理记录
	private List<DealRecordDto> record;

	private String flowClass;

	// 等待处理的角色名
	private String dealRoleName = "";

	// 等待处理的用户名字（发起人直接展示名字）
	private String dealUserName = "";

	private int flowStatus;

	private int entityType;

	private boolean canRevoke;

	private int associateType;

	public boolean isCanRevoke() {
		return canRevoke;
	}

	public void setCanRevoke(boolean canRevoke) {
		this.canRevoke = canRevoke;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowTitle() {
		return flowTitle;
	}

	public void setFlowTitle(String flowTitle) {
		this.flowTitle = flowTitle;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getOssUserName() {
		return ossUserName;
	}

	public void setOssUserName(String ossUserName) {
		this.ossUserName = ossUserName;
	}

	public Map<String, String> getLabelValueMap() {
		return labelValueMap;
	}

	public void setLabelValueMap(Map<String, String> labelValueMap) {
		this.labelValueMap = labelValueMap;
	}

	public Map<String, String> getBaseDataMap() {
		return baseDataMap;
	}

	public void setBaseDataMap(Map<String, String> baseDataMap) {
		this.baseDataMap = baseDataMap;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public List<DealRecordDto> getRecord() {
		return record;
	}

	public void setRecord(List<DealRecordDto> record) {
		this.record = record;
	}

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

	public List<FlowLabel> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<FlowLabel> labelList) {
		this.labelList = labelList;
	}

	public String getEditLabelIds() {
		return editLabelIds;
	}

	public void setEditLabelIds(String editLabelIds) {
		this.editLabelIds = editLabelIds;
	}

	public String getMustLabelIds() {
		return mustLabelIds;
	}

	public void setMustLabelIds(String mustLabelIds) {
		this.mustLabelIds = mustLabelIds;
	}

	public boolean isCanOperat() {
		return canOperat;
	}

	public void setCanOperat(boolean canOperat) {
		this.canOperat = canOperat;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public int getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public String getFlowClass() {
		return flowClass;
	}

	public void setFlowClass(String flowClass) {
		this.flowClass = flowClass;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getDealRoleName() {
		return dealRoleName;
	}

	public void setDealRoleName(String dealRoleName) {
		this.dealRoleName = dealRoleName;
	}

	public String getDealUserName() {
		return dealUserName;
	}

	public void setDealUserName(String dealUserName) {
		this.dealUserName = dealUserName;
	}

	public int getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(int flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public int getAssociateType() {
		return associateType;
	}

	public void setAssociateType(int associateType) {
		this.associateType = associateType;
	}
}
