package com.dahantc.erp.flowtask.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.dahantc.erp.vo.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import org.springframework.util.CollectionUtils;

@Service("paymentPeriodService")
public class PaymentPeriodService extends BaseFlowTask {

	private static final Logger logger = LoggerFactory.getLogger(PaymentPeriodService.class);

	private Pattern pattern = Pattern.compile("[^0-9]+");

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IFlowLogService flowLogService;

	@Override
	public String getFlowClass() {
		return Constants.PAYMENT_PERIOD_FLOW_CLASS;
	}

	@Override
	public String getFlowName() {
		return Constants.PAYMENT_PERIOD_FLOW_NAME;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal) {
		// 校验 申请的流程中，新账单周期 是否已经填写 或者与原来一致
		if (StringUtil.isBlank(labelJsonVal)) {
			return "请选择新账单周期";
		}
		JSONObject flowMsgObj = null;
		try {
			flowMsgObj = JSON.parseObject(labelJsonVal);
		} catch (Exception e) {
			logger.error("账单周期修改流程，JSON数据转换异常", e);
		}
		if (flowMsgObj == null) {
			return "流程申请失败";
		}
		String newPaymentPeriod = flowMsgObj.getString(Constants.NEW_PAYMENT_PERIOD_KEY);
		if (StringUtil.isBlank(newPaymentPeriod)) {
			return "新账单周期不能为空";
		}
		String[] periodMonths = pattern.split(newPaymentPeriod);
		if (periodMonths == null || periodMonths.length < 1) {
			return "新账单周期值配置错误";
		}
		int period = Integer.parseInt(periodMonths[0]);
		CustomerProduct product = null;
		try {
			product = customerProductService.read(productId);
		} catch (ServiceException e) {
			logger.error("流程获取产品信息异常", e);
		}
		if (product == null) {
			return "流程产品获取失败";
		}
		/*if (product.getBillPeriod() == period) {
			return "新账单周期与原账单周期相同";
		}*/
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		boolean result = false;
		try {
			String productId = flowEnt.getProductId();
			if (StringUtil.isNotBlank(productId)) {
				List<CustomerProduct> customerProductList = new ArrayList<>();
				CustomerProduct customerProduct = customerProductService.read(productId);
				if (customerProduct != null) {
					String flowMsg = flowEnt.getFlowMsg();
					if (StringUtil.isNotBlank(flowMsg)) {
						JSONObject flowMsgObj = JSON.parseObject(flowMsg);
						String changeRange = flowMsgObj.getString(Constants.CHANGE_RANGE_KEY);
						// 默认改全部产品
						if (StringUtil.isBlank(changeRange) || Constants.CHANGE_RANGE_VALUE_ALL.equals(changeRange)) {
							SearchFilter filter = new SearchFilter();
							filter.getRules().add(new SearchRule("customerId", Constants.ROP_EQ, customerProduct.getCustomerId()));
							customerProductList = customerProductService.queryAllByFilter(filter);
							// 只改单个产品
						} else if (Constants.CHANGE_RANGE_VALUE_SINGLE.equals(changeRange)) {
							customerProductList.add(customerProduct);
						}
						String newPaymentPeriodStr = flowMsgObj.getString(Constants.NEW_PAYMENT_PERIOD_KEY);
						if (StringUtil.isNotBlank(newPaymentPeriodStr)) {
							String[] periods = pattern.split(newPaymentPeriodStr);
							if (periods != null && periods.length > 0) {
								int period = Integer.parseInt(periods[0]);
								if (!CollectionUtils.isEmpty(customerProductList)) {
									customerProductList = customerProductList.stream().peek(cp -> cp.setBillPeriod(period)).collect(Collectors.toList());
									result = customerProductService.updateByBatch(customerProductList);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("账单周期流程归档异常", e);
		}
		return result;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		if (auditResult == AuditResult.CREATED.getCode()) {
			// 仅在创建的时候进行
			try {
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowEnt.getFlowId()));
				searchFilter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
				searchFilter.getRules().add(new SearchRule("auditResult", Constants.ROP_EQ, AuditResult.CREATED.getCode()));
				List<FlowLog> flowLogs = flowLogService.queryAllBySearchFilter(searchFilter);
				CustomerProduct product = customerProductService.read(flowEnt.getProductId());
				if (product != null) {
					if (flowLogs != null && !flowLogs.isEmpty()) {
						FlowLog flowLog = flowLogs.get(0);
						if (StringUtil.isNotBlank(flowLog.getFlowMsg())) {
							JSONObject flowMsgObj = JSON.parseObject(flowLog.getFlowMsg());
							flowMsgObj.put(Constants.OLD_PAYMENT_PERIOD_KEY, product.getBillPeriod() + "个月");
							flowLog.setFlowMsg(flowMsgObj.toJSONString());
							flowLogService.update(flowLog);
						}
					}
				}
			} catch (ServiceException e) {
				logger.error("流程获取产品信息异常", e);
			}
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}
}
