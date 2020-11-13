package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.FlowTaskManager;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.dsOutDepot.DsSaveOutDepotDto;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.DsOrderStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.PayType;
import com.dahantc.erp.enums.SendType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dsBuyOrder.service.IDsBuyOrderService;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;
import com.dahantc.erp.vo.dsDepotItem.service.IDsDepotItemService;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
import com.dahantc.erp.vo.dsOrderDetail.entity.DsOrderDetail;
import com.dahantc.erp.vo.dsOrderDetail.service.IDsOrderDetailService;
import com.dahantc.erp.vo.dsOutDepot.service.IDsOutDepotService;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("dsOrderFlowService")
public class DsOrderFlowService extends BaseFlowTask {

	private static Logger logger = LogManager.getLogger(DsOrderFlowService.class);
	public static final String FLOW_CLASS = Constants.DS_ORDER_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.DS_ORDER_FLOW_NAME;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IDsOrderService dsOrderService;

	@Autowired
	private IDsOrderDetailService dsOrderDetailService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IDsBuyOrderService dsBuyOrderService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowLogService flowLogService;
	
	@Autowired
	private IDsOutDepotService dsOutDepotService;
	
	@Autowired
	private IDsDepotItemService dsDepotItemService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private CommonFlowTask commonFlowTask;

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
		logger.info("不校验数据");
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		logger.info("流程归档开始，flowEntId：" + flowEnt.getId());
		boolean result = false;
		try {
			flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
			String flowMsg = flowEnt.getFlowMsg();
			if (StringUtils.isNotBlank(flowMsg)) {
				JSONObject flowJson = JSONObject.parseObject(flowMsg);
				DsOrder dsOrder = buildDsOrder(flowEnt, flowJson);
				List<DsOrderDetail> detailList = buildDsOrderDetail(flowEnt, flowJson);
				Map<String, List<DsOrderDetail>> supplierDetailMap = detailList.stream().filter(detail -> StringUtil.isNotBlank(detail.getSupplierId()))
						.collect(Collectors.groupingBy(DsOrderDetail::getSupplierId));
				for (Map.Entry<String, List<DsOrderDetail>> supplierDetail : supplierDetailMap.entrySet()) {
					List<DsOrderDetail> supplierDetailList = supplierDetail.getValue();
					buildDsPurchaseFlow(dsOrder, supplierDetail.getKey(), supplierDetailList);
				}
			}
			logger.info("流程归档结束");
		} catch (Exception e) {
			logger.info("流程归档异常，flowEntId：" + flowEnt.getId());
		}
		return result;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		String flowMsg = flowEnt.getFlowMsg();
		if (StringUtils.isNotBlank(flowMsg)) {
			JSONObject flowJson = JSONObject.parseObject(flowMsg);
			DsOrder dsOrder = new DsOrder();
			if (auditResult == AuditResult.CREATED.getCode()) {
				if (flowJson.containsKey(Constants.DS_ORDER_NUMBER)) {
					String orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
					dsOrder.setOrderId(orderId);
					// 获取下一个流水号
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, flowEnt.getOssUserId()));
					Date startDate = DateUtil.getCurrentStartDateTime();
					Date endDate = DateUtil.getCurrentEndDateTime();
					filter.getRules().add(new SearchRule("wtime", Constants.ROP_GE, startDate));
					filter.getRules().add(new SearchRule("wtime", Constants.ROP_LE, endDate));
					int serialNo = dsOrderService.getCount(filter) + 1;
					dsOrder.setOssUserId(flowEnt.getOssUserId());
					dsOrder.setSerialNo(serialNo);
					dsOrder.setOrderStatus(DsOrderStatus.TO_BE_RECEIVED.getCode());
					dsOrder.setWtime(new Date());
					dsOrderService.save(dsOrder);
				} else {
					logger.info("订单编号不能为空，flowEntId：" + flowEnt.getId());
					return;
				}
			}
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	/**
	 * 生成电商订单
	 * 
	 * @param flowEnt
	 *            流程实体
	 * @param flowJson
	 *            流程内容json对象
	 * @return 电商订单
	 */
	private DsOrder buildDsOrder(FlowEnt flowEnt, JSONObject flowJson) {
		logger.info("生成电商订单表记录开始，flowEntId：" + flowEnt.getId());
		DsOrder dsOrder = null;
		if (flowJson == null) {
			logger.info("流程内容为空");
			return null;
		}
		try {
			// 订单编号
			if (flowJson.containsKey(Constants.DS_ORDER_NUMBER)) {
				String orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
				dsOrder = dsOrderService.read(orderId);
			} else {
				logger.info("订单编号不能为空，flowEntId：" + flowEnt.getId());
				return null;
			}
			// 销售信息
			User user = userService.read(flowEnt.getOssUserId());
			dsOrder.setOssUserId(user.getOssUserId());
			dsOrder.setOssUserName(user.getRealName());
			// 客户信息
			Customer customer = customerService.read(flowEnt.getSupplierId());
			if (customer != null) {
				dsOrder.setCustomerId(customer.getCustomerId());
				dsOrder.setCustomerName(customer.getCompanyName());
			}
			// 项目信息
			CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
			if (customerProduct != null) {
				dsOrder.setProjectName(customerProduct.getProductName());
				dsOrder.setProjectId(customerProduct.getProductId());
			}
			// 采购金额
			if (flowJson.containsKey(Constants.DS_SALES_MONTY) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_SALES_MONTY))) {
				String salesMoneyStr = flowJson.getString(Constants.DS_SALES_MONTY);
				if (StringUtil.isMoneyNumber(salesMoneyStr)) {
					dsOrder.setSalesMoney(new BigDecimal(salesMoneyStr.replace(",", "")));
				}
			}
			// 交付日期
			if (flowJson.containsKey(Constants.DS_DUE_TIME) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_DUE_TIME))) {
				String dueTimeStr = flowJson.getString(Constants.DS_DUE_TIME);
				Date dueTime = DateUtil.convert(dueTimeStr, DateUtil.format1);
				dsOrder.setDueTime(dueTime);
			}
			// 采购成本总额，自动计算（包装设计费 + 采购物流费总计 + 每个配单商品的销售总额）（每个商品的销售总额也自动计算，每个商品的单价*数量）
			if (flowJson.containsKey(Constants.DS_PURCHASE_COST) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_PURCHASE_COST))) {
				String purchaseCostStr = flowJson.getString(Constants.DS_PURCHASE_COST);
				if (StringUtil.isMoneyNumber(purchaseCostStr)) {
					dsOrder.setPurchaseCost(new BigDecimal(purchaseCostStr.replace(",", "")));
				}
			}
			// 采购物流费，自动计算（每个商品的物流费总计）
			if (flowJson.containsKey(Constants.DS_LOGISTICS_COSTS) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_LOGISTICS_COSTS))) {
				String logisticsCostsStr = flowJson.getString(Constants.DS_LOGISTICS_COSTS);
				if (StringUtil.isMoneyNumber(logisticsCostsStr)) {
					dsOrder.setLogisticsCosts(new BigDecimal(logisticsCostsStr.replace(",", "")));
				}
			}
			// 包装设计费（本订单整的包装设计费）
			if (flowJson.containsKey(Constants.DS_DESIGN_FEE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_DESIGN_FEE))) {
				String designFeeStr = flowJson.getString(Constants.DS_DESIGN_FEE);
				if (StringUtil.isMoneyNumber(designFeeStr)) {
					dsOrder.setDesignFee(new BigDecimal(designFeeStr.replace(",", "")));
				}
			}
			// 发票种类
			if (flowJson.containsKey(Constants.DS_INVOICE_TYPE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_INVOICE_TYPE))) {
				dsOrder.setInvoiceType(flowJson.getString(Constants.DS_INVOICE_TYPE));
			}
			// 发票税点
			if (flowJson.containsKey(Constants.DS_INVOICE_RANT) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_INVOICE_RANT))) {
				String invoiceRantStr = flowJson.getString(Constants.DS_INVOICE_RANT);
				dsOrder.setRant(StringUtil.isMoneyNumber(invoiceRantStr) ? Integer.parseInt(invoiceRantStr) : 0);
			}
			// 发货形式
			if (flowJson.containsKey(Constants.DS_SEND_TYPE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_SEND_TYPE))) {
				String sentTypeStr = flowJson.getString(Constants.DS_SEND_TYPE);
				Optional<SendType> sendTypeOpt = SendType.getEnumsByMsg(sentTypeStr);
				if (sendTypeOpt.isPresent()) {
					dsOrder.setSendType(sendTypeOpt.get().getCode());
				}
			}
			// 配送地址
			if (flowJson.containsKey(Constants.DS_SEND_ADDRESS) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_SEND_ADDRESS))) {
				dsOrder.setSendAddress(flowJson.getString(Constants.DS_SEND_ADDRESS));
			}
			// 配送地址附件json字符串，fileName filePath
			if (flowJson.containsKey(Constants.DS_SEND_ADDRESS_FILE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_SEND_ADDRESS_FILE))) {
				dsOrder.setSendAddressFile(flowJson.getString(Constants.DS_SEND_ADDRESS_FILE));
			}
			// 配货单有效截止日期
			if (flowJson.containsKey(Constants.DS_VALID_TIME) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_VALID_TIME))) {
				String validTimeStr = flowJson.getString(Constants.DS_VALID_TIME);
				Date validTime = DateUtil.convert(validTimeStr, DateUtil.format1);
				dsOrder.setValidTime(validTime);
			}
			// 付款形式
			if (flowJson.containsKey(Constants.DS_PAY_TYPE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_PAY_TYPE))) {
				String payTypeStr = flowJson.getString(Constants.DS_PAY_TYPE);
				Optional<PayType> payTypeOpt = PayType.getEnumsByMsg(payTypeStr);
				if (payTypeOpt.isPresent()) {
					dsOrder.setPayType(payTypeOpt.get().ordinal());
				}
			}
			// 付款周期
			if (flowJson.containsKey(Constants.DS_PAY_PERIOD) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_PAY_PERIOD))) {
				String payPeriodStr = flowJson.getString(Constants.DS_PAY_PERIOD);
				if (StringUtil.isMoneyNumber(payPeriodStr)) {
					dsOrder.setPayPeriod(Integer.parseInt(payPeriodStr));
				}
			}
			// 配单员
			if (flowJson.containsKey(Constants.DS_MATCH_PEOPLE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_MATCH_PEOPLE))) {
				String matchPeopleId = flowJson.getString(Constants.DS_MATCH_PEOPLE);
				User matchPeople = userService.read(matchPeopleId);
				dsOrder.setMatchPeopleId(matchPeople.getOssUserId());
				dsOrder.setMatchPeopleName(matchPeople.getRealName());
			}
			// 收货联系人
			if (flowJson.containsKey(Constants.DS_BUY_CONTACT_PERSON) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_BUY_CONTACT_PERSON))) {
				dsOrder.setContactPerson(flowJson.getString(Constants.DS_BUY_CONTACT_PERSON));
			}
			// 联系电话
			if (flowJson.containsKey(Constants.DS_BUY_CONTACT_NO) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_BUY_CONTACT_NO))) {
				dsOrder.setContactNo(flowJson.getString(Constants.DS_BUY_CONTACT_NO));
			}
			boolean result = dsOrderService.save(dsOrder);
			logger.info("生成电商订单表记录" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("生成电商订单表记录异常，flowEntId：" + flowEnt.getId(), e);
		}
		return dsOrder;
	}

	/**
	 * 生成电商订单详情
	 *
	 * @param flowEnt
	 *            流程实体
	 * @param flowJson
	 *            流程内容json对象
	 * @return 电商订单
	 */
	private List<DsOrderDetail> buildDsOrderDetail(FlowEnt flowEnt, JSONObject flowJson) {
		logger.info("生成电商订单详情表记录开始，flowEntId：" + flowEnt.getId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.format1);
		List<DsOrderDetail> detailList = new ArrayList<>();
		DsSaveOutDepotDto dsOutDepotDto = new DsSaveOutDepotDto();
		List<DsOutDepotDetail> dsOutDepotDetails = new ArrayList<>();
		String orderId;
		if (flowJson == null) {
			logger.info("流程内容为空");
			return detailList;
		}
		try {
			// 订单编号
			if (flowJson.containsKey(Constants.DS_ORDER_NUMBER)) {
				orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
			} else {
				logger.info("订单编号不能为空，flowEntId：" + flowEnt.getId());
				return null;
			}
			// 配单信息
			if (flowJson.containsKey(Constants.DS_MATCH_ORDER) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_MATCH_ORDER))) {
				String matchOrderStr = flowJson.getString(Constants.DS_MATCH_ORDER);
				JSONArray matchOrders = JSON.parseArray(matchOrderStr);
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal logisticTotal = BigDecimal.ZERO;
				DsOutDepotDetail dsOutDepotDetail = new DsOutDepotDetail();
				DsDepotItem dsDepotItem = new DsDepotItem();
				
				for (int i = 0; i < matchOrders.size(); i++) {
					JSONObject matchOrder = matchOrders.getJSONObject(i);
					DsOrderDetail detail = new DsOrderDetail();
					detail.setOrderId(orderId);
					if (matchOrder.containsKey(Constants.DS_PRODUCT_NAME_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_PRODUCT_NAME_KEY))) {
						detail.setProductName(matchOrder.getString(Constants.DS_PRODUCT_NAME_KEY));
					}
					if (matchOrder.containsKey(Constants.DS_PRODUCT_ID_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_PRODUCT_ID_KEY))) {
						detail.setProductId(matchOrder.getString(Constants.DS_PRODUCT_ID_KEY));
					}
					if (matchOrder.containsKey(Constants.DS_SUPPLIER_ID_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_SUPPLIER_ID_KEY))) {
						detail.setSupplierId(matchOrder.getString(Constants.DS_SUPPLIER_ID_KEY));
					}
					if (matchOrder.containsKey(Constants.DS_SUPPLIER_NAME_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_SUPPLIER_NAME_KEY))) {
						detail.setSupplierName(matchOrder.getString(Constants.DS_SUPPLIER_NAME_KEY));
					}
					// 规格型号
					if (matchOrder.containsKey(Constants.DS_FORMAT_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_FORMAT_KEY))) {
						detail.setFormat(matchOrder.getString(Constants.DS_FORMAT_KEY));
					}
					// 单价，集采取集采价，一件代发取一件代发价
					if (matchOrder.containsKey(Constants.DS_PRICE_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_PRICE_KEY))) {
						String priceStr = matchOrder.getString(Constants.DS_PRICE_KEY);
						detail.setPrice(StringUtil.isMoneyNumber(priceStr) ? new BigDecimal(priceStr.replace(",", "")) : new BigDecimal(0));
					}
					// 数量
					if (matchOrder.containsKey(Constants.DS_AMOUNT_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_AMOUNT_KEY))) {
						String amountStr = matchOrder.getString(Constants.DS_AMOUNT_KEY);
						detail.setAmount(StringUtil.isMoneyNumber(amountStr) ? Integer.parseInt(amountStr.replace(",", "")) : 0);
					}
					// 总额
					if (matchOrder.containsKey(Constants.DS_TOTAL_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_TOTAL_KEY))) {
						String totalStr = matchOrder.getString(Constants.DS_TOTAL_KEY);
						detail.setTotal(StringUtil.isMoneyNumber(totalStr) ? new BigDecimal(totalStr.replace(",", "")) : new BigDecimal(0));
					}
					// 物流费
					if (matchOrder.containsKey(Constants.DS_LOGISTICS_COST_KEY)
							&& StringUtil.isNotBlank(matchOrder.getString(Constants.DS_LOGISTICS_COST_KEY))) {
						String logisticsCostStr = matchOrder.getString(Constants.DS_LOGISTICS_COST_KEY);
						detail.setLogisticsCost(StringUtil.isMoneyNumber(logisticsCostStr) ? new BigDecimal(logisticsCostStr.replace(",", "")) : new BigDecimal(0));
					}
					if (matchOrder.containsKey(Constants.DS_REMARK_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_REMARK_KEY))) {
						detail.setRemark(matchOrder.getString(Constants.DS_REMARK_KEY));
					}
					if (matchOrder.containsKey(Constants.DS_DEPOT_ITEM_ID) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_DEPOT_ITEM_ID))) {
						dsDepotItem = dsDepotItemService.read(matchOrder.getString(Constants.DS_DEPOT_ITEM_ID));
						dsOutDepotDetail.setAmount(Integer.parseInt(matchOrder.getString(Constants.DS_AMOUNT_KEY)));
						dsOutDepotDetail.setDepotItemId(dsDepotItem.getId());
						dsOutDepotDetail.setDepotNumber(String.valueOf(dsDepotItem.getAmount()));
						dsOutDepotDetail.setDepotType(matchOrder.getString(Constants.DS_DEPOT_TYPE_KEY));
						dsOutDepotDetail.setFormat(matchOrder.getString(Constants.DS_FORMAT_KEY));
						dsOutDepotDetail.setIsDelete(0);
						dsOutDepotDetail.setIsSample(dsDepotItem.getIsSample());
						String priceStr = matchOrder.getString(Constants.DS_PRICE_KEY);
						dsOutDepotDetail.setPrice(StringUtil.isMoneyNumber(priceStr) ? new BigDecimal(priceStr.replace(",", "")) : new BigDecimal(0));
						dsOutDepotDetail.setProductId(matchOrder.getString(Constants.DS_PRODUCT_ID_KEY));
						dsOutDepotDetail.setProductName(matchOrder.getString(Constants.DS_PRODUCT_NAME_KEY));
						dsOutDepotDetail.setSupplierId(matchOrder.getString(Constants.DS_SUPPLIER_ID_KEY));
						dsOutDepotDetail.setSupplierName(matchOrder.getString(Constants.DS_SUPPLIER_NAME_KEY));
						String salesMoneyStr = flowJson.getString(Constants.DS_SALES_MONTY);
						dsOutDepotDetail.setTotal(new BigDecimal(salesMoneyStr.replace(",", "")));
						total = total.add(new BigDecimal(salesMoneyStr.replace(",", "")));
						if (matchOrder.containsKey(Constants.DS_LOGISTICS_COST_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_LOGISTICS_COST_KEY))) {
							String logisticTotalStr = matchOrder.getString(Constants.DS_LOGISTICS_COST_KEY);
							BigDecimal logistic = new BigDecimal(logisticTotalStr.replace(",", ""));
							logisticTotal = logisticTotal.add(logistic);
						}
						dsOutDepotDetail.setValidTime(dsDepotItem.getValidTime());
						dsOutDepotDetails.add(dsOutDepotDetail);
					}else {
						detailList.add(detail);
					}
				}
				Customer customer = customerService.read(flowEnt.getSupplierId());
				if (customer != null) {
					dsOutDepotDto.setCustomerId(customer.getCustomerId());
					dsOutDepotDto.setCustomerName(customer.getCompanyName());
				}
				dsOutDepotDto.setIsDelete(0);
				String purchaseCostStr = flowJson.getString(Constants.DS_PURCHASE_COST);
				dsOutDepotDto.setOtherCost(logisticTotal);
				total = total.add(new BigDecimal(purchaseCostStr.replace(",", "")));
				dsOutDepotDto.setOutDepotTotal(total);
				Date date = new Date();
				dsOutDepotDto.setOutTime(simpleDateFormat.format(date));
				User user = userService.read(flowEnt.getOssUserId());
				dsOutDepotDto.setUserId(user.getOssUserId());
				dsOutDepotDto.setUserName(user.getRealName());
				dsOutDepotDto.setVerifyStatus(1);
				String jsonString = JSON.toJSONString(dsOutDepotDetails);
				dsOutDepotDto.setDsOutDepotDetials(jsonString);
			}
			boolean result = false;
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
			searchFilter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
			List<FlowLog> flowLogs = flowLogService.queryAllBySearchFilter(searchFilter);
			User createUser = userService.read(flowLogs.get(0).getOssUserId());
			if (!ListUtils.isEmpty(detailList)) {
				if (!ListUtils.isEmpty(dsOutDepotDetails)) {
					dsOutDepotService.saveDsOutDepot(dsOutDepotDto, createUser);
				}
				result = dsOrderDetailService.saveByBatch(detailList);
			}
			if (result) {
				logger.info("生成电商订单详情表记录成功，保存条数：" + detailList.size());
			} else {
				logger.error("生成电商订单详情表记录失败");
				detailList.clear();
			}
		} catch (Exception e) {
			logger.error("生成电商订单详情表记录异常，flowEntId：" + flowEnt.getId());
		}
		return detailList;
	}

	/**
	 * 创建供应商采购流程
	 * 
	 * @param order
	 *            订单信息
	 * @param supplierId
	 *            供应商id
	 * @param detailList
	 *            该供应商的配单产品列表
	 * @return
	 */
	private void buildDsPurchaseFlow(DsOrder order, String supplierId, List<DsOrderDetail> detailList) {
		logger.info("创建" + Constants.DS_PURCHASE_FLOW_NAME + "开始，supplierId：" + supplierId);
		String flowId = null;
		int flowType = 0;
		String viewerRoleId = null;
		String flowClass = null;
		try {
			User user = userService.read(order.getMatchPeopleId());
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.DS_PURCHASE_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);

			
			
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
				flowClass = flow.getFlowClass();
			} else {
				logger.error("系统无" + Constants.DS_PURCHASE_FLOW_NAME + "，创建失败");
				return;
			}
			Supplier supplier = supplierService.read(supplierId);
			String title = Constants.DS_PURCHASE_FLOW_NAME + "(" + supplier.getCompanyName() + ")";
			FlowEnt flowEnt = buildFlowEnt(title, flowId, flowType, user.getOssUserId(), null, supplierId, viewerRoleId);
			flowEnt.setDeptId(user.getDeptId());
			// 流程标签内容
			JSONObject flowMsgJson = new JSONObject();
			// 配单信息标签
			JSONArray buyOrder = new JSONArray();
			// 单笔采购单的合计，每个商品的 物流费+销售总额 之和
			BigDecimal total = new BigDecimal(0);
			for (DsOrderDetail detail : detailList) {
				// 配单信息标签中的一条产品信息
				JSONObject detailJson = new JSONObject();
				detailJson.put(Constants.DS_AMOUNT_KEY, detail.getAmount());
				detailJson.put(Constants.DS_FORMAT_KEY, detail.getFormat());
				detailJson.put(Constants.DS_LOGISTICS_COST_KEY, detail.getLogisticsCost());
				detailJson.put(Constants.DS_PRICE_KEY, detail.getPrice());
				detailJson.put(Constants.DS_PRODUCT_ID_KEY, detail.getProductId());
				detailJson.put(Constants.DS_PRODUCT_NAME_KEY, detail.getProductName());
				detailJson.put(Constants.DS_REMARK, detail.getRemark());
				detailJson.put(Constants.DS_SUPPLIER_ID_KEY, detail.getSupplierId());
				detailJson.put(Constants.DS_SUPPLIER_NAME_KEY, detail.getSupplierName());
				detailJson.put(Constants.DS_TOTAL_KEY, detail.getTotal());
				total = total.add(detail.getLogisticsCost());
				total = total.add(detail.getTotal());
				buyOrder.add(detailJson);
			}
			flowMsgJson.put(Constants.DS_ORDER_NUMBER, order.getOrderId());
			flowMsgJson.put(Constants.DS_MATCH_ORDER, buyOrder.toJSONString());
			flowMsgJson.put(Constants.DS_BUY_ORDER_NUMBER, dsBuyOrderService.buildBuyOrderNo(user, new Date()));
			flowMsgJson.put(Constants.DS_SEND_ADDRESS, order.getSendAddress());
			flowMsgJson.put(Constants.DS_SEND_ADDRESS_FILE, order.getSendAddressFile());
			flowMsgJson.put(Constants.DS_DESIGN_FEE, order.getDesignFee());
			flowMsgJson.put(Constants.DS_TOTAL, total.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
			flowMsgJson.put(Constants.DS_BUY_CONTACT_PERSON, order.getContactPerson());
			flowMsgJson.put(Constants.DS_BUY_CONTACT_NO, order.getContactNo());
			flowMsgJson.put(Constants.DS_DUE_TIME, DateUtil.convert(order.getDueTime(), DateUtil.format1));
			flowEnt.setFlowMsg(JSON.toJSONString(flowMsgJson));
			flowEnt.setEntityType(EntityType.SUPPLIER_DS.ordinal());
			boolean result = flowEntService.save(flowEnt);
			if (result) {
				BaseFlowTask task = flowTaskManager.getFlowTasks(flowClass);
				if (task == null) {
					task = commonFlowTask;
				}
				task.flowMsgModify(AuditResult.CREATED.getCode(), flowEnt);
				// 生成日志记录
				FlowLog flowLog = new FlowLog();
				flowLog.setFlowId(flowEnt.getFlowId());
				flowLog.setFlowEntId(flowEnt.getId());
				flowLog.setAuditResult(AuditResult.CREATED.getCode());
				flowLog.setNodeId(flowEnt.getNodeId());
				flowLog.setOssUserId(user.getOssUserId());
				flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
				flowLog.setRemark("");
				flowLog.setFlowMsg(flowMsgJson.toJSONString());
				flowLogService.save(flowLog);
			}
			logger.info("生成'" + title + "'" + (result ? "成功" : "失败"));
		} catch (Exception e) {

		}
	}

	/**
	 * 创建流程实体
	 * 
	 * @param title
	 *            流程标题
	 * @param flowId
	 *            流程设计id
	 * @param flowType
	 *            类型
	 * @param ossUserId
	 * @param productId
	 * @param supplierId
	 * @param viewerRoleId
	 * @return
	 * @throws Exception
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
		flowEnt.setViewerRoleId(viewerRoleId);
		flowEnt.setWtime(new Timestamp(System.currentTimeMillis()));

		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
		filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
		List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
		if (nodeList != null && nodeList.size() > 0) {
			FlowNode flowNode = nodeList.get(0);
			// 流程创建到初始节点
			flowEnt.setNodeId(flowNode.getNodeId());
		}
		return flowEnt;
	}
}
