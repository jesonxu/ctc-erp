package com.dahantc.erp.dto.invoice;

import com.dahantc.erp.dto.bill.ProductBillsDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dahantc.erp.enums.EntityType;
import com.dahantc.erp.enums.InvoiceStatus;

public class InvoiceDto implements Serializable {

	private static final long serialVersionUID = 3562838378958278922L;

	private String id;

	// 开票时间
	private String wtime;

	private String ossUserId;

	// 申请人姓名
	private String realName;

	// 申请时间
	private String applyTime;

	// 主体id，供应商id/客户id
	private String entityId;

	// 主体类型，供应商/客户
	private int entityType;

	// 公司名称
	private String entityName;

	private String productId;

	// 开票金额，即此发票的应收金额
	private String receivables;

	// 已收金额，即开票流程填的已收金额
	private String actualReceivables;

	// 我司开票信息
	private String bankInvoiceInfo;

	// 对方开票信息
	private String oppositeBankInvoiceInfo;

	// 发票状态
	private int invoiceStatus;

	// 开票服务名称
	private String serviceName;

	// 发票类型
	private String invoiceType;

	// 备注
	private String remark;

	// 作废发票时涉及的账单
	private List<ProductBillsDto> billList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOssUserId() {
		return ossUserId;
	}

	public void setOssUserId(String ossUserId) {
		this.ossUserId = ossUserId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getWtime() {
		return wtime;
	}

	public void setWtime(String wtime) {
		this.wtime = wtime;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getReceivables() {
		return receivables;
	}

	public void setReceivables(String receivables) {
		this.receivables = receivables;
	}

	public String getActualReceivables() {
		return actualReceivables;
	}

	public void setActualReceivables(String actualReceivables) {
		this.actualReceivables = actualReceivables;
	}

	public String getBankInvoiceInfo() {
		return bankInvoiceInfo;
	}

	public void setBankInvoiceInfo(String bankInvoiceInfo) {
		this.bankInvoiceInfo = bankInvoiceInfo;
	}

	public String getOppositeBankInvoiceInfo() {
		return oppositeBankInvoiceInfo;
	}

	public void setOppositeBankInvoiceInfo(String oppositeBankInvoiceInfo) {
		this.oppositeBankInvoiceInfo = oppositeBankInvoiceInfo;
	}

	public int getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(int invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 用于构造导出数据
	 *
	 * @param length
	 *            字段数量
	 * @return 导出字段数组
	 */
	public String[] toExportData(int length) {
		List<String> dataList = new ArrayList<>();
		dataList.add(wtime);
		dataList.add(realName);
		dataList.add(applyTime);
		dataList.add(entityName);
		dataList.add(EntityType.getEntityType(entityType));
		dataList.add(receivables);
		dataList.add(actualReceivables);
		dataList.add(bankInvoiceInfo);
		dataList.add(oppositeBankInvoiceInfo);
		dataList.add(InvoiceStatus.getInvoiceStatusMsg(invoiceStatus));
		dataList.add(serviceName);
		dataList.add(invoiceType);
		dataList.add(remark);
		return dataList.toArray(new String[length]);
	}

	public List<ProductBillsDto> getBillList() {
		return billList;
	}

	public void setBillList(List<ProductBillsDto> billList) {
		this.billList = billList;
	}
}
