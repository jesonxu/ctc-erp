package com.dahantc.erp.dto.productType;

import javax.validation.constraints.NotNull;

public class ProductTypeReqDto {

    @NotNull(message = "页大小不能为空")
    private String limit;

    @NotNull(message = "当前页不能为空")
    private String page;

    private String productTypeName;

    private String visible;

    private String date;

    private String endDate;

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

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
