package com.dahantc.erp.vo.customerOperate.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.DateUtils;
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
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customerOperate.BuildBillFlowReqDto;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.PriceType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.flowtask.service.CommonFlowTask;
import com.dahantc.erp.util.BillInfo;
import com.dahantc.erp.util.BillInfo.DetailInfo;
import com.dahantc.erp.util.CreateBillExcelUtil;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.IText5PdfUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.contract.service.IContractService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerOperate.service.ICustomerOperateService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.deductionPrice.service.IDeductionPriceService;
import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
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
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl.TimeQuantum;
import com.dahantc.erp.vo.operate.service.OperateService;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("customerOperateService")
public class CustomerOperateServiceImpl implements ICustomerOperateService {

	private static Logger logger = LogManager.getLogger(CustomerOperateServiceImpl.class);

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private IDsOrderService dsOrderserver;

	@Autowired
	private CommonFlowTask commonFlowTask;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IModifyPriceService modifyPriceService;

	@Autowired
	private IDeductionPriceService deductionPriceService;

	@Autowired
	private IFlowEntService entService;

	@Autowired
	private OperateService operateService;

	@Autowired
	private IContractService contractService;

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IProductTypeService productTypeService;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IUserLeaveService userLeaveService;


	/**
	 * 流程申请
	 */
	@Override
	public BaseResponse<String> applyProcess(ApplyProcessReqDto reqDto, OnlineUser onlineUser) {
		try {
			User user = onlineUser.getUser();
			// 流程是否存在
			String flowId = reqDto.getFlowId();
			ErpFlow erpFlow = erpFlowService.read(flowId);
			if (erpFlow == null) {
				return BaseResponse.error("流程申请异常，流程不存在");
			}
			// 校验标签值是否合法
			String flowClass = erpFlow.getFlowClass();
			BaseFlowTask task = flowTaskManager.getFlowTasks(flowClass);
			if (task == null) {
				task = commonFlowTask;
			}
			String result = null;
			if (StringUtils.equals(flowClass, Constants.USER_LEAVE_FLOW_CLASS) || StringUtils.equals(flowClass, Constants.USER_OVERTIME_FLOW_CLASS)
			|| StringUtils.equals(flowClass,Constants.USER_OUTSIDE_FLOW_CLASS) || StringUtils.equals(flowClass,Constants.USER_BUSINESS_TRAVEL_FLOW_CLASS)) {
				//请假流程 加班流程 外勤流程 出差流程
				FlowEnt tmp = new FlowEnt();
				tmp.setOssUserId(user.getOssUserId());
				// 请假流程
				result = task.verifyFlowMsg(erpFlow, tmp, reqDto.getFlowMsg());
			} else if (StringUtils.equals(flowClass, Constants.BILL_WRITE_OFF_FLOW_CLASS)){
				// 销账流程
				boolean flag = false;
				JSONObject modifyJson = JSON.parseObject(reqDto.getFlowMsg(), Feature.OrderedField);
				Iterator<Entry<String, Object>> modifyJsonIterator = modifyJson.entrySet().iterator();
				while (modifyJsonIterator.hasNext()) {
					Entry<String, Object> entry = modifyJsonIterator.next();
					Object obj = entry.getValue().toString();
					if (obj != null && obj.toString().startsWith("[{")) {
						JSONArray arr = JSON.parseArray(obj.toString());
						if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("isHandApplay"))) {
							flag = true;
							arr.remove(0);
							modifyJson.put(entry.getKey(), arr.toString());
							reqDto.setFlowMsg(modifyJson.toString());
							break;
						}
					}
				}
				if (flag) {
					result = task.verifyFlowMsg(erpFlow, reqDto.getProductId(), reqDto.getFlowMsg());
				}
			} else {
				result = task.verifyFlowMsg(erpFlow, reqDto.getProductId(), reqDto.getFlowMsg());

			}
			if (StringUtils.isNotBlank(result)) {
				return BaseResponse.error(result);
			}
			// 销售账单流程，判断账单时间段是否有调价记录，是否已经申请过
			Map<TimeQuantum, ModifyPrice> mpRecord = null;
			if (flowClass.equals(Constants.CUSTOMER_BILL_FLOW_CLASS)) {
				JSONObject flowMsgObject = JSON.parseObject(reqDto.getFlowMsg());
				String billMonth = flowMsgObject.getString(Constants.BILL_FLOW_MONTH_KEY); // 账单月份
				String productId = reqDto.getProductId(); // 产品
				Date startDate = DateUtil.convert(billMonth + "-01", DateUtil.format1);
				Date endDate = DateUtil.getMonthFinal(startDate);
				mpRecord = modifyPriceService.getModifyPrice(productId, startDate, endDate);
				// 没有调价记录，不能创建账单
				if (CollectionUtils.isEmpty(mpRecord)) {
					return BaseResponse.error("账单流程申请失败，账单时间段没有调价信息");
				}
				// 校验这个月份是否已经申请过
				Boolean billed = checkBillFlowExist(flowId, reqDto.getProductId(), reqDto.getFlowMsg());
				if (billed) {
					return BaseResponse.error("该月份已经申请过账单流程，请勿重复申请");
				}
			}

			// 新建flowEnt
			FlowEnt ent = new FlowEnt();
			ent.setFlowStatus(FlowStatus.NOT_AUDIT.ordinal());
			ent.setSupplierId(reqDto.getSupplierId());
			// 设置本流程除了节点中的角色外，哪些角色能看
			ent.setViewerRoleId(erpFlow.getViewerRoleId());
			ent.setDeptId(user.getDeptId());
			ent.setPlatform(reqDto.getPlatform());
			String remark = "";
			String flowMsg = reqDto.getFlowMsg();
			// 校验账单信息有没有超出剩余金额
			BaseResponse<String> verifyResult = operateService.verifyBill(flowId, flowMsg, "");
			if (verifyResult != null) {
				return verifyResult;
			}
			// 调价流程附上原来价格
			if (StringUtils.isNotBlank(flowMsg)) {
				JSONObject flowMsgJson = JSONObject.parseObject(flowMsg, Feature.OrderedField);
				flowMsgJson.put("entityType", EntityType.CUSTOMER.ordinal());
				operateService.setAdjustFlowBeforePrice(flowMsgJson, reqDto.getProductId(), flowClass);
				flowMsg = flowMsgJson.toJSONString();
				remark = flowMsgJson.getString("备注");
			}

