package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum SearchType {
	FLOW(1, "流程"),

	CUSTOMER(2, "客户"),

	SUPPLIER(3, "供应商"),

	CONTRACT(4, "合同"),

	BILL(5, "账单");

	private int code;
	private String desc;

	SearchType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static Optional<SearchType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<SearchType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}
}
