package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PlatformType {
	PC("桌面端"), // 0

	MOBILE("移动端"), // 1

	;

	private String desc;

	private static String[] descs;

	PlatformType() {

	}

	PlatformType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		PlatformType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<PlatformType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<PlatformType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getPlatformType(int type) {
		String result = "未知";
		Optional<PlatformType> platformType = PlatformType.getEnumsByCode(type);
		if (platformType.isPresent()) {
			result = platformType.get().getDesc();
		}
		return result;
	}
}
