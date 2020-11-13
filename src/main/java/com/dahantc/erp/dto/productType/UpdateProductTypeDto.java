package com.dahantc.erp.dto.productType;

import javax.validation.constraints.NotNull;

/**
 * 添加修改时提交的参数
 */
public class UpdateProductTypeDto {

    private String id;

    @NotNull(message = "产品类型名不能为空")
    private String productTypeName;

    @NotNull(message = "产品类型标识不能为空")
    private String productTypeKey;

    @NotNull(message = "产品类型值不能为空")
    private String productTypeValue;

    @NotNull(message = "成本类型不能为空")
    private String costPriceType;

    private String costPrice;

    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductTypeKey() {
        return productTypeKey;
    }

    public void setProductTypeKey(String productTypeKey) {
        this.productTypeKey = productTypeKey;
    }

    public String getProductTypeValue() {
        return productTypeValue;
    }

    public void setProductTypeValue(String productTypeValue) {
        this.productTypeValue = productTypeValue;
    }

    public String getCostPriceType() {
        return costPriceType;
    }

    public void setCostPriceType(String costPriceType) {
        this.costPriceType = costPriceType;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
