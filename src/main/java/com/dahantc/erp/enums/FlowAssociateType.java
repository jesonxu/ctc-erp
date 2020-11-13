package com.dahantc.erp.enums;

/**
 * 流程关联类型
 *
 * @author 8520
 */
public enum FlowAssociateType {

    /**
     * 客户
     */
    CUSTOMER(0, "客户"),
    /**
     * 供应商
     */
    SUPPLIER(1, "供应商"),
    /**
     * 个人
     */
    USER(2, "个人");

    private int code;

    private String desc;


    FlowAssociateType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
