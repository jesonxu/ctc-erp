package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum MaritalStatus {

	SINGLE(0,"未婚"), // 0

	MARRIED(1,"已婚"), // 1

	DIVORCED(2,"离婚"), // 2

	WIDOWED(3,"丧偶"), // 3

	;

	private int code;

	private String desc;

	private static String[] descs;

	MaritalStatus() {

	}

	MaritalStatus(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		MaritalStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<MaritalStatus> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.getCode() == code).findFirst();
	}

	public static Optional<MaritalStatus> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getMaritalStatus(int code) {
		String result = "";
		Optional<MaritalStatus> type = MaritalStatus.getEnumsByCode(code);
		if (type.isPresent()) {
			result = type.get().getDesc();
		}
		return result;
	}

}
