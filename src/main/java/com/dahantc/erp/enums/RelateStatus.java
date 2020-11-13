package com.dahantc.erp.enums;

public enum RelateStatus {

	UNRELATE("未关联"),

	RELATED("已关联");

	private String desc;

	private static String[] descs;

	RelateStatus() {

	}

	RelateStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		RelateStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
