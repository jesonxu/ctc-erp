package com.dahantc.erp.enums;

public enum BillType {
    Auto("自动"),

    Manual("手动");

    private String desc;

    private static String[] descs;

    BillType() {
    }

    BillType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public static String[] getDescs() {
        BillType[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }
    }
