package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ChargeType {

	PREPURCHASE(0, "预购充值"),

    ADVANCE(1, "预付充值"),

    DEPOSIT(2, "押金");
   
    ChargeType(
    int code, String
    msg)

    {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static Optional<ChargeType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
    }

    public static Optional<ChargeType> getEnumsByMsg(String msg) {
        return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
    }

    }
