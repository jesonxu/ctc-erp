package com.dahantc.erp.enums;

public enum UserStatus {
	DISABLED(0x0, "已禁用"),

	ACTIVE(0x1, "激活");

	private int value;

	private String desc;

	private static String[] descs;

	UserStatus() {
	}

	UserStatus(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public int getValue() {
		return value;
	}

	public static String[] getDescs() {
		UserStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static String getDesc(int value) {

		if (value == DISABLED.value) {
			return DISABLED.desc;
		} else if (value == ACTIVE.value) {
			return ACTIVE.desc;
		} else {
			return "未知";
		}
	}
}
