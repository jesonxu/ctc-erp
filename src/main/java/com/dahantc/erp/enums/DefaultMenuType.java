package com.dahantc.erp.enums;

public enum DefaultMenuType {

	/**
	 * 非默认
	 */
	NON_DEFAULT("非默认菜单"),
	/**
	 * 默认菜单
	 */
	DEFAULT("默认菜单");

	private String desc;

	private static String[] descs;

	DefaultMenuType() {

	}

	DefaultMenuType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		DefaultMenuType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
