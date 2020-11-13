package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 主体类型(区分供应商还是客户)
 */
public enum EntityType {

	SUPPLIER(0, "供应商"),

	CUSTOMER(1, "客户"),

	SUPPLIER_DS(2, "电商供应商");

	private int code;

	private String msg;

	EntityType(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static Optional<EntityType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<EntityType> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

	public static String getEntityType(int code) {
		String result = "未知";
		Optional<EntityType> entityType = getEnumsByCode(code);
		if (entityType.isPresent()) {
			result = entityType.get().getMsg();
		}
		return result;
	}
}
