package com.dahantc.erp.dto.flow;

import java.io.Serializable;

/**
 * 用户流程信息
 * @author 8520
 */
public class UserFlowInfoDto implements Serializable {
    /**
     * 流程实体ID
     */
    private String entId;
    /**
     * 实体名称
     */
    private String entName;
    /**
     * 创建者ID
     */
    private String entUserId;
    /**
     * 流程名
     */
    private String flowName;
    /**
     * 流程类型
     */
    private Integer flowType;
    /**
     * 流程标识 class
     */
    private String flowClass;
    /**
     * 节点角色ID
     */
    private String nodeRoleId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点序号
     */
    private Integer nodeIndex;

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getEntUserId() {
        return entUserId;
    }

    public void setEntUserId(String entUserId) {
        this.entUserId = entUserId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Integer getFlowType() {
        return flowType;
    }

    public void setFlowType(Integer flowType) {
        this.flowType = flowType;
    }

    public String getFlowClass() {
        return flowClass;
    }

    public void setFlowClass(String flowClass) {
        this.flowClass = flowClass;
    }

    public String getNodeRoleId() {
        return nodeRoleId;
    }

    public void setNodeRoleId(String nodeRoleId) {
        this.nodeRoleId = nodeRoleId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(Integer nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    @Override
    public String toString() {
        return "UserFlowInfoDto{" +
                "entId='" + entId + '\'' +
                ", entName='" + entName + '\'' +
                ", entUserId='" + entUserId + '\'' +
                ", flowName='" + flowName + '\'' +
                ", flowType=" + flowType +
                ", flowClass='" + flowClass + '\'' +
                ", nodeRoleId='" + nodeRoleId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeIndex=" + nodeIndex +
                '}';
    }
}
