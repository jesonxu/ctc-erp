package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import com.dahantc.erp.enums.CheckOutStatus;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.ChargeType;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowLabelType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;
import com.dahantc.erp.vo.cashflow.service.ICashFlowService;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.noInterestAccount.INoInterestAccountService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;

@Service("paymentFlowService")
public class PaymentFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(PaymentFlowService.class);
	public static final String FLOW_CLASS = Constants.PAYMENT_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.PAYMENT_FLOW_NAME;

	@Autowired
	private IChargeRecordService chargeRecordService;

	@Autowired
	private ICashFlowService cashflowService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ISupplierService supplierService;
	
	@Autowired
	private IFlowLabelService flowLabelService;
	
	@Autowired
	private INoInterestAccountService noInterestAccountService;

	private final String ACCOUNT_KEY = "rechargeAccount";
	private final String PRICE_KEY = "price";
	private final String RECHAGE_AMOUNT_KEY = "rechargeAmount";

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
		logger.info(FLOW_NAME + " 检验流程信息开始");
		String result = "";
		if (erpFlow == null) {
			result = "当前流程不存在";
			return result;
		}
		try {
			boolean flag = assertCompoundLabel(erpFlow);
			
			if (flag) { // 客户产品 -> 客户充值流程
				return convertJsonToChargeList(labelValue, erpFlow.getFlowId(), null);
			} else {
				return convertJsonToCharge(labelValue, erpFlow.getFlowId(), null);
			}
		} catch (ServiceException e) {
			logger.error("", e);
			return "数据不合法";
		}
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	// 判断有没有用到复合标签
	private boolean assertCompoundLabel(ErpFlow erpFlow) throws ServiceException {
		SearchFilter searchFilter = new SearchFilter();
		searchFilter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, erpFlow.getFlowId()));
		List<FlowLabel> list = flowLabelService.queryAllBySearchFilter(searchFilter);
		
		boolean flag = false;
		if (!CollectionUtils.isEmpty(list)) {
			for (FlowLabel flowLabel : list) {
				if (flowLabel.getType() == FlowLabelType.RechargeDetail.ordinal()) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		logger.info(FLOW_NAME + " 流程归档开始");
		flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
		if (erpFlow != null) {
			try {
				String productId = flowEnt.getProductId();
				String supplierId = flowEnt.getSupplierId();
				String labelJsonVal = flowEnt.getFlowMsg();
				// 解析 充值账号，将账号从 不计息账号 中移除，开始计息
				removeFromInterestAccount(labelJsonVal);
				
				// 保存收支记录
				List<ChargeRecord> list = new ArrayList<>();
				// 判断是否使用了 充值详情 组合标签
				boolean flag = assertCompoundLabel(erpFlow);
				if (flag) {
					convertJsonToChargeList(labelJsonVal, erpFlow.getFlowId(), list);
				} else {
					ChargeRecord record = new ChargeRecord();
					list.add(record);
					convertJsonToCharge(labelJsonVal, erpFlow.getFlowId(), record);

				}
				
				for (ChargeRecord record : list) {
					if (flowEnt.getEntityType() == EntityType.SUPPLIER.ordinal()) {
						Supplier supplier = supplierService.read(supplierId);
						if (supplier != null) {
							record.setDeptId(supplier.getDeptId());
						}
					} else if (flowEnt.getEntityType() == EntityType.CUSTOMER.ordinal()) {
						Customer customer = customerService.read(supplierId);
						if (customer != null) {
							record.setDeptId(customer.getDeptId());
						}
					}

					record.setProductId(productId);
					record.setSupplierId(supplierId);
					record.setWtime(new Timestamp(System.currentTimeMillis()));
					record.setCreaterId(flowEnt.getOssUserId());
					record.setFlowEntId(flowEnt.getId());
					chargeRecordService.save(record);
					// 发起时增加应收/付，归档时增加已收/付
					addCashFlow(flowEnt, record.getChargePrice());
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
		logger.info(FLOW_NAME + "处理流程信息修改开始，flowEntId：" + flowEnt.getId());
		if (auditResult == AuditResult.CANCLE.getCode()) {
			// 不一定是取消流程，其他操作为了不更新现金流也可传取消的code
			logger.info("不处理信息修改");
			return;
		}
		try {
			String flowMsg = flowEnt.getFlowMsg();
			if (StringUtil.isNotBlank(flowMsg)) {
				JSONObject json = JSONObject.parseObject(flowMsg);
				if (json != null) {
					BigDecimal amountMoney = new BigDecimal(0);
					String chargePrice = json.getString(Constants.PAYMENT_MONEY_KEY);
					if (StringUtil.isBlank(chargePrice)) {
						logger.error("充值金额不能为空");
						return;
					}
					Matcher matcher = amountPattern.matcher(chargePrice);
					if (matcher.matches()) {
						amountMoney = new BigDecimal(chargePrice);
					} else {
						logger.error("充值金额只能是数字并且小数位数不能超过2位");
						return;
					}
					SearchFilter filter = new SearchFilter();
					// 客户充值取收款截止日期，供应商充值取付款截止日期
					int entityType = flowEnt.getEntityType();
					String time = null;
					if (EntityType.CUSTOMER.ordinal() == entityType) {
						time = json.getString(Constants.RECEIVABLES_END_TIME_KEY);
					} else if (EntityType.SUPPLIER.ordinal() == entityType) {
						time = json.getString(Constants.PAYMENT_END_TIME_KEY);
					}
					Date date = DateUtil.convert(time, DateUtil.format1);
					// 找到产品在 收/付款截止日期 所在月的现金流记录，没有就创建
					Timestamp startTime = new Timestamp(DateUtil.getLastMonthFinal(date).getTime());
					Timestamp endTime = new Timestamp(DateUtil.getMonthFinal(date).getTime());
					filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startTime));
					filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
					filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, flowEnt.getProductId()));
					List<CashFlow> cacheFlowList = cashflowService.queryAllBySearchFilter(filter);
					CashFlow cashFlow = null;
					boolean iscreat = true;
					if (cacheFlowList != null && !cacheFlowList.isEmpty()) {
						cashFlow = cacheFlowList.get(0);
						iscreat = false;
					} else {
						cashFlow = new CashFlow();
						cashFlow.setWtime(new Timestamp(DateUtil.getThisMonthFirst(date).getTime()));
						cashFlow.setEntityType(flowEnt.getEntityType());
						cashFlow.setEntityId(flowEnt.getSupplierId());
						cashFlow.setProductId(flowEnt.getProductId());
						if (entityType == EntityType.SUPPLIER.ordinal()) {
							Product product = productService.read(flowEnt.getProductId());
							if (product != null) {
								cashFlow.setProductType(product.getProductType());
							}
							Supplier supplier = supplierService.read(flowEnt.getSupplierId());
							if (supplier != null) {
								cashFlow.setDeptId(supplier.getDeptId());
							}
						} else if (entityType == EntityType.CUSTOMER.ordinal()) {
							CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
							if (customerProduct != null) {
								cashFlow.setProductType(customerProduct.getProductType());
							}
							Customer customer = customerService.read(flowEnt.getSupplierId());
							if (customer != null) {
								cashFlow.setDeptId(customer.getDeptId());
							}
						}
					}
					// 现金流的增减要匹配：
					// 手动发起：1发起人节点创建+；2驳回到发起人节点-；3发起人节点重新申请+
					if (auditResult == AuditResult.CREATED.getCode()) {
						// 创建流程，把收/付款金额增加到现金流的应收/付
						if (EntityType.SUPPLIER.ordinal() == entityType) {
							cashFlow.setPayables(cashFlow.getPayables().add(amountMoney));
							logger.info("创建流程，增加产品id：" + flowEnt.getProductId() + " 的应付金额：" + amountMoney);
						} else if (EntityType.CUSTOMER.ordinal() == entityType) {
							cashFlow.setReceivables(cashFlow.getReceivables().add(amountMoney));
							logger.info("创建流程，增加产品id：" + flowEnt.getProductId() + " 的应收金额：" + amountMoney);
						}
					} else if (auditResult == AuditResult.REJECTED.getCode()) {
						// 这里只处理驳回到发起人节点的驳回操作，非驳回到发起人节点的驳回操作传取消的code，直接在方法开始处返回
						// 驳回流程，把收/付款金额从现金流的应收/付减去，因为发起人节点可以取消流程，或修改金额重新申请
						if (EntityType.SUPPLIER.ordinal() == entityType && cashFlow.getPayables().compareTo(amountMoney) >= 0) {
							cashFlow.setPayables(cashFlow.getPayables().subtract(amountMoney));
							logger.info("驳回流程至发起人，减去产品id：" + flowEnt.getProductId() + " 的应付金额：" + amountMoney);
						} else if (EntityType.CUSTOMER.ordinal() == entityType && cashFlow.getReceivables().compareTo(amountMoney) >= 0) {
							cashFlow.setReceivables(cashFlow.getReceivables().subtract(amountMoney));
							logger.info("驳回流程至发起人，减去产品id：" + flowEnt.getProductId() + " 的应收金额：" + amountMoney);
						}
					} else if (auditResult == AuditResult.PASS.getCode()) {
						// 这里只处理发起人节点的通过操作，非发起人的通过操作不更新现金流
						// 通过流程，把收/付款金额增加到现金流的应收/付
						FlowNode flowNode = flowNodeService.read(flowEnt.getNodeId());
						if (flowNode != null && flowNode.getNodeIndex() == 0) {
							if (EntityType.SUPPLIER.ordinal() == entityType) {
								cashFlow.setPayables(cashFlow.getPayables().add(amountMoney));
								logger.info("发起人通过流程，增加产品id：" + flowEnt.getProductId() + " 的应付金额：" + amountMoney);
							} else if (EntityType.CUSTOMER.ordinal() == entityType) {
								cashFlow.setReceivables(cashFlow.getReceivables().add(amountMoney));
								logger.info("发起人通过流程，增加产品id：" + flowEnt.getProductId() + " 的应收金额：" + amountMoney);
							}
						}
					}
					if (iscreat) {
						cashflowService.save(cashFlow);
					} else {
						cashflowService.update(cashFlow);
					}
				}
			}
			logger.info(FLOW_NAME + "处理流程信息修改结束");
		} catch (Exception e) {
			logger.error(FLOW_NAME + "处理流程信息修改异常，flowEntId：" + flowEnt.getId(), e);
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	/**
	 * 解析json字符串，转换为充值表记录
	 */
	public String convertJsonToChargeList(String labelValue, String flowId, List<ChargeRecord> recordList) {
		String result = "";
		try {
			if (StringUtils.isNotBlank(flowId)) {
				if (recordList == null) {
					recordList = new ArrayList<>();
				}

				JSONObject lVaule = new JSONObject();
				if (StringUtils.isNotBlank(labelValue)) {
					lVaule = JSONObject.parseObject(labelValue);
				}
				if (lVaule != null) {
					// 默认预付
					int chargeType = ChargeType.ADVANCE.getCode();
					String chargeTypeStr = lVaule.getString(Constants.PAYMENT_TYPE_KEY);
					Optional<ChargeType> chargeTypeOpt = ChargeType.getEnumsByMsg(chargeTypeStr);
					if (chargeTypeOpt.isPresent()) {
						chargeType = chargeTypeOpt.get().getCode();
					}
					// 充值详情组合标签
					JSONArray arr = JSON.parseArray(lVaule.get(Constants.PAYMENT_DETAIL_KEY).toString());
					for (Object object : arr) {
						ChargeRecord record = new ChargeRecord();
						record.setChargeType(chargeType);
						
						JSONObject json = (JSONObject) object;

						String rechargeAccount = json.getString(ACCOUNT_KEY);
						String price = json.getString(PRICE_KEY);
						String rechargeAmount = json.getString(RECHAGE_AMOUNT_KEY);

						if (StringUtils.isBlank(rechargeAccount)) {
							return "账号不能为空";
						} else {
							record.setAccount(rechargeAccount);
						}

						if (StringUtil.isNotBlank(rechargeAmount)) {
							Matcher matcher = amountPattern.matcher(rechargeAmount);
							if (matcher.matches()) {
								record.setChargePrice(new BigDecimal(rechargeAmount));
							} else {
								return "充值金额只能是数字并且小数位数不能超过两位";
							}
						}

						if ((ChargeType.ADVANCE.getCode() == chargeType) || (ChargeType.PREPURCHASE.getCode() == chargeType)) {
							if (StringUtil.isNotBlank(price)) {
								Matcher matcher = pricePattern.matcher(price);
								if (matcher.matches()) {
									record.setPrice(new BigDecimal(price));
								} else {
									return "单价只能是数字并且小数位数不能超过六位";
								}
							}
						}
						
						result = verifyDate(lVaule, record);
						record.setRemark(lVaule.getString(Constants.DAHAN_REMARK_KEY));
						// 未核销
						record.setCheckOut(CheckOutStatus.NO_CHECKED.ordinal());
						recordList.add(record);
					}
				}
			}
		} catch (Exception e) {
			logger.error("封装数据异常:" + labelValue, e);
			result = "封装数据异常";
		}
		return result;
	}

	/**
	 * 解析json字符串，转换为充值表记录
	 */
	public String convertJsonToCharge(String labelValue, String flowId, ChargeRecord record) {
		String result = "";
		try {
			if (StringUtils.isNotBlank(flowId)) {
				if (record == null) {
					record = new ChargeRecord();
				}
				JSONObject lVaule = new JSONObject();
				if (StringUtils.isNotBlank(labelValue)) {
					lVaule = JSONObject.parseObject(labelValue);
				}
				if (lVaule != null) {
					// 默认预付
					int chargeType = ChargeType.ADVANCE.getCode();
					String chargeTypeStr = lVaule.getString(Constants.PAYMENT_TYPE_KEY);
					Optional<ChargeType> chargeTypeOpt = ChargeType.getEnumsByMsg(chargeTypeStr);
					if (chargeTypeOpt.isPresent()) {
						chargeType = chargeTypeOpt.get().getCode();
					}
					record.setChargeType(chargeType);
					if (ChargeType.ADVANCE.getCode() == chargeType || ChargeType.PREPURCHASE.getCode() == chargeType) {
						String price = lVaule.getString(Constants.PAYMENT_PRICE_KEY);
						if (StringUtil.isNotBlank(price)) {
							Matcher matcher = pricePattern.matcher(price);
							if (matcher.matches()) {
								record.setPrice(new BigDecimal(price));
							} else {
								return "单价只能是数字并且小数位数不能超过六位";
							}
						}
					}
					String chargePrice = lVaule.getString(Constants.PAYMENT_MONEY_KEY);
					if (StringUtil.isNotBlank(chargePrice)) {
						Matcher matcher = amountPattern.matcher(chargePrice);
						if (matcher.matches()) {
							record.setChargePrice(new BigDecimal(chargePrice));
						} else {
							return "充值金额只能是数字并且小数位数不能超过两位";
						}
					}
					result = verifyDate(lVaule, record);
					record.setRemark(lVaule.getString(Constants.DAHAN_REMARK_KEY));
				}
			}
		} catch (Exception e) {
			logger.error("封装数据异常:" + labelValue, e);
			result = "封装数据异常";
		}
		return result;
	}

	private void addCashFlow(FlowEnt flowEnt, BigDecimal bigDecimal) {
		logger.info(FLOW_NAME + " 更新现金流开始");
		try {
			String flowMsg = flowEnt.getFlowMsg();
			JSONObject json = JSONObject.parseObject(flowMsg);
			SearchFilter filter = new SearchFilter();
			String time = "";
			if (EntityType.SUPPLIER.ordinal() == flowEnt.getEntityType()) {
				time = json.getString(Constants.PAYMENT_END_TIME_KEY);
			} else {
				time = json.getString(Constants.RECEIVABLES_END_TIME_KEY);
			}
			Date date = DateUtil.convert(time, DateUtil.format1);
			Timestamp startTime = new Timestamp(DateUtil.getLastMonthFinal(date).getTime());
			Timestamp endTime = new Timestamp(DateUtil.getMonthFinal(date).getTime());
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startTime));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
			filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, flowEnt.getProductId()));
			List<CashFlow> cacheFlowList = cashflowService.queryAllBySearchFilter(filter);
			CashFlow cashFlow = null;
			boolean iscreat = true;
			if (cacheFlowList != null && !cacheFlowList.isEmpty()) {
				cashFlow = cacheFlowList.get(0);
				iscreat = false;
			} else {
				cashFlow = new CashFlow();
				cashFlow.setWtime(new Timestamp(DateUtil.getThisMonthFirst(date).getTime()));
				cashFlow.setEntityType(flowEnt.getEntityType());
				cashFlow.setEntityId(flowEnt.getSupplierId());
				cashFlow.setProductId(flowEnt.getProductId());
				if (flowEnt.getEntityType() == EntityType.SUPPLIER.ordinal()) {
					Product product = productService.read(flowEnt.getProductId());
					if (product != null) {
						cashFlow.setProductType(product.getProductType());
					}
					Supplier supplier = supplierService.read(flowEnt.getSupplierId());
					if (supplier != null) {
						cashFlow.setDeptId(supplier.getDeptId());
					}
				} else if (flowEnt.getEntityType() == EntityType.CUSTOMER.ordinal()) {
					CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
					if (customerProduct != null) {
						cashFlow.setProductType(customerProduct.getProductType());
					}
					Customer customer = customerService.read(flowEnt.getSupplierId());
					if (customer != null) {
						cashFlow.setDeptId(customer.getDeptId());
					}
				}
			}
			if (EntityType.SUPPLIER.ordinal() == flowEnt.getEntityType()) {
				cashFlow.setActualPayables(cashFlow.getActualPayables().add(bigDecimal));
			} else {
				cashFlow.setActualReceivables(cashFlow.getActualReceivables().add(bigDecimal));
			}
			if (iscreat) {
				cashflowService.save(cashFlow);
			} else {
				cashflowService.update(cashFlow);
			}
			logger.info(FLOW_NAME + " 更新现金流结束");
		} catch (Exception e) {
			logger.error(FLOW_NAME + " 更新现金流异常", e);
		}
	}

	/**
	 * 解析充值的账号目前可以解析 空格、，,、|\/#$@ 分隔 其余的暂时不解析 一次就支持一种分隔方式
	 */
	private void removeFromInterestAccount(String labelValues) {
		if (StringUtil.isBlank(labelValues)) {
			return;
		}
		JSONObject labelValueMap = JSON.parseObject(labelValues);
		String accountInfo = labelValueMap.getString(Constants.PAYMENT_ACCOUNT_KEY);
		if (StringUtil.isBlank(accountInfo)) {
			return;
		}
		String[] accounts = null;
		if (accountInfo.contains("、")) {
			accounts = accountInfo.split("、");
		} else if (accountInfo.contains(",")) {
			accounts = accountInfo.split(",");
		} else if (accountInfo.contains("，")) {
			accounts = accountInfo.split("，");
		} else if (accountInfo.contains(" ")) {
			accounts = accountInfo.split(" ");
		} else if (accountInfo.contains("|")) {
			accounts = accountInfo.split("\\|");
		} else if (accountInfo.contains("\\")) {
			accounts = accountInfo.split("\\\\");
		} else if (accountInfo.contains("/")) {
			accounts = accountInfo.split("/");
		} else if (accountInfo.contains("#")) {
			accounts = accountInfo.split("#");
		} else if (accountInfo.contains("$")) {
			accounts = accountInfo.split("$");
		} else if (accountInfo.contains("@")) {
			accounts = accountInfo.split("@");
		} else {
			accounts = new String[] { accountInfo };
		}
		List<String> validAccounts = new ArrayList<>();
		for (String account : accounts) {
			if (StringUtil.isNotBlank(account)) {
				account = account.trim();
				if (StringUtil.isNotBlank(account)) {
					validAccounts.add(account);
				}
			}
		}
		// 从不计费列表中删除
		noInterestAccountService.delete(validAccounts);
	}
}
