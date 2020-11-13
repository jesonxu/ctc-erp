package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FeeType {
    
    SMS_INCOME(0, "短信收入"),

    FOURG_INCOME(1, "4G收入"),
    
    IOT_INCOME(2, "物联网收入");

    private int code;

    private String msg;

    FeeType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static Optional<FeeType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
    }

    public static Optional<FeeType> getEnumsByMsg(String msg) {
        return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
    }

}
