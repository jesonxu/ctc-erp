package com.dahantc.erp.dto.finance;

import com.dahantc.erp.vo.cashflow.entity.CashFlow;

import java.io.Serializable;

public class CashFlowRspDto implements Serializable {

    private static final long serialVersionUID = 3717053546065651234L;

    public CashFlowRspDto() {
        this.receivables = "0.00";
        this.actualReceivables = "0.00";
        this.payables = "0.00";
        this.actualPayables = "0.00";
    }

    public CashFlowRspDto(CashFlow cashFlow) {
        this.receivables = cashFlow.getReceivables().setScale(2).toString();
        this.actualReceivables = cashFlow.getActualReceivables().setScale(2).toString();
        this.payables = cashFlow.getPayables().setScale(2).toString();
        this.actualPayables = cashFlow.getActualPayables().setScale(2).toString();
    }

    /**
     * 年份
     */
    private String year;

    /**
     * 月份
     */
    private String month;

    /**
     * 应收金额
     */
    private String receivables;

    /**
     * 实收金额
     */
    private String actualReceivables;

    /**
     * 应付金额
     */
    private String payables;

    /**
     * 实付金额
     */
    private String actualPayables;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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
}
