package com.dahantc.erp.vo.customerOperate.service.entity;

import org.apache.commons.lang3.StringUtils;

import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.user.entity.User;

/**
 * 流程归档
 *
 * @author 8520
 */
public class FlowArchiveThread implements Runnable {

    /**
     * 流程任务处理
     */
    private BaseFlowTask finalTask;
    /**
     * 流程信息
     */
    private ErpFlow erpFlow;
    /**
     * 流程节点
     */
    private FlowEnt flowEnt;
    /**
     * 申请人
     */
    private User user;

    public FlowArchiveThread(BaseFlowTask finalTask, ErpFlow erpFlow, FlowEnt flowEnt, User user) {
        this.finalTask = finalTask;
        this.erpFlow = erpFlow;
        this.flowEnt = flowEnt;
        this.user = user;
    }

    @Override
    public void run() {
        finalTask.flowArchive(erpFlow, flowEnt);
        String content = "友情提醒，您发起的流程 [" + flowEnt.getFlowTitle() + "],已审批通过并归档！";
        String sendRes = WeixinMessage.sendMessageByMobile(user.getContactMobile(), content);
        if (StringUtils.isBlank(sendRes)) {
			WeixinMessage.sendMessage("", user.getOssUserId(), content);
		}
    }
}
