package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FlowType {
	// 运营
	OPERATE(0, "运营"),

	// 结算(弃用)
	FINANCE(1, "结算"),

	// 对账
	BILL(2, "对账"),

	// 发票
	INVOICE(3, "发票"),

	// 销账
	WRITE_OFF(4, "销账"),
	
	EMPLOYEE(5, "员工");

	private int code;

	private String desc;

	private static String[] descs;

	FlowType() {

	}

	FlowType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public int getCode() {
		return this.code;
	}

	public static String[] getDescs() {
		FlowType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<FlowType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<FlowType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}
}
