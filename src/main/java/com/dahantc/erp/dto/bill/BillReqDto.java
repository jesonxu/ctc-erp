package com.dahantc.erp.dto.bill;

import javax.validation.constraints.NotNull;

public class BillReqDto {

    @NotNull(message = "页大小不能为空")
    private String limit;

    @NotNull(message = "当前页不能为空")
    private String page;

    private String entityType;

    private String searchCompanyName;

    private String searchProductName;

    private String realName;

    private String billMonth;

    private String billStatus;

    private String settleType;

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSearchCompanyName() {
        return searchCompanyName;
    }

    public void setSearchCompanyName(String searchCompanyName) {
        this.searchCompanyName = searchCompanyName;
    }

    public String getSearchProductName() {
        return searchProductName;
    }

    public void setSearchProductName(String searchProductName) {
        this.searchProductName = searchProductName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getSettleType() {
        return settleType;
    }

    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }
}
