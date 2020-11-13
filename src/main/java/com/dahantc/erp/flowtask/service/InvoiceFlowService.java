package com.dahantc.erp.flowtask.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.FlowLabelType;
import com.dahantc.erp.enums.FlowStatus;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.invoice.entity.Invoice;
import com.dahantc.erp.vo.invoice.service.IInvoiceService;
import com.dahantc.erp.vo.user.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("invoiceFlowService")
public class InvoiceFlowService extends BaseFlowTask {
	private static Logger logger = LogManager.getLogger(InvoiceFlowService.class);

	public static final String FLOW_CLASS = Constants.INVOICE_CLASS;
	public static final String FLOW_NAME = Constants.INVOICE_NAME;

	private static final String UPDATE_PRODUCTBILL_SQL = "update erp_bill set actualInvoiceAmount = actualInvoiceAmount+? where id =?";
	@Autowired
	private IInvoiceService invoiceService;
	@Autowired
	private IBaseDao baseDao;

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
		// TODO
		logger.info("不校验数据");
		return null;
	}

	@Override
	public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
		return null;
	}

	@Override
	public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
		List<Invoice> invoiceList = new ArrayList<>();
		try {
			flowEnt.setFlowStatus(FlowStatus.FILED.ordinal());

			String jsonmsg = flowEnt.getFlowMsg();
			JSONObject json = JSONObject.parseObject(jsonmsg);
			// 开票流程中选择的账单
			JSONArray billArray = json.getJSONArray(Constants.BILL_INFO_KEY);
			BigDecimal amountMoney = new BigDecimal(0);
			if (billArray != null && !billArray.isEmpty()) {
				// 选择账单开票
				for (int i = 0; i < billArray.size(); i++) {
					JSONObject bill = billArray.getJSONObject(i);
					// 账单id
					String productBillId = bill.getString("id");
					// 此账单本次开票金额
					BigDecimal thisReceivables = new BigDecimal(bill.getString(Constants.BILL_THIS_RECEIVABLES_KEY));
					// 更新此账单的已开金额
					int result = baseDao.executeSqlUpdte(UPDATE_PRODUCTBILL_SQL, new Object[] { thisReceivables, productBillId },
							new Type[] { StandardBasicTypes.BIG_DECIMAL, StandardBasicTypes.STRING });
					if (result == 0) {
						logger.error("id：" + productBillId + "的产品账单记录增加已开发票金额失败，增加金额：" + thisReceivables);
					}
//					amountMoney = amountMoney.add(thisReceivables);
				}
			} else {
				// 不选账单，先开票
				// TODO 后期要关联发票和账单
				if (json.containsKey(Constants.NO_BILL_INVOICE_AMOUNT_KEY)) {
					String noBillAmountStr = json.getString(Constants.NO_BILL_INVOICE_AMOUNT_KEY);
					if (StringUtil.isNotBlank(noBillAmountStr)) {
						BigDecimal noBillAmount = new BigDecimal(noBillAmountStr);
						amountMoney = amountMoney.add(noBillAmount);
					}
				}
			}
			String selfInvoice = json.getString(FlowLabelType.SelfInvoice.getDesc());
			String bankInvoiceId = getBasicsId(selfInvoice);

			String serviceName = json.getString(Constants.INVOICE_SERVICE_NAME_KEY);
			String invoiceType = json.getString(Constants.INVOICE_TYPE_KEY);
			String remark = json.getString(Constants.DAHAN_REMARK_KEY);
			//数据入发票表
			JSONArray custInvoiceArray = null;
			if (json.containsKey(Constants.CUSTOMER_INVOICE_INFO)) {
				custInvoiceArray = json.getJSONArray(Constants.CUSTOMER_INVOICE_INFO);
			} else {
				custInvoiceArray = json.getJSONArray(Constants.OTHER_INVOICE_INFO);
			}
			if (custInvoiceArray != null && !custInvoiceArray.isEmpty()) {
				for (int i = 0; i < custInvoiceArray.size(); i++) {
					JSONObject invoiceObj = custInvoiceArray.getJSONObject(i);
					if(invoiceObj != null) {
						Invoice invoice = new Invoice();
						invoice.setOssUserId(flowEnt.getOssUserId());
						invoice.setFlowEntId(flowEnt.getId());
						invoice.setApplyTime(flowEnt.getWtime());
						invoice.setProductId(flowEnt.getProductId());
						invoice.setEntityId(flowEnt.getSupplierId());
						invoice.setEntityType(flowEnt.getEntityType());
						// 此账单本次开票金额
						BigDecimal thisReceivables = new BigDecimal(invoiceObj.getString(Constants.BILL_THIS_RECEIVABLES_KEY));
						invoice.setReceivables(thisReceivables);
						//已收金额
						BigDecimal receivablesPrice = new BigDecimal(invoiceObj.getString(Constants.BILL_RECEIVABLES_KEY));
						invoice.setActualReceivables(receivablesPrice);

						//我司开票信息ID 关联InvoiceInformation
						invoice.setBankInvoiceId(bankInvoiceId);
						//对方开票信息ID 关联InvoiceInformation
						String custInvoiceInfo = invoiceObj.getString(Constants.CUST_INVOICE_INFO);
						invoice.setOppositeBankInvoiceId(getBasicsId(custInvoiceInfo));

						invoice.setServiceName(serviceName);
						invoice.setInvoiceType(invoiceType);
						invoice.setRemark(remark);
						invoiceList.add(invoice);
					}
				}
			}

			// 本发票的已收
			if(invoiceList != null && !invoiceList.isEmpty()) {
				invoiceService.saveByBatch(invoiceList);
			}
		} catch (Exception e) {
			logger.error("", e);
			return false;
		} finally {
			if(invoiceList != null && !invoiceList.isEmpty()) {
				invoiceList.clear();
				invoiceList = null;
			}
		}
		return true;
	}

	private String getBasicsId(String custInvoiceInfo) {
		String basicsId = "";
		if(StringUtil.isNotBlank(custInvoiceInfo)) {
			String[] custInvoiceInfos = custInvoiceInfo.split("####");
			if(custInvoiceInfos[0].startsWith("basicsId:")){
				basicsId = custInvoiceInfos[0].split(":")[1];
			}
		}
		return basicsId;
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
		logger.info("不处理信息变更操作");
	}

	@Override
	public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
		flowMsgModify(auditResult, flowEnt);
	}

}
