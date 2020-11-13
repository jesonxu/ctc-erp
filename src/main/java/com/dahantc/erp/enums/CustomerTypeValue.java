package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 客户类型对应的值
 */
public enum CustomerTypeValue {

	CONTRACTED(1, "合同客户"),

	TESTING(2, "测试客户"),

	INTENTION(3, "意向客户"),

	SILENCE(4, "沉默客户"),

	PUBLIC(5, "公共池客户");

	private int code;

	private String msg;

	CustomerTypeValue(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static Optional<CustomerTypeValue> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<CustomerTypeValue> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

	public static String getDesc(int code) {
		Optional<CustomerTypeValue> entityType = getEnumsByCode(code);
		if (entityType.isPresent()) {
			return entityType.get().getMsg();
		}
		return "";
	}
}
