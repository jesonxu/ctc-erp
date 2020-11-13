package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.PlatformType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.FlowTaskManager;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.PriceType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.deductionPrice.service.IDeductionPriceService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productBills.entity.OperateCostDetail;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.unitPrice.entity.UnitPrice;
import com.dahantc.erp.vo.unitPrice.service.IUnitPriceService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("customerBillFlowService")
public class CustomerBillFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(CustomerBillFlowService.class);

	private static final String FLOW_CLASS = Constants.CUSTOMER_BILL_FLOW_CLASS;
	private static final String FLOW_NAME = Constants.CUSTOMER_BILL_FLOW_NAME;
	private static final String operareCostKeyPrefix = "unified_operate_single_cost_";

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBaseDao baseDao;
	@Autowired
	private IUnitPriceService unitPriceService;

	@Autowired
	private IModifyPriceService modifyPriceService;

	@Autowired
	private IDeductionPriceService deductionPriceService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private CommonFlowTask commonFlowTask;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IParameterService parameterService;

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
				// 生成账单记录
				ProductBills productBills = buildProductBill(flowEnt, json);
				if (productBills != null && StringUtil.isNotBlank(productBills.getId())) {
					result = true;
					// 发起账单收款流程
					boolean BILL_FLOW_BUILD_RECEIVABLES_KEY = "1".equals(json.getString(Constants.BILL_FLOW_BUILD_RECEIVABLES_KEY));
					if (BILL_FLOW_BUILD_RECEIVABLES_KEY) {
						buildBillReceivablesFlow(flowEnt, json, productBills.getId());
					}
				}
			}
		} catch (Exception e) {
			logger.info("销售账单流程归档异常", e);
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

	/**
	 * 生成销售账单记录
	 */
	private ProductBills buildProductBill(FlowEnt flowEnt, JSONObject flowMsg) {
		logger.info("根据账单流程生成销售账单记录开始，flowEntId：" + flowEnt.getId());
		ProductBills productBills = null;
		if (flowMsg != null) {
			try {
				boolean isInter = false; // 是否国际账单
				String yearMonth = flowMsg.getString(Constants.BILL_FLOW_MONTH_KEY);// 账单月份
				long dahanSuccessCount = 0;// 平台成功数
				if (flowMsg.containsKey(Constants.FLOW_BASE_DATA_KEY)) {
					JSONObject baseData = flowMsg.getJSONObject(Constants.FLOW_BASE_DATA_KEY);
					if (baseData != null && baseData.containsKey(Constants.DAHAN_SUCCESS_COUNT_KEY)) {
						dahanSuccessCount = baseData.getLongValue(Constants.DAHAN_SUCCESS_COUNT_KEY);
					}
				}
				long customerSuccessCount = 0; // 实际成功数->客户成功数
				BigDecimal receivablesAmount = new BigDecimal(0);// 实际收款金额
				String actuallyDataStr = flowMsg.getString(Constants.RECEIVABLES_AMOUNT_KEY); // 实际账单金额
				if (StringUtil.isNotBlank(actuallyDataStr)) {
					String[] actuallyData = actuallyDataStr.split(",");
					if (actuallyData.length == 3) { // 销售账单流程的实际账单金额（成功数，单价，金额）
						customerSuccessCount = Long.parseLong(actuallyData[0]);
						receivablesAmount = new BigDecimal(actuallyData[2]).setScale(2, BigDecimal.ROUND_CEILING);
					} else { // 销售国际账单的实际账单金额
						isInter = true;
						if (actuallyData.length == 1) {
							receivablesAmount = new BigDecimal(actuallyDataStr).setScale(2, BigDecimal.ROUND_CEILING);
						}
						if (flowMsg.containsKey(Constants.CUSTOMER_SUCCESS_COUNT_KEY)) {
							customerSuccessCount = flowMsg.getLongValue(Constants.CUSTOMER_SUCCESS_COUNT_KEY);
						}
					}
				}
				// 查询充值记录
				Timestamp startDate = new Timestamp(DateUtil.convert(yearMonth + "-01", DateUtil.format1).getTime());
				Timestamp endDate = new Timestamp(DateUtil.getMonthFinal(startDate).getTime());

				productBills = new ProductBills();

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

				CustomerProduct cusProduct = customerProductService.read(flowEnt.getProductId());
				if (receiveEndTime == null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(startDate);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.add(Calendar.MONTH, cusProduct.getBillPeriod() + 1);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					receiveEndTime = new Timestamp(cal.getTimeInMillis());
				}

				productBills.setBillNumber(flowMsg.getString(Constants.DAHAN_BILL_NUM_KEY));
				productBills.setEntityId(flowEnt.getSupplierId());
				productBills.setProductId(flowEnt.getProductId());
				productBills.setPlatformCount(dahanSuccessCount);
				productBills.setSupplierCount(customerSuccessCount);
				// 应付金额
				productBills.setPayables(new BigDecimal(0));
				// 应收金额
				productBills.setReceivables(receivablesAmount);
				// 实付金额
				productBills.setActualPayables(new BigDecimal(0));
				// 实收金额
				productBills.setActualReceivables(BigDecimal.ZERO);
				productBills.setWtime(startDate);
				productBills.setEntityType(EntityType.CUSTOMER.ordinal()); // 客户
				productBills.setFinalPayTime(payEndTime);
				productBills.setFinalReceiveTime(receiveEndTime);
				productBills.setFlowEntId(flowEnt.getId());
				productBills.setBillStatus(BillStatus.RECONILED.ordinal());
				JSONObject json = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
				JSONObject baseDataJson = JSON.parseObject(json.get(Constants.FLOW_BASE_DATA_KEY).toString());
				if (StringUtils.isNotBlank(baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY))) {
					productBills.setBillNumber(baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY));
				}
				Customer cus = customerService.read(flowEnt.getSupplierId());
				if (cus != null) {
					productBills.setDeptId(cus.getDeptId());
				}

				List<String> loginNameList = Arrays.asList(cusProduct.getAccount().split("\\|"));
				loginNameList = loginNameList.stream().map(String::trim).filter(StringUtil::isNotBlank).collect(Collectors.toList());
				// 设置账单账号为产品有效账号
				productBills.setLoginName(String.join(",", loginNameList));
				// 查产品在当月的总成本
				BigDecimal totalCost = customerProductService.queryCustomerProductCost(startDate, endDate, loginNameList, cusProduct.getProductType(), cusProduct.getYysType());
				productBills.setCost(totalCost.setScale(4, BigDecimal.ROUND_HALF_UP));
				if (dahanSuccessCount > 0) {
					productBills.setUnitPrice(receivablesAmount.divide(new BigDecimal(dahanSuccessCount), 6, BigDecimal.ROUND_HALF_UP));
				}
				productBills.setGrossProfit(receivablesAmount.subtract(totalCost).setScale(2, BigDecimal.ROUND_HALF_UP));

				// 添加运营成本信息
				CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
				if (customerProduct != null) {
					OperateCostDetail operateCostDetail = new OperateCostDetail();

					operateCostDetail.setUnifiedOperateSingleCost(BigDecimal.ZERO);
					SearchFilter costFilter = new SearchFilter();
					costFilter.getRules().add(new SearchRule("paramkey", Constants.ROP_EQ, operareCostKeyPrefix + customerProduct.getProductType()));
					costFilter.getOrders().add(new SearchOrder("entityid", Constants.ROP_DESC));
					List<Parameter> params = parameterService.findAllByCriteria(costFilter);

					if (!CollectionUtils.isEmpty(params)) {
						if (StringUtils.isNotBlank(params.get(0).getParamvalue()) && NumberUtils.isParsable(params.get(0).getParamvalue())) {
							operateCostDetail.setUnifiedOperateSingleCost(new BigDecimal(params.get(0).getParamvalue()));
						}
					}

					operateCostDetail.setProductOperateFixedCost(customerProduct.getProductOperateFixedCost());
					operateCostDetail.setProductOperateSingleCost(customerProduct.getProductOperateSingleCost());
					productBills.setFixedCostInfo(JSON.toJSONString(operateCostDetail));
				}

				if (productBillsService.save(productBills)) {
					if (!isInter) {
						// 账单归档后更新月实际单价
						updateUnitPrice(productBills, startDate, endDate);
					}
					logger.info("根据账单流程生成销售账单记录成功");
					logger.info("账单信息：" + productBills.toString());
				} else {
					productBills = null;
					logger.info("根据账单流程生成销售账单记录失败");
				}
			} catch (Exception e) {
				logger.error("根据账单流程生成销售账单记录异常，flowEntId：" + flowEnt.getId(), e);
				productBills = null;
			}
		}
		return productBills;
	}

	/**
	 * 创建账单收款流程
	 */
	private void buildBillReceivablesFlow(FlowEnt settleFlow, JSONObject flowMsg, String productBillId) {
		String flowId = null;
		String flowClass = null;
		int flowType = 0;
		String viewerRoleId = null;
		try {
			// 查询销售收款流程
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.BILL_RECEIVABLES_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowClass = flow.getFlowClass();
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
			} else {
				logger.error(
						"系统无" + Constants.BILL_RECEIVABLES_FLOW_NAME + "，" + settleFlow.getFlowTitle() + " 创建" + Constants.BILL_RECEIVABLES_FLOW_NAME + "失败");
				return;
			}
			BaseFlowTask task = flowTaskManager.getFlowTasks(flowClass);
			if (task == null) {
				task = commonFlowTask;
			}
			String dateMonth = flowMsg.getString(Constants.BILL_FLOW_MONTH_KEY); // 月份
			String productId = settleFlow.getProductId();
			String customerId = settleFlow.getSupplierId(); // 表中实际存的是customerId
			CustomerProduct product = customerProductService.read(productId);
			Customer customer = customerService.read(customerId);
			User user = userService.read(product.getOssUserId());
			String title = Constants.buildBillReceivablesFlowTitle(customer.getCompanyName(), product.getProductName(), dateMonth);
			// 创建流程实体
			FlowEnt flowEnt = buildFlowEnt(title, flowId, flowType, user.getOssUserId(), productId, customerId, viewerRoleId);
			flowEnt.setDeptId(user.getDeptId());
			BigDecimal receivablesAmount = new BigDecimal(0);// 应收金额
			String actuallyDataStr = flowMsg.getString(Constants.RECEIVABLES_AMOUNT_KEY); // 实际账单金额
			if (StringUtil.isNotBlank(actuallyDataStr)) {
				String[] actuallyData = actuallyDataStr.split(",");
				if (actuallyData.length == 3) { // 销售账单流程的实际账单金额（成功数，单价，金额）
					receivablesAmount = new BigDecimal(actuallyData[2]).setScale(2, BigDecimal.ROUND_CEILING);
				} else {// 国际账单流程
					receivablesAmount = new BigDecimal(actuallyDataStr).setScale(2, BigDecimal.ROUND_CEILING);
				}
			}
			JSONObject json = new JSONObject();
			JSONArray billInfoArray = new JSONArray();
			JSONObject bill = new JSONObject();
			bill.put("actualReceivables", "0.00");
			bill.put("id", productBillId);
			bill.put("receivables", receivablesAmount);
			bill.put("thisReceivables", receivablesAmount);
			bill.put("title", Constants.buildProductBillTitle(customer.getCompanyName(), product.getProductName(), dateMonth));
			billInfoArray.add(bill);
			json.put(Constants.BILL_INFO_KEY, billInfoArray);
			json.put("备注", "流程自动发起");
			flowEnt.setFlowMsg(json.toJSONString());
			flowEnt.setEntityType(EntityType.CUSTOMER.ordinal());
			flowEnt.setPlatform(PlatformType.PC.ordinal());
			boolean result = flowEntService.save(flowEnt);
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
				flowLog.setPlatform(PlatformType.PC.ordinal());
				flowLogService.save(flowLog);
			}
			logger.info("生成'" + title + "'" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("生成'" + Constants.BILL_RECEIVABLES_FLOW_NAME + "'异常：", e);
		}
	}

	/**
	 * 创建流程实体
	 */
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
		List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
		if (nodeList != null && nodeList.size() > 0) {
			FlowNode flowNode = nodeList.get(0);
			if (flowNode != null) { // 不要跳过发起人节点
				flowEnt.setNodeId(flowNode.getNodeId());
			}
		}
		return flowEnt;
	}

	/**
	 * 更新单价
	 * 
	 * @param productBills
	 *            月账单
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            截止日期
	 */
	private void updateUnitPrice(ProductBills productBills, Timestamp startDate, Timestamp endDate) {
		try {
			String productId = productBills.getProductId();
			// 当前月份账单出来后更新成实际单价，国际短信产品生成的账单这里返回-1
			double price = getPrice(productBills, startDate, endDate);
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

	private double getPrice(ProductBills productBills, Timestamp startDate, Timestamp endDate) {
		double price = -1;
		try {
			CustomerProduct product = customerProductService.read(productBills.getProductId());
			long successCount = productBills.getSupplierCount();
			// 根据产品和时间找到调价记录
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("productId", Constants.ROP_EQ, product.getProductId()));
			filter.getRules().add(new SearchRule("validityDateStart", Constants.ROP_LE, startDate));
			filter.getRules().add(new SearchRule("validityDateEnd", Constants.ROP_GE, endDate));
			filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<ModifyPrice> modifylist = modifyPriceService.queryAllBySearchFilter(filter);
			if (modifylist == null || modifylist.isEmpty()) {
				logger.error("产品productId：" + product.getProductId() + "，未找到调价记录，获取单价失败");
				return price;
			}
			// 根据最近的调价记录查询梯度价格表
			// 一般调价流程，先生成调价记录，再生成价格梯度，最后用默认梯度的价格生成单价记录
			// 国际调价流程，先生成调价记录，再根据报价单生成每个国别号的单价记录，不生成中间的价格梯度
			filter = new SearchFilter();
			filter.getRules().add(new SearchRule("modifyPriceId", Constants.ROP_EQ, modifylist.get(0).getModifyPriceId()));
			filter.getOrders().add(new SearchOrder("gradient", Constants.ROP_ASC));
			List<DeductionPrice> deductionList = deductionPriceService.queryAllBySearchFilter(filter);
			// 国际调价没有价格梯度记录
			if (deductionList == null || deductionList.isEmpty()) {
				logger.error("调价表modifyPriceId：" + modifylist.get(0).getModifyPriceId() + "，未找到梯度价格表数据，获取单价失败");
				return price;
			}

			double sum = 0;
			if (modifylist.get(0).getPriceType() == PriceType.UNIFORM_PRICE.getCode()) { // 统一价
				price = deductionList.get(0).getPrice().doubleValue();
			} else if (modifylist.get(0).getPriceType() == PriceType.STAGE_PRICE.getCode()) { // 阶段价
				for (DeductionPrice deductionPrice : deductionList) {
					double ladderPrice = deductionPrice.getPrice().doubleValue();
					// 总发送量 > 当前阶段最大发送量，当前阶段金额 = 当前阶段发送量 x 当前阶段价格
					if (successCount >= deductionPrice.getMaxSend() && deductionPrice.getMaxSend() > deductionPrice.getMinSend()) {
						sum += ((deductionPrice.getMaxSend() - deductionPrice.getMinSend()) * ladderPrice);
					} else if (successCount > deductionPrice.getMinSend()) {
						// 总发送量未超出当前阶段
						sum += ((successCount - (deductionPrice.getMinSend())) * ladderPrice);
					}
				}
				price = sum / successCount;
			} else if (modifylist.get(0).getPriceType() == PriceType.STEPPED_PRICE.getCode()) { // 阶梯价
				double ladderPrice = 0;
				for (DeductionPrice deductionPrice : deductionList) {
					// 是哪个梯度就用哪个梯度的价格
					if (successCount >= deductionPrice.getMinSend() && (deductionPrice.getMinSend() >= deductionPrice.getMaxSend() // 当前梯度是最大梯度
							|| successCount < deductionPrice.getMaxSend())) {
						ladderPrice = deductionPrice.getPrice().doubleValue();
						sum = successCount * ladderPrice;
						continue;
					}
				}
				price = sum / successCount;
			} else {
				logger.error(modifylist.get(0).getPriceType() + "为未知价格类型，未找到价格类型数据异常，账单流程生成失败");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return price;
	}

	/**
	 * 查询供应商单价
	 * 
	 * @param endTime
	 * @param startTime
	 */
	@SuppressWarnings("unused")
	private void querySupplierPrice(List<Map<String, Object>> mapList, Timestamp startTime, Timestamp endTime) {
		final Map<String, Double> priceCacheMap = new HashMap<>();
		Map<String, String> productCacheMap = new HashMap<>();
		try {
			List<Product> productList = productService.queryAllBySearchFilter(null);
			if (!ListUtils.isEmpty(productList)) {
				for (Product product : productList) {
					if (StringUtils.isNotBlank(product.getProductMark())) {
						productCacheMap.put(product.getProductId(), product.getProductMark());
					}
				}
			}
			SearchFilter priceFilter = new SearchFilter();
			priceFilter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startTime));
			priceFilter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endTime));
			priceFilter.getRules().add(new SearchRule("entityType", Constants.ROP_EQ, EntityType.SUPPLIER.ordinal()));
			List<UnitPrice> unitPriceList = unitPriceService.queryAllBySearchFilter(priceFilter);
			if (!ListUtils.isEmpty(unitPriceList)) {
				for (UnitPrice unitPrice : unitPriceList) {
					if (StringUtils.isNotBlank(productCacheMap.get(unitPrice.getBasicsId()))) {
						priceCacheMap.put(productCacheMap.get(unitPrice.getBasicsId()) + "," + DateUtil.convert(unitPrice.getWtime(), DateUtil.format4) + ","
								+ unitPrice.getCountryCode(), unitPrice.getUnitPrice().doubleValue());
					}
				}
			}

			mapList = mapList.stream().map(map -> {
				String channelId = (String) map.get("channelId");
				String date = (String) map.get("date");
				String countryCode = (String) map.get("countryCode");
				String key = channelId + "," + date + "," + countryCode;
				if (StringUtils.isNoneBlank(date, channelId)) {
					double price = 0;
					if (priceCacheMap.get(key) != null) {
						price = priceCacheMap.get(key);
					}
					map.put("unitPrice", price);
				}
				return map;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (priceCacheMap != null && !priceCacheMap.isEmpty()) {
				priceCacheMap.clear();
			}
			if (productCacheMap != null && !productCacheMap.isEmpty()) {
				productCacheMap.clear();
			}
		}
	}

	/**
	 * 获取发送量
	 */
	@SuppressWarnings("unused")
	private List<Map<String, Object>> queryTj(Timestamp startTime, Timestamp endTime, List<String> loginNameList, int productType) throws BaseException {
		long _start = System.currentTimeMillis();
		List<Map<String, Object>> result = new ArrayList<>();
		String dateFormat = "DATE_FORMAT(tj.statsDate, '%Y-%m')";

		String selSuccessCount = "SELECT SUM(tj.successCount), " + dateFormat
				+ ", tj.channelId, tj.loginName, tj.countryCode, tj.productType FROM CustomerProductTj tj "
				+ " WHERE tj.loginName IN :loginNameList AND  tj.statsDate >= :startTime AND tj.statsDate <= :endTime and productType = :productType "
				+ " GROUP BY " + dateFormat + ", tj.channelId, tj.loginName, tj.countryCode, tj.productType ";
		Map<String, Object> selMap = new HashMap<>();
		selMap.put("loginNameList", loginNameList);
		selMap.put("startTime", startTime);
		selMap.put("endTime", endTime);
		selMap.put("productType", productType);
		List<Object> sendCountList = baseDao.findByhql(selSuccessCount, selMap, 0);
		logger.info("查询销售统计成功数表数据完成，耗时：" + (System.currentTimeMillis() - _start));
		for (Object obj : sendCountList) {
			Map<String, Object> map = new HashMap<>();
			Object[] ob = (Object[]) obj;
			map.put("successCount", ((Number) ob[0]).longValue());
			map.put("date", (String) ob[1]);
			map.put("channelId", (String) ob[2]);
			map.put("loginName", (String) ob[3]);
			map.put("countryCode", (String) ob[4]);
			map.put("productType", (int) ob[5] + "");
			result.add(map);
		}
		return result;
	}
}
