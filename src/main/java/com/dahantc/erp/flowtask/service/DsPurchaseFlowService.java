package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.dahantc.erp.vo.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.DsOrderStatus;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dsBuyOrder.entity.DsBuyOrder;
import com.dahantc.erp.vo.dsBuyOrder.service.IDsBuyOrderService;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
import com.dahantc.erp.vo.dsOrderDetail.entity.DsOrderDetail;
import com.dahantc.erp.vo.dsOrderDetail.service.IDsOrderDetailService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;

@Service("dsPurchaseFlowService")
public class DsPurchaseFlowService extends BaseFlowTask {
    private static Logger logger = LogManager.getLogger(DsPurchaseFlowService.class);
    public static final String FLOW_CLASS = Constants.DS_PURCHASE_FLOW_CLASS;
    public static final String FLOW_NAME = Constants.DS_PURCHASE_FLOW_NAME;
    
	@Autowired
	private ISupplierService supplierService;
	
	@Autowired
	private IDsBuyOrderService dsBuyOrderService;
	
	@Autowired
	private IDsOrderService dsOrderService;
	
	@Autowired
	private IDsOrderDetailService dsOrderDetailService;
	
	@Autowired
	private IProductBillsService productBillsService;
    
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
    	boolean result = false;
    	try {
			flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
			String flowMsg = flowEnt.getFlowMsg();
			if (StringUtils.isNotBlank(flowMsg)) {
				JSONObject flowJson = JSONObject.parseObject(flowMsg);
				buildDsBuyOrder(flowEnt, flowJson);
				buildDsBill(flowEnt, flowJson);
			}
		} catch (Exception e) {

		}
		return result;
	}

    /**
	 * 生成电商采购单
	 * 
	 * @param flowEnt
	 *            流程实体
	 * @param flowJson
	 *            流程内容json对象
	 * @return 电商订单
	 */
	private DsBuyOrder buildDsBuyOrder(FlowEnt flowEnt, JSONObject flowJson) {
		logger.info("生成采购单记录开始，flowEntId：" + flowEnt.getId());
		DsBuyOrder dsBuyOrder = null;
		if (flowJson == null) {
			logger.info("流程内容为空");
			return null;
		}
		try {
			// 采购订单编号
			if (flowJson.containsKey(Constants.DS_BUY_ORDER_NUMBER)) {
				String buyOrderId = flowJson.getString(Constants.DS_BUY_ORDER_NUMBER);
				dsBuyOrder = dsBuyOrderService.read(buyOrderId);
			} else {
				logger.info("采购订单编号不能为空，flowEntId：" + flowEnt.getId());
				return null;
			}
			// 订单编号
			if (flowJson.containsKey(Constants.DS_ORDER_NUMBER)) {
				String orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
				DsOrder dsOrder = dsOrderService.read(orderId);
				dsOrder.setOrderStatus(DsOrderStatus.FINISH.getCode());
				dsOrderService.save(dsOrder);
				dsBuyOrder.setOrderId(orderId);
			} else {
				logger.info("订单编号不能为空，flowEntId：" + flowEnt.getId());
				return null;
			}
			// 框架合同编号
			if (flowJson.containsKey(Constants.DS_BUY_CONTRACT_NO) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_BUY_CONTRACT_NO))) {
				String contractNo = flowJson.getString(Constants.DS_BUY_CONTRACT_NO);
				dsBuyOrder.setContractNo(contractNo);
			}
			// 供应商信息
			Supplier supplier = supplierService.read(flowEnt.getSupplierId());
			if (supplier != null) {
				dsBuyOrder.setSupplierId(supplier.getSupplierId());
				dsBuyOrder.setSupplierName(supplier.getCompanyName());
			}
			// 配送地址
			if (flowJson.containsKey(Constants.DS_SEND_ADDRESS) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_SEND_ADDRESS))) {
				dsBuyOrder.setSendAddress(flowJson.getString(Constants.DS_SEND_ADDRESS));
			}
			String orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
			DsOrder dsOrder = dsOrderService.read(orderId);
			if (dsOrder != null) {
				// 联系人
				dsBuyOrder.setContactPerson(dsOrder.getContactPerson());
				// 联系电话
				dsBuyOrder.setContractNo(dsOrder.getContactNo());
			}
			// 备注
			if (flowJson.containsKey(Constants.DS_REMARK) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_REMARK))) {
				dsBuyOrder.setRemark(flowJson.getString(Constants.DS_REMARK));
			}
			// 包装约定
			if (flowJson.containsKey(Constants.DS_PACKAGE_PROMISE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_PACKAGE_PROMISE))) {
				dsBuyOrder.setPackagePromise(flowJson.getString(Constants.DS_PACKAGE_PROMISE));
			}
			// 产品约定
			if (flowJson.containsKey(Constants.DS_PRODUCT_PROMISE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_PRODUCT_PROMISE))) {
				dsBuyOrder.setProductPromise(flowJson.getString(Constants.DS_PRODUCT_PROMISE));
			}
			// 物流约定
			if (flowJson.containsKey(Constants.DS_LOGISTICS_PROMISE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_LOGISTICS_PROMISE))) {
				dsBuyOrder.setLogisticsPromise(flowJson.getString(Constants.DS_LOGISTICS_PROMISE));
			}
			// pdf文件地址
			if (flowJson.containsKey(Constants.DS_PURCHASE_ORDER_FILE) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_PURCHASE_ORDER_FILE))) {
				dsBuyOrder.setPdfPath(flowJson.getString(Constants.DS_PURCHASE_ORDER_FILE));
			}
			// 采购时间
			if (flowJson.containsKey(Constants.DS_BUY_TIME) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_BUY_TIME))) {
				dsBuyOrder.setWtime(flowJson.getDate(Constants.DS_BUY_TIME));
			}
			boolean result = dsBuyOrderService.save(dsBuyOrder);
			logger.info("生成采购单记录" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("生成采购单记录异常，flowEntId：" + flowEnt.getId(), e);
		}
		return dsBuyOrder;
	}
    
    @Override
    public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
    	String flowMsg = flowEnt.getFlowMsg();
		if (StringUtils.isNotBlank(flowMsg)) {
			JSONObject flowJson = JSONObject.parseObject(flowMsg);
			DsBuyOrder dsBuyOrder = new DsBuyOrder();
			if (auditResult == AuditResult.CREATED.getCode()) {
				if (flowJson.containsKey(Constants.DS_BUY_ORDER_NUMBER)) {
					String orderId = flowJson.getString(Constants.DS_BUY_ORDER_NUMBER);
					dsBuyOrder.setBuyOrderId(orderId);
					 //获取下一个流水号
					SearchFilter filter = new SearchFilter();
			        Date startDate = DateUtil.getThisYearFirst();
			        Date endDate = DateUtil.getCurrYearLast();
			        filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startDate));
			        filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
			        int serialNo = dsBuyOrderService.getCount(filter) + 1; 
			        dsBuyOrder.setSerialNo(serialNo);
			        dsBuyOrder.setWtime(new Date());
			        dsBuyOrderService.save(dsBuyOrder);
				} else {
					logger.info("订单编号不能为空，flowEntId：" + flowEnt.getId());
					return ;
				}
			}
		}
    }

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}


	/**
	 * 生成电商账单
	 * 
	 * @param flowEnt
	 *            流程实体
	 * @param flowJson
	 *            流程内容json对象
	 * @return 电商订单
	 */
	private boolean buildDsBill(FlowEnt flowEnt, JSONObject flowJson) {
		logger.info("生成电商账单记录开始，flowEntId：" + flowEnt.getId());
		boolean result = false;
		DsBuyOrder dsBuyOrder = null;
		DsOrder dsOrder = null;
		ProductBills purchaseBill = new ProductBills();
		ProductBills orderBill = new ProductBills();
		if (flowJson == null) {
			logger.info("流程内容为空");
			return result;
		}
		try {
			// 采购订单编号
			if (flowJson.containsKey(Constants.DS_BUY_ORDER_NUMBER)) {
				String buyOrderId = flowJson.getString(Constants.DS_BUY_ORDER_NUMBER);
				dsBuyOrder = dsBuyOrderService.read(buyOrderId);
				purchaseBill.setBillNumber(buyOrderId);
				purchaseBill.setEntityId(dsBuyOrder.getSupplierId());
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("orderId", Constants.ROP_EQ, dsBuyOrder.getOrderId()));
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, dsBuyOrder.getSupplierId()));
				List<DsOrderDetail> dsOrderDetails = dsOrderDetailService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(dsOrderDetails)) {
					purchaseBill.setProductId(dsOrderDetails.get(0).getProductId());
					BigDecimal payables = BigDecimal.ZERO;
					for (DsOrderDetail dsOrderDetail : dsOrderDetails) {
						payables = payables.add(dsOrderDetail.getTotal());
						payables = payables.add(dsOrderDetail.getLogisticsCost());
					}
					purchaseBill.setPayables(payables);
				}else {
					logger.info("该采购单详情为空");
					return result;
				}
				orderBill.setEntityType(EntityType.SUPPLIER_DS.ordinal());
			} else {
				logger.info("采购订单编号不能为空，flowEntId：" + flowEnt.getId());
				return result;
			}
			// 订单编号
			if (flowJson.containsKey(Constants.DS_ORDER_NUMBER)) {
				String orderId = flowJson.getString(Constants.DS_ORDER_NUMBER);
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("billNumber", Constants.ROP_EQ, orderId));
				List<ProductBills> orderBills = productBillsService.queryAllBySearchFilter(filter);
				dsOrder = dsOrderService.read(orderId);
				// 判断是否存在此订单账单
				if (CollectionUtils.isEmpty(orderBills)) {
					orderBill.setBillNumber(orderId);
					orderBill.setEntityId(dsOrder.getCustomerId());
					//查询订单详情获取产品id
					SearchFilter orderDetailFilter = new SearchFilter();
					orderDetailFilter.getRules().add(new SearchRule("orderId", Constants.ROP_EQ, orderId));
					List<DsOrderDetail> dsOrderDetails = dsOrderDetailService.queryAllBySearchFilter(filter);
					if (!CollectionUtils.isEmpty(dsOrderDetails)) {
						orderBill.setProductId(dsOrderDetails.get(0).getProductId());
					}
					orderBill.setReceivables(dsOrder.getSalesMoney());
					orderBill.setEntityType(EntityType.SUPPLIER_DS.ordinal());
					orderBill.setCost(dsOrder.getPurchaseCost());
					BigDecimal grossProfit = dsOrder.getSalesMoney().divide(dsOrder.getPurchaseCost());
					orderBill.setGrossProfit(grossProfit);
					// 生成订单账单
					if (!productBillsService.save(orderBill)) {
						return result;
					}
				}
			} else {
				logger.info("订单编号不能为空，flowEntId：" + flowEnt.getId());
				return result;
			}
			result = productBillsService.save(purchaseBill);
			logger.info("生成采购单记录" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("生成采购单记录异常，flowEntId：" + flowEnt.getId(), e);
		}
		return result;
	}
    
}
