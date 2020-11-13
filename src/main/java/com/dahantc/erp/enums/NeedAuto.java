package com.dahantc.erp.enums;

public enum NeedAuto {

	TRUE("需要自动销账"),

	FALSE("不需要自动销账");

	private String desc;

	private static String[] descs;

	NeedAuto() {

	}

	NeedAuto(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		NeedAuto[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

}