			ent.setFlowId(erpFlow.getFlowId());
			String flowTitle = erpFlow.getFlowName() + "(";
			Customer customer = customerService.read(reqDto.getSupplierId());
			if (customer != null) {
				flowTitle += customer.getCompanyName();
			}
			CustomerProduct product = customerProductService.read(reqDto.getProductId());
			if (product != null) {
				flowTitle += "-";
				flowTitle += product.getProductName() + ")";
				ent.setProductId(product.getProductId());
			} else {
				flowTitle += ")";
			}
			ent.setFlowTitle(flowTitle);
			String startNodeId = erpFlow.getStartNodeId();
			FlowNode flowNode = flowNodeService.read(startNodeId);
			// 发起流程后，流程到第2节点，保存一条发起节点的log
			if (flowNode != null) {
				ent.setNodeId(flowNode.getNextNodeId());
			}
			// 账单流程基础数据
			String baseData = getBillBaseData(reqDto, product, flowMsg, mpRecord);
			if (StringUtil.isNotBlank(baseData)) {
				JSONObject flowMsgJson = JSON.parseObject(flowMsg, Feature.OrderedField);
				flowMsgJson.put(Constants.FLOW_BASE_DATA_KEY, baseData);
				flowMsg = flowMsgJson.toJSONString();
			}
			if (StringUtils.equals(Constants.CONTRACT_FLOW_CLASS, flowClass)) {
				// 合同流程，生成合同编号
				JSONObject flowMsgJson = JSON.parseObject(flowMsg, Feature.OrderedField);
				if (flowMsgJson.containsKey(Constants.CONTRACT_NUMBER)) {
					if (product != null) {
						String no = contractService.buildContractNumber(EntityType.CUSTOMER.ordinal(), product.getProductType());
						if (StringUtil.isNotBlank(no)) {
							flowMsgJson.put(Constants.CONTRACT_NUMBER, no);
							logger.info("生成合同编号成功：" + no);
						} else {
							return BaseResponse.error("生成合同编号失败");
						}
					} else {
						return BaseResponse.error("产品类型错误");
					}
				}
				flowMsg = flowMsgJson.toString();
			} else if (StringUtils.equals(Constants.DS_ORDER_FLOW_CLASS, flowClass)) {
				// 电商购销单流程，生成订单编号
				JSONObject flowMsgJson = JSON.parseObject(flowMsg, Feature.OrderedField);
				if (flowMsgJson.containsKey(Constants.DS_ORDER_NUMBER)) {
					Date date = new Date();
					String no = dsOrderserver.buildOrderNo(user, date, flowMsgJson.getString(Constants.DS_SEND_TYPE));
					flowMsgJson.put(Constants.DS_ORDER_NUMBER, no);
					logger.info("成功生成订单编号：" + no);
				}
				flowMsg = flowMsgJson.toString();
			}
			//请假流程日期带有中文，处理中文字符串
			JSONObject flowMsgJson = JSON.parseObject(flowMsg);
			if(StringUtils.equals(flowClass, Constants.USER_LEAVE_FLOW_CLASS)){
				String dateFrom = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_FROM);
				String dateTo = flowMsgJson.getString(Constants.USER_LEAVE_TIME_KEY_TO);
				if(verifyHasCHN(dateFrom) || verifyHasCHN(dateTo)){
					//存在中文字符
					dateFrom = getDateFormate(dateFrom,true);
					dateTo = getDateFormate(dateTo,false);
					flowMsgJson.put(Constants.USER_LEAVE_TIME_KEY_FROM,dateFrom);
					flowMsgJson.put(Constants.USER_LEAVE_TIME_KEY_TO,dateTo);
				}
				//重新计天数
				BigDecimal days = userLeaveService.getLeaveDaysByTimeShot(DateUtil.convert(dateFrom,DateUtil.format2), DateUtil.convert(dateTo,DateUtil.format2));
				flowMsgJson.put(Constants.USER_LEAVE_DAYS,days);
			}
			ent.setFlowType(erpFlow.getFlowType());
			ent.setOssUserId(user.getOssUserId());
			ent.setRemark(remark);
			ent.setWtime(new Timestamp(System.currentTimeMillis()));
			//TODO
			ent.setFlowMsg(flowMsgJson.toString());
			ent.setEntityType(EntityType.CUSTOMER.ordinal());
			flowEntService.save(ent);

			// 生成日志记录
			FlowLog flowLog = new FlowLog();
			flowLog.setFlowId(ent.getFlowId());
			flowLog.setFlowEntId(ent.getId());
			flowLog.setAuditResult(AuditResult.CREATED.getCode());
			flowLog.setNodeId(startNodeId);
			flowLog.setOssUserId(ent.getOssUserId());
			flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
			flowLog.setRemark("");
			flowLog.setFlowMsg(flowMsgJson.toString());
			flowLog.setPlatform(reqDto.getPlatform());
			flowLogService.save(flowLog);
			task.flowMsgModify(AuditResult.CREATED.getCode(), ent);

			// 查询下一个节点
			String nodeId = ent.getNodeId();
			if (StringUtil.isNotBlank(nodeId)) {
				FlowNode nextNode = flowNodeService.read(ent.getNodeId());
				if (nextNode != null) {
					new Thread(() -> {
						logger.info("成功发起流程，自动审核下一个节点：" + nextNode.getNodeName());
						try {
							// 休眠一秒，等待流程实体更新
							Thread.sleep(1000);
						} catch (Exception e) {
							logger.error("", e);
						}
						// 根据条件 自动通过下一个节点
						operateService.automaticAuditByCondition(ent, nextNode);
					}).start();
				}
			}
		} catch (Exception e) {
			logger.error("流程申请异常", e);
			return BaseResponse.error("流程申请异常");
		}
		return BaseResponse.success("申请成功");
	}

	/**
	 * 校验是否已经申请过账单流程
	 *
	 * @param flowId
	 *            流程id
	 * @param productId
	 *            产品id
	 * @param flowMsg
	 *            获取月份
	 * @return Boolean
	 */
	private Boolean checkBillFlowExist(String flowId, String productId, String flowMsg) {
		if (StringUtil.isNotBlank(flowMsg)) {
			JSONObject msgObject = JSON.parseObject(flowMsg);
			String billMonth = msgObject.getString(Constants.BILL_FLOW_MONTH_KEY);
			if (StringUtil.isNotBlank(billMonth)) {
				try {
					return entService.existSameFlow(flowId, productId, "\"" + Constants.BILL_FLOW_MONTH_KEY + "\":\"" + billMonth + "\"");
				} catch (ServiceException e) {
					logger.error("检查是否已经存在指定月份的账单流程时异常", e);
				}
			}
		}
		return false;
	}

	/**
	 * 创建销售账单流程
	 * 
	 * @param reqDto
	 *            提交数据
	 * @param flowId
	 *            流程id
	 * @param flowType
	 *            流程类型
	 * @return
	 */
	@Override
	public BaseResponse<String> buildBillFlow(BuildBillFlowReqDto reqDto, String flowId, int flowType, String viewerRoleId) {
		String msg;
		try {
			String billMonth = reqDto.getBillMonth(); // 月份
			String productId = reqDto.getProductId(); // 产品
			Date startDate = DateUtil.convert(billMonth + "-01", DateUtil.format1);
			Date endDate = DateUtil.getMonthFinal(startDate);
			Map<TimeQuantum, ModifyPrice> mpRecord = modifyPriceService.getModifyPrice(productId, startDate, endDate);
			// 没有调价记录，不能创建账单
			if (CollectionUtils.isEmpty(mpRecord)) {
				return BaseResponse.error();
			}
			// 校验这个月份是否已经申请过
			Boolean billed = checkBillFlowExist(flowId, productId, "{\"" + Constants.BILL_FLOW_MONTH_KEY + "\":\"" + reqDto.getBillMonth() + "\"}");
			if (billed) {
				logger.info("该月份已经申请过账单流程，不自动发起，账单月份：" + reqDto.getBillMonth() + "，productId：" + productId);
				return BaseResponse.error();
			}
			CustomerProduct product = customerProductService.read(productId);
			String customerId = product.getCustomerId(); // 客户
			Customer customer = customerService.read(customerId);
			User user = userService.read(customer.getOssuserId());
			int interProductType = productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS);
			String title = product.getProductType() == interProductType ? Constants.CUSTOMER_INTER_BILL_FLOW_NAME
					: Constants.CUSTOMER_BILL_FLOW_NAME;
			title += "-" + customer.getCompanyName() + "-" + product.getProductName() + "-" + billMonth;
			// 创建流程实体
			FlowEnt flowEnt = buildFlowEnt(title, flowId, flowType, user.getOssUserId(), productId, customerId, viewerRoleId);
			flowEnt.setDeptId(user.getDeptId());
			JSONObject flowMsg = new JSONObject(); // 流程标签内容
			flowMsg.put(Constants.BILL_FLOW_MONTH_KEY, billMonth); // 账单月份
			// 平台账单数据
			String baseData = getBillBaseData(null, product, flowMsg.toJSONString(), mpRecord);
			if (StringUtil.isNotBlank(baseData)) {
				flowMsg.put(Constants.FLOW_BASE_DATA_KEY, baseData);
				JSONObject _baseData = JSON.parseObject(baseData);
				String successCount = _baseData.getString(Constants.DAHAN_SUCCESS_COUNT_KEY);
				if (Long.parseLong(successCount) == 0) {
					logger.info("产品id：" + productId + "在账单月份：" + billMonth + "没有成功数，不生成账单");
					return BaseResponse.error();
				}
				String amount = _baseData.getString(Constants.DAHAN_PAYMENT_AMOUNT_KEY);
				// 国际
				if (product.getProductType() == interProductType) {
					flowMsg.put(Constants.DAHAN_CUSTOMER_BILL_MONEY, amount);
					flowMsg.put(Constants.DAHAN_REAL_BILL_MONEY, amount);
					flowMsg.put(Constants.DAHAN_CUSTOMER_SCCUESS_COUNT, successCount);
				} else {
					String price = "";
					// 阶段价获取不到单价，手动用账单金额除以成功数得出
					if (_baseData.containsKey(Constants.DAHAN_PRICE_KEY)) {
						price = _baseData.getString(Constants.DAHAN_PRICE_KEY);
					} else {
						double amountValue = Double.parseDouble(amount);
						BigDecimal _amount = BigDecimal.valueOf(amountValue);
						long successCountValue = Long.parseLong(successCount);
						price = _amount.divide(new BigDecimal(successCountValue), 4, BigDecimal.ROUND_HALF_UP).toPlainString();
					}
					flowMsg.put(Constants.DAHAN_CUSTOMER_BILL_MONEY, successCount + "," + price + "," + amount);
					flowMsg.put(Constants.DAHAN_REAL_BILL_MONEY, successCount + "," + price + "," + amount);
				}
			} else {
				logger.info("产品id：" + productId + "在账单月份：" + billMonth + "获取的平台数据为空");
				return BaseResponse.error();
			}
			JSONArray fileArray = new JSONArray(); // 附件
			JSONObject file;
			// 账单PDF附件
			if (StringUtils.isNotBlank(reqDto.getPdfFileName()) && StringUtils.isNotBlank(reqDto.getPdfFilePath())) {
				file = new JSONObject();
				file.put("fileName", reqDto.getPdfFileName());
				file.put("filePath", reqDto.getPdfFilePath());
				fileArray.add(file);
			}
			// 账单Excel附件
			if (StringUtils.isNotBlank(reqDto.getExcelFileName()) && StringUtils.isNotBlank(reqDto.getExcelFilePath())) {
				file = new JSONObject();
				file.put("fileName", reqDto.getExcelFileName());
				file.put("filePath", reqDto.getExcelFilePath());
				fileArray.add(file);
			}
			flowMsg.put("附件", fileArray);
			flowEnt.setFlowMsg(flowMsg.toJSONString());
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
				flowLog.setFlowMsg(flowMsg.toJSONString());
				flowLogService.save(flowLog);
			}
			msg = "生成'" + title + "'" + (result ? "成功" : "失败");
			logger.info(msg);
		} catch (Exception e) {
			logger.error("生成'" + Constants.CUSTOMER_BILL_FLOW_NAME + "'异常：", e);
			return BaseResponse.error("生成'" + Constants.CUSTOMER_BILL_FLOW_NAME + "'异常");
		}
		return BaseResponse.success(msg);
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
		flowEnt.setViewerRoleId(viewerRoleId);
		flowEnt.setEntityType(EntityType.CUSTOMER.ordinal());
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
	 * 获取账单的基础数据（平台的基础数据）
	 *
	 * @param product
	 *            产品
	 * @return JSONObject
	 * @throws Exception
	 */
	private String getBillBaseData(ApplyProcessReqDto reqDto, CustomerProduct product, String flowMsg, Map<TimeQuantum, ModifyPrice> mpRecord)
			throws Exception {
		if (product == null || StringUtil.isBlank(flowMsg)) {
			return null;
		}
		JSONObject flowMsgObject = JSON.parseObject(flowMsg);
		String billTime = flowMsgObject.getString(Constants.BILL_FLOW_MONTH_KEY);
		if (StringUtil.isBlank(billTime)) {
			return null;
		}
		// 获取产品下的账号
		String accounts = product.getAccount();
		Date startDate = DateUtil.convert(billTime + "-01", DateUtil.format1);
		Date endDate = DateUtil.getMonthFinal(startDate);
		// 产品类型
		int productType = product.getProductType();
		// 成功数
		Map<String, Long> accountSuccessMap = new HashMap<>(); // 账号 --> 发送量
		String dataStr = null;
		if (productType == productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS)) {
			Map<Date, Map<String, Long>> countryCountMap = getPlatformSuccessDateCount4Inter(productType, accounts, startDate, endDate, accountSuccessMap);
			dataStr = getFlowBaseData4Inter(product, countryCountMap, startDate, endDate, mpRecord);
		} else {
			Map<Date, Long> platformSuccessCountMap = getPlatformSuccessDateCount(productType, accounts, startDate, endDate, accountSuccessMap);
			dataStr = getFlowBaseData(product, platformSuccessCountMap, startDate, endDate, mpRecord);
		}
		// 创建 pdf 和 excel文件
		try {
			// {BILL_PRICE_INFO_KEY: {一个调价的所有日期区间, 成功数, 单价信息},
			//	平台成功数: ,
			//	单价(元): ,
			//	平台账单金额: ,
			//	备注: }
			if (StringUtils.isNotBlank(dataStr) && !CollectionUtils.isEmpty(accountSuccessMap)) {
				JSONObject json = JSON.parseObject(dataStr, Feature.OrderedField);
				BigDecimal count = new BigDecimal(json.get(Constants.DAHAN_SUCCESS_COUNT_KEY).toString());
				BigDecimal fee = new BigDecimal(json.get(Constants.DAHAN_PAYMENT_AMOUNT_KEY).toString());
				Customer customer = customerService.read(product.getCustomerId());
				User user = userService.read(product.getOssUserId());
				BillInfo billInfo = getBillData(customer, user, product, accountSuccessMap,
						count.signum() == 0 ? BigDecimal.ZERO : fee.divide(count, 4, BigDecimal.ROUND_HALF_UP), startDate);

				if (reqDto != null) {
					JSONObject msgJson = null;
					try {
						msgJson = JSON.parseObject(reqDto.getFlowMsg());
					} catch (Exception e) {
					}

					String realStr = msgJson.getString(Constants.PAYMENT_AMOUNT_KEY);
					String customerSuccessCountStr = msgJson.getString(Constants.CUSTOMER_SUCCESS_COUNT_KEY);
					if (StringUtils.isNotBlank(realStr)) {
						String[] arr = realStr.split(",");
						if (arr.length == 3 && NumberUtils.isParsable(arr[0]) && NumberUtils.isParsable(arr[1]) && NumberUtils.isParsable(arr[2])) {
							if (billInfo.getRealFeeInfo() == null) {
								billInfo.setRealFeeInfo(new DetailInfo());
							}
							billInfo.getRealFeeInfo().setFeeCount(new BigDecimal(arr[0]));
							billInfo.getRealFeeInfo().setUnitPrice(new BigDecimal(arr[1]));
							billInfo.getRealFeeInfo().setFee(new BigDecimal(arr[2]));
						} else if (StringUtils.isNoneBlank(realStr, customerSuccessCountStr) && NumberUtils.isParsable(realStr)
								&& NumberUtils.isParsable(customerSuccessCountStr)) {
							if (billInfo.getRealFeeInfo() == null) {
								billInfo.setRealFeeInfo(new DetailInfo());
							}
							BigDecimal feeCount = new BigDecimal(customerSuccessCountStr);
							BigDecimal f = new BigDecimal(realStr);
							billInfo.getRealFeeInfo().setFeeCount(feeCount);
							billInfo.getRealFeeInfo().setUnitPrice(f.divide(feeCount, 4, BigDecimal.ROUND_HALF_UP));
							billInfo.getRealFeeInfo().setFee(f);
						}
					}
				}
				// 获取银行信息
				BankAccount bankAccount = bankAccountService.read(customer.getBankAccountId());
				if (bankAccount == null) {
					throw new Exception("客户：" + customer.getCompanyName() + "，没有关联我方银行信息");
				}

				// 文件存放路径
				String fileSavePath = Constants.RESOURCE + File.separator + "billfiles" + File.separator + customer.getCustomerId() + File.separator
						+ product.getProductId() + File.separator + DateUtil.convert(startDate, "yyyyMM");
				String excelFile = fileSavePath + File.separator + billInfo.getBillNumber() + ".xls";
				String pdfFile = IText5PdfUtil.getNextCopyFileName(fileSavePath + File.separator + billInfo.getBillNumber() + ".pdf");
				// 写入json
				File jsonFile = new File(fileSavePath + File.separator + billInfo.getBillNumber() + "json.txt");
				if (!jsonFile.exists()) {
					if (!jsonFile.getParentFile().exists()) {
						jsonFile.getParentFile().mkdirs();
					}
				}
				try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(jsonFile, true), "UTF-8");
						BufferedWriter buffer = new BufferedWriter(osw);) {
					buffer.write(JSON.toJSONString(billInfo));
					buffer.flush();
				} catch (Exception e) {
					logger.error("", e);
				}
				
				User saler = null;
				if (StringUtils.isNotBlank(customer.getOssuserId())) {
					saler = userService.read(customer.getOssuserId());
				}
				
				CreateBillExcelUtil.createBillExcel(billInfo, bankAccount, excelFile, customer, saler);
				IText5PdfUtil.createBillPdf(billInfo, bankAccount, pdfFile, customer, saler);
				json.put(Constants.DAHAN_BILL_NUM_KEY, billInfo.getBillNumber());
				json.put(Constants.DAHAN_BILL_FILE_KEY, excelFile + ";" + pdfFile);
				dataStr = json.toString();
			} else { // 没有发送量也要生成账单编号
				JSONObject baseJson = null;
				if (StringUtils.isNotBlank(dataStr) && dataStr.contains("{")) {
					baseJson = JSON.parseObject(dataStr);
				} else {
					baseJson = new JSONObject();
				}
				baseJson.put(Constants.DAHAN_BILL_NUM_KEY,
						productBillsService.getBillNumber(DateUtil.convert(startDate, DateUtil.format4), Constants.CUST_PRODUCT_BILL_NUM_KEY));
				dataStr = baseJson.toString();
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		return dataStr;
	}

	/**
	 * 获取对账单内容
	 * 
	 * @param customer
	 *            客户
	 * @param user
	 *            对应销售
	 * @param product
	 *            产品
	 * @param accountSuccessMap
	 *            账号成功数map {loginName -> 成功数}
	 * @param unitPrice
	 *            产品平均单价
	 * @param billDate
	 *            账单月份
	 * @return
	 */
	private BillInfo getBillData(Customer customer, User user, CustomerProduct product, Map<String, Long> accountSuccessMap, BigDecimal unitPrice,
			Date billDate) {
		BillInfo billInfo = new BillInfo();
		billInfo.setBillDate(billDate);
		billInfo.setBillNumber(productBillsService.getBillNumber(DateUtil.convert(billDate, DateUtil.format4), Constants.CUST_PRODUCT_BILL_NUM_KEY));
		billInfo.setCompanyName(customer.getCompanyName());
		billInfo.setContactsName(customer.getContactName());
		billInfo.setPhone(customer.getContactPhone());
		billInfo.setCreateDate(DateUtil.getDateStartDateTime(new Date()));
		Calendar cal = Calendar.getInstance();
		cal.setTime(billDate);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, product.getBillPeriod() + 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		billInfo.setFinalPayDate(cal.getTime());
		billInfo.setSaleName(user.getRealName());
		String phone = null;
		if (StringUtils.isNotBlank(user.getContactMobile())) {
			phone = user.getContactMobile().split(",")[0];
		}
		if (StringUtils.isBlank(phone) && StringUtils.isNotBlank(user.getContactPhone())) {
			phone = user.getContactMobile().split(",")[0];
		}
		billInfo.setPhone(phone);
		billInfo.setAccountInfos(new ArrayList<>());
		// 总计信息
		DetailInfo realFeeInfo = new DetailInfo();
		realFeeInfo.setFee(new BigDecimal(0));
		realFeeInfo.setFeeCount(new BigDecimal(0));
		realFeeInfo.setUnitPrice(unitPrice);
		// 每个账号的成功数等信息
		accountSuccessMap.forEach((account, successCount) -> {
			DetailInfo detail = new DetailInfo();
			detail.setUnitPrice(unitPrice);
			detail.setAccountName(account);
			detail.setFeeCount(new BigDecimal(successCount));
			detail.setFee(detail.getUnitPrice().multiply(detail.getFeeCount()));
			realFeeInfo.setFee(realFeeInfo.getFee().add(detail.getFee()));
			realFeeInfo.setFeeCount(realFeeInfo.getFeeCount().add(detail.getFeeCount()));
			billInfo.getAccountInfos().add(detail);
		});
		billInfo.setRealFeeInfo(realFeeInfo);
		return billInfo;
	}

	/**
	 * 根据产品类型、产品id、时间段 获取成功量(国际)
	 *
	 * @param productType
	 *            产品类型
	 * @param accounts
	 *            产品下的账号
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return Long
	 */
	@Override
	public Map<Date, Map<String, Long>> getPlatformSuccessDateCount4Inter(int productType, String accounts, Date startDate, Date endDate,
			Map<String, Long> accountSuccessMap) {
		Map<Date, Map<String, Long>> countryCountMap = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		List<String> loginNameList = StringUtil.isBlank(accounts) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(accounts.split("\\|")));
		loginNameList = loginNameList.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		if (loginNameList.size() == 0) {
			logger.info("该产品下没有有效账号");
			return countryCountMap;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select statsDate, countryCode, sum(successCount), loginName from CustomerProductTj "
				+ " where loginName in :loginNameList and statsDate >=: startDate and statsDate <: endDate and productType = :productType "
				+ " group by statsDate, countryCode";
		params.put("loginNameList", loginNameList);
		params.put("productType", productType);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		// 查询短信统表数据
		List<Object[]> smsCountList = null;
		try {
			smsCountList = baseDao.findByhql(hql, params, 0);
			if (!ListUtils.isEmpty(smsCountList)) {
				smsCountList.forEach(item -> {
					Object[] objs = (Object[]) item;
					if (objs.length == 4) {
						Date statsDate = (Date) objs[0];
						if (countryCountMap.get(statsDate) == null) {
							countryCountMap.put(statsDate, new HashMap<>());
						}
						long lastCountryCount = 0;
						if (countryCountMap.get(statsDate).get((String) objs[1]) != null) {
							lastCountryCount = countryCountMap.get(statsDate).get((String) objs[1]);
						}
						countryCountMap.get(statsDate).put((String) objs[1], lastCountryCount + ((Number) objs[2]).longValue());
						if (accountSuccessMap != null) {
							long lastAccountCount = 0;
							if (accountSuccessMap.get((String) objs[3]) != null) {
								lastAccountCount = accountSuccessMap.get((String) objs[3]);
							}
							accountSuccessMap.put((String) objs[3], lastAccountCount + ((Number) objs[2]).longValue());
						}
					}
				});
			}
		} catch (BaseException e) {
			logger.error("查询平台成功数时错误，错误信息：", e);
		}
		return countryCountMap;
	}

	/**
	 * 国际账单基础数据
	 *
	 * @param product
	 *            产品信息
	 * @param countryCountMap
	 *            成功数
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 */
	@Override
	public String getFlowBaseData4Inter(CustomerProduct product, Map<Date, Map<String, Long>> countryCountMap, Date startDate, Date endDate,
			Map<TimeQuantum, ModifyPrice> mpRecord) {
		try {
			JSONArray timeQuantumPriceArrInfo = new JSONArray();
			if (CollectionUtils.isEmpty(mpRecord)) {
				mpRecord = modifyPriceService.getModifyPrice(product.getProductId(), startDate, endDate);
			}
			if (!CollectionUtils.isEmpty(countryCountMap) && !CollectionUtils.isEmpty(mpRecord)) {
				StringBuffer markMsg = new StringBuffer();
				Map<TimeQuantum, Map<String, Double>> interPricesMap = modifyPriceService.getInterPrices(new ArrayList<>(mpRecord.values()), startDate,
						endDate);
				Date lastEnd = null;
				if (CollectionUtils.isEmpty(mpRecord)) {
					markMsg.append(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
				} else {
					lastEnd = startDate;
					for (TimeQuantum timeQuantum : mpRecord.keySet()) {
						if (lastEnd != null) {
							if (DateUtil.getNextDayStart(lastEnd).getTime() < timeQuantum.getStartDate().getTime()) {
								markMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
										+ DateUtil.convert(DateUtil.getLastDayStart(timeQuantum.getStartDate()), DateUtil.format1) + "，");
							}
						}
						lastEnd = timeQuantum.getEndDate();
					}
					if (!StringUtils.equals(DateUtil.convert(lastEnd, DateUtil.format1), DateUtil.convert(endDate, DateUtil.format1))) {
						markMsg.append(
								DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
					}
				}
				if (markMsg.length() > 0) {
					markMsg.insert(0, "无调价信息时间段：");
				}
				JSONObject json = new JSONObject();
				long successCount = 0;
				BigDecimal paymentAmount = new BigDecimal(0);
				for (Entry<TimeQuantum, Map<String, Double>> entry : interPricesMap.entrySet()) {
					JSONObject timeQuantumJsonInfo = new JSONObject();
					timeQuantumJsonInfo.put("timeQuantum", DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1) + "~"
							+ DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1));
					timeQuantumJsonInfo.put("modifyPriceInfo", mpRecord.get(entry.getKey()).getRemark());
					markMsg.append(DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1)).append("~")
							.append(DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1)).append("：");
					Map<String, Double> priceMap = entry.getValue();
					Date sDate = entry.getKey().getStartDate();
					Date eDate = entry.getKey().getEndDate();
					List<String> withOutPriceInfo = new ArrayList<>();
					long sectionCount = 0;
					for (; !sDate.after(eDate); sDate = DateUtil.getNextDayStart(sDate)) {
						Map<String, Long> successCountMap = countryCountMap.get(sDate);
						if (!CollectionUtils.isEmpty(successCountMap)) {
							for (Entry<String, Long> entry2 : successCountMap.entrySet()) {
								Double price = priceMap.get(entry2.getKey());
								if (price == null) {
									withOutPriceInfo.add(entry2.getKey() + "：" + entry2.getValue());
								} else {
									sectionCount += entry2.getValue();
									successCount += entry2.getValue();
									paymentAmount = paymentAmount.add(new BigDecimal(price).multiply(new BigDecimal(entry2.getValue())));
								}
							}
						}
					}
					timeQuantumJsonInfo.put("successCount", sectionCount);
					timeQuantumPriceArrInfo.add(timeQuantumJsonInfo);
					if (!CollectionUtils.isEmpty(withOutPriceInfo)) {
						markMsg.append("无单价国家发送量如下：【").append(StringUtils.join(withOutPriceInfo.iterator(), "、")).append("】，");
					} else {
						markMsg.append("发送量：【").append(sectionCount).append("】，");
					}
				}
				json.put(Constants.BILL_PRICE_INFO_KEY, timeQuantumPriceArrInfo.toJSONString());
				json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successCount + "");
				json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, paymentAmount.setScale(2, BigDecimal.ROUND_CEILING).toString());
				if (markMsg.length() > 0) {
					json.put(Constants.DAHAN_REMARK_KEY, markMsg.toString());
				}
				return json.toJSONString();
			}
		} catch (Exception e) {
			logger.error("生成国际账单流程基础数据异常", e);
		}
		return null;
	}

	/**
	 * 根据产品类型、账号、时间段 获取每天的成功数
	 *
	 * @param productType
	 *            产品类型
	 * @param accounts
	 *            产品下的账号
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param accountSuccessMap
	 *            账号的成功数map {loginName -> 成功数}
	 * 
	 * @return 每天的成功数map {Date -> 成功数}
	 */
	@Override
	public Map<Date, Long> getPlatformSuccessDateCount(int productType, String accounts, Date startDate, Date endDate, Map<String, Long> accountSuccessMap) {
		Map<Date, Long> platformSuccessCountMap = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		List<String> loginNameList = StringUtil.isBlank(accounts) ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(accounts.split("\\|")));
		loginNameList = loginNameList.stream().map(String::trim).filter(StringUtils::isNotBlank).collect(Collectors.toList());
		if (loginNameList.size() == 0) {
			logger.info("该产品下没有有效账号");
			return platformSuccessCountMap;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(successCount), statsDate, loginName from CustomerProductTj "
				+ " where loginName in :loginNameList and statsDate>=:startDate and statsDate<:endDate and productType=" + productType + " group by statsDate";
		params.put("loginNameList", loginNameList);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		// 查询短信统表数据
		List<Object[]> smsCountList = null;
		try {
			smsCountList = baseDao.findByhql(hql, params, 0);
		} catch (BaseException e) {
			logger.error("查询平台成功数时错误，错误信息：", e);
		}
		if (smsCountList != null && smsCountList.size() > 0) {
			for (Object[] objects : smsCountList) {
				// 累计每天的成功数
				Date date = (Date) objects[1];
				long dateCount = platformSuccessCountMap.getOrDefault(date, 0L);
				platformSuccessCountMap.put(date, dateCount + ((Number) objects[0]).longValue());
				if (accountSuccessMap != null) {
					// 累计每个账号的成功数
					String loginName = (String) objects[2];
					long accountCount = accountSuccessMap.getOrDefault(loginName, 0L);
					accountSuccessMap.put(loginName, accountCount + ((Number) objects[0]).longValue());
				}
			}
		}
		return platformSuccessCountMap;
	}

	private long getSuccessCountByDate(Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate) {
		long successCount = 0L;
		for (; !startDate.after(endDate); startDate = DateUtil.getNextDayStart(startDate)) {
			for (Entry<Date, Long> entry : platformSuccessCountMap.entrySet()) {
				if (StringUtils.equals(DateUtil.convert(entry.getKey(), DateUtil.format1), DateUtil.convert(startDate.getTime(), DateUtil.format1))
						&& entry.getValue() != null) {
					successCount += entry.getValue();
					break;
				}
			}
		}
		return successCount;
	}

	/**
	 * 获取产品的平台统计数据
	 *
	 * @param product
	 *            产品信息
	 * @param platformSuccessCountMap
	 *            每天的成功数map {Date -> 成功数}
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param mpRecord
	 *            调价记录
	 */
	@Override
	public String getFlowBaseData(CustomerProduct product, Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate,
			Map<TimeQuantum, ModifyPrice> mpRecord) {
		// 总数
		double sum = 0d;
		// 结果的JSON对象
		JSONObject json = new JSONObject();
		// 获取调价记录
		if (!CollectionUtils.isEmpty(mpRecord)) {
			mpRecord = modifyPriceService.getModifyPrice(product.getProductId(), startDate, endDate);
		}
		Date lastEnd = null;
		// 无调价的时间段信息
		StringBuffer withOutModifyPriceMsg = new StringBuffer();
		if (CollectionUtils.isEmpty(mpRecord)) {
			withOutModifyPriceMsg.append(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
		} else {
			lastEnd = startDate;
			// 遍历每个调价区间
			for (TimeQuantum timeQuantum : mpRecord.keySet()) {
				// TODO 日期判断不准确
				if (lastEnd != null) {
					// 如果 调价区间开始 在 检查日期的明天 之后，那么从 检查日期的明天 到 调价区间开始的昨天 这一段区间没有价格
					if (DateUtil.getNextDayStart(lastEnd).getTime() < timeQuantum.getStartDate().getTime()) {
						withOutModifyPriceMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
								+ DateUtil.convert(DateUtil.getLastDayStart(timeQuantum.getStartDate()), DateUtil.format1) + "，");
					}
				}
				// 更新检查日期为 调价区间的结束
				lastEnd = timeQuantum.getEndDate();
			}
			// 遍历完了所有调价区间，检查日期 还不是 统计结束日期，说明从 检查日期 到 统计结束日期 这一段区间都没有价格
			if (!StringUtils.equals(DateUtil.convert(lastEnd, DateUtil.format1), DateUtil.convert(endDate, DateUtil.format1))) {
				withOutModifyPriceMsg
						.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
			}
		}
		long successSum = 0L;
		// 调价区间按 调价记录id 分组
		Map<String, List<Entry<TimeQuantum, ModifyPrice>>> groupMap = mpRecord.entrySet().stream().collect(Collectors.groupingBy(entry -> entry.getValue().getModifyPriceId()));
		String msg = "";
		JSONArray timeQuantumPriceArrInfo = new JSONArray();
		// 遍历每个调价记录
		for (Entry<String, List<Entry<TimeQuantum, ModifyPrice>>> entry : groupMap.entrySet()) {
			// 该调价记录 被划分成的 调价区间列表
			List<TimeQuantum> list = entry.getValue().stream().map(Entry::getKey).collect(Collectors.toList());
			ModifyPrice modifyPrice = entry.getValue().get(0).getValue();
			if (!CollectionUtils.isEmpty(list) && modifyPrice != null) {
				JSONObject timeQuantumJsonInfo = new JSONObject();
				// 列表中的所有调价区间 对应 同一个价格
				List<String> dates = list.stream().map(timeQuantum -> DateUtil.convert(timeQuantum.getStartDate(), DateUtil.format1) + "~"
						+ DateUtil.convert(timeQuantum.getEndDate(), DateUtil.format1)).collect(Collectors.toList());
				msg += StringUtils.join(dates.iterator(), "、") + "：";
				timeQuantumJsonInfo.put("timeQuantum", StringUtils.join(dates.iterator(), "、"));
				// 从 产品每天的成功数 中获取 每个调价区间的成功数 之和，即使用同一个调价记录的单价计算销售额的成功数
				long successCount = list.stream().mapToLong(timeQuantum -> getSuccessCountByDate(platformSuccessCountMap, timeQuantum.getStartDate(), timeQuantum.getEndDate())).sum();
				successSum += successCount;
				//
				timeQuantumJsonInfo.put("successCount", successCount);
				// 根据调价记录查找梯度价格
				List<DeductionPrice> deductionList = getDeductionPrice(modifyPrice.getModifyPriceId());
				if (!CollectionUtils.isEmpty(deductionList)) {
					DeductionPrice deductionPriceInfo = deductionList.get(0);
					// 统一价
					if (modifyPrice.getPriceType() == PriceType.UNIFORM_PRICE.getCode()) {
						// 单价
						double price = deductionPriceInfo.getPrice().doubleValue();
						msg += "统一价：【" + String.format("%.4f", price) + "元】，";
						timeQuantumJsonInfo.put("modifyPriceInfo", "统一价【" + String.format("%.4f", price) + "元】");
						sum += successCount * price;
					} else if (modifyPrice.getPriceType() == PriceType.STAGE_PRICE.getCode()) {
						// 阶段价
						String stagePriceMsg = "阶段价【";
						for (DeductionPrice deductionPrice : deductionList) {
							double ladderPrice = deductionPrice.getPrice().doubleValue();
							// 成功数大于一个阶段的最大发送量，这一阶段的金额 = 阶段数量 * 阶段单价
							if (successCount >= deductionPrice.getMaxSend() && deductionPrice.getMaxSend() > deductionPrice.getMinSend()) {
								sum += ((deductionPrice.getMaxSend() - deductionPrice.getMinSend()) * ladderPrice);
								stagePriceMsg += "第" + (deductionPrice.getGradient() + 1) + "阶段最大发送：" + String.format("%d", (long) deductionPrice.getMaxSend())
										+ "，阶段价格：" + String.format("%.4f", ladderPrice) + "元；";
							} else if (successCount > deductionPrice.getMinSend()) {
								// 成功数超过了一个阶段的最小发送量，但没超过该阶段最大发送量，这一阶段的金额 = 超出部分 * 阶段单价
								sum += ((successCount - (deductionPrice.getMinSend())) * ladderPrice);
								stagePriceMsg += "第" + (deductionPrice.getGradient() + 1) + "阶段最大发送："
										+ (deductionPrice.getMaxSend() == 0L ? "∞" : String.format("%d", (long) deductionPrice.getMaxSend())) + "，阶段价格："
										+ String.format("%.4f", ladderPrice) + "元";
							}
						}
						stagePriceMsg += "】";
						timeQuantumJsonInfo.put("modifyPriceInfo", stagePriceMsg);
						msg += stagePriceMsg + "，";
					} else if (modifyPrice.getPriceType() == PriceType.STEPPED_PRICE.getCode()) {
						String steppedMsg = "阶梯价：";
						// 阶梯价
						double ladderPrice = 0;
						for (DeductionPrice deductionPrice : deductionList) {
							if (successCount >= deductionPrice.getMinSend()
									&& (deductionPrice.getMinSend() >= deductionPrice.getMaxSend() || successCount < deductionPrice.getMaxSend())) {
								ladderPrice = deductionPrice.getPrice().doubleValue();
								sum += successCount * ladderPrice;
								steppedMsg = "【符合第" + (deductionPrice.getGradient() + 1) + "阶梯，最小发送：" + String.format("%d", (long) deductionPrice.getMinSend())
										+ "，最大发送：" + (deductionPrice.getMaxSend() == 0L ? "∞" : String.format("%d", (long) deductionPrice.getMaxSend()))
										+ "，阶梯价：" + ladderPrice + "】";
							}
						}
						timeQuantumJsonInfo.put("modifyPriceInfo", steppedMsg);
						msg += steppedMsg + "，";
					}
					timeQuantumPriceArrInfo.add(timeQuantumJsonInfo);
				}
			}
		}
		if (withOutModifyPriceMsg.length() > 0) {
			msg += "无调价信息时间段：" + withOutModifyPriceMsg.toString();
		}
		json.put(Constants.BILL_PRICE_INFO_KEY, timeQuantumPriceArrInfo.toJSONString());
		json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successSum + "");
		// 平均单价
		json.put(Constants.DAHAN_PRICE_KEY, String.format("%.4f", successSum == 0 ? 0 : (sum / successSum)));
		json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, String.format("%.2f", sum));
		json.put(Constants.DAHAN_REMARK_KEY, msg);
		return json.toString();
	}

	/**
	 * 获取梯度价格
	 *
	 * @param modifyPriceId
	 *            调价信息id
	 * @return DeductionPrice 梯度价格
	 */
	private List<DeductionPrice> getDeductionPrice(String modifyPriceId) {
		// 查询梯度价格表
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("modifyPriceId", Constants.ROP_EQ, modifyPriceId));
		filter.getOrders().add(new SearchOrder("gradient", Constants.ROP_ASC));
		try {
			return deductionPriceService.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("获取梯度价格时出现异常，", e);
		}
		return null;
	}

	@Override
	public long getPlatformSuccessCount(int productType, String accounts, Date startDate, Date endDate) {
		Map<Date, Long> platformSuccessDateCount = getPlatformSuccessDateCount(productType, accounts, startDate, endDate, null);
		if (CollectionUtils.isEmpty(platformSuccessDateCount)) {
			return 0L;
		}
		return platformSuccessDateCount.values().stream().mapToLong(Long::longValue).sum();
	}

	@Override
	public Map<String, Long> getPlatformSuccessCount4Inter(int productType, String accounts, Date startDate, Date endDate) {
		Map<Date, Map<String, Long>> platformSuccessDateCount4Inter = getPlatformSuccessDateCount4Inter(productType, accounts, startDate, endDate, null);
		if (CollectionUtils.isEmpty(platformSuccessDateCount4Inter)) {
			return null;
		}
		Map<String, Long> result = new HashMap<>();
		platformSuccessDateCount4Inter.values().forEach(map -> mergeSccuessCount(result, map));
		return result;
	}

	private void mergeSccuessCount(Map<String, Long> result1, Map<String, Long> result2) {
		result2.entrySet().stream().forEach(entry -> {
			Long old = result1.get(entry.getKey());
			old = old == null ? 0 : old;
			Long value = entry.getValue();
			value = value == null ? 0 : value;
			result1.put(entry.getKey(), old + value);
		});
	}

	/**
	 * 判断传入的字符串中是否有中文字符
	 * @param date
	 * @return
	 */
	private boolean verifyHasCHN(String date){
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(date);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据 2020/11/11 上午 获取时间
	 * @param time 时间
	 * @param isStart 是否开始日期
	 * @return
	 */
	private String getDateFormate(String time,boolean isStart){
		//获取工作时间
		String workTime = null;
		try {
			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.WORK_TIME_KEY);
			if (parameter != null && parameter.getParamvalue() != null) {
				workTime = parameter.getParamvalue();
			}
		} catch (ServiceException e) {
			logger.error("", e);
		}
		if (null == workTime) {
			logger.info("未获取到工作时间，使用默认工作时间：" + Constants.DEFAULT_WORK_TIME);
			workTime = Constants.DEFAULT_WORK_TIME;
		} else {
			logger.info("获取到工作时间：" + workTime);
		}
		// 工作时间字符串
		String[] workTimes = workTime.split(","); // [8:30-11:45, 13:15-18:00]
		String[] amWorkTime = workTimes[0].split("-"); // [8:30, 11:45]
		String[] pmWorkTime = workTimes[1].split("-"); // [13:15, 18:00]

		Map<String, Date> workTimeMap = new HashMap<>();
		// 处理中文字符
		Pattern pat = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher mat = pat.matcher(time);
		String newTime = mat.replaceAll("");
		String dateStr = DateUtil.convert(DateUtil.convert1(newTime) , DateUtil.format1);

		// 上午开始
		String dateTimeStrMB = dateStr + " " + amWorkTime[0] + ":00";
		// 上午结束
		String dateTimeStrME = dateStr + " " + amWorkTime[1] + ":00";
		// 下午开始
		String dateTimeStrAB = dateStr + " " + pmWorkTime[0] + ":00";
		// 下午结束
		String dateTimeStrAE = dateStr + " " + pmWorkTime[1] + ":00";

		if(time != null && time.contains("上午")){
			if(isStart){
				time = dateTimeStrMB;
			}else{
				//结束时间为  ...上午
				time = dateTimeStrME;
			}

		}else if(time != null && time.contains("下午")){
			if(isStart){
				time = dateTimeStrAB;
			}else{
				time = dateTimeStrAE;
			}
		}
		String[] leaveTimes = null;
		Date leaveTimeStart = null;
		if (time.contains("{")) {
			JSONObject leaveInfo = JSON.parseObject(time);
			leaveTimes = leaveInfo.getString("datetime").split(" - ");
			leaveTimeStart = DateUtil.convert(leaveTimes[0], DateUtil.format2);
			return leaveTimes[0];
		}
		return time;
	}

}
