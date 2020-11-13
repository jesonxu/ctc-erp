package com.dahantc.erp.enums;

public enum DefaultPrice {

	/**
	 * 非默认
	 */
	NON_DEFAULT("非默认"),
	/**
	 * 默认
	 */
	DEFAULT("默认");

	private String desc;

	private static String[] descs;

	DefaultPrice() {

	}

	DefaultPrice(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		DefaultPrice[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
