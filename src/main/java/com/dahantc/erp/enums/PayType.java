package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PayType {

    /**
     * 预付
     */
    Advance("预付费"),

    /**
     * 后付
     */
    After("后付费");

    private String msg;

    PayType(String msg) {
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }

    public static Optional<PayType> getEnumsByMsg(String msg) {
        return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
    }

    public static Optional<PayType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
    }

    public static String getPayType(int type) {
        String result = "";
        Optional<PayType> payType = PayType.getEnumsByCode(type);
        if (payType.isPresent()) {
            result = payType.get().getMsg();
        }
        return result;
    }
}
