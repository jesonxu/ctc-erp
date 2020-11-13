package com.dahantc.erp.enums;

public enum DataPermission {

    Self("自己"), // 0

    Dept("部门"), // 1

    All("全部"), // 2

    Flow("流程"), // 3

    Customize("自定义"); // 4

    private String desc;

    private static String[] descs;

    DataPermission() {

    }

    DataPermission(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public static String[] getDescs() {
        DataPermission[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }
}
