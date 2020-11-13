package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 打卡异常类型
 */
public enum CheckinExceptionType {

    WRONG_TIME("时间异常"),

    WRONG_LOCATION("地点异常"),

    NO_CHECK("未打卡"),

    WRONG_WIFI("wifi异常"),

    WRONG_DEVICE("非常用设备"),

    ;

    private String desc;

    private static String[] descs;

    CheckinExceptionType() {
    }

    CheckinExceptionType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public static String[] getDescs() {
        CheckinExceptionType[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }

    public static Optional<CheckinExceptionType> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
    }

    public static Optional<CheckinExceptionType> getEnumsByDesc(String desc) {
        return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
    }

    public static String getCheckinDataType(int type) {
        String result = "";
        Optional<CheckinExceptionType> opt = CheckinExceptionType.getEnumsByCode(type);
        if (opt.isPresent()) {
            result = opt.get().getDesc();
        }
        return result;
    }
}
