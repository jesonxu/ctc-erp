package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.productBills.entity.OperateCostDetail;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("checkBillFlowService")
public class CheckBillFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(CheckBillFlowService.class);

	private static final String FLOW_CLASS = Constants.CHECK_BILL_FLOW_CLASS;
	private static final String FLOW_NAME = Constants.CHECK_BILL_FLOW_NAME;

	@Autowired
	private IProductBillsService billsService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IProductTypeService productTypeService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IMsgCenterService msgCenterService;

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
		String flowMsg = flowEnt.getFlowMsg();
		List<String> billIdList = new ArrayList<>();
		JSONArray billInfos = null;
		// 获取需要更新的账单
		if (StringUtil.isNotBlank(flowMsg)) {
			JSONObject flowMsgJson = JSONObject.parseObject(flowMsg);
			if (flowMsgJson != null) {
				String uncheckBillInfo = flowMsgJson.getString(Constants.UNCHECKED_BILL_KEY);
				JSONObject uncheckBillInfoJson = JSONObject.parseObject(uncheckBillInfo);
				if (uncheckBillInfoJson != null) {
					String billInfosStr = uncheckBillInfoJson.getString("billInfos");
					billInfos = JSON.parseArray(billInfosStr);
					if (billInfos != null) {
						billInfos.forEach(billInfo -> {
							billIdList.add(((JSONObject) billInfo).getString("id"));
						});
					}
				}
			}
		}
		boolean result = false;
		// 将对账流程中的数据更新到账单表
		if (!billIdList.isEmpty()) {
			SearchFilter searchFilter = new SearchFilter();
			searchFilter.getRules().add(new SearchRule("id", Constants.ROP_IN, billIdList));
			try {
				List<ProductBills> billList = billsService.queryAllBySearchFilter(searchFilter);
				if (!CollectionUtils.isEmpty(billList)) {
					// 查产品
					List<String> productIdList = billList.stream().map(ProductBills::getProductId).collect(Collectors.toList());
					searchFilter = new SearchFilter();
					searchFilter.getRules().add(new SearchRule("productId", Constants.ROP_IN, productIdList));
					List<CustomerProduct> customerProductList = customerProductService.queryAllByFilter(searchFilter);
					Map<String, CustomerProduct> customerProductMap = customerProductList.stream().collect(Collectors.toMap(CustomerProduct::getProductId, v -> v));
					for (ProductBills bill : billList) {
						// 流程中的账单数据
						for (Object billInfo : billInfos) {
							JSONObject billInfoJson = (JSONObject) billInfo;
							if (bill.getId().equals(billInfoJson.getString("id"))) {
								long supplierSuccessCount = Long.parseLong(billInfoJson.getString("checkedSuccessCount"));
								bill.setSupplierCount(supplierSuccessCount);
								BigDecimal unitPrice = new BigDecimal(billInfoJson.getString("checkedUnitPrice"));
								bill.setUnitPrice(unitPrice);
								BigDecimal receivables = new BigDecimal(billInfoJson.getString("checkedAmount"));
								bill.setReceivables(receivables);
								BigDecimal grossProfit = bill.getReceivables().subtract(bill.getCost());
								bill.setGrossProfit(grossProfit);
								bill.setBillStatus(BillStatus.RECONILED.ordinal());
								bill.setFlowEntId(flowEnt.getId());

								CustomerProduct customerProduct = customerProductMap.get(bill.getProductId());
								// 添加运营成本基本信息
								OperateCostDetail operateCostDetail = new OperateCostDetail();
								// 统一单条运营成本
								operateCostDetail.setUnifiedOperateSingleCost(BigDecimal.ZERO);
								SearchFilter costFilter = new SearchFilter();
								costFilter.getRules().add(new SearchRule("paramkey", Constants.ROP_EQ, Constants.OPERARE_COST_KEY_PREFIX + customerProduct.getProductType()));
								costFilter.getOrders().add(new SearchOrder("entityid", Constants.ROP_DESC));
								List<Parameter> params = parameterService.findAllByCriteria(costFilter);
								if (!CollectionUtils.isEmpty(params)) {
									if (StringUtils.isNotBlank(params.get(0).getParamvalue()) && NumberUtils.isParsable(params.get(0).getParamvalue())) {
										operateCostDetail.setUnifiedOperateSingleCost(new BigDecimal(params.get(0).getParamvalue()));
									}
								}
								// 客户固定运营成本
								operateCostDetail.setProductOperateFixedCost(customerProduct.getProductOperateFixedCost());
								// 产品单条运营成本
								operateCostDetail.setProductOperateSingleCost(customerProduct.getProductOperateSingleCost());
								// 账单比例
								operateCostDetail.setBillMoneyRatio(customerProduct.getBillMoneyRatio());
								// 毛利润比例
								operateCostDetail.setBillGrossProfitRatio(customerProduct.getBillGrossProfitRatio());
								bill.setFixedCostInfo(JSON.toJSONString(operateCostDetail));
								
								// 更新产品第一次账单时间
								if (customerProduct.getFirstGenerateBillTime() == null) {
									updateCustomerProduct1stBillTime(customerProduct, bill);
								}

								break;
							}
						}
					}
					result = billsService.updateByBatch(billList);
				}
			} catch (ServiceException e) {
				logger.error("获取流程数据更新账单记录异常", e);
			}
		}
		return result;
	}
	
	private void updateCustomerProduct1stBillTime(CustomerProduct product, ProductBills bill) {
		try {
			String sysParam = parameterService.getSysParam(productTypeService.getProductTypeKeyByValue(product.getProductType()) + "_1st_bill_threshold");
			if (StringUtils.isNotBlank("sysParam") && NumberUtils.isParsable(sysParam)) {
				int threshold = Integer.parseInt(sysParam);
				if (threshold > 0 && bill.getPlatformCount() >= threshold) {
					product.setFirstGenerateBillTime(bill.getWtime());
					customerProductService.update(product);
					logger.info("客户【" + product.getProductName() + "】的第一账单时间改为【" + DateUtil.convert(bill.getWtime(), DateUtil.format2) + "】");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		flowMsgModify(auditResult, flowEnt, null);
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		String flowMsg = flowEnt.getFlowMsg();
		// 节点审核前勾选的账单id
		List<String> beforeBillIdList = new ArrayList<>();
		// 节点审核后勾选的账单id
		List<String> afterBillIdList = new ArrayList<>();
		// 选中的账单id
		Set<String> checkedIdSet = new HashSet<>();
		// 取消选中的账单id
		Set<String> uncheckedIdSet = new HashSet<>();
		JSONArray billInfos = null;

		// 获取节点审核后选中的账单
		if (StringUtil.isNotBlank(flowMsg)) {
			JSONObject flowMsgJson = JSONObject.parseObject(flowMsg);
			if (flowMsgJson != null) {
				String uncheckBillInfo = flowMsgJson.getString(Constants.UNCHECKED_BILL_KEY);
				JSONObject uncheckBillInfoJson = JSONObject.parseObject(uncheckBillInfo);
				if (uncheckBillInfoJson != null) {
					String billInfosStr = uncheckBillInfoJson.getString("billInfos");
					billInfos = JSON.parseArray(billInfosStr);
					if (billInfos != null) {
						billInfos.forEach(billInfo -> {
							afterBillIdList.add(((JSONObject) billInfo).getString("id"));
						});
					}
				}
			}
		}
		// 获取节点审核前选中的账单，用于与审核后的比较
		if (StringUtil.isNotBlank(changes)) {
			JSONObject changesJson = JSONObject.parseObject(changes);
			if (changesJson != null) {
				String lastBillInfo = changesJson.getString(Constants.UNCHECKED_BILL_KEY + "_修改前");
				JSONObject lastBillInfoJson = JSONObject.parseObject(lastBillInfo);
				if (lastBillInfoJson != null) {
					String billInfosStr = lastBillInfoJson.getString("billInfos");
					billInfos = JSON.parseArray(billInfosStr);
					if (billInfos != null) {
						billInfos.forEach(billInfo -> {
							beforeBillIdList.add(((JSONObject) billInfo).getString("id"));
						});
					}
				}
			}
		}

		if (AuditResult.CANCLE.getCode() == auditResult || AuditResult.REJECTED.getCode() == auditResult){
			// 取消、驳回至发起人
			uncheckedIdSet.addAll(afterBillIdList);
			uncheckedIdSet.addAll(beforeBillIdList);
		} else if (AuditResult.PASS.getCode() == auditResult){
			// 创建、通过、驳回至非发起人节点
			checkedIdSet.addAll(afterBillIdList);
			uncheckedIdSet.addAll(beforeBillIdList);
		}

		// 将取消选中的账单更新为未对账状态
		if (!CollectionUtils.isEmpty(uncheckedIdSet)) {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("id", Constants.ROP_IN, new ArrayList<>(uncheckedIdSet)));
			try {
				List<ProductBills> billList = billsService.queryAllBySearchFilter(filter);
				billList = billList.stream().peek(bill -> {
					bill.setBillStatus(BillStatus.NO_RECONCILE.ordinal());
					bill.setFlowEntId(null);
				}).collect(Collectors.toList());
				boolean result = billsService.updateByBatch(billList);
				logger.info("更新账单状态为未对账" + (result ? "成功" : "失败"));
			} catch (BaseException e) {
				logger.error("更新账单状态异常", e);
			}
		}
		// 将选中的账单更新为对账中状态
		if (!CollectionUtils.isEmpty(checkedIdSet)) {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("id", Constants.ROP_IN, afterBillIdList));
			try {
				List<ProductBills> billList = billsService.queryAllBySearchFilter(filter);
				billList = billList.stream().peek(bill -> {
					bill.setBillStatus(BillStatus.RECONILING.ordinal());
					bill.setFlowEntId(flowEnt.getId());
				}).collect(Collectors.toList());
				boolean result = billsService.updateByBatch(billList);
				logger.info("更新账单状态为对账中" + (result ? "成功" : "失败"));
			} catch (BaseException e) {
				logger.error("更新账单状态异常", e);
			}
		}
		// 保存取消流程信息
		if (AuditResult.CANCLE.getCode() == auditResult) {
			buildCancelMessage(flowEnt);
		}
	}

	private void buildCancelMessage(FlowEnt flowEnt) {
		try {
			User user = userService.read(flowEnt.getOssUserId());
			String msgDetail = user.getRealName() + " 取消了流程：" + flowEnt.getFlowTitle();
			List<User> leaderList = userService.findUserAllLeader(user);
			List<String> alarmUserIds = new ArrayList<>();
			alarmUserIds.add(user.getOssUserId());
			if (!CollectionUtils.isEmpty(leaderList)) {
				alarmUserIds.addAll(leaderList.stream().map(User::getOssUserId).collect(Collectors.toList()));
			}
			msgCenterService.buildMessage(user, alarmUserIds, msgDetail, flowEnt.getId());
		} catch (ServiceException e) {
			logger.error("", e);
		}
	}
}
