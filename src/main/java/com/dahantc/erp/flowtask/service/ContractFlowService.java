package com.dahantc.erp.flowtask.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import com.dahantc.erp.vo.customerType.entity.CustomerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.ContractFlowStatus;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.enums.PayType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.contract.entity.Contract;
import com.dahantc.erp.vo.contract.service.IContractService;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.customerProduct.entity.CustomerProduct;
import com.dahantc.erp.vo.customerProduct.service.ICustomerProductService;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.product.entity.Product;
import com.dahantc.erp.vo.product.service.IProductService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

/**
 * 销售合同评审流程
 */
@Service("contractFlowService")
public class ContractFlowService extends BaseFlowTask {

	private static Logger logger = LogManager.getLogger(ContractFlowService.class);

	public static final String FLOW_CLASS = Constants.CONTRACT_FLOW_CLASS;

	public static final String FLOW_NAME = Constants.CONTRACT_FLOW_NAME;

	@Autowired
	private IContractService contractService;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private ICustomerProductService customerProductService;

	@Autowired
	private ISupplierService supplierService;

	@Autowired
	private IProductService productService;

	@Autowired
	private ICustomerTypeService customerTypeService;

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
		logger.info(FLOW_NAME + " 流程归档开始");
		flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());
		if (erpFlow != null) {
			try {
				String flowMsg = flowEnt.getFlowMsg();
				if (StringUtil.isNotBlank(flowMsg)) {
					JSONObject json = JSONObject.parseObject(flowMsg);
					if (json != null) {
						// 合同编号
						if (json.containsKey(Constants.CONTRACT_NUMBER) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_NUMBER))) {
							String contractNo = json.getString(Constants.CONTRACT_NUMBER);
							Contract contract = contractService.read(contractNo);
							contract.setStatus(ContractFlowStatus.FILED.getCode());
							boolean result = contractService.update(contract);
							logger.info("更新合同表记录" + (result ? "成功" : "失败") + "，合同编号：" + contractNo);
							// 合同流程归档，把客户更新为合同客户，客户的合同日期为空时，把合同流程的申请日期作为客户的合同日期
							if (flowEnt.getEntityType() == EntityType.CUSTOMER.ordinal()) {
								Customer customer = customerService.read(flowEnt.getSupplierId());
								if (null != customer) {
									try {
										Date contractDate = customer.getContractDate();
										if (null == contractDate) {
											customer.setContractDate(contract.getApplyDate());
										}
										boolean changeGrade = false;
										CustomerType nowCustomerType = customerTypeService.read(customer.getCustomerTypeId());
										CustomerType contractCustomerType = customerTypeService.getCustomerTypeByValue(CustomerTypeValue.CONTRACTED.getCode());
										// 当前客户类型不是合同客户时，才更新客户的客户类型为合同客户，并写一条升级记录
										// 已经是合同客户时不需要
										if (nowCustomerType.getCustomerTypeValue() != CustomerTypeValue.CONTRACTED.getCode()) {
											if (null != contractCustomerType) {
												customer.setCustomerTypeId(contractCustomerType.getCustomerTypeId());
												changeGrade = true;
											} else {
												logger.info("获取 客户类型-合同客户 失败，无法更新客户为合同客户");
											}
										}
										result = customerService.update(customer);
										logger.info("更新客户为合同客户" + (result ? "成功" : "失败") + "，客户id：" + customer.getCustomerId());
										if (result && changeGrade) {
											String depict = "客户：" + customer.getCompanyName() + "，" + FLOW_NAME + "归档，由" + nowCustomerType.getCustomerTypeName() + "升至" + contractCustomerType.getCustomerTypeName();
											customerService.changeGrade(customer, userService.read(flowEnt.getOssUserId()), nowCustomerType,
													contractCustomerType, depict, false);
										}
									} catch (Exception e) {
										logger.info("流程归档，更新客户为合同客户异常", e);
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error(erpFlow.getFlowName() + "流程归档异常，flowEntId：" + flowEnt.getId(), e);
				return false;
			}
		}
		logger.info(FLOW_NAME + " 流程归档结束");
		return true;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		logger.info(FLOW_NAME + "处理流程信息修改开始，flowEntId：" + flowEnt.getId());
		try {
			String flowMsg = flowEnt.getFlowMsg();
			if (StringUtil.isNotBlank(flowMsg)) {
				JSONObject json = JSONObject.parseObject(flowMsg);
				if (json != null) {
					saveOrUpdateContract(flowEnt, json, auditResult);
				}
			}
			logger.info(FLOW_NAME + "处理流程信息修改结束");
		} catch (Exception e) {
			logger.error(FLOW_NAME + "处理流程信息修改异常，flowEntId：" + flowEnt.getId(), e);
		}
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

	/**
	 * 生成合同表记录
	 *
	 * @param flowEnt
	 *            流程实体
	 * @param json
	 *            流程内容
	 * @param auditResult
	 *            审核操作代码
	 * @return
	 */
	public Contract saveOrUpdateContract(FlowEnt flowEnt, JSONObject json, int auditResult) {
		logger.info("生成或更新合同表记录开始，flowEntId：" + flowEnt.getId());
		Contract contract = null;
		String contractNo;
		// 是否保存，为true时保存，为false时更新
		boolean needSave = false;
		try {
			// 合同编号
			if (json.containsKey(Constants.CONTRACT_NUMBER) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_NUMBER))) {
				contractNo = json.getString(Constants.CONTRACT_NUMBER);
			} else {
				logger.info(Constants.CONTRACT_NUMBER + "不能为空，flowEntId：" + flowEnt.getId());
				return null;
			}

			if (AuditResult.CREATED.getCode() == auditResult) {
				// 创建操作，生成一条合同记录，设置合同状态为申请中状态
				contract = new Contract();
				contract.setContractId(contractNo);
				contract.setStatus(ContractFlowStatus.APPLYING.getCode());
				needSave = true;
			} else if (AuditResult.PASS.getCode() == auditResult || AuditResult.REJECTED.getCode() == auditResult) {
				// 通过、驳回流程，不用更新合同状态
				contract = contractService.read(contractNo);
			} else if (AuditResult.CANCLE.getCode() == auditResult) {
				// 取消流程，更新合同状态为取消
				contract = contractService.read(contractNo);
				contract.setStatus(ContractFlowStatus.CANCLE.getCode());
			}
			if (contract == null) {
				logger.error("生成或查询合同记录失败，合同编号：" + contractNo + "，flowEntId：" + flowEnt.getId());
				return null;
			}

			// 合同名称
			if (json.containsKey(Constants.CONTRACT_NAME) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_NAME))) {
				contract.setContractName(json.getString(Constants.CONTRACT_NAME));
			}
			// 申请人信息
			User user = userService.read(flowEnt.getOssUserId());
			contract.setOssUserId(user.getOssUserId());
			contract.setDeptId(user.getDeptId());
			contract.setProjectLeader(user.getRealName());
			// 申请日期
			if (json.containsKey(Constants.APPLY_DATE) && StringUtil.isNotBlank(json.getString(Constants.APPLY_DATE))) {
				String applyDateStr = json.getString(Constants.APPLY_DATE);
				Date applyDate = DateUtil.convert(applyDateStr, DateUtil.format1);
				contract.setApplyDate(new Timestamp(applyDate.getTime()));
			}
			// 实体信息，客户/供应商
			contract.setEntityType(flowEnt.getEntityType());
			int entityType = flowEnt.getEntityType();
			if (EntityType.CUSTOMER.ordinal() == entityType) {
				Customer customer = customerService.read(flowEnt.getSupplierId());
				contract.setEntityId(customer.getCustomerId());
				contract.setEntityName(customer.getCompanyName());
				contract.setEntityRegion(customer.getCustomerRegion());
				contract.setContactName(customer.getContactName());
				contract.setContactPhone(customer.getContactPhone());
				contract.setAddress(customer.getPostalAddress());

				CustomerProduct customerProduct = customerProductService.read(flowEnt.getProductId());
				contract.setProductType(customerProduct.getProductType());
			} else if (EntityType.SUPPLIER.ordinal() == entityType) {
				Supplier supplier = supplierService.read(flowEnt.getSupplierId());
				contract.setEntityId(supplier.getSupplierId());
				contract.setEntityName(supplier.getCompanyName());
				contract.setContactName(supplier.getContactName());
				contract.setContactPhone(supplier.getContactPhone());
				contract.setAddress(supplier.getPostalAddress());

				Product product = productService.read(flowEnt.getProductId());
				contract.setProductType(product.getProductType());
			}
			// 付费方式
			if (json.containsKey(Constants.PAY_TYPE) && StringUtil.isNotBlank(json.getString(Constants.PAY_TYPE))) {
				String payTypeStr = json.getString(Constants.PAY_TYPE);
				Optional<PayType> payTypeOpt = PayType.getEnumsByMsg(payTypeStr);
				if (payTypeOpt.isPresent()) {
					contract.setSettleType(payTypeOpt.get().ordinal());
				}
			}
			// 合同归属
			if (json.containsKey(Constants.CONTRACT_REGION) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_REGION))) {
				contract.setContractRegion(json.getString(Constants.CONTRACT_REGION));
			}
			// 合同类型
			if (json.containsKey(Constants.CONTRACT_TYPE) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_TYPE))) {
				contract.setContractType(json.getString(Constants.CONTRACT_TYPE));
			}
			// 月发送量，可能会写 “xxx万”
			if (json.containsKey(Constants.MONTH_COUNT) && StringUtil.isNotBlank(json.getString(Constants.MONTH_COUNT))) {
				contract.setMonthCount(json.getString(Constants.MONTH_COUNT));
			}
			// 合同金额
			if (json.containsKey(Constants.CONTRACT_AMOUNT) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_AMOUNT))) {
				String contractAmountStr = json.getString(Constants.CONTRACT_AMOUNT);
				contract.setContractAmount(contractAmountStr);
			}
			// 单价
			if (json.containsKey(Constants.UNIT_PRICE) && StringUtil.isNotBlank(json.getString(Constants.UNIT_PRICE))) {
				String unitPriceStr = json.getString(Constants.UNIT_PRICE);
				contract.setPrice(unitPriceStr);
			}
			// 开始有效期
			if (json.containsKey(Constants.VALIDITY_DATE_START) && StringUtil.isNotBlank(json.getString(Constants.VALIDITY_DATE_START))) {
				String dateStartStr = json.getString(Constants.VALIDITY_DATE_START);
				Date dateStart = DateUtil.convert(dateStartStr, DateUtil.format1);
				contract.setValidityDateStart(new Timestamp(dateStart.getTime()));
			} else {
				contract.setValidityDateStart(new Timestamp(DateUtil.getCurrentStartDateTime().getTime()));
			}
			// 结束有效期
			if (json.containsKey(Constants.VALIDITY_DATE_END) && StringUtil.isNotBlank(json.getString(Constants.VALIDITY_DATE_END))) {
				String dateEndStr = json.getString(Constants.VALIDITY_DATE_END);
				Date dateEnd = DateUtil.convert(dateEndStr, DateUtil.format1);
				dateEnd = DateUtil.getDateEndDateTime(dateEnd, true);
				contract.setValidityDateEnd(new Timestamp(dateEnd.getTime()));
			} else {
				contract.setValidityDateEnd(new Timestamp(DateUtil.getCurrentEndDateTime().getTime()));
			}
			// 项目情况说明
			if (json.containsKey(Constants.PROJECT_DESCRIPTION) && StringUtil.isNotBlank(json.getString(Constants.PROJECT_DESCRIPTION))) {
				contract.setDescription(json.getString(Constants.PROJECT_DESCRIPTION));
			}
			// 合同扫描件
			if (json.containsKey(Constants.CONTRACT_FILES_SCAN) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_FILES_SCAN))) {
				contract.setContractFilesScan(json.getString(Constants.CONTRACT_FILES_SCAN));
			} else if (json.containsKey(Constants.CONTRACT_FILE) && StringUtil.isNotBlank(json.getString(Constants.CONTRACT_FILE))) {
				// 没有扫描件，从合同附件取
				contract.setContractFilesScan(json.getString(Constants.CONTRACT_FILE));
			}
			boolean result;
			if (needSave) {
				result = contractService.save(contract);
				logger.info("生成合同表记录" + (result ? "成功" : "失败") + "，合同编号：" + contractNo);
			} else {
				result = contractService.update(contract);
				logger.info("更新合同表记录" + (result ? "成功" : "失败") + "，合同编号：" + contractNo);
			}
		} catch (Exception e) {
			logger.error("生成或更新合同表记录异常，flowEntId：" + flowEnt.getId(), e);
			return null;
		}
		return contract;
	}
}
