package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.BusinessType;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.IncomeExpenditureType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.task.AutoWriteOffBillTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.chargeRecord.entity.ChargeRecord;
import com.dahantc.erp.vo.chargeRecord.service.IChargeRecordService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.dao.IErpFlowDao;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.dao.IFlowEntDao;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.flowNode.dao.IFlowNodeDao;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.supplier.dao.ISupplierDao;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.suppliertype.entity.SupplierType;
import com.dahantc.erp.vo.suppliertype.service.ISupplierTypeService;
import com.dahantc.erp.vo.tj.entity.CustomerProductTj;
import com.dahantc.erp.vo.tj.service.ICustomerProductTjService;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;
import com.dahantc.erp.vo.user.dao.IUserDao;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("billFlowService")
public class BillFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(BillFlowService.class);

	private static final String FLOW_CLASS = Constants.BILL_FLOW_CLASS;
	private static final String FLOW_NAME = Constants.BILL_FLOW_NAME;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IFlowNodeDao flowNodeDao;

	@Autowired
	private IFlowEntDao flowEntDao;

	@Autowired
	private IProductService productService;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IErpFlowDao erpFlowDao;

	@Autowired
	private ISupplierDao supplierDao;

	@Autowired
	private IChargeRecordService chargeRecordService;

	@Autowired
	private IUnitPriceService unitPriceService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private AutoWriteOffBillTask autoWriteOffBillTask;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISupplierTypeService supplierTypeService;

	@Autowired
	private ICustomerProductTjService customerProductTjService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
	}

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal) {
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		logger.info(FLOW_NAME + " 流程归档开始，flowEntId：" + flowEnt.getId());
		boolean result = false;
		try {
			flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
			String flowMsg = flowEnt.getFlowMsg();
			if (StringUtils.isNotBlank(flowMsg)) {
				JSONObject json = JSONObject.parseObject(flowMsg);
				ProductBills productBills = buildProductBill(flowEnt, json);
				if (productBills != null && StringUtil.isNotBlank(productBills.getId())) {
					result = true;
					boolean BILL_FLOW_BUILD_PAYMENT_KEY = "1".equals(json.getString(Constants.BILL_FLOW_BUILD_PAYMENT_KEY));// 是否自动发起账单付款
					if (BILL_FLOW_BUILD_PAYMENT_KEY) {
						buildBillPaymentFlow(flowEnt, json, productBills.getId());
					}
					boolean BILL_FLOW_BUILD_REMUNERATION_KEY = "1".equals(json.getString(Constants.BILL_FLOW_BUILD_REMUNERATION_KEY));// 是否自动发起酬金收款
					if (BILL_FLOW_BUILD_REMUNERATION_KEY) {
						buildRemunerationFlow(flowEnt, json, productBills.getId());
					}
				}
				insertDirectConnectTj(flowEnt, json);
				autoWriteOffBillTask.interrupt();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) {
		logger.info("不处理信息变更操作");
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	/** 生成产品账单记录 */
	private ProductBills buildProductBill(FlowEnt flowEnt, JSONObject flowMsg) {
		logger.info("根据账单流程生成销售账单记录开始，flowEntId：" + flowEnt.getId());
		ProductBills productBills = null;
		if (flowMsg != null) {
			try {
				boolean isInter = false;
				String yearMonth = flowMsg.getString(Constants.BILL_FLOW_MONTH_KEY);// 账单月份
				long dahanSuccessCount = 0;// 平台成功数
				if (flowMsg.containsKey(Constants.FLOW_BASE_DATA_KEY)) {
					JSONObject baseData = flowMsg.getJSONObject(Constants.FLOW_BASE_DATA_KEY);
					if (baseData != null && baseData.containsKey(Constants.DAHAN_SUCCESS_COUNT_KEY)) {
						dahanSuccessCount = baseData.getLongValue(Constants.DAHAN_SUCCESS_COUNT_KEY);
					}
				}
				long supplierSuccessCount = 0; // 供应商成功数
				BigDecimal paymentAmount = new BigDecimal(0);// 付款金额（给供应商）
				String paymentAmountStr = flowMsg.getString(Constants.PAYMENT_AMOUNT_KEY);
				if (StringUtil.isNotBlank(paymentAmountStr)) {
					String[] paymentAmountData = paymentAmountStr.split(",");
					if (paymentAmountData.length == 3) {
						supplierSuccessCount = Long.valueOf(paymentAmountData[0]);
						paymentAmount = new BigDecimal(paymentAmountData[2]).setScale(2, BigDecimal.ROUND_CEILING);
					} else {// 国际账单流程
						isInter = true;
						if (paymentAmountData.length == 1) {
							paymentAmount = new BigDecimal(paymentAmountStr);
						}
						if (flowMsg.containsKey(Constants.SUPPLIER_SUCCESS_COUNT_KEY)) {
							supplierSuccessCount = flowMsg.getLongValue(Constants.SUPPLIER_SUCCESS_COUNT_KEY);
						}
					}
				}
				double remuneration = 0;// 酬金（从运营商）
				String remunerationStr = flowMsg.getString(Constants.REMUNERATION_KEY);
				if (StringUtil.isNotBlank(remunerationStr)) {
					String[] remunerationData = remunerationStr.split(",");
					if (remunerationData.length == 5) {
						remuneration = new Double(remunerationData[4]);
					}
				}
				// 付款截止日期
				Timestamp payEndTime = null;
				String payEndTimeStr = flowMsg.getString(Constants.PAYMENT_END_TIME_KEY);
				if (StringUtil.isNotBlank(payEndTimeStr)) {
					Date payEndDate = DateUtil.convert(payEndTimeStr, DateUtil.format1);
					if (payEndDate != null) {
						payEndTime = new Timestamp(payEndDate.getTime());
					}
				}
				// 收款截止日期
				String receiveEndTimeStr = flowMsg.getString(Constants.RECEIVABLES_END_TIME_KEY);
				Timestamp receiveEndTime = null;
				if (StringUtil.isNotBlank(receiveEndTimeStr)) {
					Date receiveEndDate = DateUtil.convert(receiveEndTimeStr, DateUtil.format1);
					if (receiveEndDate != null) {
						receiveEndTime = new Timestamp(receiveEndDate.getTime());
					}
				}
				// 查询充值记录
				BigDecimal chargeAmount = new BigDecimal(0);// 充值金额（即实付金额）
				Timestamp startDate = new Timestamp(DateUtil.convert(yearMonth + "-01", DateUtil.format1).getTime());
				Timestamp endDate = new Timestamp(DateUtil.getMonthFinal(startDate).getTime());
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, flowEnt.getProductId()));
				filter.getRules().add(new SearchRule("chargeType", Constants.ROP_IN,
						new Object[] { IncomeExpenditureType.PREPURCHASE.ordinal(), IncomeExpenditureType.ADVANCE.ordinal() }));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
				filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endDate));
				List<ChargeRecord> chargeRecordList = chargeRecordService.queryAllBySearchFilter(filter);
				if (chargeRecordList != null && chargeRecordList.size() > 0) {
					for (ChargeRecord chargeRecord : chargeRecordList) {
						if (chargeRecord.getChargePrice() != null) {
							chargeAmount = chargeAmount.add(chargeRecord.getChargePrice());
						}
					}
				}
				productBills = new ProductBills();
				productBills.setEntityId(flowEnt.getSupplierId());
				productBills.setProductId(flowEnt.getProductId());
				productBills.setPlatformCount(dahanSuccessCount);
				productBills.setSupplierCount(supplierSuccessCount);
				productBills.setPayables(paymentAmount);
				productBills.setReceivables(new BigDecimal(remuneration).setScale(2, BigDecimal.ROUND_CEILING));
				productBills.setActualPayables(chargeAmount.setScale(2, BigDecimal.ROUND_CEILING));
				productBills.setActualReceivables(new BigDecimal(0));
				productBills.setWtime(startDate);
				productBills.setEntityType(EntityType.SUPPLIER.ordinal()); // 供应商
				productBills.setFinalPayTime(payEndTime);
				productBills.setFinalReceiveTime(receiveEndTime);
				productBills.setFlowEntId(flowEnt.getId());
				productBills.setBillStatus(BillStatus.RECONILED.ordinal());
				Supplier supplier = supplierService.read(flowEnt.getSupplierId());
				if (supplier != null) {
					productBills.setDeptId(supplier.getDeptId());
				}
				JSONObject json = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
				JSONObject baseDataJson = JSON.parseObject(json.get(Constants.FLOW_BASE_DATA_KEY).toString());
				if (StringUtils.isNotBlank(baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY))) {
					productBills.setBillNumber(baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY));
				}
				if (productBillsService.save(productBills)) {
					if (!isInter) {
						updateUnitPrice(productBills, startDate, endDate);
					}
					logger.info("根据账单流程生成销售账单记录成功");
					logger.info("账单信息：" + productBills.toString());
				} else {
					productBills = null;
				}
			} catch (Exception e) {
				logger.error("根据账单流程生成销售账单记录异常，flowEntId：" + flowEnt.getId(), e);
				productBills = null;
			}
		}
		return productBills;
	}

	/** 更新单价 */
	private void updateUnitPrice(ProductBills productBills, Timestamp startDate, Timestamp endDate) {
		try {
			String productId = productBills.getProductId();

			double price = getPrice(productBills);
			if (price <= 0) {
				return;
			}
			UnitPrice unitPrice = null;
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("basicsId", Constants.ROP_EQ, productId));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
			filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endDate));
			filter.getRules().add(new SearchRule("countryCode", Constants.ROP_EQ, Constants.CHINA_COUNTRY_CODE));
			List<UnitPrice> list = unitPriceService.queryAllBySearchFilter(filter);
			if (list != null && !list.isEmpty()) {
				unitPrice = list.get(0);
				unitPrice.setUnitPrice(new BigDecimal(price));
				unitPriceService.update(unitPrice);
			} else {
				unitPrice = new UnitPrice();
				unitPrice.setBasicsId(productId);
				unitPrice.setCountryCode(Constants.CHINA_COUNTRY_CODE);
				unitPrice.setEntityType(productBills.getEntityType());
				unitPrice.setUnitPrice(new BigDecimal(price));
				unitPrice.setWtime(startDate);
				unitPriceService.save(unitPrice);
			}
		} catch (ServiceException e) {
			logger.error("账单归档保存单价异常", e);
		}

	}

	/** 创建账单付款流程 */
	private void buildBillPaymentFlow(FlowEnt settleFlow, JSONObject flowMsg, String productBillId) {
		String flowId = null;
		int flowType = 0;
		String viewerRoleId = null;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.BILL_PAYMENT_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowDao.queryAllBySearchFilter(filter);

			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
			} else {
				logger.error("系统无" + Constants.BILL_PAYMENT_FLOW_NAME + "，" + settleFlow.getFlowTitle() + " 创建" + Constants.BILL_PAYMENT_FLOW_NAME + "失败");
				return;
			}
			String dateMonth = flowMsg.getString(Constants.BILL_FLOW_MONTH_KEY); // 月份
			String productId = settleFlow.getProductId();
			String supplierId = settleFlow.getSupplierId();
			Product product = productService.read(productId);
			Supplier supplier = supplierDao.read(supplierId);
			User user = userDao.read(product.getOssUserId());
			String title = Constants.buildBillPaymentFlowTitle(supplier.getCompanyName(), product.getProductName(), dateMonth);
			FlowEnt flowEnt = buildFlowEnt(title, flowId, flowType, user.getOssUserId(), productId, supplierId, viewerRoleId);
			flowEnt.setDeptId(user.getDeptId());
			BigDecimal paymentAmount = new BigDecimal(0);// 付款金额（给供应商）
			String paymentAmountStr = flowMsg.getString(Constants.PAYMENT_AMOUNT_KEY);
			if (StringUtil.isNotBlank(paymentAmountStr)) {
				String[] paymentAmountData = paymentAmountStr.split(",");
				if (paymentAmountData.length == 3) {
					paymentAmount = new BigDecimal(paymentAmountData[2]).setScale(2, BigDecimal.ROUND_CEILING);
				} else {// 国际账单流程
					paymentAmount = new BigDecimal(paymentAmountStr).setScale(2, BigDecimal.ROUND_CEILING);
				}
			}
			JSONObject json = new JSONObject();
			JSONArray billInfoArray = new JSONArray();
			JSONObject bill = new JSONObject();
			bill.put("actualpayables", "0.00");
			bill.put("id", productBillId);
			bill.put("payables", paymentAmount);
			bill.put("thisPayment", paymentAmount);
			bill.put("title", Constants.buildProductBillTitle(supplier.getCompanyName(), product.getProductName(), dateMonth));
			billInfoArray.add(bill);
			json.put(Constants.BILL_INFO_KEY, billInfoArray);
			json.put("备注", "流程自动发起");
			flowEnt.setDeptId(supplier.getDeptId());
			flowEnt.setFlowMsg(json.toJSONString());
			boolean result = flowEntDao.save(flowEnt);
			if (result) {
				User admin = userService.findAdmin();
				String auditUserId = admin != null ? admin.getOssUserId() : flowEnt.getOssUserId();
				// 生成日志记录
				FlowLog flowLog = new FlowLog();
				flowLog.setFlowId(flowEnt.getFlowId());
				flowLog.setFlowEntId(flowEnt.getId());
				flowLog.setAuditResult(AuditResult.CREATED.getCode());
				flowLog.setNodeId(flowEnt.getNodeId());
				flowLog.setOssUserId(auditUserId);
				flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
				flowLog.setRemark("");
				flowLog.setFlowMsg(json.toJSONString());
				flowLogService.save(flowLog);
			}
			logger.info("生成'" + title + "'" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("生成'" + Constants.BILL_PAYMENT_FLOW_NAME + "'异常：", e);
		}
	}

	/** 创建酬金流程 */
	private void buildRemunerationFlow(FlowEnt settleFlow, JSONObject flowMsg, String productBillId) {
		String flowId = null;
		int flowType = 0;
		String viewerRoleId = null;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.REMUNERATION_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowDao.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
			} else {
				logger.error("系统无" + Constants.REMUNERATION_FLOW_NAME + "，" + settleFlow.getFlowTitle() + " 创建" + Constants.REMUNERATION_FLOW_NAME + "失败");
				return;
			}
			String dateMonth = flowMsg.getString(Constants.BILL_FLOW_MONTH_KEY); // 月份
			String productId = settleFlow.getProductId();
			String supplierId = settleFlow.getSupplierId();
			Product product = productService.read(productId);
			Supplier supplier = supplierDao.read(supplierId);
			User user = userDao.read(product.getOssUserId());
			String title = Constants.buildRemunerationFlowTitle(supplier.getCompanyName(), product.getProductName(), dateMonth);
			FlowEnt flowEnt = buildFlowEnt(title, flowId, flowType, user.getOssUserId(), productId, supplierId, viewerRoleId);
			flowEnt.setDeptId(user.getDeptId());
			BigDecimal remuneration = new BigDecimal(0);// 酬金（即应收金额）
			String remunerationStr = flowMsg.getString(Constants.REMUNERATION_KEY);
			if (StringUtil.isNotBlank(remunerationStr) && remunerationStr.split(",").length == 5) {
				remuneration = new BigDecimal(remunerationStr.substring(remunerationStr.lastIndexOf(",") + 1, remunerationStr.length())).setScale(2,
						BigDecimal.ROUND_CEILING);
			}
			JSONObject json = new JSONObject();
			JSONArray billInfoArray = new JSONArray();
			JSONObject bill = new JSONObject();
			bill.put("actualReceivables", "0.00");
			bill.put("id", productBillId);
			bill.put("receivables", remuneration);
			bill.put("thisReceivables", remuneration);
			bill.put("title", Constants.buildProductBillTitle(supplier.getCompanyName(), product.getProductName(), dateMonth));
			billInfoArray.add(bill);
			json.put(Constants.BILL_INFO_KEY, billInfoArray);
			json.put("备注", "流程自动发起");
			flowEnt.setDeptId(supplier.getDeptId());
			flowEnt.setFlowMsg(json.toJSONString());
			boolean result = flowEntDao.save(flowEnt);
			if (result) {
				User admin = userService.findAdmin();
				String auditUserId = admin != null ? admin.getOssUserId() : flowEnt.getOssUserId();
				// 生成日志记录
				FlowLog flowLog = new FlowLog();
				flowLog.setFlowId(flowEnt.getFlowId());
				flowLog.setFlowEntId(flowEnt.getId());
				flowLog.setAuditResult(AuditResult.CREATED.getCode());
				flowLog.setNodeId(flowEnt.getNodeId());
				flowLog.setOssUserId(auditUserId);
				flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
				flowLog.setRemark("");
				flowLog.setFlowMsg(json.toJSONString());
				flowLogService.save(flowLog);
			}
			logger.info("生成'" + title + "'" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("生成'" + Constants.REMUNERATION_FLOW_NAME + "'异常：", e);
		}
	}

	private FlowEnt buildFlowEnt(String title, String flowId, int flowType, String ossUserId, String productId, String supplierId, String viewerRoleId)
			throws Exception {
		FlowEnt flowEnt = new FlowEnt();
		flowEnt.setFlowTitle(title);
		flowEnt.setFlowId(flowId);
		flowEnt.setFlowType(flowType);
		flowEnt.setOssUserId(ossUserId);
		flowEnt.setSupplierId(supplierId);
		flowEnt.setProductId(productId);
		flowEnt.setSupplierId(supplierId);
		flowEnt.setViewerRoleId(viewerRoleId);
		flowEnt.setWtime(new Timestamp(System.currentTimeMillis()));

		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
		filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
		List<FlowNode> nodeList = flowNodeDao.queryAllBySearchFilter(filter);
		if (nodeList != null && nodeList.size() > 0) {
			FlowNode flowNode = nodeList.get(0);
			// 流程创建到初始节点
			flowEnt.setNodeId(flowNode.getNodeId());
		}
		return flowEnt;
	}

	private double getPrice(ProductBills productBills) {
		if (productBills.getPayables().signum() > 0) {
			if (productBills.getSupplierCount() > 0) {
				return productBills.getPayables().divide(new BigDecimal(productBills.getSupplierCount()), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {
				return productBills.getPayables().divide(new BigDecimal(productBills.getPlatformCount()), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		}
		return -1;
	}

	/**
	 * 供应商账单流程归档之后插入数据到客户统计明细表
	 *
	 * @param flowEnt
	 *            流程实体
	 * @param flowMsgJson
	 *            流程标签内容
	 */
	private void insertDirectConnectTj(FlowEnt flowEnt, JSONObject flowMsgJson) {
		boolean direct = false;
		Supplier supplier = null;
		String supplierId = flowEnt.getSupplierId();
		String productId = flowEnt.getProductId();
		Product product = null;
		try {
			product = productService.read(productId);
			if (product != null) {
				direct = product.getDirectConnect();
			}
			supplier = supplierService.read(supplierId);
			if (supplier == null) {
				logger.info("供应商不存在，supplierId：" + supplierId);
				return;
			}
		} catch (Exception e) {
			logger.error("查询供应商产品异常，productId：" + productId);
		}
		if (!direct)
			return;
		CustomerProductTj insertData = new CustomerProductTj();
		insertData.setChannelId(product.getProductMark());
		insertData.setLoginName(product.getProductMark());
		insertData.setProductType(product.getProductType());
		long supplierSuccessCount = 0; // 实际成功数->供应商成功数
		BigDecimal paymentAmount = new BigDecimal(0);// 实际付款金额
		if (flowMsgJson.containsKey(Constants.RECEIVABLES_AMOUNT_KEY)) {
			String actuallyDataStr = flowMsgJson.getString(Constants.RECEIVABLES_AMOUNT_KEY); // 实际账单金额
			if (StringUtil.isNotBlank(actuallyDataStr)) {
				String[] actuallyData = actuallyDataStr.split(",");
				if (actuallyData.length == 3) { // 账单流程的实际账单金额（成功数，单价，金额）
					supplierSuccessCount = Long.parseLong(actuallyData[0]);
					paymentAmount = new BigDecimal(actuallyData[2]).setScale(2, BigDecimal.ROUND_CEILING);
				} else { // 销售国际账单的实际账单金额
					// isInter = true;
					if (actuallyData.length == 1) { // 账单流程的实际账单金额（只有一个值是金额）
						paymentAmount = new BigDecimal(actuallyDataStr).setScale(2, BigDecimal.ROUND_CEILING);
					}
					if (flowMsgJson.containsKey(Constants.SUPPLIER_SUCCESS_COUNT_KEY)) {
						supplierSuccessCount = flowMsgJson.getLongValue(Constants.SUPPLIER_SUCCESS_COUNT_KEY);
					}
				}
			}
		}
		insertData.setTotalCount(supplierSuccessCount);
		insertData.setFailCount(0);
		insertData.setSuccessCount(supplierSuccessCount);
		String billMonth = flowMsgJson.getString(Constants.BILL_FLOW_MONTH_KEY);
		Date statsDate = DateUtil.convert(billMonth, DateUtil.format4);
		insertData.setStatsDate(new Timestamp(statsDate.getTime()));
		int yysType = 0;
		String supplierTypeId = supplier.getSupplierTypeId();
		try {
			SupplierType supplierType = supplierTypeService.read(supplierTypeId);
			if (supplierType != null) {
				yysType = supplierType.getSequence();
			}
		} catch (ServiceException e) {
			logger.error("获取供应商类型异常，supplierTypeId：" + supplierTypeId, e);
		}
		insertData.setYysType(yysType);
		insertData.setCountryCode(Constants.CHINA_COUNTRY_CODE);
		insertData.setBusinessType(BusinessType.YTX.ordinal());
		BigDecimal costPrice = new BigDecimal("0.0000");
		if (supplierSuccessCount > 0) {
			costPrice = paymentAmount.divide(new BigDecimal(supplierSuccessCount), 4, BigDecimal.ROUND_HALF_UP);
		}
		insertData.setCostPrice(costPrice);
		insertData.setDataSource("1");
		boolean result = false;
		try {
			result = customerProductTjService.save(insertData);
			logger.info("插入直连通道数据到客户统计明细表成功，产品标识：" + product.getProductMark() + "，产品类型：" + product.getProductType() + "，平台成功数：" + supplierSuccessCount);
		} catch (Exception e) {
			logger.error("插入直连通道数据到客户统计明细表异常，flowEntId：" + flowEnt.getId(), e);
		}
		if (result) {
			buildCustomerBill(product.getProductMark(), product.getProductType(), statsDate);
		}
	}

	/**
	 * 发起销售账单流程
	 *
	 * @return
	 */
	private void buildCustomerBill(String productMark, int productType, Date billMonth) {
		logger.info("直连通道账单流程归档，自动生成直连客户产品的账单开始");
		CustomerProduct product = null;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("account", Constants.ROP_EQ, productMark));
			filter.getRules().add(new SearchRule("productType", Constants.ROP_EQ, productType));
			List<CustomerProduct> productList = customerProductService.queryAllByFilter(filter);
			if (CollectionUtils.isEmpty(productList)) {
				logger.info("直连通道：" + productMark + " ，产品类型：" + productType + " 没有对应的直连客户产品");
				return;
			}
			product = productList.get(0);
		} catch (Exception e) {
			logger.error("查询直连通道：" + productMark + " ，产品类型：" + productType + " 对应的直连客户产品异常", e);
			return;
		}
		logger.info("自动生成销售账单开始，productId： " + product.getProductId() + "账单月份：" + DateUtil.convert(billMonth, DateUtil.format4));
		BaseResponse<ProductBills> resp = productBillsService.buildCustomerBill(product.getProductId(), DateUtil.convert(billMonth, DateUtil.format4), true, true);
		ProductBills bill = resp.getData();
		if (null != bill) {
			List<ProductBills> billList = new ArrayList<>();
			billList.add(bill);
			productBillsService.buildCheckBillFlow(billList);
		}
	}
}
