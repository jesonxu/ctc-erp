package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ContractFlowStatus {

    APPLYING(1,"申请中"),

    FILED(2,"已归档"),

    CANCLE(3,"已取消");

    private int code;
    private String desc;
    ContractFlowStatus(int code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return desc;
    }

    public static Optional<ContractFlowStatus> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
    }

    public static Optional<ContractFlowStatus> getEnumsByMsg(String desc) {
        return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
    }
}
