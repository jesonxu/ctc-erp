package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum SendType {
    GROUP(2, "集采"),

    SINGLE(1, "一件代发");

	private int code;
	
    private String desc;

    private static String[] descs;

    SendType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
    
    SendType() {
    }

    SendType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
    
    public int getCode() {
		return this.code;
	}

    public static String[] getDescs() {
        SendType[] values = values();
        descs = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            descs[i] = values[i].getDesc();
        }
        values = null;
        return descs;
    }

	public static Optional<SendType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<SendType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}
}
