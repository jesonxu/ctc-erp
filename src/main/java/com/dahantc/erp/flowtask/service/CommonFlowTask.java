package com.dahantc.erp.flowtask.service;

import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;

@Service("commonFlowTask")
public class CommonFlowTask extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(CommonFlowTask.class);

	public static final String FLOW_CLASS = Constants.COMMON_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.COMMON_FLOW_NAME;

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal) {
		logger.info("不校验数据");
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
		logger.info("不做其他归档操作");
		return true;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) {
		logger.info("不处理信息变更操作");
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}
}
