package com.dahantc.erp.enums;

public enum RoleType {

	/**
	 * 管理员
	 */
	ADMIN("超级管理员"),

	/**
	 * 终端客户
	 */
	DEPAETADMIN("机构管理员"),

	/**
	 * 员工
	 */
	CUSTOMER("员工");

	private String desc;

	private static String[] descs;

	RoleType() {

	}

	RoleType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		RoleType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
