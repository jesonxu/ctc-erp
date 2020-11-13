package com.dahantc.erp.dto.flow;

import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;

import java.util.List;
import java.util.Set;

/**
 * 移动端 流程详情
 */
public class FlowDetail {
    /**
     * 流程id
     */
    private String flowId;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 流程类
     */
    private String flowClass;
    /**
     * 绑定的类型
     */
    private int bindType;
    /**
     * 关联类型
     */
    private Integer associateType;
    /**
     * 可编辑标签
     */
    private Set<String> editLabels;
    /**
     * 必须标签
     */
    private Set<String> mustLabels;
    /**
     * 流程标签
     */
    private List<FlowLabel> labels;


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

    public String getFlowClass() {
        return flowClass;
    }

    public void setFlowClass(String flowClass) {
        this.flowClass = flowClass;
    }

    public int getBindType() {
        return bindType;
    }

    public void setBindType(int bindType) {
        this.bindType = bindType;
    }

    public Set<String> getEditLabels() {
        return editLabels;
    }

    public void setEditLabels(Set<String> editLabels) {
        this.editLabels = editLabels;
    }

    public Set<String> getMustLabels() {
        return mustLabels;
    }

    public void setMustLabels(Set<String> mustLabels) {
        this.mustLabels = mustLabels;
    }

    public List<FlowLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<FlowLabel> labels) {
        this.labels = labels;
    }

    public Integer getAssociateType() {
        return associateType;
    }

    public void setAssociateType(Integer associateType) {
        this.associateType = associateType;
    }

    @Override
    public String toString() {
        return "FlowDetail{" +
                "flowId='" + flowId + '\'' +
                ", flowName='" + flowName + '\'' +
                ", flowClass='" + flowClass + '\'' +
                ", bindType=" + bindType +
                ", associateType=" + associateType +
                ", editLabels=" + editLabels +
                ", mustLabels=" + mustLabels +
                ", labels=" + labels +
                '}';
    }
}
