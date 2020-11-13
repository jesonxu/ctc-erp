package com.dahantc.erp.dto.flow;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 移动端流程节点信息
 * 
 * @author 8520
 */
public class FlowEntForMobileDto implements Serializable {
	private static final long serialVersionUID = 3801378369628504821L;
	/**
	 * 流程实体id
	 */
	private String flowEntId;
	/**
	 * 流程id
	 */
	private String flowId;
	/**
	 * 流程名
	 */
	private String flowName;
	/**
	 * 流程标题
	 */
	private String flowTitle;
	/**
	 * 用户id(申请人)
	 */
	private String userId;
	/**
	 * 用户名(申请人) 后期查询
	 */
	private String userName;
	/**
	 * 流程状态
	 */
	private String flowStatus;
	/**
	 * 当前节点id
	 */
	private String nodeId;
	/**
	 * 节点名称
	 */
	private String nodeName;
	/**
	 * 创建时间（申请时间）
	 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date wtime;
	/**
	 * 是否可操作（通过、驳回）
	 */
	private boolean canOperat;
	/**
	 * 实体id（客户|供应商
	 */
	private String entityId;
	/**
	 * 客户类型
	 */
	private Integer entityType;
	/**
	 * 实体名称（客户|供应商）
	 */
	private String entityName;
	/**
	 * 产品id
	 */
	private String productId;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 流程类型
	 */
	private Integer flowType;

	public String getFlowEntId() {
		return flowEntId;
	}

	public void setFlowEntId(String flowEntId) {
		this.flowEntId = flowEntId;
	}

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

	public String getFlowTitle() {
		return flowTitle;
	}

	public void setFlowTitle(String flowTitle) {
		this.flowTitle = flowTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getWtime() {
		return wtime;
	}

	public void setWtime(Date wtime) {
		this.wtime = wtime;
	}


	public boolean isCanOperat() {
		return canOperat;
	}

	public void setCanOperat(boolean canOperat) {
		this.canOperat = canOperat;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getEntityType() {
		return entityType;
	}

	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}

	public Integer getFlowType() {
		return flowType;
	}

	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}

	public boolean setDataInfo(Object[] data) {
		if (data.length >= 16) {
			if (data[0] != null && data[0] instanceof String) {
				this.flowEntId = (String) data[0];
			}
			if (data[1] != null && data[1] instanceof String) {
				this.flowId = (String) data[1];
			}
			if (data[2] != null && data[2] instanceof String) {
				this.flowName = (String) data[2];
			}
			if (data[3] != null) {
				this.flowTitle = String.valueOf(data[3]);
			}
			if (data[4] != null && data[4] instanceof String) {
				this.userId = (String) data[4];
			}
			if (data[5] != null) {
				this.flowStatus = String.valueOf(data[5]);
			}
			if (data[6] != null && data[6] instanceof String) {
				this.nodeId = (String) data[6];
			}
			if (data[7] != null && data[7] instanceof String) {
				this.nodeName = (String) data[7];
			}
			if (data[8] != null && data[8] instanceof Date) {
				this.wtime = (Date) data[8];
			}
			if (data[9] != null) {
				this.productId = String.valueOf(data[9]);
			}
			if (data[10] != null) {
				this.canOperat = "1".equals(String.valueOf(data[10]));
			}
			if (data[11] != null) {
				this.entityId = String.valueOf(data[11]);
			}
			if (data[12] != null && data[12] instanceof Integer) {
				this.entityType = (Integer) data[12];
			}
			if (data[13] != null) {
				this.entityName = String.valueOf(data[13]);
			}
			if (data[14] != null) {
				this.entityName = String.valueOf(data[14]);
			}
			if (data[15] != null && data[15] instanceof Number) {
				this.flowType = ((Number) data[15]).intValue();
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "FlowEntForMobileDto{" +
				"flowEntId='" + flowEntId + '\'' +
				", flowId='" + flowId + '\'' +
				", flowName='" + flowName + '\'' +
				", flowTitle='" + flowTitle + '\'' +
				", userId='" + userId + '\'' +
				", userName='" + userName + '\'' +
				", flowStatus='" + flowStatus + '\'' +
				", nodeId='" + nodeId + '\'' +
				", nodeName='" + nodeName + '\'' +
				", wtime=" + wtime +
				", canOperat=" + canOperat +
				", entityId='" + entityId + '\'' +
				", entityType=" + entityType +
				", entityName='" + entityName + '\'' +
				", productId='" + productId + '\'' +
				", productName='" + productName + '\'' +
				", flowType=" + flowType +
				'}';
	}
}
