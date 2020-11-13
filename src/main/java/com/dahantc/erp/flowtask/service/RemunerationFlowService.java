package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.cashflow.entity.CashFlow;
import com.dahantc.erp.vo.cashflow.service.ICashFlowService;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;

@Service("remunerationFlowService")
public class RemunerationFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(RemunerationFlowService.class);
	public static final String FLOW_CLASS = Constants.REMUNERATION_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.REMUNERATION_FLOW_NAME;

	private static final String UPDATE_PRODUCTBILL_SQL = "update erp_bill set actualReceivables = actualReceivables+? where id =?";

	@Autowired
	private IChargeRecordService chargeRecordService;

	@Autowired
	private IBaseDao baseDao;

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
		return convertJsonToCharge(labelValue, erpFlow.getFlowId(), null) + verifyThisRecivables(labelValue);
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
						BigDecimal amountMoney = new BigDecimal(0);
						JSONArray billArray = json.getJSONArray(Constants.BILL_INFO_KEY);
						if (billArray != null && billArray.size() > 0) {
							for (int i = 0; i < billArray.size(); i++) {
								JSONObject bill = billArray.getJSONObject(i);
								String productBillId = bill.getString("id");
								BigDecimal thisReceivables = new BigDecimal(bill.getString("thisReceivables"));
								int result = baseDao.executeSqlUpdte(UPDATE_PRODUCTBILL_SQL, new Object[] { thisReceivables, productBillId },
										new Type[] { StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.STRING });
								if (result == 0) {
									logger.error("id：" + productBillId + "的产品账单记录增加实收金额失败，增加金额：" + thisReceivables);
								}
								amountMoney = amountMoney.add(thisReceivables);
							}
						}
						if (amountMoney.doubleValue() > 0) {
							// 保存一条收支记录
							buildChargeRecord(flowEnt, erpFlow, amountMoney.setScale(2, BigDecimal.ROUND_CEILING));
							// 更新现金流的实收
							addCashFlow(flowEnt, amountMoney.setScale(2, BigDecimal.ROUND_CEILING));
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

	private void addCashFlow(FlowEnt flowEnt, BigDecimal bigDecimal) {
		logger.info(FLOW_NAME + " 更新现金流开始");
		try {
			String flowMsg = flowEnt.getFlowMsg();
			JSONObject json = JSONObject.parseObject(flowMsg);
			SearchFilter filter = new SearchFilter();
			String time = json.getString("收款截止日期");
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
			cashFlow.setActualReceivables(cashFlow.getActualReceivables().add(bigDecimal));
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
					JSONArray billArray = json.getJSONArray(Constants.BILL_INFO_KEY);
					if (billArray != null && billArray.size() > 0) {
						for (int i = 0; i < billArray.size(); i++) {
							JSONObject bill = billArray.getJSONObject(i);
							BigDecimal thisPayment = new BigDecimal(0);
							if (StringUtils.isNotBlank(bill.getString(Constants.BILL_THIS_RECEIVABLES_KEY))) {
								thisPayment = new BigDecimal(bill.getString(Constants.BILL_THIS_RECEIVABLES_KEY));
							}
							amountMoney = amountMoney.add(thisPayment);
						}
						// 找到产品在 收款截止日期 所在月的现金流记录，没有就创建
						SearchFilter filter = new SearchFilter();
						String time = json.getString(Constants.RECEIVABLES_END_TIME_KEY);
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
						// 现金流的增减要匹配：
						// 手动发起：1发起人节点创建+；2驳回到发起人节点-；3发起人节点重新申请+
						// 自动发起：1发起人节点通过+；2驳回到发起人节点-；3发起人节点重新申请+
						if (auditResult == AuditResult.CREATED.getCode()) {
							// 创建流程，把收款金额增加到现金流的应收
							cashFlow.setReceivables(cashFlow.getReceivables().add(amountMoney));
						} else if (auditResult == AuditResult.REJECTED.getCode()) {
							// 这里只处理驳回到发起人节点的驳回操作，非驳回到发起人节点的驳回操作传取消的code，直接在方法开始处返回
							// 驳回流程，把收款金额从现金流的应收减去，因为发起人节点可以取消流程，或修改金额重新申请
							if (cashFlow.getReceivables().compareTo(amountMoney) >= 0) {
								cashFlow.setReceivables(cashFlow.getReceivables().subtract(amountMoney));
							}
						} else if (auditResult == AuditResult.PASS.getCode()) {
							// 这里只处理发起人节点的通过操作，非发起人的通过操作不更新现金流
							// 通过流程，把收款金额增加到现金流的应收
							FlowNode flowNode = flowNodeService.read(flowEnt.getNodeId());
							if (flowNode != null && flowNode.getNodeIndex() == 0) {
								cashFlow.setReceivables(cashFlow.getReceivables().add(amountMoney));
							}
						} // 取消操作不更新现金流，直接在方法开始处返回
						if (iscreat) {
							cashflowService.save(cashFlow);
						} else {
							cashflowService.update(cashFlow);
						}
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

	private void buildChargeRecord(FlowEnt flowEnt, ErpFlow erpFlow, BigDecimal amountMoney) {
		try {
			ChargeRecord record = new ChargeRecord();
			convertJsonToCharge(flowEnt.getFlowMsg(), erpFlow.getFlowId(), record);
			record.setChargeType(IncomeExpenditureType.REMUNERATION.getCode());
			record.setProductId(flowEnt.getProductId());
			record.setSupplierId(flowEnt.getSupplierId());
			Supplier supplier = supplierService.read(flowEnt.getSupplierId());
			if (supplier != null) {
				record.setDeptId(supplier.getDeptId());
			}
			record.setWtime(new Timestamp(System.currentTimeMillis()));
			record.setCreaterId(flowEnt.getOssUserId());
			record.setFlowEntId(flowEnt.getId());
			record.setChargePrice(amountMoney);
			chargeRecordService.save(record);
		} catch (Exception e) {
			logger.error("保存收支记录异常", e);
		}
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
					result = verifyReceiveDate(lVaule, record);
					record.setRemark(lVaule.getString("备注"));
				}
			}
		} catch (Exception e) {
			logger.error("封装数据异常:" + labelValue, e);
			result = "封装数据异常";
		}
		return result;
	}

	private String verifyThisRecivables(String labelValue) {
		try {
			JSONObject json = null;
			if (StringUtils.isNotBlank(labelValue)) {
				json = JSONObject.parseObject(labelValue);
			}
			if (json != null) {
				JSONArray billArray = json.getJSONArray(Constants.BILL_INFO_KEY);
				if (billArray != null && billArray.size() > 0) {
					for (int i = 0; i < billArray.size(); i++) {
						JSONObject bill = billArray.getJSONObject(i);
						if (StringUtils.isNumeric(bill.getString("actualReceivables")) && StringUtils.isNumeric(bill.getString("thisReceive"))
								&& StringUtils.isNumeric(bill.getString("receivables"))) {
							BigDecimal actualReceivables = bill.getBigDecimal("actualReceivables");
							BigDecimal thisReceivables = bill.getBigDecimal("thisReceivables");
							BigDecimal receivables = bill.getBigDecimal("receivables");
							if (receivables.compareTo(actualReceivables.add(thisReceivables)) < 0) {
								return "第" + (i + 1) + "个账单,已收金额[" + actualReceivables + "]加本次收款[" + thisReceivables + "]大于应收金额[" + receivables + "]";
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("", e);
			return "验证异常";
		}
		return "";
	}
}
