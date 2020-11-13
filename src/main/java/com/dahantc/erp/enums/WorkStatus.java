package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 工作状态
 *
 * @author : 8523
 * @date : 2020/10/21 9:55
 */
public enum WorkStatus {

    Unknown("待确认"),         // 确认之后手动设置为其他值

    Normal("正常出勤"),         // 正常上下班打卡

    Special("特殊出勤"),        // 存在请假、加班、外勤、出差的情况

    Exceptional("异常出勤"),    // 存在迟到、未打卡、矿工的情况

    ;

    private String desc;

    private static String[] descs;

    WorkStatus() {
    }

    WorkStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public static String[] getDescs() {
        WorkStatus[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }

    public static Optional<WorkStatus> getEnumsByCode(int code) {
        return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
    }

    public static Optional<WorkStatus> getEnumsByDesc(String desc) {
        return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
    }

    public static String getWorkStatus(int type) {
        String result = "";
        Optional<WorkStatus> opt = WorkStatus.getEnumsByCode(type);
        if (opt.isPresent()) {
            result = opt.get().getDesc();
        }
        return result;
    }
}
