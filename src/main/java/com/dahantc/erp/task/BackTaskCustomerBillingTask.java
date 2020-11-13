package com.dahantc.erp.task;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.enums.BillType;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.productBills.entity.ProductBills;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;

@Component
public class BackTaskCustomerBillingTask implements SchedulingConfigurer {

	private static final Logger logger = LogManager.getLogger(BackTaskCustomerBillingTask.class);

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private AcquireAccountReportDetailHandler acquireAccountReportDetailHandler;

	private static String CRON = "0 50 05 * * ?"; // 0秒30分0时 每日每月不管星期几

	private static final String billFileDir = "billfiles";

	private List<String> taskList; // 要生成账单流程的产品id列表

	private Map<String, Map<String, Object>> customerProductInfo; // 每个客户的产品的信息

	private String flowId;

	private int flowType;

	private String viewerRoleId;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// 执行任务
				logger.info("BackTask-CustomerBilling-Task is running...");
				if (getTodayTaskList()) {
					String billMonth = DateUtil.convert(DateUtil.getLastMonthFirst(), DateUtil.format4);
					boolean hasFlow = getFlowId();
					getCustomerProductInfo();
					// 生成账单
					for (String productId : taskList) {
						productBillsService.buildCustomerBill(productId, billMonth, false, true);
					}
					// 发起对账流程，默认勾选上月全部未对账账单
					if (hasFlow) {
						buildCheckBillFlow(taskList, billMonth);
					}
					taskList.clear();
					customerProductInfo.clear();
				}
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				// 任务触发，可修改任务的执行周期
				Date nextExecutionTime = null;
				logger.info("BackTask-CustomerBilling-Task：cron change to : " + CRON);
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
		Calendar c = Calendar.getInstance();
		int today = c.get(Calendar.DATE);
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select productId from CustomerProduct where billType =" + BillType.Auto.ordinal() + " and billTaskDay=:today";
		params.put("today", today);
		try {
			taskList = baseDao.findByhql(hql, params, 0);
		} catch (Exception e) {
			logger.error("获取今日自动账单产品异常", e);
		}
		return taskList != null && taskList.size() > 0;
	}

	/**
	 * 判断流程是否存在，获取流程id和type
	 * 
	 * @return
	 */
	private boolean getFlowId() {
		boolean result = false;
		try {
			// 获取销售账单流程的id及类型
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.CUSTOMER_CHECK_BILL_FLOW));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);
			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
				// 查找流程节点表中nodeid
				filter = new SearchFilter();
				filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, 0));
				filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
				List<FlowNode> nodeList = flowNodeService.queryAllBySearchFilter(filter);
				if (nodeList != null && nodeList.size() > 0) {
					result = true;
				} else {
					logger.error("'" + Constants.CUSTOMER_CHECK_BILL_FLOW + "'无节点，请重新设计");
				}
			} else {
				logger.error("系统无'" + Constants.CUSTOMER_CHECK_BILL_FLOW + "'，请尽快创建该流程");
			}
		} catch (Exception e) {
			logger.error("获取" + Constants.CUSTOMER_CHECK_BILL_FLOW + "异常", e);
		}
		return result;
	}

	/**
	 * 检查产品所在客户的所有产品是否出完上月账单，出完了就发起一个对账流程，默认勾选上月全部账单
	 * 
	 * @param productIdList
	 *            产品id列表
	 * @param billMonth
	 *            账单月份
	 */
	private void buildCheckBillFlow(List<String> productIdList, String billMonth) {
		long _start = System.currentTimeMillis();
		// 本次已发起对账流程的客户id，多个产品可能属于同一个客户，避免重复判断
		List<String> finished = new ArrayList<>();
		try {
			if (CollectionUtils.isEmpty(productIdList)) {
				logger.info("待发起对账流程的产品列表为空");
				return;
			}
			logger.info("自动发起对账流程开始，账单月份：" + billMonth + "，产品数：" + productIdList.size());
			long __start;
			// 遍历每个产品
			for (String productId : productIdList) {
				__start = System.currentTimeMillis();
				Map<String, Object> productInfo = customerProductInfo.getOrDefault(productId, new HashMap<>());
				String customerId = (String) productInfo.get("customerId");
				if (StringUtil.isBlank(customerId)) {
					logger.info("客户产品没有对应的客户，productId：" + productId);
					continue;
				}
				String ossUserId = (String) productInfo.get("ossUserId");
				if (StringUtil.isBlank(ossUserId)) {
					logger.info("客户没有对应的销售，productId：" + productId);
					continue;
				}
				if (finished.contains(customerId)) {
					continue;
				}
				String companyName = (String) productInfo.getOrDefault("companyName", "无公司名称");
				// 查上月的未对账账单账单
				String hql = "from ProductBills where entityId = :entityId and entityType = :entityType and wtime = :wtime and billStatus = :billStatus";
				Map<String, Object> params = new HashMap<>();
				params.put("entityId", customerId);
				params.put("entityType", EntityType.CUSTOMER.ordinal());
				params.put("wtime", DateUtil.convert(billMonth, DateUtil.format4));
				params.put("billStatus", BillStatus.NO_RECONCILE.ordinal());
				List<ProductBills> billList = productBillsService.findByhql(hql, params, 0);
				// 客户上个月有未对账账单，自动发起对账流程
				if (!CollectionUtils.isEmpty(billList)) {
					logger.info("客户上月有未对账账单" + billList.size() + "个，自动对上月所有未对账账单发起对账流程，customerId：" + customerId);
					// 新建流程实体
					String flowTitle = Constants.CUSTOMER_CHECK_BILL_FLOW + "(" + companyName + ")";
					FlowEnt flowEnt = flowEntService.buildFlowEnt(flowTitle, flowId, flowType, ossUserId, productId, customerId, viewerRoleId,
							EntityType.CUSTOMER.getCode());

					// 流程标签内容
					String flowMsg = "";
					JSONObject flowMsgJson = new JSONObject();
					JSONObject labelValue = new JSONObject();
					JSONArray billInfos = new JSONArray();
					JSONObject billTotal = new JSONObject();
					long platformSuccessCountTotal = 0L;
					BigDecimal platformAmountTotal = new BigDecimal(0);
					long customerSuccessCountTotal = 0L;
					BigDecimal customerAmountTotal = new BigDecimal(0);
					long checkedSuccessCountTotal = 0L;
					BigDecimal checkedAmountTotal = new BigDecimal(0);
					for (ProductBills bill : billList) {
						productInfo = customerProductInfo.getOrDefault(bill.getProductId(), new HashMap<>());
						String productName = (String) productInfo.getOrDefault("productName", "无产品名称");

						JSONObject billInfo = new JSONObject();
						billInfo.put("id", bill.getId());
						billInfo.put("productId", bill.getProductId());
						billInfo.put("entityId", bill.getEntityId());
						billInfo.put("billNumber", bill.getBillNumber());
						billInfo.put("billMonth", billMonth);
						billInfo.put("title", "账单-" + billMonth + "-" + companyName + "-" + productName);
						billInfo.put("remark", bill.getRemark());

						billInfo.put("platformSuccessCount", bill.getPlatformCount());
						billInfo.put("platformAmount", bill.getReceivables());
						platformSuccessCountTotal += bill.getPlatformCount();
						platformAmountTotal = platformAmountTotal.add(bill.getReceivables());
						if (bill.getPlatformCount() > 0) {
							billInfo.put("platformUnitPrice", bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP));
						} else {
							billInfo.put("platformUnitPrice", "0.000000");
						}

						billInfo.put("customerSuccessCount", bill.getSupplierCount());
						billInfo.put("customerAmount", bill.getReceivables());
						customerSuccessCountTotal += bill.getSupplierCount();
						customerAmountTotal = customerAmountTotal.add(bill.getReceivables());
						if (bill.getSupplierCount() > 0) {
							billInfo.put("customerUnitPrice", bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP));
						} else {
							billInfo.put("customerUnitPrice", "0.000000");
						}

						billInfo.put("checkedSuccessCount", bill.getSupplierCount());
						billInfo.put("checkedAmount", bill.getReceivables());
						checkedSuccessCountTotal += bill.getSupplierCount();
						checkedAmountTotal = checkedAmountTotal.add(bill.getReceivables());
						if (bill.getSupplierCount() > 0) {
							billInfo.put("checkedUnitPrice", bill.getUnitPrice().setScale(6, BigDecimal.ROUND_HALF_UP));
						} else {
							billInfo.put("checkedUnitPrice", "0.000000");
						}

						billInfos.add(billInfo);
					}

					// 更新账单状态为对账中
					billList = billList.stream().peek(bill -> bill.setBillStatus(BillStatus.RECONILING.ordinal())).collect(Collectors.toList());
					productBillsService.updateByBatch(billList);

					// 总计
					billTotal.put("platformSuccessCount", platformSuccessCountTotal);
					billTotal.put("platformAmount", platformAmountTotal);
					if (platformSuccessCountTotal > 0) {
						billTotal.put("platformUnitPrice", platformAmountTotal.divide(new BigDecimal(platformSuccessCountTotal), 6, BigDecimal.ROUND_HALF_UP));
					} else {
						billTotal.put("platformUnitPrice", "0.000000");
					}
					billTotal.put("customerSuccessCount", customerSuccessCountTotal);
					billTotal.put("customerAmount", customerAmountTotal);
					if (customerSuccessCountTotal > 0) {
						billTotal.put("customerUnitPrice", customerAmountTotal.divide(new BigDecimal(customerSuccessCountTotal), 6, BigDecimal.ROUND_HALF_UP));
					} else {
						billTotal.put("customerUnitPrice", "0.000000");
					}
					billTotal.put("checkedSuccessCount", checkedSuccessCountTotal);
					billTotal.put("checkedAmount", checkedAmountTotal);
					if (checkedSuccessCountTotal > 0) {
						billTotal.put("checkedUnitPrice", checkedAmountTotal.divide(new BigDecimal(checkedSuccessCountTotal), 6, BigDecimal.ROUND_HALF_UP));
					} else {
						billTotal.put("checkedUnitPrice", "0.000000");
					}

					labelValue.put("billInfos", billInfos);
					labelValue.put("billTotal", billTotal);

					/*long ___start = System.currentTimeMillis();
					Date today = new Date();
					String fileName = companyName + "-已选中账单的对账单-" + DateUtil.convert(today, DateUtil.format10) + ".pdf";
					String filePath = Constants.RESOURCE + File.separator + billFileDir + File.separator + customerId + File.separator
							+ DateUtil.convert(today, "yyyyMM") + File.separator + fileName;
					// 默认只生成电子账单
					List<String> defaultOptions = new ArrayList<>();
					defaultOptions.add(Constants.BILL_OPTION_BILL_FILE);
					// 生成对账单pdf
					String msg = productBillsService.createMergePdf(billList, filePath, billTotal, defaultOptions);
					if (StringUtil.isBlank(msg)) {
						logger.info("合并账单完成，耗时：" + (System.currentTimeMillis() - ___start));
						JSONObject billFile = new JSONObject();
						billFile.put("fileName", fileName);
						billFile.put("filePath", filePath);
						billFile.put("options", JSON.toJSONString(defaultOptions));
						labelValue.put("billFile", billFile);
					}*/

					flowMsgJson.put(Constants.UNCHECKED_BILL_KEY, labelValue);
					flowMsg = flowMsgJson.toJSONString();
					flowEnt.setFlowMsg(flowMsg);
					String deptId = (String) productInfo.getOrDefault("deptId", null);
					flowEnt.setDeptId(deptId);
					flowEnt.setRemark("上月账单已出完，自动发起流程");
					boolean result = flowEntService.save(flowEnt);
					if (result) {
						logger.info("创建" + Constants.CUSTOMER_CHECK_BILL_FLOW + "的流程实体成功，flowEntId：" + flowEnt.getId());
						// 记录本产品所在客户已经发起了对账流程
						finished.add(customerId);

						logger.info("请求生成数据分析报告，taskId：" + flowEnt.getId());
						acquireAccountReportDetailHandler.doAcquireReportDetail(flowEnt);

					} else {
						logger.info("创建" + Constants.CUSTOMER_CHECK_BILL_FLOW + "的流程实体失败，customerId：" + customerId);
					}
					logger.info("自动发起单个客户上月对账流程完成，customerId：" + customerId + "，耗时：" + (System.currentTimeMillis() - __start));
				} else {
					logger.info("客户上月无未对账账单，不自动发起对账流程，customerId：" + customerId);
				}
			}
			logger.info("自动发起所有客户上月对账流程完成，发起对账流程数：" + finished.size() + "，耗时：" + (System.currentTimeMillis() - _start));
		} catch (Exception e) {
			logger.error("自动发起对账流程异常", e);
		}
	}

	/**
	 * 获取每个客户和产品的信息
	 */
	private void getCustomerProductInfo() {
		try {
			// 查询每个产品的信息 {productId -> {productName, customerId, companyName,
			// ossUserId, productType, deptId, loginName}}
			customerProductInfo = customerProductService.getProductAndCustomerInfo();
		} catch (Exception e) {
			logger.error("获取每个客户和产品的信息异常", e);
			customerProductInfo = new HashMap<>();
		}
	}
}
