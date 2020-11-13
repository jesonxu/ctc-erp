package com.dahantc.erp.enums;

public enum BusinessType {

	YTX("云通讯"), // 0

	IOT("物联网"), // 1

	G4("4G"); // 2

	private String desc;

	private static String[] descs;

	BusinessType() {
	}

	BusinessType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static String[] getDescs() {
		BusinessType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
