package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 打卡数据类型
 */
public enum CheckinDataType {

    Commuting(1, "上下班打卡"),

    Outside(2, "外出打卡"),

    All(3, "全部打卡");

    private int code;

    private String desc;

    private static String[] descs;

    CheckinDataType() {
    }

    CheckinDataType(int code, String desc) {
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
        CheckinDataType[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }

    public static Optional<CheckinDataType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
    }

    public static Optional<CheckinDataType> getEnumsByDesc(String desc) {
        return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
    }

    public static String getCheckinDataType(int type) {
        String result = "";
        Optional<CheckinDataType> opt = CheckinDataType.getEnumsByCode(type);
        if (opt.isPresent()) {
            result = opt.get().getDesc();
        }
        return result;
    }
}
