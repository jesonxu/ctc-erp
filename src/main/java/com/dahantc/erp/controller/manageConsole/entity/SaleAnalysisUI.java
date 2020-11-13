package com.dahantc.erp.controller.manageConsole.entity;

import java.math.BigDecimal;

public class SaleAnalysisUI {
    private static final long serialVersionUID = 2437664217543695717L;
    //部门名称
    private String deptName;
    //员工名称
    private String saleUserName;
    //客户名称
    private String customerName;
    //到款金额
    private BigDecimal receivables = new BigDecimal(0);
    //发送成功条数
    private long successCount = 0;
    //销售单价
    private BigDecimal saleUnitPrice = new BigDecimal(0);
    // 消费金额
    private BigDecimal expenses = new BigDecimal(0);
    // 成本单价
    private BigDecimal costUnitPrice = new BigDecimal(0);
    // 成本金额
    private BigDecimal costSum = new BigDecimal(0);
    // 毛利润
    private BigDecimal grossProfit = new BigDecimal(0);
    // 权益提成
    private BigDecimal royalty = new BigDecimal(0);
    // 结算方式
    private String settleType;
    // 账期
    private String accounTperiod;
    // 累计欠款
    private BigDecimal arrears = new BigDecimal(0);
    // 客户余额
    private BigDecimal currentbalance = new BigDecimal(0);
    // 销售费用
    private BigDecimal sellingExpenses = new BigDecimal(0);
    // 修正毛利润
    private BigDecimal correctGrossprofit = new BigDecimal(0);

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSaleUserName() {
        return saleUserName;
    }

    public void setSaleUserName(String saleUserName) {
        this.saleUserName = saleUserName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getReceivables() {
        return receivables;
    }

    public void setReceivables(BigDecimal receivables) {
        this.receivables = receivables;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public BigDecimal getSaleUnitPrice() {
        return saleUnitPrice;
    }

    public void setSaleUnitPrice(BigDecimal saleUnitPrice) {
        this.saleUnitPrice = saleUnitPrice;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getCostUnitPrice() {
        return costUnitPrice;
    }

    public void setCostUnitPrice(BigDecimal costUnitPrice) {
        this.costUnitPrice = costUnitPrice;
    }

    public BigDecimal getCostSum() {
        return costSum;
    }

    public void setCostSum(BigDecimal costSum) {
        this.costSum = costSum;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getRoyalty() {
        return royalty;
    }

    public void setRoyalty(BigDecimal royalty) {
        this.royalty = royalty;
    }

    public String getAccounTperiod() {
        return accounTperiod;
    }

    public void setAccounTperiod(String accounTperiod) {
        this.accounTperiod = accounTperiod;
    }

    public BigDecimal getArrears() {
        return arrears;
    }

    public void setArrears(BigDecimal arrears) {
        this.arrears = arrears;
    }

    public BigDecimal getCurrentbalance() {
        return currentbalance;
    }

    public void setCurrentbalance(BigDecimal currentbalance) {
        this.currentbalance = currentbalance;
    }

    public BigDecimal getSellingExpenses() {
        return sellingExpenses;
    }

    public void setSellingExpenses(BigDecimal sellingExpenses) {
        this.sellingExpenses = sellingExpenses;
    }

    public BigDecimal getCorrectGrossprofit() {
        return correctGrossprofit;
    }

    public void setCorrectGrossprofit(BigDecimal correctGrossprofit) {
        this.correctGrossprofit = correctGrossprofit;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }
}
