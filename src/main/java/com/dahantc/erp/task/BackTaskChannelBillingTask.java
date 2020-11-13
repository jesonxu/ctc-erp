package com.dahantc.erp.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.operate.service.OperateService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;

@Component
public class BackTaskChannelBillingTask implements SchedulingConfigurer {
	private static final Logger logger = LogManager.getLogger(BackTaskChannelBillingTask.class);

	@Autowired
	private IProductService productService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private OperateService operateService;

	@Autowired
	private IProductTypeService productTypeService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IUserService userService;

	private static String CRON = "0 0 4 3 * ?";

	private String flowId;

	private int flowType;

	private String nodeId;

	private String viewerRoleId;

	private String interBillFlowId;

	private int interBillFlowType;

	private String interBillNodeId;

	private String interViewerRoleId;

	private String flowName;

	private String interFlowName;

	private List<Product> productList;

	private String billMonth;

	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTask-ChannelBilling-Task is running...");
				Date startDate = DateUtil.getLastMonthFirst();
				billMonth = DateUtil.convert(startDate, DateUtil.format4);

				// 查询国际账单流程信息 和 普通账单流程信息
				getFlowId();
				getInterFlowId();

				if (getTodayTaskList()) {
					billApplicationProcessTask();
				} else {
					logger.info("未查询到需要自动生成账单的产品，任务退出");
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-ChannelBilling-Task：cron change to : " + CRON);
				CronTrigger trigger = new CronTrigger(CRON);
				nextExecutionTime = trigger.nextExecutionTime(triggerContext);
				return nextExecutionTime;
			}
		});
	}

	/**
	 * 获取今日要生成账单并发起账单流程的产品
	 * 
	 * @return
	 */
	private boolean getTodayTaskList() {
		try {
			List<Product> list = productService.queryAllBySearchFilter(null);
			if (CollectionUtils.isEmpty(list)) {
				return false;
			}
			for (Iterator<Product> iterator = list.iterator(); iterator.hasNext();) {
				Product product = iterator.next();
				boolean isInter = product.getProductType() == productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS);
				if (flowEntService.existSameFlow(isInter ? interBillFlowId : flowId, product.getProductId(), billMonth)) {
					iterator.remove();
				}
			}
			productList = list;
			return true;
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return false;
	}

	private boolean getFlowId() {
		boolean result = false;
		try {
			// 获取账单流程的id及类型
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.BILL_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
				flowName = flow.getFlowName();
				// 查找流程节点表中nodeid
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
				if (nodeList != null && nodeList.size() > 0) {
					nodeId = nodeList.get(0).getNodeId();
					result = true;
				} else {
					logger.info("'" + Constants.BILL_FLOW_NAME + "'无节点，请重新设计");
				}
			} else {
				logger.info("系统无'" + Constants.BILL_FLOW_NAME + "'，请尽快创建该流程");
			}
		} catch (Exception e) {
			logger.error("获取" + Constants.BILL_FLOW_NAME + "异常", e);
		}
		return result;
	}

	private boolean getInterFlowId() {
		boolean result = false;
		try {
			// 获取国际账单流程的id及类型
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.INTER_BILL_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				interBillFlowId = flow.getFlowId();
				interBillFlowType = flow.getFlowType();
				interViewerRoleId = flow.getViewerRoleId();
				interFlowName = flow.getFlowName();
				// 查找流程节点表中nodeid
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, interBillFlowId));
				List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
				if (nodeList != null && nodeList.size() > 0) {
					interBillNodeId = nodeList.get(0).getNodeId();
					result = true;
				} else {
					logger.info("'" + Constants.INTER_BILL_FLOW_NAME + "'无节点，请重新设计");
				}
			} else {
				logger.info("系统无'" + Constants.INTER_BILL_FLOW_NAME + "'，请尽快创建该流程");
			}
		} catch (Exception e) {
			logger.error("获取" + Constants.INTER_BILL_FLOW_NAME + "异常", e);
		}
		return result;
	}

	/** 根据通道每月发送量生成短信账单流程 */
	public void billApplicationProcessTask() {
		long _start = System.currentTimeMillis();
		logger.info("自动生成供应商客户流程开始，共" + productList.size() + "个产品");
		User admin = userService.findAdmin();
		int interProductType = productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS);
		productList.forEach(product -> {
			try {
				JSONObject json = new JSONObject();
				json.put(Constants.BILL_FLOW_MONTH_KEY, billMonth);

				boolean isInter = product.getProductType() == interProductType;

				// 新建flowEnt
				FlowEnt ent = new FlowEnt();
				ent.setFlowStatus(FlowStatus.NOT_AUDIT.ordinal());
				ent.setSupplierId(product.getSupplierId());
				ent.setFlowId(isInter ? interBillFlowId : flowId);

				// 设置本流程除了节点中的角色外，哪些角色能看
				ent.setViewerRoleId(isInter ? interViewerRoleId : viewerRoleId);

				Supplier supplier = supplierService.read(product.getSupplierId());

				String flowTitle = (isInter ? interFlowName : flowName) + "(";
				if (supplier != null) {
					flowTitle += supplier.getCompanyName() + "-";
				}
				flowTitle += product.getProductName();
				flowTitle += ")";
				ent.setFlowTitle(flowTitle);

				ent.setProductId(product.getProductId());

				// 账单流程基础数据
				String baseData = operateService.getBillBaseData(product, json.toString());

				if (StringUtil.isNotBlank(baseData)) {
					json.put(Constants.FLOW_BASE_DATA_KEY, baseData);
					JSONObject _baseData = JSON.parseObject(baseData);
					String successCount = _baseData.getString(Constants.DAHAN_SUCCESS_COUNT_KEY);
					if (Long.parseLong(successCount) == 0) {
						logger.info("产品id：" + product.getProductId() + "在账单月份：" + billMonth + "没有成功数，不生成账单");
						return;
					}

					String amount = _baseData.getString(Constants.DAHAN_PAYMENT_AMOUNT_KEY);

					// 国际
					if (product.getProductType() == interProductType) {
						json.put(Constants.SUPPLIER_SUCCESS_MONEY_KEY, amount);
						json.put(Constants.DAHAN_REAL_BILL_MONEY, amount);
						json.put(Constants.SUPPLIER_SUCCESS_COUNT_KEY, successCount);
					} else {
						String price = "";
						// 阶段价获取不到单价，手动用账单金额除以成功数得出
						if (_baseData.containsKey(Constants.DAHAN_PRICE_KEY)) {
							price = _baseData.getString(Constants.DAHAN_PRICE_KEY);
						} else {
							double amountValue = Double.parseDouble(amount);
							BigDecimal _amount = BigDecimal.valueOf(amountValue);
							long successCountValue = Long.parseLong(successCount);
							price = _amount.divide(new BigDecimal(successCountValue), 6, BigDecimal.ROUND_HALF_UP).toPlainString();
						}
						JSONObject moneyInfo = new JSONObject();
						moneyInfo.put("index", 1);
						moneyInfo.put("start", DateUtil.convert(DateUtil.convert4(billMonth), DateUtil.format1));
						moneyInfo.put("end", DateUtil.convert(DateUtil.getMonthFinal(DateUtil.convert4(billMonth)), DateUtil.format1));
						moneyInfo.put("success", successCount);
						moneyInfo.put("price", price);
						moneyInfo.put("total", amount);
						JSONArray jsonArray = new JSONArray();
						jsonArray.add(moneyInfo);
						json.put(Constants.SUPPLIER_SUCCESS_MONEY_KEY, jsonArray.toString());
					}
				} else {
					logger.info("产品id：" + product.getProductId() + "在账单月份：" + billMonth + "获取的平台数据为空");
					return;
				}

				ent.setNodeId(isInter ? interBillNodeId : nodeId);
				ent.setFlowMsg(json.toString());
				ent.setFlowType(isInter ? interBillFlowType : flowType);
				ent.setOssUserId(product.getOssUserId());
				ent.setRemark("");
				ent.setWtime(new Timestamp(System.currentTimeMillis()));
				ent.setEntityType(EntityType.SUPPLIER.ordinal());
				boolean result = flowEntService.save(ent);
				if (result) {
					FlowLog flowLog = new FlowLog();
					flowLog.setFlowEntId(ent.getId());
					flowLog.setFlowId(ent.getId());
					flowLog.setAuditResult(AuditResult.CREATED.getCode());
					JSONObject logMsg = new JSONObject();
					logMsg.put(Constants.FLOW_BASE_DATA_KEY, baseData);
					flowLog.setFlowMsg(logMsg.toJSONString());
					flowLog.setNodeId(ent.getNodeId());
					flowLog.setRemark("自动创建账单流程");
					flowLog.setOssUserId(null != admin ? admin.getOssUserId() : ent.getOssUserId());
					flowLogService.save(flowLog);
				}
			} catch (Exception e) {
				logger.error("自动创建账单流程异常，productId：" + product.getProductId() + "，billMonth：" + billMonth, e);
			}
		});
		logger.info("自动生成供应商客户流程耗时：【" + (System.currentTimeMillis() - _start) + "】毫秒");
	}

}