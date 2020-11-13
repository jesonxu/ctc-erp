package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

// 进行状态（请假、加班、外勤、出差）
public enum TimeState {

	READY("未开始"), // 0

	DURING("进行中"), // 1

	PASSED("已度过"), // 2

	;

	private String desc;

	private static String[] descs;

	TimeState() {

	}

	TimeState(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		TimeState[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<TimeState> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<TimeState> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getTimeState(int code) {
		String result = "";
		Optional<TimeState> type = TimeState.getEnumsByCode(code);
		if (type.isPresent()) {
			result = type.get().getDesc();
		}
		return result;
	}

}
