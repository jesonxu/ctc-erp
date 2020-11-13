package com.dahantc.erp.dto.bill;

import com.dahantc.erp.enums.BillStatus;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.productBills.entity.ProductBills;

public class BillRespDto {

    private static final long serialVersionUID = -5681419809308423208L;

    private String billTitle;
    private String billId;
    private String billNumber;
    private int entityType;
    private String entityTypeName;
    private String entityId;
    private String companyName;
    private String productId;
    private String productName;
    private int productType;
    private String productTypeName;
    private String billPeriod;
    private String settleType;
    private String billMonth;
    private String ossUserId;
    // 销售/商务姓名
    private String realName;
    // 账单状态描述
    private String billStatus;
    // 平台成功数
    private String platformSuccessCount;
    // 实际成功数
    private String checkedSuccessCount;
    // 应收（给客户的账单金额）
    private String receivables;
    // 实收
    private String actualReceivables;
    // 应付（给供应商的账单金额）
    private String payables;
    // 实收
    private String actualPayables;
    // 已开票金额
    private String actualInvoiceAmount;
    // 综合成本
    private String cost;
    // 平均销售单价
    private String unitPrice;
    // 账单毛利
    private String grossProfit;
    // 销账时间
    private String writeOffTime;
    // 销账关联的到款
    private String relatedInfo;
    // 账号
    private String loginName;
    // 备注
    private String remark;
    // 修改记录
    private String operationLog;

    public BillRespDto() {}

    public BillRespDto(ProductBills bill) {
        this.billId = bill.getId();
        this.billNumber = bill.getBillNumber();
        this.entityType = bill.getEntityType();
        this.entityId = bill.getEntityId();
        this.productId = bill.getProductId();
        this.billMonth = DateUtil.convert(bill.getWtime(), DateUtil.format4);
        this.billStatus = BillStatus.getBillStatus(bill.getBillStatus());
        this.platformSuccessCount = bill.getPlatformCount() + "";
        this.checkedSuccessCount = bill.getSupplierCount() + "";
        this.receivables = bill.getReceivables().toPlainString();
        this.actualReceivables = bill.getActualReceivables().toPlainString();
        this.payables = bill.getPayables().toPlainString();
        this.actualPayables = bill.getActualPayables().toPlainString();
        this.actualInvoiceAmount = bill.getActualInvoiceAmount().toPlainString();
        this.cost = bill.getCost().toPlainString();
        this.unitPrice = bill.getUnitPrice().toPlainString();
        this.grossProfit = bill.getGrossProfit().toPlainString();
        // 销账时间
        if (null != bill.getWriteOffTime()) {
            this.writeOffTime = DateUtil.convert(bill.getWriteOffTime(), DateUtil.format2);
        }
        // 关联的到款
        this.relatedInfo = bill.getRelatedInfo();
        this.loginName = bill.getLoginName();
        this.remark = bill.getRemark();
        this.operationLog = bill.getOperationLog();
    }

    public String getBillTitle() {
        return billTitle;
    }

    public void setBillTitle(String billTitle) {
        this.billTitle = billTitle;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public String getEntityTypeName() {
        return entityTypeName;
    }

    public void setEntityTypeName(String entityTypeName) {
        this.entityTypeName = entityTypeName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
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

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getPlatformSuccessCount() {
        return platformSuccessCount;
    }

    public void setPlatformSuccessCount(String platformSuccessCount) {
        this.platformSuccessCount = platformSuccessCount;
    }

    public String getCheckedSuccessCount() {
        return checkedSuccessCount;
    }

    public void setCheckedSuccessCount(String checkedSuccessCount) {
        this.checkedSuccessCount = checkedSuccessCount;
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

    public String getPayables() {
        return payables;
    }

    public void setPayables(String payables) {
        this.payables = payables;
    }

    public String getActualPayables() {
        return actualPayables;
    }

    public void setActualPayables(String actualPayables) {
        this.actualPayables = actualPayables;
    }

    public String getActualInvoiceAmount() {
        return actualInvoiceAmount;
    }

    public void setActualInvoiceAmount(String actualInvoiceAmount) {
        this.actualInvoiceAmount = actualInvoiceAmount;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(String grossProfit) {
        this.grossProfit = grossProfit;
    }

    public String getWriteOffTime() {
        return writeOffTime;
    }

    public void setWriteOffTime(String writeOffTime) {
        this.writeOffTime = writeOffTime;
    }

    public String getRelatedInfo() {
        return relatedInfo;
    }

    public void setRelatedInfo(String relatedInfo) {
        this.relatedInfo = relatedInfo;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperationLog() {
        return operationLog;
    }

    public void setOperationLog(String operationLog) {
        this.operationLog = operationLog;
    }

    public String getBillPeriod() {
        return billPeriod;
    }

    public void setBillPeriod(String billPeriod) {
        this.billPeriod = billPeriod;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }
}
