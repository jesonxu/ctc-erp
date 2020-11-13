package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 用户身份
 *
 */
public enum IdentityType {
	
	ORDINARY_MEMBER("普通成员"),

	LEADER_IN_DEPT("部门上级");

	private String desc;

	private static String[] descs;

	IdentityType() {

	}

	IdentityType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		IdentityType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<IdentityType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<IdentityType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getIdentityType(int code) {
		String result = "未知";
		Optional<IdentityType> opt = getEnumsByCode(code);
		if (opt.isPresent()) {
			result = opt.get().getDesc();
		}
		return result;
	}
}
