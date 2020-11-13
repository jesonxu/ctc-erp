package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 充值核销状态
 */
public enum CheckOutStatus {

	NO_CHECKED("未核销"),

	PARTIAL_CHECKED("部分核销"),

	CHECKED_OUT("已核销");

	private String desc;

	private static String[] descs;

	CheckOutStatus() {

	}

	CheckOutStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		CheckOutStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<CheckOutStatus> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static String getCheckOutStatus(int code) {
		String result = "未知";
		Optional<CheckOutStatus> status = CheckOutStatus.getEnumsByCode(code);
		if (status.isPresent()) {
			result = status.get().getDesc();
		}
		return result;
	}
}
