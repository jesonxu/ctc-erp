package com.dahantc.erp.enums;

/**
 * 绑定类型
 *
 */
public enum BindType {
	
	PRODUCT("产品"),

	ENTITY("客户/供应商"),
	
	WITH_OUT("无");

	private String desc;

	private static String[] descs;

	BindType() {

	}

	BindType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		BindType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
