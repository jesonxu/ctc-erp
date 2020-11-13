package com.dahantc.erp.dto.search;

import java.io.Serializable;
import java.math.BigDecimal;

public class BillSearchRespDto implements Serializable {

    private static final long serialVersionUID = -4753576333465960559L;

    //客户/供应商名称
    private String entityName;
    //主体类型
    private String entityType;

    //产品名称
    private String productName;

    //供应商成功数
    private long supplierCount;

    //平台成功数
    private long platformCount;

    //应收金额
    private BigDecimal receivables;

    //实收金额
    private BigDecimal actualReceivables;

    //应付金额
    private BigDecimal payables;

    //实付金额
    private BigDecimal actualPayables;

    //已开发票金额
    private BigDecimal actualInvoiceAmount;

    //成本
    private BigDecimal cost;

    //平均成本单价
    private BigDecimal unitPrice;

    //毛利润
    private BigDecimal grossProfit;

    //支付截止日期
    private String finalPayTime;

    //收款截止日期
    private String finalReceiveTime;

    //账单编号
    private String billNumber;


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getSupplierCount() {
        return supplierCount;
    }

    public void setSupplierCount(long supplierCount) {
        this.supplierCount = supplierCount;
    }

    public long getPlatformCount() {
        return platformCount;
    }

    public void setPlatformCount(long platformCount) {
        this.platformCount = platformCount;
    }

    public BigDecimal getReceivables() {
        return receivables;
    }

    public void setReceivables(BigDecimal receivables) {
        this.receivables = receivables;
    }

    public BigDecimal getActualReceivables() {
        return actualReceivables;
    }

    public void setActualReceivables(BigDecimal actualReceivables) {
        this.actualReceivables = actualReceivables;
    }

    public BigDecimal getPayables() {
        return payables;
    }

    public void setPayables(BigDecimal payables) {
        this.payables = payables;
    }

    public BigDecimal getActualPayables() {
        return actualPayables;
    }

    public void setActualPayables(BigDecimal actualPayables) {
        this.actualPayables = actualPayables;
    }

    public BigDecimal getActualInvoiceAmount() {
        return actualInvoiceAmount;
    }

    public void setActualInvoiceAmount(BigDecimal actualInvoiceAmount) {
        this.actualInvoiceAmount = actualInvoiceAmount;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public String getFinalPayTime() {
        return finalPayTime;
    }

    public void setFinalPayTime(String finalPayTime) {
        this.finalPayTime = finalPayTime;
    }

    public String getFinalReceiveTime() {
        return finalReceiveTime;
    }

    public void setFinalReceiveTime(String finalReceiveTime) {
        this.finalReceiveTime = finalReceiveTime;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }
}
