package com.dahantc.erp.enums;

public enum BillCycle {
    Month("月"),

    Quarter("季"),

    Halfyear("半年"),

    Year("年");

    private String desc;

    private static String[] descs;

    BillCycle() {
    }

    BillCycle(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public static String[] getDescs() {
        BillCycle[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }
}
