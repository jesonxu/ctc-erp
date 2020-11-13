package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 打卡类型
 */
public enum CheckinType {

    Checkin(1, "上班打卡"),

    Checkout(2, "下班打卡"),

    Outside(3, "外出打卡");

    private int code;

    private String desc;

    private static String[] descs;

    CheckinType() {
    }

    CheckinType(int code, String desc) {
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
        return this.desc;
    }

    public static String[] getDescs() {
        CheckinType[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }

    public static Optional<CheckinType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
    }

    public static Optional<CheckinType> getEnumsByDesc(String desc) {
        return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
    }

    public static String getCheckinType(int type) {
        String result = "";
        Optional<CheckinType> opt = CheckinType.getEnumsByCode(type);
        if (opt.isPresent()) {
            result = opt.get().getDesc();
        }
        return result;
    }
}
