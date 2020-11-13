package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum LeaveType {

	PERSONAL_LEAVE(0,"事假"), // 0

	SICK_LEAVE(1,"病假"), // 1

	HOME_LEAVE(2,"探亲假"), // 2

	ANNUAL_LEAVE(3,"年假"), // 3

	WEDDING_LEAVE(4,"婚假"), // 4

	MATERNITY_OR_PATERNITY_LEAVE(5,"产假及看护假"), // 5

	LACTATION_LEAVE(6,"哺乳假"), // 6

	BEREAVEMENT_LEAVE(7,"丧假"), // 7

	PAID_SICK_LEAVE(8,"带薪病假"), // 8

	COMPENSATORY_LEAVE(9,"调休"), // 9

	;

	private int code;

	private String desc;

	private static String[] descs;

	LeaveType() {

	}

	LeaveType(int code, String desc) {
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
		LeaveType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<LeaveType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.getCode() == code).findFirst();
	}

	public static Optional<LeaveType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getLeaveType(int code) {
		String result = "";
		Optional<LeaveType> type = LeaveType.getEnumsByCode(code);
		if (type.isPresent()) {
			result = type.get().getDesc();
		}
		return result;
	}

}
