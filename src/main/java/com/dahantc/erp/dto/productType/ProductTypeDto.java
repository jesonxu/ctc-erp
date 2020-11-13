package com.dahantc.erp.dto.productType;

import java.io.Serializable;

public class ProductTypeDto implements Serializable {

    private static final long serialVersionUID = 8191501541574515129L;

    private String id;

    private String productTypeName;

    private String productTypeKey;

    private int productTypeValue;

    private String costPriceType;

    private String costPrice;

    private String userName;

    private String wtime;

    private int visible;

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

    public int getProductTypeValue() {
        return productTypeValue;
    }

    public void setProductTypeValue(int productTypeValue) {
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
