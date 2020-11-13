package com.dahantc.erp.vo.operate.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.dahantc.erp.enums.*;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import org.apache.commons.io.FileUtils;
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
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.flow.FlowThresholdDto;
import com.dahantc.erp.dto.operate.ApplyProcessReqDto;
import com.dahantc.erp.dto.operate.AuditProcessReqDto;
import com.dahantc.erp.dto.operate.GradientPriceDto;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.flowtask.service.CommonFlowTask;
import com.dahantc.erp.util.BillInfo;
import com.dahantc.erp.util.BillInfo.DetailInfo;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.DsOrderPdfUtil;
import com.dahantc.erp.util.IText5PdfUtil;
import com.dahantc.erp.util.JavaScriptUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerOperate.service.entity.FlowArchiveThread;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.deductionPrice.dao.IDeductionPriceDao;
import com.dahantc.erp.vo.deductionPrice.entity.DeductionPrice;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.dsBuyOrder.service.IDsBuyOrderService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import com.dahantc.erp.vo.flowLabel.entity.FlowLabel;
import com.dahantc.erp.vo.flowLabel.service.IFlowLabelService;
import com.dahantc.erp.vo.flowLog.entity.FlowLog;
import com.dahantc.erp.vo.flowLog.service.IFlowLogService;
import com.dahantc.erp.vo.flowNode.entity.FlowNode;
import com.dahantc.erp.vo.flowNode.service.IFlowNodeService;
import com.dahantc.erp.vo.modifyPrice.entity.ModifyPrice;
import com.dahantc.erp.vo.modifyPrice.service.IModifyPriceService;
import com.dahantc.erp.vo.modifyPrice.service.impl.ModifyPriceServiceImpl.TimeQuantum;
import com.dahantc.erp.vo.msgCenter.entity.MsgCenter;
import com.dahantc.erp.vo.msgCenter.service.IMsgCenterService;
import com.dahantc.erp.vo.msgDetail.entity.MsgDetail;
import com.dahantc.erp.vo.msgDetail.service.IMsgDetailService;
import com.dahantc.erp.vo.operate.service.OperateService;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.productType.service.IProductTypeService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("operateService")
public class OperateServiceImpl implements OperateService {

	private static Logger logger = LogManager.getLogger(OperateServiceImpl.class);

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private IFlowNodeService flowNodeService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IFlowEntService entService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private CommonFlowTask commonFlowTask;

	@Autowired
	private IModifyPriceService modifyPriceService;

	@Autowired
	private IDeductionPriceDao deductionPriceDao;

	@Autowired
	private IBaseDao baseDao;

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IFlowLabelService labelService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IDsBuyOrderService dsBuyOrderService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private IBankAccountService bankAccountService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IMsgCenterService msgCenterService;

	@Autowired
	private IMsgDetailService msgDetailService;

	@Autowired
	private IProductTypeService productTypeService;

