package com.dahantc.erp.flowtask.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;

/**
 * 测试账号申请流程
 * 
 * @author 8520
 */
@Service("accountFlowService")
public class AccountFlowService extends BaseFlowTask {

	private static Logger logger = LogManager.getLogger(AccountFlowService.class);

	public static final String FLOW_CLASS = Constants.ACCOUNT_FLOW_CLASS;

	public static final String FLOW_NAME = Constants.ACCOUNT_FLOW_NAME;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerTypeService customerTypeService;

	@Autowired
	private IUserService userService;

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
				String flowMsg = flowEnt.getFlowMsg();
				if (StringUtil.isNotBlank(flowMsg)) {
					JSONObject json = JSONObject.parseObject(flowMsg);
					if (json != null) {
						String accountInfoStr = json.getString(Constants.PLATFORM_ACCOUNT_INFO);
						if (StringUtil.isNotBlank(accountInfoStr)) {
							JSONArray accountInfoArr = JSONArray.parseArray(accountInfoStr);
							if (accountInfoArr != null && accountInfoArr.size() > 0) {
								Set<String> accountInfos = new HashSet<>();
								for (int index = 0; index < accountInfoArr.size(); index++) {
									JSONObject accountInfo = accountInfoArr.getJSONObject(index);
									String account = accountInfo.getString("account");
									accountInfos.add(account);
								}
								if (!accountInfos.isEmpty()) {
									CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
									if (customerProduct != null) {
										String account = customerProduct.getAccount();
										if (StringUtil.isNotBlank(account)) {
											accountInfos.addAll(Arrays.asList(account.split("\\|")));
										}
										customerProduct.setAccount(String.join("|", accountInfos));
										customerProductService.update(customerProduct);
									}
									// 走完测试账号申请流程，更新客户类型为测试客户
									Customer customer = customerService.read(flowEnt.getSupplierId());
									if (customer != null) {
										try {
											boolean changeGrade = false;
											CustomerType nowCustomerType = customerTypeService.read(customer.getCustomerTypeId());
											CustomerType testCustomerType = customerTypeService.getCustomerTypeByValue(CustomerTypeValue.TESTING.getCode());
											// 当前客户类型不是合同、测试客户时，才更新客户的客户类型为测试客户，并写一条升级记录
											// 已经是合同、测试客户时不需要
											if (nowCustomerType.getCustomerTypeValue() != CustomerTypeValue.CONTRACTED.getCode()
													&& nowCustomerType.getCustomerTypeValue() != CustomerTypeValue.TESTING.getCode()) {
												if (null != testCustomerType) {
													customer.setCustomerTypeId(testCustomerType.getCustomerTypeId());
													changeGrade = true;
												} else {
													logger.info("获取 客户类型-测试客户 失败，无法更新客户为测试客户");
												}
											}
											boolean result = customerService.update(customer);
											logger.info("流程归档，更新客户为测试客户" + (result ? "成功" : "失败"));
											if (result && changeGrade) {
												String depict = "客户：" + customer.getCompanyName() + "，" + FLOW_NAME + "归档，由" + nowCustomerType.getCustomerTypeName() + "升至" + testCustomerType.getCustomerTypeName();
												// 写一条变更记录
												customerService.changeGrade(customer, userService.read(flowEnt.getOssUserId()), nowCustomerType,
														testCustomerType, depict, false);
											}
										} catch (Exception e) {
											logger.info("流程归档，更新客户为测试客户异常", e);
										}
									}
								}
							}
						}
					}
				}
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

	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}
}