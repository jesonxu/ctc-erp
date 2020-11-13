package com.dahantc.erp.flowtask.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
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
import com.dahantc.erp.vo.supplierHistory.entity.SupplierHistory;
import com.dahantc.erp.vo.supplierHistory.service.ISupplierHistoryService;
import com.dahantc.erp.vo.user.entity.User;

@Service("dsSupplierFlowService")
public class DsSupplierFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(DsSupplierFlowService.class);

	public static final String FLOW_CLASS = Constants.DS_SUPPLIER_FLOW_CLASS;
	public static final String FLOW_NAME = Constants.DS_SUPPLIER_FLOW_NAME;

	@Autowired
	private IErpFlowService erpFlowService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private CommonFlowTask commonFlowTask;

	@Autowired
	private IFlowEntService flowEntService;

	@Autowired
	private IFlowLogService flowLogService;

	@Autowired
	private IFlowNodeService flowNodeService;
	
	@Autowired
	private ISupplierService supplierService;
	
	@Autowired
	private ISupplierHistoryService supplierHistoryService;

	@Override
	public String getFlowName() {
		return FLOW_NAME;
	}

	@Override
	public String getFlowClass() {
		return FLOW_CLASS;
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
			Supplier supplier = supplierService.read(flowEnt.getSupplierId());
			supplier.setStatus(0);
			supplierService.save(supplier, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) {
		if (auditResult == AuditResult.CANCLE.getCode()) {
			try {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("supplierId", Constants.ROP_EQ, flowEnt.getSupplierId()));
				filter.getOrders().add(new SearchOrder("wtime", Constants.ROP_DESC));
				List<SupplierHistory> supplierHistorys = supplierHistoryService.queryAllBySearchFilter(filter);
				if (!ListUtils.isEmpty(supplierHistorys)) {
					Supplier supplier = new Supplier();
					BeanUtils.copyProperties(supplierHistorys.get(0), supplier);
					supplierService.save(supplier, null, null);
					logger.info("恢复供应商信息到供应商表成功。");
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	/**
	 * 创建供应商审核流程
	 * 
	 * @param supplier
	 *            供应商信息
	 * @param user
	 *            用户信息
	 * @return
	 */
	public void buildDsSupplierFlow(Supplier supplier, User user, List<BankAccount> bankInfos) {
		logger.info("创建" + Constants.DS_SUPPLIER_FLOW_NAME + "开始，supplierId：" + supplier.getSupplierId());
		String flowId = null;
		int flowType = 0;
		String viewerRoleId = null;
		String flowClass = null;
		try {
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("flowName", Constants.ROP_EQ, Constants.DS_SUPPLIER_FLOW_NAME));
			List<ErpFlow> flowList = erpFlowService.queryAllBySearchFilter(filter);

			if (flowList != null && flowList.size() > 0) {
				ErpFlow flow = flowList.get(0);
				flowId = flow.getFlowId();
				flowType = flow.getFlowType();
				viewerRoleId = flow.getViewerRoleId();
				flowClass = flow.getFlowClass();
			} else {
				logger.error("系统无" + Constants.DS_SUPPLIER_FLOW_NAME + "，创建失败");
				return;
			}
			String title = Constants.DS_SUPPLIER_FLOW_NAME + "(" + supplier.getCompanyName() + ")";
			FlowEnt flowEnt = buildFlowEnt(title, flowId, flowType, user.getOssUserId(), null, supplier.getSupplierId(),
					viewerRoleId);
			flowEnt.setDeptId(user.getDeptId());
			// 流程标签内容
			JSONObject flowMsgJson = new JSONObject();
			flowMsgJson.put(Constants.COMPANY_NAME, supplier.getCompanyName());
			flowMsgJson.put(Constants.LEGAL_PERSON, supplier.getLegalPerson());
			flowMsgJson.put(Constants.REGISTRATION_NUMBER, supplier.getRegistrationNumber());
			flowMsgJson.put(Constants.POSTAL_ADDRESS, supplier.getPostalAddress());
			flowMsgJson.put(Constants.TELEPHONE_NUMBER, supplier.getTelephoneNumber());
			flowMsgJson.put(Constants.EMAIL, supplier.getEmail());
			flowMsgJson.put(Constants.WEBSITE, supplier.getWebsite());
			flowMsgJson.put(Constants.CONTACT_NAME, supplier.getContactName());
			flowMsgJson.put(Constants.CONTACT_PHONE, supplier.getContactPhone());
			flowMsgJson.put(Constants.CREATION_DATE, DateUtil.convert(supplier.getCreationDate(), DateUtil.format1));
			flowMsgJson.put(Constants.REGISTERED_CAPITAL, supplier.getRegisteredCapital());
			flowMsgJson.put(Constants.CORPORATE_NATURE, supplier.getCorporateNature());
			flowMsgJson.put(Constants.SUPPLIER_TYPE_ID, supplier.getSupplierTypeId());
			flowMsgJson.put(Constants.COMPANY_QUALIFICATION, supplier.getCompanyQualification());
			flowMsgJson.put(Constants.LEGAL_RISK, supplier.getLegalRisk());
			flowMsgJson.put(Constants.DELIVERY_CYCLE, supplier.getDeliveryCycle());
			flowMsgJson.put(Constants.COOPERATION_TYPE, supplier.getCooperationType());
			flowMsgJson.put(Constants.SETTLEMENT_TYPE, supplier.getSettlementType());
			flowMsgJson.put(Constants.SALE_TYPE, supplier.getSaleType());
			flowMsgJson.put(Constants.CERTIFICATION, supplier.getCertification());
			flowMsgJson.put(Constants.CONTRACT_FILES, supplier.getContractFiles());
			flowMsgJson.put(Constants.CORPORATE_CREDIT, supplier.getCorporateCredit());
			flowMsgJson.put(Constants.PRODUCT_RANGE, supplier.getProductRange());
			flowMsgJson.put(Constants.ADVANTAGE_PRODUCT, supplier.getAdvantageProduct());
			flowMsgJson.put(Constants.LOGISTICS, supplier.getLogistics());
			flowMsgJson.put(Constants.CASE_CONTRACT, supplier.getCaseContract());
			flowMsgJson.put(Constants.COMPANY_INTRODUCTION, supplier.getCompanyIntroduction());
			flowMsgJson.put(Constants.ANNUAL_INCOME, supplier.getAnnualIncome());
			flowMsgJson.put(Constants.IS_INCOME_PROVE, supplier.getIsIncomeProve());
			flowMsgJson.put(Constants.FINANCIAL_FILE, supplier.getFinancialFile());
			flowMsgJson.put(Constants.MANAGE_CERTIFICATION_FILE, supplier.getManageCertificationFile());
			
			JSONArray jsonArray = new JSONArray();
			if (!ListUtils.isEmpty(bankInfos)) {
				for (BankAccount bankAccount : bankInfos) {
					JSONObject json = new JSONObject();
					json.put("accountName", bankAccount.getAccountName());
					json.put("accountBank", bankAccount.getAccountBank());
					json.put("bankAccount", bankAccount.getBankAccount());
					jsonArray.add(json);
				}
			}
			flowMsgJson.put(Constants.DS_BANK_INFO, jsonArray.toJSONString());
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
	private FlowEnt buildFlowEnt(String title, String flowId, int flowType, String ossUserId, String productId,
			String supplierId, String viewerRoleId) throws Exception {
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
