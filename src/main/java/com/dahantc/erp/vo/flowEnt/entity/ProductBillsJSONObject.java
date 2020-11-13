package com.dahantc.erp.vo.flowEnt.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class ProductBillsJSONObject implements Serializable {
    private static final long serialVersionUID = -6752096765234762182L;

    /**
     * 账单id
     */
    @NotBlank(message = "账单id不能为空")
    private String id;

    /**
     * 账单名称
     */
    private String title;

    /**
     * 本次开票金额
     * 最大值小于等于可开发票金额
     */
    private BigDecimal thisReceivables;

    /**
     * 可开发票金额
     * 应开发票金额-已开发票金额
     */
    private BigDecimal invoiceableAmount;

    /**
     * 应开发票金额
     */
    private BigDecimal receivables;

    /**
     * 已开发票金额
     */
    private BigDecimal actualInvoiceAmount;

    /**
     * 扣减金额
     */
    @NotNull(message = "扣减金额不能为空")
    private BigDecimal deductionAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getThisReceivables() {
        return thisReceivables;
    }

    public void setThisReceivables(BigDecimal thisReceivables) {
        this.thisReceivables = thisReceivables;
    }

    public BigDecimal getInvoiceableAmount() {
        return invoiceableAmount;
    }

    public void setInvoiceableAmount(BigDecimal invoiceableAmount) {
        this.invoiceableAmount = invoiceableAmount;
    }

    public BigDecimal getReceivables() {
        return receivables;
    }

    public void setReceivables(BigDecimal receivables) {
        this.receivables = receivables;
    }

    public BigDecimal getActualInvoiceAmount() {
        return actualInvoiceAmount;
    }

    public void setActualInvoiceAmount(BigDecimal actualInvoiceAmount) {
        this.actualInvoiceAmount = actualInvoiceAmount;
    }

    public BigDecimal getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(BigDecimal deductionAmount) {
        this.deductionAmount = deductionAmount;
    }
}