	@Autowired
	private ISpecialAttendanceRecordService specialAttendanceRecordService;

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000), r -> new Thread(r, "流程审核线程池"));

	/**
	 * 流程审核
	 */
	@Override
	public BaseResponse<String> auditProcess(AuditProcessReqDto reqDto, String ossUserId) {
		logger.info("审核操作开始，flowEntId:" + reqDto.getFlowEntId());
		String msg = "";
		boolean isRevoke = false;
		try {
			FlowEnt flowEnt = entService.read(reqDto.getFlowEntId());
			if (flowEnt != null) {
				if (!StringUtils.equals(reqDto.getNodeId(), flowEnt.getNodeId()) && reqDto.getOperateType() != 6) { // 撤销
					logger.info("审核节点不匹配，当前节点已经被审核过了，审核节点Id：" + reqDto.getNodeId() + "流程节点id：" + flowEnt.getNodeId());
					return BaseResponse.error("审核节点不匹配，当前节点已经被审核过了");
				}
				if (reqDto.getOperateType() == AuditResult.REVOKE.getCode()) { // 撤销逻辑
					reqDto.setOperateType(AuditResult.CANCLE.getCode());
					isRevoke = true;
				}

				String baseDataMap = reqDto.getBaseDataMap();
				String labelValueMap = reqDto.getLabelValueMap();
				String labelJsonVal = "";
				if (StringUtils.isNotBlank(labelValueMap)) {
					JSONObject jsonObject = JSONObject.parseObject(labelValueMap, Feature.OrderedField);
					jsonObject.put(Constants.FLOW_BASE_DATA_KEY, baseDataMap);
					labelJsonVal = JSONObject.toJSONString(jsonObject);
				}
				ErpFlow erpFlow = erpFlowService.read(flowEnt.getFlowId());
				// 流程类别对应的service
				String flowClass = erpFlow.getFlowClass();
				BaseFlowTask task = flowTaskManager.getFlowTasks(flowClass);
				if (task == null) {
					task = commonFlowTask;
				}
				// 校验标签值是否合法
				String result = "";
				if (StringUtils.equals(flowClass, Constants.USER_LEAVE_FLOW_CLASS)) {
					result = task.verifyFlowMsg(erpFlow, flowEnt, labelJsonVal);
				} else if (StringUtils.equals(flowClass, Constants.BILL_WRITE_OFF_FLOW_CLASS)) {
					boolean flag = false;
					JSONObject modifyJson = JSON.parseObject(labelJsonVal, Feature.OrderedField);
					Iterator<Entry<String, Object>> modifyJsonIterator = modifyJson.entrySet().iterator();
					while (modifyJsonIterator.hasNext()) {
						Entry<String, Object> entry = modifyJsonIterator.next();
						Object obj = entry.getValue();
						if (obj != null && obj.toString().startsWith("[{")) {
							JSONArray arr = JSON.parseArray(obj.toString());
							if (StringUtils.isNotBlank(arr.getJSONObject(0).getString("isHandApplay"))) {
								flag = true;
								arr.remove(0);
								modifyJson.put(entry.getKey(), arr.toString());
								labelJsonVal = modifyJson.toString();
								break;
							}
						}
					}
					if (flag) {
						result = task.verifyFlowMsg(erpFlow, flowEnt.getProductId(), labelJsonVal);
					}
				} else {
					result = task.verifyFlowMsg(erpFlow, flowEnt.getProductId(), labelJsonVal);
				}
				if (StringUtils.isNotBlank(result)) {
					return BaseResponse.error(result);
				}
				// 电商购销单流程，更新配单员id
				if (StringUtils.equals(flowClass, Constants.DS_ORDER_FLOW_CLASS)) {
					labelJsonVal = updateMatchPeople(flowEnt.getNodeId(), labelJsonVal, ossUserId);
				}
				if (StringUtils.equals(flowClass, Constants.DS_PURCHASE_FLOW_CLASS)) {
					JSONObject flowJson = JSONObject.parseObject(labelJsonVal, Feature.OrderedField);
					labelJsonVal = updatePurchaseOrder(flowEnt, flowJson);
				}
				// 检查标签值修改的部分，生成日志
				String changes = checkChanges(flowEnt.getFlowMsg(), labelJsonVal, flowClass);
				// 保存之前的flowMsg内容
				if (StringUtils.equals(flowClass, Constants.CUSTOMER_BILL_FLOW_CLASS)) {
					JSONObject json = JSON.parseObject(labelJsonVal, Feature.OrderedField);
					JSONObject oldJson = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
					json.put(Constants.FLOW_BASE_DATA_KEY, oldJson.get(Constants.FLOW_BASE_DATA_KEY).toString());
					labelJsonVal = json.toString();
				}
				flowEnt.setFlowMsg(labelJsonVal);
				FlowLog flowLog = getFlowLog(changes, flowEnt, ossUserId, reqDto.getRemark());
				flowLog.setPlatform(reqDto.getPlatform());
				if (isRevoke) { // 撤销设置日志节点为申请人节点
					flowLog.setNodeId(reqDto.getNodeId());
				}
				// 检查流程信息修改，用于收付款流程生成或更新现金流（充值、酬金、账单付款、账单收款）
				if (StringUtils.equals(flowClass, Constants.BILL_WRITE_OFF_FLOW_CLASS) && reqDto.getOperateType() == AuditResult.REJECTED.getCode()) {
					if (StringUtils.equals(reqDto.getRejectToNode(), "0")) {
						task.flowMsgModify(AuditResult.CANCLE.getCode(), flowEnt);
					} else {
						task.flowMsgModify(reqDto.getOperateType(), flowEnt);
					}
				} else if (isReceivablesOrPaymentFlow(flowClass)) {
					// 以下操作会对现金流进行更新：
					// 手动发起：1发起人节点创建+；2驳回到发起人节点-；3发起人节点重新申请+
					// 自动发起：1发起人节点通过+；2驳回到发起人节点-；3发起人节点重新申请+
					// 以下操作不对现金流进行更新：
					// 非发起人节点的通过、驳回到非发起人节点、发起人节点取消
					if (reqDto.getOperateType() == AuditResult.REJECTED.getCode() && !StringUtils.equals(reqDto.getRejectToNode(), "0")) {
						// 驳回到非发起人节点，调用取消的处理，即不对现金流更新
						task.flowMsgModify(AuditResult.CANCLE.getCode(), flowEnt);
					} else {
						task.flowMsgModify(reqDto.getOperateType(), flowEnt);
					}
				} else if (StringUtils.equals(flowClass, Constants.CHECK_BILL_FLOW_CLASS) || StringUtils.equals(flowClass, Constants.USER_LEAVE_FLOW_CLASS) || StringUtils.equals(flowClass, Constants.USER_OVERTIME_FLOW_CLASS)) {
					// 驳回到非发起人节点，不调用驳回的处理
					if (reqDto.getOperateType() == AuditResult.REJECTED.getCode() && !StringUtils.equals(reqDto.getRejectToNode(), "0")) {
						task.flowMsgModify(AuditResult.PASS.getCode(), flowEnt, changes);
					} else {
						task.flowMsgModify(reqDto.getOperateType(), flowEnt, changes);
					}
				} else {
					task.flowMsgModify(reqDto.getOperateType(), flowEnt);
				}

				// 是否需要自动审核下一节点，审核通过并且有下一节点时为true
				boolean needAutoAudit = false;
				String nextNodeId = "";
				if (reqDto.getOperateType() == AuditResult.PASS.getCode()) {
					boolean needUpdateBill = false;
					if (flowEnt.getFlowStatus() == FlowStatus.NO_PASS.ordinal() || flowEnt.getFlowStatus() == FlowStatus.NOT_AUDIT.ordinal()) {
						needUpdateBill = true;
					}
					// 审核通过操作
					logger.info("审核操作：" + AuditResult.PASS.getMsg());
					flowLog.setAuditResult(AuditResult.PASS.getCode());
					flowEnt.setFlowStatus(FlowStatus.NO_PASS.ordinal());
					// 将流程节点设为下一个节点
					FlowNode flowNode = flowNodeService.read(flowEnt.getNodeId());
					if (flowNode != null) {
						logger.info("流程当前节点：" + flowNode.getNodeName());
						nextNodeId = flowNode.getNextNodeId();
						if (flowNode.getNodeIndex() == 0 && StringUtils.equals(flowClass, Constants.CUSTOMER_BILL_FLOW_CLASS) && needUpdateBill
								&& StringUtils.isNotBlank(changes) && (changes.contains(Constants.PAYMENT_AMOUNT_KEY + "_修改前")
										|| changes.contains(Constants.CUSTOMER_SUCCESS_COUNT_KEY + "_修改前"))) {
							// 销售账单重新申请, 添加文件修改记录到log
							updateBillFile(flowEnt, flowLog);
						}
						flowEnt.setNodeId(nextNodeId);
						logger.info("设置下一节点id：" + nextNodeId);
						msg = AuditResult.PASS.getMsg();
						if (StringUtils.isBlank(nextNodeId)) {
							boolean checkResult = verifyDsMatchOrderAmount(flowEnt);
							if (!checkResult) {
								return BaseResponse.error("库存不足！请重新填写数量");
							}
							// 流程归档，并发送提醒
							User user = (User) baseDao.get(User.class, flowEnt.getOssUserId());
							// 复制最终文档
							JSONObject json = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
							if (json != null && StringUtils.equals(flowClass, Constants.CUSTOMER_BILL_FLOW_CLASS)
									&& json.get(Constants.FLOW_BASE_DATA_KEY) != null
									&& StringUtils.isNotBlank(json.get(Constants.FLOW_BASE_DATA_KEY).toString())) {
								JSONObject baseDataJson = JSON.parseObject(json.get(Constants.FLOW_BASE_DATA_KEY).toString());
								if (StringUtils.isNotBlank(baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY))
										&& StringUtils.isNotBlank(baseDataJson.getString(Constants.DAHAN_BILL_FILE_KEY))) {
									String[] filePathArr = baseDataJson.getString(Constants.DAHAN_BILL_FILE_KEY).split(";");
									String pdfFilePath = null;
									String excelFilePath = null;
									for (String path : filePathArr) {
										if (path.endsWith(".pdf")) {
											pdfFilePath = path;
										}
										if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
											excelFilePath = path;
										}
									}
									if (StringUtils.isNotBlank(pdfFilePath)) {
										FileUtils.copyFile(new File(pdfFilePath), new File(IText5PdfUtil.getFinalFileName(pdfFilePath)));
									}
									baseDataJson.put(Constants.DAHAN_BILL_FILE_KEY, excelFilePath + ";" + IText5PdfUtil.getFinalFileName(pdfFilePath));
									json.put(Constants.FLOW_BASE_DATA_KEY, baseDataJson.toString());
									flowEnt.setFlowMsg(json.toString());

									// 把最终文档写到log里面
									String flowMsg = flowLog.getFlowMsg();
									JSONObject logFlowMsgJson = new JSONObject();
									if (StringUtils.isNotBlank(flowMsg)) {
										logFlowMsgJson = JSONObject.parseObject(flowMsg, Feature.OrderedField);
									}
									logFlowMsgJson.put(Constants.DAHAN_BILL_FILE_KEY, IText5PdfUtil.getFinalFileName(pdfFilePath));
									flowLog.setFlowMsg(logFlowMsgJson.toString());
								}
							}

							FlowArchiveThread archiveThread = new FlowArchiveThread(task, erpFlow, flowEnt, user);
							executor.execute(archiveThread);
							// 流程实体更新为归档状态
							flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
							//TODO 归档后才会显示为有效状态
							SearchFilter filter = new SearchFilter();
							filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
							List<SpecialAttendanceRecord> specialAttendanceRecords = specialAttendanceRecordService.queryAllBySearchFilter(filter);
							if(!CollectionUtils.isEmpty(specialAttendanceRecords)){
								SpecialAttendanceRecord specialAttendanceRecord = specialAttendanceRecords.get(0);
								specialAttendanceRecord.setValid(EntityStatus.NORMAL.ordinal());
								//更新 有效状态
								specialAttendanceRecordService.update(specialAttendanceRecord);
							}
							msg = upDateFlowAndSaveLog(flowEnt, flowLog);
							return BaseResponse.success(msg);
						} else {
							// 审核通过，并且有下一个节点，需要自动审核
							needAutoAudit = true;
						}
					}
				} else if (reqDto.getOperateType() == AuditResult.REJECTED.getCode()) {
					// 审核驳回操作
					logger.info("审核操作：" + AuditResult.REJECTED.getMsg());
					flowLog.setAuditResult(AuditResult.REJECTED.getCode());
					flowEnt.setFlowStatus(FlowStatus.NO_PASS.ordinal());
					// 将流程节点退回到第几节点
					int nodeIndex = Integer.parseInt(reqDto.getRejectToNode());
					SearchFilter filter = new SearchFilter();
					filter.getRules().add(new SearchRule("nodeIndex", Constants.ROP_EQ, nodeIndex));
					filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowEnt.getFlowId()));
					List<FlowNode> list = flowNodeService.queryAllBySearchFilter(filter);
					if (list != null && !list.isEmpty()) {
						FlowNode flowNode = list.get(0);
						flowEnt.setNodeId(flowNode.getNodeId());
						logger.info("驳回到第" + flowNode.getNodeIndex() + "节点：" + flowNode.getNodeName());
					} else {
						flowEnt.setNodeId(erpFlow.getStartNodeId());
						logger.info("指定节点不存在，驳回到起始节点");
					}
					msg = AuditResult.REJECTED.getMsg();
				} else if (reqDto.getOperateType() == AuditResult.SAVE.getCode()) {
					// 保存操作
					logger.info("审核操作：" + AuditResult.SAVE.getMsg());
					flowLog.setAuditResult(AuditResult.SAVE.getCode());
					msg = AuditResult.SAVE.getMsg();
				} else if (reqDto.getOperateType() == AuditResult.CANCLE.getCode()) {
					// 取消操作
					logger.info("审核操作：" + AuditResult.CANCLE.getMsg());
					flowEnt.setFlowStatus(FlowStatus.CANCLE.ordinal());
					flowLog.setAuditResult(AuditResult.CANCLE.getCode());
					flowEnt.setNodeId("");
					msg = AuditResult.CANCLE.getMsg();
				} else {
					logger.error("审核操作参数不正确，operateType:" + reqDto.getOperateType());
					return BaseResponse.error("审核操作异常");
				}
				// 更新流程实体，保存审核操作日志
				msg = upDateFlowAndSaveLog(flowEnt, flowLog);
				if (needAutoAudit) {
					// 下一个流程节点
					FlowNode nextNode = flowNodeService.read(nextNodeId);
					executor.execute(() -> {
						logger.info("审核完当前节点，自动审核下一个节点：" + nextNode.getNodeName());
						try {
							// 休眠一秒，等待流程实体更新
							Thread.sleep(1000);
						} catch (Exception e) {
							logger.error("", e);
						}
						// 根据条件自动审核通过下一个节点
						automaticAuditByCondition(flowEnt, nextNode);
					});
				}
			} else {
				return BaseResponse.error("id为:" + reqDto.getFlowEntId() + "的流程实体不存在");
			}
		} catch (Exception e) {
			logger.error("审核流程异常，flowEntId:" + reqDto.getFlowEntId(), e);
			return BaseResponse.error("审核流程异常");
		}
		return BaseResponse.success(msg);
	}

	/**
	 * 流程审核
	 */
	@Override
	public BaseResponse<String> revokeProcess(String flowEntId, String revokeReson, String ossUserId, int platform) {
		logger.info("撤销操作开始，flowEntId：" + flowEntId);
		try {
			FlowEnt flowEnt = entService.read(flowEntId);
			if (flowEnt != null) {
				FlowNode flowNode = flowNodeService.read(flowEnt.getNodeId());
				// 1.创建人为当前用户 2.状态(非取消、归档状态) 3.节点不在第一个
				if (!StringUtils.equals(ossUserId, flowEnt.getOssUserId())
						|| (flowEnt.getFlowStatus() != FlowStatus.NOT_AUDIT.ordinal() && flowEnt.getFlowStatus() != FlowStatus.NO_PASS.ordinal())
						|| flowNode == null || flowNode.getNodeIndex() == 0) {
					logger.info("不可撤销状态，flowEntId：" + flowEntId + "，flowStatus：" + flowEnt.getFlowStatus() + "，ossUserId：" + ossUserId);
					return BaseResponse.error("不可撤销状态");
				}
			} else {
				return BaseResponse.error("流程不存在");
			}
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEntId));
			List<FlowLog> logs = flowLogService.queryAllBySearchFilter(filter);

			AuditProcessReqDto dto = new AuditProcessReqDto();
			dto.setFlowEntId(flowEntId);
			dto.setNodeId(erpFlowService.read(flowEnt.getFlowId()).getStartNodeId());
			dto.setRemark(revokeReson);
			dto.setOperateType(AuditResult.REVOKE.getCode());
			dto.setLabelValueMap(flowEnt.getFlowMsg());
			dto.setPlatform(platform);
			BaseResponse<String> result = auditProcess(dto, ossUserId);

			MsgCenter msgCenter = new MsgCenter();
			msgCenter.setInfotype(MsgCenter.CUSTOMER_WARNING);
			msgCenter.setMessagesourceid(flowEnt.getSupplierId());
			msgCenter.setMessagedetail("您参与审核的【" + flowEnt.getFlowTitle() + "】已由申请人【" + userService.read(ossUserId).getRealName() + "】撤回，请注意相关变化！");
			msgCenter.setWtime(new Date());
			msgCenterService.save(msgCenter);

			if (!CollectionUtils.isEmpty(logs)) {
				Set<String> userIds = logs.stream().map(FlowLog::getOssUserId).collect(Collectors.toSet());
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, new ArrayList<>(userIds)));
				List<User> users = userService.queryAllBySearchFilter(filter);
				if (!CollectionUtils.isEmpty(users)) {
					List<MsgDetail> msgDetailList = new ArrayList<>();
					for (User user : users) {
						if (!StringUtils.equals(ossUserId, user.getOssUserId())) { // 自己不通知
							MsgDetail msgDetail = new MsgDetail();
							msgDetail.setState(1);
							msgDetail.setMessageid(msgCenter.getMessageid());
							msgDetail.setUserid(user.getOssUserId());
							msgDetail.setWtime(new Date());
							msgDetailList.add(msgDetail);
						}
					}
					msgDetailService.saveByBatch(msgDetailList);
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("审核流程异常，flowEntId:" + flowEntId, e);
			return BaseResponse.error("审核流程异常");
		}
	}

	private void updateBillFile(FlowEnt flowEnt, FlowLog flowLog) {
		if (StringUtils.isNotBlank(flowEnt.getFlowMsg())) {
			JSONObject json = JSON.parseObject(flowEnt.getFlowMsg(), Feature.OrderedField);
			JSONObject baseDataJson = JSON.parseObject(json.get(Constants.FLOW_BASE_DATA_KEY).toString());
			if (StringUtils.isNotBlank(baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY))
					&& StringUtils.isNotBlank(baseDataJson.getString(Constants.DAHAN_BILL_FILE_KEY))) {
				String[] filePathArr = baseDataJson.getString(Constants.DAHAN_BILL_FILE_KEY).split(";");
				String pdfFilePath = null;
				String excelFilePath = null;
				for (String path : filePathArr) {
					if (path.endsWith(".pdf")) {
						pdfFilePath = path;
					}
					if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
						excelFilePath = path;
					}
				}
				if (StringUtils.isNoneBlank(pdfFilePath, excelFilePath)) {
					// 获取json
					String jsonPath = pdfFilePath.substring(0, pdfFilePath.lastIndexOf(File.separator)) + File.separator
							+ baseDataJson.getString(Constants.DAHAN_BILL_NUM_KEY) + "json.txt";
					try (InputStreamReader osr = new InputStreamReader(new FileInputStream(jsonPath), "UTF-8");
							BufferedReader buffer = new BufferedReader(osr);) {
						String content = buffer.readLine();
						if (StringUtils.isNotBlank(content)) {
							BillInfo billInfo = JSON.parseObject(content, BillInfo.class);
							String realStr = json.getString(Constants.PAYMENT_AMOUNT_KEY);
							if (StringUtils.isNotBlank(realStr)) {
								String[] arr = realStr.split(",");
								if (arr.length == 1) { // 国际账单
									String scuccessCount = json.getString(Constants.CUSTOMER_SUCCESS_COUNT_KEY);
									billInfo.getRealFeeInfo().setFeeCount(new BigDecimal(scuccessCount));
									billInfo.getRealFeeInfo()
											.setUnitPrice(new BigDecimal(realStr).divide(new BigDecimal(scuccessCount), 4, BigDecimal.ROUND_HALF_UP));
									billInfo.getRealFeeInfo().setFee(new BigDecimal(realStr));
								} else {
									if (billInfo.getRealFeeInfo() == null) {
										billInfo.setRealFeeInfo(new DetailInfo());
									}
									billInfo.getRealFeeInfo().setFeeCount(new BigDecimal(arr[0]));
									billInfo.getRealFeeInfo().setUnitPrice(new BigDecimal(arr[1]));
									billInfo.getRealFeeInfo().setFee(new BigDecimal(arr[2]));
								}

								// 记录修改文件
								String flowMsg = flowLog.getFlowMsg();
								JSONObject logFlowMsgJson = new JSONObject();
								if (StringUtils.isNotBlank(flowMsg)) {
									logFlowMsgJson = JSONObject.parseObject(flowMsg, Feature.OrderedField);
								}
								logFlowMsgJson.put(Constants.DAHAN_BILL_FILE_KEY + "_修改前", pdfFilePath);

								// 获取银行信息
								CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
								Customer customer = customerService.read(customerProduct.getCustomerId());
								BankAccount bankAccount = bankAccountService.read(customer.getBankAccountId());
								if (bankAccount == null) {
									throw new Exception("客户：" + customer.getCompanyName() + "，没有关联我方银行信息");
								}

								pdfFilePath = IText5PdfUtil.getNextCopyFileName(pdfFilePath);

								User saler = null;
								if (StringUtils.isNotBlank(customer.getOssuserId())) {
									saler = userService.read(customer.getOssuserId());
								}

								IText5PdfUtil.createBillPdf(billInfo, bankAccount, pdfFilePath, customer, saler);

								baseDataJson.put(Constants.DAHAN_BILL_FILE_KEY, excelFilePath + ";" + pdfFilePath);
								json.put(Constants.FLOW_BASE_DATA_KEY, baseDataJson.toString());
								flowEnt.setFlowMsg(json.toString());

								logFlowMsgJson.put(Constants.DAHAN_BILL_FILE_KEY, pdfFilePath);
								flowLog.setFlowMsg(logFlowMsgJson.toString());
							}
						}
					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}
		}
	}

	/**
	 * 判断是否是收付款或充值流程
	 * 
	 * @param flowClass
	 *            流程类别
	 * @return
	 */
	private boolean isReceivablesOrPaymentFlow(String flowClass) {
		List<String> flowClassList = new ArrayList<>();
		flowClassList.add(Constants.PAYMENT_FLOW_CLASS);
		flowClassList.add(Constants.BILL_PAYMENT_FLOW_CLASS);
		flowClassList.add(Constants.BILL_RECEIVABLES_FLOW_CLASS);
		flowClassList.add(Constants.REMUNERATION_FLOW_CLASS);
		return flowClassList.contains(flowClass);
	}

	private FlowLog getFlowLog(String changes, FlowEnt flowEnt, String ossUserId, String remark) {
		FlowLog flowLog = new FlowLog();
		flowLog.setFlowMsg(changes);
		flowLog.setFlowEntId(flowEnt.getId());
		flowLog.setNodeId(flowEnt.getNodeId());
		flowLog.setOssUserId(ossUserId);
		flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
		flowLog.setRemark(remark);
		flowLog.setFlowId(flowEnt.getFlowId());
		return flowLog;
	}

	/**
	 * 电商配单员节点，配单完成把流程标签里的配单员更新为当前审核的用户
	 *
	 * @param nodeId
	 *            流程当前节点
	 * @param flowMsg
	 *            流程标签
	 * @param ossUserId
	 *            审核人id
	 * @return
	 */
	private String updateMatchPeople(String nodeId, String flowMsg, String ossUserId) {
		try {
			// 流程当前审核节点的角色是配单员角色
			FlowNode node = flowNodeService.read(nodeId);
			Role role = roleService.readOneByProperty("rolename", Constants.ROLE_NAME_MATCH_ORDER);
			if (node.getRoleId().contains(role.getRoleid())) {
				JSONObject flowMsgJson = JSONObject.parseObject(flowMsg, Feature.OrderedField);
				flowMsgJson.put(Constants.DS_MATCH_PEOPLE, ossUserId);
				flowMsg = flowMsgJson.toJSONString();
			}
		} catch (Exception e) {
			logger.error("更新电商流程的配单员异常", e);
		}
		return flowMsg;
	}

	/**
	 * 电商配单员节点，配单完成把流程标签里的配单员更新为当前审核的用户
	 *
	 * @param flowEnt
	 *            流程实体
	 * @param flowJson
	 *            流程详情
	 * @return
	 */
	private String updatePurchaseOrder(FlowEnt flowEnt, JSONObject flowJson) {
		String flowMsg = flowEnt.getFlowMsg();
		String path = DsOrderPdfUtil.createPurchaseOrderPdf(flowEnt, flowJson);
		try {
			User user = userService.read(flowEnt.getOssUserId());
			Supplier supplier = supplierService.read(flowEnt.getSupplierId());
			JSONObject fileJson = new JSONObject();
			JSONArray fileArray = new JSONArray();
			fileJson.put("fileName", user.getRealName() + ":" + supplier.getCompanyName() + "采购单.pdf");
			fileJson.put("filePath", path);
			fileArray.add(fileJson);
			flowJson.put(Constants.DS_PURCHASE_ORDER_FILE, fileArray.toJSONString());
			flowMsg = JSONObject.toJSONString(flowJson);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return flowMsg;
	}

	private String upDateFlowAndSaveLog(FlowEnt flowEnt, FlowLog flowLog) {
		String msg = "";
		try {
			boolean update = entService.update(flowEnt);
			if (!update) {
				msg += "失败";
			} else {
				boolean save = flowLogService.save(flowLog);
				if (save) {
					msg += "成功";
				} else {
					msg += "失败";
				}
			}
		} catch (Exception e) {
			logger.error("更新流程或者保存日志的时候出现异常", e);
		}
		return msg;
	}

	/**
	 * 根据条件自动审核通过
	 *
	 * @param flowEnt
	 *            流程实体
	 * @param flowNode
	 *            流程节点
	 */
	@Override
	public void automaticAuditByCondition(FlowEnt flowEnt, FlowNode flowNode) {
		logger.info("检查流程节点阈值开始，节点：" + flowNode.getNodeName());

		StringBuilder msg = new StringBuilder("满足条件：");

		Boolean pass = false;
		String operator = null;

		try {
			// 获取流程在当前节点的处理角色和人
			Map<String, List<String>> map = userService.getDealRoleAndUser(userService.read(flowEnt.getOssUserId()), flowNode.getRoleId());

			if (CollectionUtils.isEmpty(map)) {
				pass = true;
				msg.append("当前节点无审核人");
			} else {
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
				List<FlowLog> logList = flowLogService.queryAllBySearchFilter(searchFilter);

				if (!CollectionUtils.isEmpty(logList)) {
					logList.sort((log1, log2) -> {
						return log1.getWtime().compareTo(log2.getWtime());
					});

					FlowLog flowLog = logList.get(logList.size() - 1);
					String ossUserId = flowLog.getOssUserId();
					// 上个审核人
					User lastOperator = userService.read(ossUserId);
					// 当前节点是否审核人
					boolean flag = false;
					for (Entry<String, List<String>> entry : map.entrySet()) {
						if (!CollectionUtils.isEmpty(entry.getValue())) {
							flag = true; // 当前节点有审核人
							if (entry.getValue().contains(lastOperator.getRealName())) {
								pass = true;
								msg.append("上一个节点审核人和当前节点审核人相同：").append(lastOperator.getRealName());
								operator = ossUserId; // 使用上个节点的审核人来通过本节点
								break;
							}
						}
					}
					// 当前节点没有审核人，直接通过
					if (!flag) {
						pass = true;
						msg.append("当前节点无审核人");
					}
				}
			}

		} catch (Exception e) {
			logger.error("按角色自动审核通过异常", e);
		}

		String flowMsg = flowEnt.getFlowMsg();
		JSONObject flowMsgJsonObject = JSON.parseObject(flowMsg, Feature.OrderedField);
		String baseDataMap = flowMsgJsonObject.getString(Constants.FLOW_BASE_DATA_KEY);
		// 删除BaseData
		flowMsgJsonObject.remove(Constants.FLOW_BASE_DATA_KEY);
		// 阈值标签对应的填写值
		String labelValueMap = flowMsgJsonObject.toJSONString();
		JSONObject labelValues = null;
		try {
			labelValues = JSON.parseObject(labelValueMap, Feature.OrderedField);
		} catch (Exception e) {
			logger.error("解析流程标签错误", e);
		}
		if (labelValues == null) {
			return;
		}

		// 按审核人判断不通过，再按流程阈值判断是否通过
		if (!pass) {
			List<FlowThresholdDto> flowThresholds = parseFlowThreshold(flowNode.getFlowThreshold());
			if (flowThresholds != null && !flowThresholds.isEmpty()) {
				List<String> labelIds = flowThresholds.stream().map(FlowThresholdDto::getLabelId).collect(Collectors.toList());
				List<FlowLabel> labelList = labelService.readByIds(labelIds);
				if (labelList != null && !labelList.isEmpty()) {
					// 标签id --> 标签类型
					Map<String, Integer> labelInfos = labelList.stream().collect(Collectors.toMap(FlowLabel::getId, FlowLabel::getType));
					// 标签id --> 标签名称
					Map<String, String> labelNames = labelList.stream().collect(Collectors.toMap(FlowLabel::getId, FlowLabel::getName));
					// 读取系统参数
					List<String> parameterId = flowThresholds.stream().map(FlowThresholdDto::getThresholdValue).collect(Collectors.toList());
					List<Parameter> parameterList = parameterService.readByIds(parameterId);
					if (parameterList != null && !parameterList.isEmpty()) {
						// 系统参数id --> 系统参数值
						Map<String, String> parameters = parameterList.stream().collect(Collectors.toMap(Parameter::getEntityid, Parameter::getParamvalue));
						boolean match = false;
						// 多个条件，必须同时满足才会自动审核通过
						for (FlowThresholdDto flowThreshold : flowThresholds) {
							// 阈值对应标签的数据类型
							Integer thresholdType = labelInfos.get(flowThreshold.getLabelId());
							// 阈值
							String thresholdValue = parameters.get(flowThreshold.getThresholdValue());
							// 关系
							String relation = flowThreshold.getRelationship();
							// 标签名
							String labelName = labelNames.get(flowThreshold.getLabelId());
							String labelValue = labelValues.getString(labelName);
							String opsName = getMatchName(relation);
							match = matchThreshold(thresholdType, labelValue, thresholdValue, relation);
							if (!match) {
								break;
							}
							msg.append(labelName).append(" ").append(opsName).append(" ").append(thresholdValue).append(" ");
						}
						pass = match;
						msg.append("系统自动通过");
					}
				}
			}

			// 按审核人判断不通过，按流程阈值判断不通过，按脚本判断是否通过
			if (!pass) {
				if (StringUtil.isNotBlank(flowNode.getThresholdFile())) {
					try {
						User user = userService.read(flowEnt.getOssUserId());
						if (user != null) {
							// 部门信息
							Department department = departmentService.read(user.getDeptId());
							String deptName = "";
							if (department != null) {
								deptName = department.getDeptname();
							}
							// 角色信息
							String hql = "SELECT r.rolename FROM RoleRelation rr INNER JOIN Role r ON rr.roleId = r.roleid WHERE rr.ossUserId = :userId";
							Map<String, Object> params = new HashMap<>();
							params.put("userId", flowEnt.getOssUserId());
							List<Object> roleNameList = baseDao.findByhql(hql, params, Integer.MAX_VALUE);
							String roleName = "";
							if (roleNameList != null && !roleNameList.isEmpty()) {
								roleName = roleNameList.stream().map(roleInfo -> {
									if (roleInfo.getClass().isArray()) {
										return ((Object[]) roleInfo)[0].toString();
									}
									return null;
								}).collect(Collectors.joining(","));
							}
							// 脚本文件信息
							JSONObject fileInfo = JSON.parseObject(flowNode.getThresholdFile());
							String filePath = fileInfo.getString("filePath");
							if (StringUtil.isNotBlank(filePath)) {
								pass = JavaScriptUtil.invokeScript("flow_threshold_judge", filePath, Boolean.class, flowMsg, user.getRealName(), deptName,
										roleName);
							}
							msg.append("流程阈值脚本条件");
						}
					} catch (Exception e) {
						logger.info("脚本阈值判断出现异常", e);
					}
				}
			}
		}

		if (pass != null && pass) {
			logger.info("检查流程节点阈值结束，节点：" + flowNode.getNodeName() + "，" + msg);
			// 指定谁通过
			if (operator == null) {
				// 不指定用管理员通过
				User admin = userService.findAdmin();
				if (admin != null) {
					operator = admin.getOssUserId();
				}
			}
			if (operator == null) {
				logger.info("找不到自动审核处理人，取消自动审核");
				return;
			}
			AuditProcessReqDto reqDto = new AuditProcessReqDto();
			reqDto.setLabelValueMap(labelValueMap);
			reqDto.setBaseDataMap(baseDataMap);
			reqDto.setFlowEntId(flowEnt.getId());
			reqDto.setRemark(msg.toString());
			reqDto.setOperateType(AuditResult.PASS.getCode());
			reqDto.setNodeId(flowNode.getNodeId());
			reqDto.setPlatform(PlatformType.PC.ordinal());
			auditProcess(reqDto, operator);
		}

	}

	/**
	 * 转换流程阈值为对象
	 *
	 * @param flowThresholdInfo
	 *            阈值JSON字符串
	 * @return 阈值信息
	 */
	private List<FlowThresholdDto> parseFlowThreshold(String flowThresholdInfo) {
		if (StringUtil.isBlank(flowThresholdInfo)) {
			return null;
		}
		try {
			return JSONArray.parseArray(flowThresholdInfo, FlowThresholdDto.class);
		} catch (Exception e) {
			logger.error("流程根据条件自动审核时，阈值数据解析异常，阈值数据：" + flowThresholdInfo, e);
		}
		return null;
	}

	/**
	 * 匹配 阈值
	 *
	 * @param thresholdType
	 *            阈值类型
	 * @param labelValue
	 *            标签值
	 * @param thresholdValue
	 *            阈值
	 * @param relation
	 *            关系
	 * @return 匹配结果
	 */
	private boolean matchThreshold(Integer thresholdType, String labelValue, String thresholdValue, String relation) {
		if (Objects.isNull(labelValue)) {
			return false;
		}
		// 判断
		if (thresholdType == FlowLabelType.String.ordinal()) {
			// 字符串
			return labelValue.equals(thresholdValue);
		} else if (FlowLabelType.Boolean.ordinal() == thresholdType) {
			// boolean 类型
			if (StringUtil.isNotBlank(thresholdValue) && StringUtil.isNotBlank(labelValue)) {
				return thresholdValue.equals(labelValue);
			}
			return false;
		} else if (FlowLabelType.Double.ordinal() == thresholdType || FlowLabelType.Integer.ordinal() == thresholdType) {
			Integer type = FlowLabelType.Integer.ordinal() == thresholdType ? 1 : 2;
			return matchNum(relation, thresholdValue, labelValue, type);
		} else if (FlowLabelType.Select.ordinal() == thresholdType) {
			// 选择框 （比较值）
			return labelValue.equals(thresholdValue);
		} else if (FlowLabelType.AccountBill.ordinal() == thresholdType) {
			// 账单金额 = 成功数X单价 // 数据格式 "1000,0.1000,100.00"
			String[] billInfos = labelValue.split(",");
			if (billInfos.length < 3) {
				return false;
			}
			return matchNum(relation, thresholdValue, billInfos[2], 2);
		} else if (FlowLabelType.Gradient.ordinal() == thresholdType) {
			// 价格梯度
			return matchPriceGradient(labelValue, thresholdValue, relation);
		} else if (FlowLabelType.Date.ordinal() == thresholdType || FlowLabelType.DateTime.ordinal() == thresholdType
				|| FlowLabelType.DateMonth.ordinal() == thresholdType) {
			return matchData(relation, thresholdValue, labelValue, thresholdType);
		} else if (FlowLabelType.File.ordinal() == thresholdType && thresholdValue.equals("文件(空)") && Constants.ROP_NE.equals(relation)) {
			// 文件不为空
			if (StringUtils.isBlank(labelValue)) {
				return false;
			} else {
				return new File(labelValue).exists();
			}
		} else if (FlowLabelType.SelfInvoice.ordinal() == thresholdType || FlowLabelType.OtherInvoice.ordinal() == thresholdType) {
			if (StringUtil.isBlank(labelValue)) {
				return false;
			}
			return matchInvoiceInfo(relation, thresholdValue, labelValue);
		}
		return false;
	}

	/**
	 * 匹配价格梯度类型
	 *
	 * @param labelValue
	 *            标签值
	 * @param thresholdValue
	 *            阈值
	 * @param relation
	 *            关系
	 * @return 匹配结果
	 */
	private boolean matchPriceGradient(String labelValue, String thresholdValue, String relation) {
		List<GradientPriceDto> gradientPrices = null;
		try {
			gradientPrices = JSONArray.parseArray(labelValue, GradientPriceDto.class);
		} catch (Exception e) {
			logger.error("解析梯度价格异常，字符串：" + labelValue, e);
		}
		if (gradientPrices == null || gradientPrices.isEmpty()) {
			return false;
		}
		gradientPrices = gradientPrices.stream().filter(gradientPrice -> gradientPrice.getPrice() != null)
				.sorted(Comparator.comparingDouble(GradientPriceDto::getPrice)).collect(Collectors.toList());
		if (gradientPrices.isEmpty()) {
			return false;
		}
		GradientPriceDto gradientPrice = gradientPrices.get(0);
		Double price = gradientPrice.getPrice();
		return matchNum(relation, thresholdValue, String.valueOf(price), 2);
	}

	/**
	 * 检查数字对应的是否匹配
	 *
	 * @param relationship
	 *            关系
	 * @param thresholdValueStr
	 *            阈值
	 * @param labelValueStr
	 *            标签值
	 * @return 匹配结果
	 */
	private boolean matchNum(String relationship, String thresholdValueStr, String labelValueStr, Integer type) {
		if (StringUtil.isBlank(thresholdValueStr) || StringUtil.isBlank(labelValueStr)) {
			return false;
		}
		// 数字类型
		Number labelValue = null;
		Number thresholdValue = null;
		try {
			labelValue = Double.parseDouble(labelValueStr);
			thresholdValue = Double.parseDouble(thresholdValueStr);
		} catch (Exception e) {
			logger.error("double 数据转换时异常", e);
		}
		if (labelValue == null || thresholdValue == null) {
			return false;
		}
		if (Constants.ROP_EQ.equals(relationship)) {
			// 等于
			return thresholdValue.equals(labelValue);
		} else if (Constants.ROP_NE.equals(relationship)) {
			// 不等于
			return !thresholdValue.equals(labelValue);
		} else if (Constants.ROP_LT.equals(relationship)) {
			// 小于
			if (type == 1) {
				return (labelValue.intValue() < thresholdValue.intValue());
			} else {
				return (labelValue.doubleValue() < thresholdValue.doubleValue());
			}
		} else if (Constants.ROP_GT.equals(relationship)) {
			// 大于
			if (type == 1) {
				return (labelValue.intValue() > thresholdValue.intValue());
			} else {
				return (labelValue.doubleValue() > thresholdValue.doubleValue());
			}
		} else if (Constants.ROP_LE.equals(relationship)) {
			// 小于等于
			if (type == 1) {
				return (labelValue.intValue() <= thresholdValue.intValue());
			} else {
				return (labelValue.doubleValue() <= thresholdValue.doubleValue());
			}
		} else if (Constants.ROP_GE.equals(relationship)) {
			// 大于等于
			if (type == 1) {
				return (labelValue.intValue() >= thresholdValue.intValue());
			} else {
				return (labelValue.doubleValue() >= thresholdValue.doubleValue());
			}
		}
		return false;
	}

	/**
	 * 检查数字对应的是否匹配
	 *
	 * @param relationship
	 *            关系
	 * @param thresholdValueStr
	 *            阈值
	 * @param labelValueStr
	 *            标签值
	 * @return 匹配结果
	 */
	private boolean matchData(String relationship, String thresholdValueStr, String labelValueStr, Integer flowLabelType) {
		if (StringUtil.isBlank(thresholdValueStr) || StringUtil.isBlank(labelValueStr)) {
			return false;
		}
		String format = null;
		// 标签类型
		if (FlowLabelType.Date.ordinal() == flowLabelType) {
			// 日期类型 yyyy-MM-dd
			format = DateUtil.format1;
		} else if (FlowLabelType.DateTime.ordinal() == flowLabelType) {
			// 时间日期类型 yyyy-MM-dd HH:mm:ss
			format = DateUtil.format2;
		} else if (FlowLabelType.DateMonth.ordinal() == flowLabelType) {
			// 月份类型 yyyy-MM
			format = DateUtil.format4;
		} else {
			return false;
		}
		Date labelDate = null;
		Date thresholdDate = null;
		try {
			labelDate = DateUtil.convert(labelValueStr, format);
			thresholdDate = DateUtil.convert(thresholdValueStr, format);
		} catch (Exception e) {
			logger.error("String 转换 日期 时异常，转换数据：" + thresholdValueStr + "," + labelValueStr, e);
		}
		if (labelDate == null || thresholdDate == null) {
			return false;
		}
		if (Constants.ROP_EQ.equals(relationship)) {
			return labelDate.getTime() == thresholdDate.getTime();
		} else if (Constants.ROP_NE.equals(relationship)) {
			return labelDate.getTime() != thresholdDate.getTime();
		} else if (Constants.ROP_LT.equals(relationship)) {
			return labelDate.getTime() < thresholdDate.getTime();
		} else if (Constants.ROP_GT.equals(relationship)) {
			return labelDate.getTime() > thresholdDate.getTime();
		} else if (Constants.ROP_LE.equals(relationship)) {
			return labelDate.getTime() <= thresholdDate.getTime();
		} else if (Constants.ROP_GE.equals(relationship)) {
			return labelDate.getTime() >= thresholdDate.getTime();
		}
		return false;
	}

	/**
	 * 校验开票信息中的关键字段
	 *
	 * @param relationship			关系
	 * @param thresholdValueStr		阈值
	 * @param labelValueStr			标签内容
	 * @return
	 */
	private boolean matchInvoiceInfo(String relationship, String thresholdValueStr, String labelValueStr) {
		if (StringUtil.isBlank(thresholdValueStr) || StringUtil.isBlank(labelValueStr)) {
			return false;
		}
		String key = null;
		try {
			Parameter parameter = parameterService.readOneByProperty("paramkey", Constants.INVOICE_INFO_THRESHOLD_KEY);
			if (parameter == null) {
				return false;
			}
			key = parameter.getParamvalue();
		} catch (ServiceException e) {
			logger.error("", e);
		}
		if (StringUtil.isBlank(key)) {
			return false;
		}
		String[] infoItems = labelValueStr.split("###");
		String labelValue = "";
		for (String item : infoItems) {
			if (item.contains(key)) {
				String[] kv = item.split(":");
				if (kv.length < 2) {
					return false;
				}
				labelValue = kv[1];
				break;
			}
		}
		if (Constants.ROP_EQ.equals(relationship)) {
			return StringUtils.equals(labelValue, thresholdValueStr);
		} else if (Constants.ROP_NE.equals(relationship)) {
			return !StringUtils.equals(labelValue, thresholdValueStr);
		}
		return false;
	}

	/**
	 * 获取关系的名称
	 *
	 * @param relationship
	 *            关系
	 */
	private String getMatchName(String relationship) {
		if (Constants.ROP_EQ.equals(relationship)) {
			return "等于";
		} else if (Constants.ROP_NE.equals(relationship)) {
			return "不等于";
		} else if (Constants.ROP_LT.equals(relationship)) {
			return "小于";
		} else if (Constants.ROP_GT.equals(relationship)) {
			return "大于";
		} else if (Constants.ROP_LE.equals(relationship)) {
			return "小于等于";
		} else if (Constants.ROP_GE.equals(relationship)) {
			return "大于等于";
		}
		return "";
	}

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
			// 电商供应商的流程中，产品id不是必须的
			String entityTypeStr = reqDto.getEntityType();
			int entityType = EntityType.SUPPLIER.ordinal();
			boolean needProductId = true;

			if (erpFlow.getAssociateType() == FlowAssociateType.USER.ordinal()) {
				needProductId = false;
			} else {
				if (StringUtils.isBlank(reqDto.getSupplierId())) {
					logger.info("供应商id不能为空");
					return BaseResponse.error("供应商id不能为空");
				}
			}

			if (StringUtils.isNotBlank(entityTypeStr) && StringUtils.isNumeric(entityTypeStr)) {
				Optional<EntityType> entityTypeOpt = EntityType.getEnumsByCode(Integer.parseInt(entityTypeStr));
				if (entityTypeOpt.isPresent()) {
					needProductId = entityTypeOpt.get() != EntityType.SUPPLIER_DS;
					entityType = entityTypeOpt.get().ordinal();
				}
			}
			if (needProductId && StringUtil.isBlank(reqDto.getProductId())) {
				logger.info("产品id不能为空");
				return BaseResponse.error("产品id不能为空");
			}

			// 校验标签值是否合法
			String flowClass = erpFlow.getFlowClass();
			BaseFlowTask task = flowTaskManager.getFlowTasks(flowClass);
			if (task == null) {
				task = commonFlowTask;
			}
			if (needProductId) {
				String result = task.verifyFlowMsg(erpFlow, reqDto.getProductId(), reqDto.getFlowMsg());
				if (StringUtils.isNotBlank(result)) {
					return BaseResponse.error(result);
				}
			}
			// 账单流程，判断账单时间段是否有调价记录
			if (needProductId && Constants.BILL_FLOW_CLASS.equals(flowClass)) {
				JSONObject flowMsgObject = JSON.parseObject(reqDto.getFlowMsg(), Feature.OrderedField);
				String billMonth = flowMsgObject.getString(Constants.BILL_FLOW_MONTH_KEY); // 账单月份
				String productId = reqDto.getProductId(); // 产品
				Date startDate = DateUtil.convert(billMonth + "-01", DateUtil.format1);
				Date endDate = DateUtil.getMonthFinal(startDate);
				Map<TimeQuantum, ModifyPrice> mpRecord = modifyPriceService.getModifyPrice(productId, startDate, endDate);
				// 没有调价记录，不能创建账单
				if (mpRecord == null) {
					return BaseResponse.error("账单流程申请失败，账单时间段没有调价信息");
				}
			}
			String remark = "";
			String flowMsg = reqDto.getFlowMsg();
			// 校验账单信息有没有超出剩余金额
			BaseResponse<String> verifyResult = verifyBill(flowId, flowMsg, "");
			if (verifyResult != null) {
				return verifyResult;
			}
			if (needProductId && StringUtils.isNotBlank(flowMsg)) {
				JSONObject flowMsgJson = JSONObject.parseObject(flowMsg, Feature.OrderedField);
				flowMsgJson.put("entityType", EntityType.SUPPLIER.ordinal());
				// 调价流程，附加上一次调价的信息
				setAdjustFlowBeforePrice(flowMsgJson, reqDto.getProductId(), flowClass);
				flowMsg = flowMsgJson.toJSONString();
				remark = flowMsgJson.getString("备注");
			}
			// 校验这个月份是否已经申请过
			if (needProductId) {
				Boolean billed = checkBillFlowExist(flowId, reqDto.getProductId(), flowMsg);
				if (billed) {
					return BaseResponse.error("该月份已经申请过，请勿重复申请");
				}
			}
			// 新建flowEnt
			FlowEnt ent = new FlowEnt();
			ent.setFlowStatus(FlowStatus.NOT_AUDIT.ordinal());
			ent.setSupplierId(reqDto.getSupplierId());
			ent.setFlowId(erpFlow.getFlowId());
			// 设置本流程除了节点中的角色外，哪些角色能看
			ent.setViewerRoleId(erpFlow.getViewerRoleId());
			ent.setDeptId(user.getDeptId());
			String flowTitle = erpFlow.getFlowName();
			Supplier supplier = supplierService.read(reqDto.getSupplierId());
			if (supplier != null) {
				flowTitle += "(";
				flowTitle += supplier.getCompanyName();
			}
			if (needProductId) {
				flowTitle += "-";
				Product product = productService.read(reqDto.getProductId());
				if (product != null) {
					flowTitle += product.getProductName();
					ent.setProductId(product.getProductId());
					// 账单流程基础数据
					String baseData = getBillBaseData(product, flowMsg);
					if (StringUtil.isNotBlank(baseData)) {
						JSONObject flowMsgJson = JSON.parseObject(flowMsg, Feature.OrderedField);
						flowMsgJson.put(Constants.FLOW_BASE_DATA_KEY, baseData);
						flowMsg = flowMsgJson.toJSONString();
					}
				}
			}
			if (supplier != null) {
				flowTitle += ")";
			}
			ent.setFlowTitle(flowTitle);
			String startNodeId = erpFlow.getStartNodeId();
			FlowNode flowNode = flowNodeService.read(startNodeId);
			if (flowNode != null) {
				ent.setNodeId(flowNode.getNextNodeId());
			}
			if (flowClass.equals(Constants.DS_PURCHASE_FLOW_CLASS)) {
				// 供应商采购流程，生成采购单编号
				JSONObject flowMsgJson = JSON.parseObject(flowMsg, Feature.OrderedField);
				if (flowMsgJson.containsKey(Constants.DS_BUY_ORDER_NUMBER)) {
					Date date = new Date();
					String no = dsBuyOrderService.buildBuyOrderNo(user, date);
					flowMsgJson.put(Constants.DS_BUY_ORDER_NUMBER, no);
				}
				flowMsg = flowMsgJson.toString();
			}
			ent.setFlowMsg(flowMsg);
			ent.setFlowType(erpFlow.getFlowType());
			ent.setOssUserId(user.getOssUserId());
			ent.setRemark(remark);
			ent.setWtime(new Timestamp(System.currentTimeMillis()));
			ent.setEntityType(entityType);
			ent.setPlatform(reqDto.getPlatform());
			entService.save(ent);
			// 生成日志记录
			FlowLog flowLog = new FlowLog();
			flowLog.setFlowId(ent.getFlowId());
			flowLog.setFlowEntId(ent.getId());
			flowLog.setAuditResult(AuditResult.CREATED.getCode());
			flowLog.setNodeId(startNodeId);
			flowLog.setOssUserId(ent.getOssUserId());
			flowLog.setWtime(new Timestamp(System.currentTimeMillis()));
			flowLog.setRemark("");
			flowLog.setFlowMsg(flowMsg);
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
						automaticAuditByCondition(ent, nextNode);
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
			JSONObject msgObject = JSON.parseObject(flowMsg, Feature.OrderedField);
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
	 * 获取账单的基础数据（平台的基础数据）
	 *
	 * @param product
	 *            产品
	 * @return JSONObject
	 */
	@Override
	public String getBillBaseData(Product product, String flowMsg) {
		if (product == null || StringUtil.isBlank(flowMsg)) {
			return null;
		}
		JSONObject flowMsgObject = JSON.parseObject(flowMsg, Feature.OrderedField);
		String billTime = flowMsgObject.getString(Constants.BILL_FLOW_MONTH_KEY);
		if (StringUtil.isBlank(billTime)) {
			return null;
		}
		// 产品对应短信云的通道id
		String channelId = product.getProductMark();
		// 产品没有通道id
		if (StringUtil.isBlank(channelId)) {
			return null;
		}
		Date startDate = DateUtil.convert(billTime + "-01", DateUtil.format1);
		Date endDate = DateUtil.getMonthFinal(startDate);
		// 产品类型
		int productType = product.getProductType();
		// 获取成功数
		if (productType == productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_INTER_SMS)) {
			// 国际短信
			Map<Date, Map<String, Long>> countryCountMap = getPlatformSuccessCount4Inter(productType, channelId, startDate, endDate);
			return getFlowBaseData4Inter(product, countryCountMap, startDate, endDate);
		} else {
			Map<Date, Long> platformSuccessCountMap = getPlatformSuccessCount(productType, channelId, startDate, endDate);
			return getFlowBaseData(product, platformSuccessCountMap, startDate, endDate);
		}
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
	 * 根据不同的产品类型 获取不同的统计数据
	 *
	 * @param product
	 *            产品信息
	 * @param platformSuccessCountMap
	 *            成功数
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 */
	private String getFlowBaseData(Product product, Map<Date, Long> platformSuccessCountMap, Date startDate, Date endDate) {
		// 总数
		double sum = 0d;
		// 省份
		int province = 0;
		// 省份价格
		double provincePrice = 0d;
		// 产品类型
		int productType = product.getProductType();
		// 通道id
		String channelId = product.getProductMark();
		// 消息类型
		String msgType = getMsgType(productType);
		// 结果的JSON对象
		JSONObject json = new JSONObject();
		// 套餐最低消费金额
		double lowdissipation = product.getLowdissipation();
		// 套餐最低消费条数
		long unitvalue = product.getUnitvalue();
		// 省网成功数
		long provinceCountSum = 0L;
		// 省网计费金额
		BigDecimal provincePaymentSum = new BigDecimal(0);
		// 成功数 小于 最小消费条数
		// 单价 = 最低消费金额/最低消费条数
		// 平台账单金额 = 最低消费金额
		long successCountSum = platformSuccessCountMap.values().stream().mapToLong(Long::longValue).sum();
		if (unitvalue >= successCountSum) {
			json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successCountSum);
			// 单价保留6位
			json.put(Constants.DAHAN_PRICE_KEY, String.format("%.6f", lowdissipation / unitvalue));
			// 金额保留2位
			json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, String.format("%.2f", lowdissipation));
			json.put(Constants.DAHAN_REMARK_KEY, "发送" + msgType + "条数低于最低套餐，最低套餐条数为" + unitvalue + "条");
			return json.toJSONString();
		} else {
			successCountSum = 0L;
			// 调价信息
			Map<TimeQuantum, ModifyPrice> mPrice = modifyPriceService.getModifyPrice(product.getProductId(), startDate, endDate);
			StringBuffer markMsg = new StringBuffer();
			Date lastEnd = null;
			if (CollectionUtils.isEmpty(mPrice)) {
				markMsg.append(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
			} else {
				lastEnd = startDate;
				for (TimeQuantum timeQuantum : mPrice.keySet()) {
					if (lastEnd != null) {
						if (DateUtil.getNextDayStart(lastEnd).getTime() < timeQuantum.getStartDate().getTime()) {
							markMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
									+ DateUtil.convert(DateUtil.getLastDayStart(timeQuantum.getStartDate()), DateUtil.format1) + "，");
						}
					}
					lastEnd = timeQuantum.getEndDate();
				}
				if (!StringUtils.equals(DateUtil.convert(lastEnd, DateUtil.format1), DateUtil.convert(endDate, DateUtil.format1))) {
					markMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
				}
			}
			if (markMsg.length() > 0) {
				markMsg.insert(0, "无调价信息时间段：");
			}
			if (!CollectionUtils.isEmpty(mPrice) && !CollectionUtils.isEmpty(platformSuccessCountMap)) {
				Map<String, List<Entry<TimeQuantum, ModifyPrice>>> groupMap = mPrice.entrySet().stream()
						.collect(Collectors.groupingBy(entry -> entry.getValue().getModifyPriceId()));
				JSONArray timeQuantumPriceArrInfo = new JSONArray();
				for (Entry<String, List<Entry<TimeQuantum, ModifyPrice>>> entry : groupMap.entrySet()) {
					JSONObject timeQuantumJsonInfo = new JSONObject();
					List<TimeQuantum> list = entry.getValue().stream().map(Entry::getKey).collect(Collectors.toList());
					ModifyPrice modifyPrice = entry.getValue().get(0).getValue();
					if (!CollectionUtils.isEmpty(list) && modifyPrice != null) {
						long successCount = list.stream()
								.mapToLong(timeQuantum -> getSuccessCountByDate(platformSuccessCountMap, timeQuantum.getStartDate(), timeQuantum.getEndDate()))
								.sum();
						successCountSum += successCount;
						// 梯度价格
						List<DeductionPrice> deductionList = getProvincePrice(modifyPrice.getModifyPriceId());
						if (deductionList != null && deductionList.size() > 0) {
							List<String> dates = list.stream().map(timeQuantum -> DateUtil.convert(timeQuantum.getStartDate(), DateUtil.format1) + "~"
									+ DateUtil.convert(timeQuantum.getEndDate(), DateUtil.format1)).collect(Collectors.toList());
							markMsg.append(StringUtils.join(dates.iterator(), "、")).append("：");
							timeQuantumJsonInfo.put("timeQuantum", StringUtils.join(dates.iterator(), "、"));
							timeQuantumJsonInfo.put("successCount", successCount);
							DeductionPrice deductionPriceInfo = deductionList.get(0);
							if (deductionPriceInfo.getProvincePrice() != null && deductionPriceInfo.getProvincePrice().doubleValue() != 0) {
								// 省网价格
								provincePrice = deductionPriceInfo.getProvincePrice().doubleValue();
							}
							// 短信类型 产品省份不是全国的时候
							long pCount = 0L;
							if ((productTypeService.getProductTypeValueByKey(Constants.PRODUCT_TYPE_KEY_SMS) == productType)
									&& product.getBaseProvince() != 0) {
								pCount = list.stream().map(timeQuantum -> getProvinceCount(channelId, product.getBaseProvince(), timeQuantum.getStartDate(),
										timeQuantum.getEndDate())).mapToLong(Long::longValue).sum();
								if (pCount != 0L) {
									provinceCountSum += pCount;
								}
							}
							// 统一价
							if (modifyPrice.getPriceType() == PriceType.UNIFORM_PRICE.getCode()) {
								// 单价
								double price = deductionPriceInfo.getPrice().doubleValue();
								markMsg.append("统一价：单价【").append(price).append("】、省网价【").append(provincePrice).append("】，");
								if (pCount != 0) {
									if (deductionPriceInfo.getProvincePrice() != null && deductionPriceInfo.getProvincePrice().doubleValue() > 0) {
										sum += (((successCount - pCount) * price) + (pCount * provincePrice));
										provincePaymentSum = provincePaymentSum.add((new BigDecimal(pCount)).multiply(new BigDecimal(provincePrice)));
									} else {
										sum += successCount * price;
									}
								} else {
									sum += successCount * price;
								}
								timeQuantumJsonInfo.put("modifyPriceInfo", "统一价：单价【" + price + "】、省网价【" + provincePrice + "】");
								timeQuantumJsonInfo.put("provinceSuccessCount", pCount);
							} else if (modifyPrice.getPriceType() == PriceType.STAGE_PRICE.getCode()) {
								String stagePriceMsg = "阶段价：【";
								// 阶段价
								for (DeductionPrice deductionPrice : deductionList) {
									double ladderPrice = deductionPrice.getPrice().doubleValue();
									if (successCount >= deductionPrice.getMaxSend() && deductionPrice.getMaxSend() > deductionPrice.getMinSend()) {
										sum += ((deductionPrice.getMaxSend() - deductionPrice.getMinSend()) * ladderPrice);
										stagePriceMsg += "第" + (deductionPrice.getGradient() + 1) + "阶段最大发送："
												+ String.format("%d", (long) deductionPrice.getMaxSend()) + "，阶段价格：" + String.format("%.6f", ladderPrice)
												+ "元；";
									} else if (successCount > deductionPrice.getMinSend()) {
										sum += ((successCount - (deductionPrice.getMinSend())) * ladderPrice);
										stagePriceMsg += "第" + (deductionPrice.getGradient() + 1) + "阶段最大发送："
												+ String.format("%d", (long) deductionPrice.getMaxSend()) + "，阶段价格：" + String.format("%.6f", ladderPrice) + "元";
									}
								}
								stagePriceMsg += "】";
								timeQuantumJsonInfo.put("modifyPriceInfo", stagePriceMsg);
								markMsg.append(stagePriceMsg + "，");
							} else if (modifyPrice.getPriceType() == PriceType.STEPPED_PRICE.getCode()) {
								String steppedPriceMsg = "阶梯价：";
								// 阶梯价
								double ladderPrice = 0;
								for (DeductionPrice deductionPrice : deductionList) {
									if (successCount >= deductionPrice.getMinSend()
											&& (deductionPrice.getMinSend() >= deductionPrice.getMaxSend() || successCount < deductionPrice.getMaxSend())) {
										ladderPrice = deductionPrice.getPrice().doubleValue();
										sum += successCount * ladderPrice;
										steppedPriceMsg += "【符合第" + (deductionPrice.getGradient() + 1) + "阶梯，最小发送："
												+ String.format("%d", (long) deductionPrice.getMinSend()) + "，最大发送："
												+ String.format("%d", (long) deductionPrice.getMaxSend()) + "，阶梯价：" + ladderPrice + "】";
									}
								}
								timeQuantumJsonInfo.put("modifyPriceInfo", steppedPriceMsg);
								markMsg.append(steppedPriceMsg + "，");
							}
							timeQuantumPriceArrInfo.add(timeQuantumJsonInfo);
						}
					}
				}
				json.put(Constants.BILL_PRICE_INFO_KEY, timeQuantumPriceArrInfo.toJSONString());
				json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successCountSum);
				if (provinceCountSum > 0) {
					json.put(Constants.DAHAN_PROVINCE_SUCCESS_COUNT_KEY, provinceCountSum);
					if (provincePaymentSum.divide(new BigDecimal(provinceCountSum), 6, BigDecimal.ROUND_HALF_UP).signum() >= 0) {
						json.put(Constants.DAHAN_PROVINCE_PRICE_KEY,
								String.format("%.6f", provincePaymentSum.divide(new BigDecimal(provinceCountSum), 6, BigDecimal.ROUND_HALF_UP).doubleValue()));
					}
				}
				json.put(Constants.DAHAN_PRICE_KEY, String.format("%.6f", successCountSum == 0 ? 0 : (sum / successCountSum)));
				json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY, String.format("%.2f", sum));
				json.put(Constants.DAHAN_REMARK_KEY, markMsg.toString());
				return json.toJSONString();
			}
		}
		return null;
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
	private String getFlowBaseData4Inter(Product product, Map<Date, Map<String, Long>> countryCountMap, Date startDate, Date endDate) {
		try {
			if (!CollectionUtils.isEmpty(countryCountMap)) {
				DecimalFormat df = new DecimalFormat("###.####");
				String msgType = productTypeService.getProductTypeNameByValue(product.getProductType());
				long successCount = countryCountMap.values().stream().mapToLong(map -> map.values().stream().mapToLong(Long::longValue).sum()).sum();
				double lowdissipation = product.getLowdissipation();
				long unitvalue = product.getUnitvalue();
				JSONObject json = new JSONObject();
				if (unitvalue >= successCount) {
					json.put(Constants.DAHAN_SUCCESS_COUNT_KEY, successCount);
					json.put(Constants.DAHAN_PRICE_KEY,
							(lowdissipation / unitvalue) == ((int) lowdissipation / unitvalue) ? df.format(lowdissipation / unitvalue)
									: String.format("%.6f", lowdissipation / unitvalue));
					json.put(Constants.DAHAN_PAYMENT_AMOUNT_KEY,
							lowdissipation == (int) lowdissipation ? (int) lowdissipation : String.format("%.2f", lowdissipation));
					json.put(Constants.DAHAN_REMARK_KEY, "发送" + msgType + "条数低于最低套餐，最低套餐条数为" + (int) unitvalue + "条");
					return json.toJSONString();
				} else {
					JSONArray timeQuantumPriceArrInfo = new JSONArray();
					Map<TimeQuantum, ModifyPrice> mdPrice = modifyPriceService.getModifyPrice(product.getProductId(), startDate, endDate);
					if (!CollectionUtils.isEmpty(mdPrice)) {
						Map<TimeQuantum, Map<String, Double>> interPricesMap = modifyPriceService.getInterPrices(new ArrayList<>(mdPrice.values()), startDate,
								endDate);
						StringBuffer markMsg = new StringBuffer();
						Date lastEnd = null;
						if (CollectionUtils.isEmpty(interPricesMap)) {
							markMsg.append(DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1));
						} else {
							lastEnd = startDate;
							for (TimeQuantum timeQuantum : interPricesMap.keySet()) {
								if (lastEnd != null) {
									if (DateUtil.getNextDayStart(lastEnd).getTime() < timeQuantum.getStartDate().getTime()) {
										markMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
												+ DateUtil.convert(DateUtil.getLastDayStart(timeQuantum.getStartDate()), DateUtil.format1) + "，");
									}
								}
								lastEnd = timeQuantum.getEndDate();
							}
							if (!StringUtils.equals(DateUtil.convert(lastEnd, DateUtil.format1), DateUtil.convert(endDate, DateUtil.format1))) {
								markMsg.append(DateUtil.convert(DateUtil.getNextDayStart(lastEnd), DateUtil.format1) + "~"
										+ DateUtil.convert(endDate, DateUtil.format1));
							}
						}
						if (markMsg.length() > 0) {
							markMsg.insert(0, "无调价信息时间段：");
						}
						successCount = 0L;
						BigDecimal paymentAmount = new BigDecimal(0);
						for (Entry<TimeQuantum, Map<String, Double>> entry : interPricesMap.entrySet()) {
							JSONObject timeQuantumJsonInfo = new JSONObject();
							timeQuantumJsonInfo.put("timeQuantum", DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1) + "~"
									+ DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1) + "：");
							timeQuantumJsonInfo.put("modifyPriceInfo", mdPrice.get(entry.getKey()).getRemark());
							markMsg.append(DateUtil.convert(entry.getKey().getStartDate(), DateUtil.format1)).append("~")
									.append(DateUtil.convert(entry.getKey().getEndDate(), DateUtil.format1)).append("：");
							Map<String, Double> priceMap = entry.getValue();
							Date sDate = entry.getKey().getStartDate();
							Date eDate = entry.getKey().getEndDate();
							List<String> withOutPriceInfo = new ArrayList<>();
							long sectionCount = 0;
							for (; !sDate.after(eDate); sDate = DateUtil.getNextDayStart(sDate)) {
								Map<String, Long> successCountMap = countryCountMap.get(sDate);
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
				}
			}
		} catch (Exception e) {
			logger.error("生成国际账单流程基础数据异常", e);
		}

		return null;
	}

	// 获取省份发送量
	private Long getProvinceCount(String channelId, int baseProvince, Date startDate, Date endDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select sum(successCount) from SupplierProductTj where statsDate>=:startDate "
				+ "and statsDate<:endDate and regionId=:regionId and channelId=:channelId";
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("regionId", baseProvince);
		params.put("channelId", channelId);
		List<Object> smsprovinceCountList = null;
		try {
			smsprovinceCountList = baseDao.findByhql(hql, params, 0);
		} catch (BaseException e) {
			logger.error("查询短信省份统计数据出现异常", e);
		}
		if (smsprovinceCountList != null && smsprovinceCountList.size() > 0) {
			Object smsprovinceCountLists = smsprovinceCountList.get(0);
			if (smsprovinceCountLists != null) {
				long provinceCount = new BigInteger(smsprovinceCountLists.toString()).longValue();
				logger.info("短信通道id：" + channelId + "，该通道落地省份在" + DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1) + "的省网发送量：" + provinceCount);
				return provinceCount;
			} else {
				logger.info("短信通道id：" + channelId + "，该通道落地省份在" + DateUtil.convert(startDate, DateUtil.format1) + "~" + DateUtil.convert(endDate, DateUtil.format1) + "无发送量");
			}
		}
		return 0L;
	}

	/**
	 * 获取梯度价格
	 *
	 * @param modifyPriceId
	 *            调价信息id
	 * @return DeductionPrice 梯度价格
	 */
	private List<DeductionPrice> getProvincePrice(String modifyPriceId) {
		// 查询梯度价格表
		SearchFilter filter = new SearchFilter();
		filter.getRules().add(new SearchRule("modifyPriceId", Constants.ROP_EQ, modifyPriceId));
		filter.getOrders().add(new SearchOrder("gradient", Constants.ROP_ASC));
		try {
			return deductionPriceDao.queryAllBySearchFilter(filter);
		} catch (DaoException e) {
			logger.error("获取梯度价格时出现异常，", e);
		}
		return null;
	}

	/**
	 * 根据产品类型、产品id、时间段 获取成功量
	 *
	 * @param productType
	 *            产品类型
	 * @param channelId
	 *            产品对应短信云通道id
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return Long
	 */
	private Map<Date, Long> getPlatformSuccessCount(int productType, String channelId, Date startDate, Date endDate) {
		Map<Date, Long> platformSuccessCountMap = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select statsDate,channelId,sum(successCount) from SupplierProductTj "
				+ " where channelId =:channelId and statsDate>=:startDate and statsDate<:endDate and productType=" + productType + " group by statsDate";
		params.put("channelId", channelId);
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
			for (Object[] countArr : smsCountList) {
				if (countArr != null && countArr.length >= 3) {
					if (countArr[1] != null) {
						platformSuccessCountMap.put((Date) countArr[0], ((Number) countArr[2]).longValue());
					}
				}
			}
		}
		return platformSuccessCountMap;
	}

	/**
	 * 根据产品类型、产品id、时间段 获取成功量(国际)
	 *
	 * @param productType
	 *            产品类型
	 * @param channelId
	 *            产品对应短信云通道id
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return Long
	 */
	private Map<Date, Map<String, Long>> getPlatformSuccessCount4Inter(int productType, String channelId, Date startDate, Date endDate) {
		Map<Date, Map<String, Long>> countryCountMap = new TreeMap<>(new Comparator<Date>() {

			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}

		});
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select statsDate, countryCode, sum(successCount) from SupplierProductTj "
				+ " where channelId =:channelId and statsDate >=:startDate and statsDate <:endDate and productType = :productType "
				+ " group by countryCode, statsDate";
		params.put("channelId", channelId);
		params.put("productType", productType);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		// 查询短信统表数据
		List<Object> smsCountList = null;
		try {
			smsCountList = baseDao.findByhql(hql, params, Integer.MAX_VALUE);
			if (!ListUtils.isEmpty(smsCountList)) {
				smsCountList.forEach(item -> {
					Object[] objs = (Object[]) item;
					if (objs.length == 3) {
						if (countryCountMap.get((Date) objs[0]) == null) {
							countryCountMap.put((Date) objs[0], new HashMap<>());
						}
						countryCountMap.get((Date) objs[0]).put((String) objs[1], ((Number) objs[2]).longValue());
					}
				});
			}
		} catch (BaseException e) {
			logger.error("查询平台成功数时错误，错误信息：", e);
		}
		return countryCountMap;
	}

	/**
	 * 根据产品类型、产品id、时间段 获取成功量
	 *
	 * @param productType
	 *            产品类型
	 * @return Long
	 */
	private String getMsgType(int productType) {
		String productTypeKey = productTypeService.getProductTypeKeyByValue(productType);
		if (Constants.PRODUCT_TYPE_KEY_SMS.equals(productTypeKey)) {
			return "短信";
		} else if (Constants.PRODUCT_TYPE_KEY_MMS.equals(productTypeKey)) {
			return "彩信";
		} else {
			logger.info("未识别产品类型，productType：{}", productType);
		}
		return "";
	}

	/**
	 * 设置调价信息 原来价格信息
	 * 
	 * @param flowJson
	 *            流程申请的JSON数据
	 * @param productId
	 *            产品id
	 * @param flowClass
	 *            流程类
	 */
	@Override
	public void setAdjustFlowBeforePrice(JSONObject flowJson, String productId, String flowClass) {
		if (Constants.ADJUST_PRICE_FLOW_CLASS.equals(flowClass) || Constants.INTER_ADJUST_PRICE_FLOW_CLASS.equals(flowClass)) {
			Date startValidTime = null;
			Date endValidTime = null;
			try {
				// 调价开始、结束日期，已经是精确到天
				String start = flowJson.getString(Constants.PRICE_START_DATE_KEY);
				String end = flowJson.getString(Constants.PRICE_END_DATE_KEY);
				if (StringUtil.isNotBlank(start) && StringUtil.isNotBlank(end)) {
					startValidTime = DateUtil.getTodayStatrTime(start);
					endValidTime = DateUtil.getTodayEndTime(end);
				}
			} catch (Exception e) {
				logger.error("从JSON对象中获取日期异常", e);
			}
			// 只有调价流程的时候才需要设置原来的价格信息
			List<Map<String, Object>> productPriceInfo = modifyPriceService.findProductPriceInfo(productId, startValidTime, endValidTime);
			if (productPriceInfo != null && !productPriceInfo.isEmpty()) {
				flowJson.put(Constants.PRICE_BEFORE_ADJUST_KEY, JSON.toJSONString(productPriceInfo));
			}
		}
	}

	/**
	 * 按 流程id 查 账单的某个金额字段 被还没走完的流程占用的金额
	 * 例如：要对一个账单新建开票流程，先检查这个账单有没有在走的开票流程，有的话先计算出在走的流程一共开了多少金额的票，
	 * 然后再从这个账单减去已在走流程的开票金额，得到本次新流程能开多少金额的票
	 * 
	 * @param flowId
	 *            指定查询某个流程设计id
	 * @param billId
	 *            账单id
	 * @param type
	 *            字段（thisReceivables，thisPayment）
	 * @param flowEntId
	 *            要排除某个流程（待处理流程自己）
	 * @return
	 */
	@Override
	public BaseResponse<String> queryApplying(String flowId, String billId, String type, String flowEntId) {
		logger.info("查询账单被申请中的流程占用的金额开始，flowId：" + flowId + "，账单id：" + billId + "，字段：" + type + "，排除flowEntId：" + flowEntId);
		try {
			SearchFilter filter = new SearchFilter();
			List<Integer> fsList = new ArrayList<>();
			fsList.add(FlowStatus.NOT_AUDIT.ordinal());
			fsList.add(FlowStatus.NO_PASS.ordinal());
			filter.getRules().add(new SearchRule("flowId", Constants.ROP_EQ, flowId));
			filter.getRules().add(new SearchRule("flowStatus", Constants.ROP_IN, fsList));
			filter.getRules().add(new SearchRule("flowMsg", Constants.ROP_CN, billId));
			if (StringUtils.isNotBlank(flowEntId)) {
				filter.getRules().add(new SearchRule("id", Constants.ROP_NE, flowEntId));
			}
			List<FlowEnt> list = entService.queryAllBySearchFilter(filter);
			BigDecimal usedAmount = new BigDecimal(0);
			if (!ListUtils.isEmpty(list)) {
				for (FlowEnt flowEnt : list) {
					String flowMsg = flowEnt.getFlowMsg();
					JSONObject json = JSON.parseObject(flowMsg, Feature.OrderedField);
					JSONArray jsonArray = json.getJSONArray(Constants.BILL_INFO_KEY);
					if (jsonArray != null && !jsonArray.isEmpty()) {
						for (Object object : jsonArray) {
							JSONObject billJson = (JSONObject) object;
							if (StringUtils.equals(billId, billJson.getString("id"))) {
								String thisMoney = billJson.getString(type);
								if (NumberUtils.isParsable(thisMoney)) {
									usedAmount = usedAmount.add(new BigDecimal(thisMoney));
								}
							}
						}
					}
				}
			}
			logger.info("查询到在flowId：" + flowId + " 的流程中，账单id：" + billId + " 的字段：" + type + " 的被占用金额：" + usedAmount.toPlainString());
			return BaseResponse.success(String.format("%.2f", usedAmount));
		} catch (ServiceException e) {
			logger.error("查询账单被申请中的流程占用的金额异常", e);
			return BaseResponse.error("查询账单被申请中的流程占用的金额异常");
		}
	}

	/**
	 * 验证账单金额是否合法
	 */
	@Override
	public BaseResponse<String> verifyBill(String flowId, String flowMsg, String flowEntId) {
		if (StringUtils.isNotBlank(flowMsg)) {
			JSONObject json = JSON.parseObject(flowMsg, Feature.OrderedField);
			if (json != null && !json.isEmpty()) {
				JSONArray billArray = json.getJSONArray(Constants.BILL_INFO_KEY);
				if (billArray != null && !billArray.isEmpty()) {
					for (Object obj : billArray) {
						JSONObject billJson = (JSONObject) obj;
						String billId = billJson.getString(Constants.ID_KEY);
						if (StringUtils.isNotBlank(billJson.getString(Constants.BILL_THIS_RECEIVABLES_KEY))) {
							String thisReceivables = billJson.getString(Constants.BILL_THIS_RECEIVABLES_KEY);
							String receivables = billJson.getString(Constants.BILL_RECEIVABLES_KEY);
							BaseResponse<String> result = queryApplying(flowId, billId, Constants.BILL_THIS_RECEIVABLES_KEY, flowEntId);
							if (result.getCode() == 200) {
								BigDecimal thisRec = new BigDecimal(thisReceivables);
								BigDecimal rec = new BigDecimal(receivables);
								BigDecimal applying = new BigDecimal(result.getMsg());
								if (applying.add(thisRec).compareTo(rec) > 0) {
									return BaseResponse.error(billJson.getString(Constants.BILL_TITLE_KEY) + "本次付款超出剩余应付");
								}
							}
						} else if (StringUtils.isNotBlank(billJson.getString(Constants.BILL_THIS_PAYMENT_KEY))) {
							String thisPayment = billJson.getString(Constants.BILL_THIS_PAYMENT_KEY);
							String payables = billJson.getString(Constants.BILL_PAYABLES_KEY);
							BaseResponse<String> result = queryApplying(flowId, billId, Constants.BILL_THIS_PAYMENT_KEY, flowEntId);
							if (result.getCode() == 200) {
								BigDecimal thisPay = new BigDecimal(thisPayment);
								BigDecimal pay = new BigDecimal(payables);
								BigDecimal applying = new BigDecimal(result.getMsg());
								if (applying.add(thisPay).compareTo(pay) > 0) {
									return BaseResponse.error(billJson.getString(Constants.BILL_TITLE_KEY) + "本次收款超出剩余应收");
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 检查流程标签信息修改的部分
	 *
	 * @param before
	 *            审核前的流程标签信息
	 * @param after
	 *            审核后的流程标签信息
	 * @return
	 */
	private String checkChanges(String before, String after, String flowClass) {
		if (before.equals(after)) {
			return "";
		}
		JSONObject beforeJson = JSONObject.parseObject(before, Feature.OrderedField);
		JSONObject afterJson = JSONObject.parseObject(after, Feature.OrderedField);
		JSONObject changes = new JSONObject();
		// 取标签名的并集，防止修改前后标签数不一致导致没有保存修改的内容
		Set<String> beforeKeys = beforeJson.keySet();
		Set<String> afterKeys = afterJson.keySet();
		Set<String> keys = new HashSet<String>();
		keys.addAll(beforeKeys);
		keys.addAll(afterKeys);
		for (String key : keys) {
			if (StringUtils.equals(flowClass, Constants.BILL_FLOW_CLASS)
					&& (StringUtils.equals(key, Constants.FLOW_BASE_DATA_KEY) || StringUtils.equals(key, Constants.BILL_PRICE_INFO_KEY))) { // 账单流程不修改baseData
																																			// BILL_PRICE_INFO_KEY
																																			// 数据
				continue;
			}
			String beforeValue = beforeJson.getString(key);
			beforeValue = (beforeValue == null) ? "" : beforeValue;
			String afterValue = afterJson.getString(key);
			afterValue = (afterValue == null) ? "" : afterValue;
			if (!beforeValue.equals(afterValue)) {
				changes.put(key + "_修改前", beforeValue);
				changes.put(key, afterValue);
			}
		}
		return changes.toJSONString();
	}

	/**
	 * 验证账单金额是否合法
	 */
	public boolean verifyDsMatchOrderAmount(FlowEnt flowEnt) {
		String flowMsg = flowEnt.getFlowMsg();
		JSONObject flowJson = JSONObject.parseObject(flowMsg);
		if (flowJson.containsKey(Constants.DS_MATCH_ORDER) && StringUtil.isNotBlank(flowJson.getString(Constants.DS_MATCH_ORDER))) {
			String matchOrderStr = flowJson.getString(Constants.DS_MATCH_ORDER);
			JSONArray matchOrders = JSON.parseArray(matchOrderStr);
			for (int i = 0; i < matchOrders.size(); i++) {
				JSONObject matchOrder = matchOrders.getJSONObject(i);
				if (matchOrder.containsKey(Constants.DS_AMOUNT_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_AMOUNT_KEY))) {
					String amountStr = matchOrder.getString(Constants.DS_AMOUNT_KEY);
					Integer amount = Integer.parseInt(amountStr);
					if (matchOrder.containsKey(Constants.DS_DEPOT_NUM_KEY) && StringUtil.isNotBlank(matchOrder.getString(Constants.DS_DEPOT_NUM_KEY))) {
						String depotNumString = matchOrder.getString(Constants.DS_DEPOT_NUM_KEY);
						Integer depotNum = Integer.parseInt(depotNumString);
						if (amount > depotNum) {
							return false;
						}
					}
				}

			}
		}
		return true;
	}
}
