package com.dahantc.erp.flowtask.service;

import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.base.IBaseDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 账户申请流程处理
 * 
 * @author 8520
 */
@Service("applyCustomerFlowService")
public class ApplyCustomerFlowService extends BaseFlowTask {

	private static Logger logger = LogManager.getLogger(ApplyCustomerFlowService.class);

	public static final String FLOW_CLASS = Constants.APPLY_CUSTOMER_FLOW_CLASS;

	public static final String FLOW_NAME = Constants.APPLY_CUSTOMER_FLOW_NAME;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IBaseDao baseDao;

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelValue) {
		logger.info(FLOW_NAME + "检验流程信息开始");
		String result = "";
		if (erpFlow == null) {
			result = "当前流程不存在";
			return result;
		}
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		logger.info(FLOW_NAME + " 流程归档开始");
		flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
		if (erpFlow != null) {
			try {
				String customerId = flowEnt.getSupplierId();
				Customer customer = customerService.read(customerId);
				String customerTypeId = customerTypeService.getCustomerTypeIdByValue(CustomerTypeValue.INTENTION.getCode());
				if (StringUtils.isNotBlank(customerTypeId)) {
					customer.setCustomerTypeId(customerTypeId);
				}
				boolean result = customerService.update(customer, null, null, null, null);
				// 记录日志
				customerService.changeGrade(customer, userService.read(customer.getOssuserId()),
						customerTypeService.getCustomerTypeByValue(CustomerTypeValue.PUBLIC.getCode()),
						customerTypeService.getCustomerTypeByValue(CustomerTypeValue.INTENTION.getCode()),
						"客户：" + customer.getCompanyName() + "，申请客户流程归档，由公共池客户升至意向客户", false);
				// 更新产品的ossUserId
				if (result) {
					String updateProduct = "update erp_customer_product set ossuserid = ? where customerid = ?";
					baseDao.executeSqlUpdte(updateProduct, new Object[] {customer.getOssuserId(), customer.getCustomerId()});
				}
				return true;
			} catch (Exception e) {
				logger.error(erpFlow.getFlowName() + "流程归档异常，flowEntId：" + flowEnt.getId(), e);
				return false;
			}
		}
		logger.info(FLOW_NAME + " 流程归档结束");
		return true;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) {
		try {
			if (auditResult == AuditResult.CREATED.getCode()) {
				String customerId = flowEnt.getSupplierId();
				Customer customer = customerService.read(customerId);
				if (customer == null) {
					logger.info("客户不存在：" + customerId);
					return;
				}
				User user = userService.read(flowEnt.getOssUserId());
				if (user == null) {
					logger.info("销售不存在：" + flowEnt.getOssUserId());
					return;
				}
				customer.setOssuserId(user.getOssUserId());
				customer.setDeptId(user.getDeptId());
				customerService.update(customer, null, null, null, null);
			} else if (auditResult == AuditResult.CANCLE.getCode()) {
				String customerId = flowEnt.getSupplierId();
				Customer customer = customerService.read(customerId);
				if (customer == null) {
					logger.info("客户不存在：" + customerId);
					return;
				}
				customer.setOssuserId("");
				customer.setDeptId("");
				customerService.update(customer, null, null, null, null);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}
}