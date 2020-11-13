package com.dahantc.erp.dto.search;

import java.io.Serializable;
import java.util.List;

import com.dahantc.erp.vo.flowLabel.service.impl.FlowLabelServiceImpl.LabelValue;

public class FlowEntSearchRespDto implements Serializable {

    private static final long serialVersionUID = 3510517579139337281L;

    //创建日期
    private String wtime;

    //创建人
    private String createUser;

    //流程类型
    private String flowType;

    //流程状态
    private String flowStatus;

    //流程标题
    private String flowTitle;

    //当前节点
    private String nodeName;

    //接收日期
    private String receiveTime;

    //flowMsg
    private String flowMsg;

    //当前状态
    private String nowStatus;

    //未操作者
    private String noOperator;
    
    private List<LabelValue> labelValue;

    public List<LabelValue> getLabelValue() {
		return labelValue;
	}

	public void setLabelValue(List<LabelValue> labelValue) {
		this.labelValue = labelValue;
	}

	public String getFlowMsg() {
        return flowMsg;
    }

    public void setFlowMsg(String flowMsg) {
        this.flowMsg = flowMsg;
    }

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
    }

    public String getFlowTitle() {
        return flowTitle;
    }

    public void setFlowTitle(String flowTitle) {
        this.flowTitle = flowTitle;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getNowStatus() {
        return nowStatus;
    }

    public void setNowStatus(String nowStatus) {
        this.nowStatus = nowStatus;
    }

    public String getNoOperator() {
        return noOperator;
    }

    public void setNoOperator(String noOperator) {
        this.noOperator = noOperator;
    }
}
