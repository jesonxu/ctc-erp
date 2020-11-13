package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 可见状态
 * 用于标识数据的状态是展示还是隐藏
 */
public enum VisibleStatus {

	HIDE("隐藏"),

	SHOW("展示");

	private String desc;

	private static String[] descs;

	VisibleStatus() {

	}

	VisibleStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		VisibleStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<VisibleStatus> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<VisibleStatus> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getVisibleStatus(int code) {
		String result = "";
		Optional<VisibleStatus> visibleStatus = VisibleStatus.getEnumsByCode(code);
		if (visibleStatus.isPresent()) {
			result = visibleStatus.get().getDesc();
		}
		return result;
	}
}
