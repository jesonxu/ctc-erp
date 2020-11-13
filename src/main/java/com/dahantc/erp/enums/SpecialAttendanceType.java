package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 特殊出勤类型
 */
public enum SpecialAttendanceType {

	Leave("请假"), // 0

	Overtime("加班"), // 1

	Outside("外勤"), // 2

	BusinessTravel("出差"), // 3

	;

	private String desc;

	private static String[] descs;

	SpecialAttendanceType() {

	}

	SpecialAttendanceType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		SpecialAttendanceType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<SpecialAttendanceType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<SpecialAttendanceType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getTypeDesc(int code) {
		String result = "";
		Optional<SpecialAttendanceType> type = SpecialAttendanceType.getEnumsByCode(code);
		if (type.isPresent()) {
			result = type.get().getDesc();
		}
		return result;
	}

}
