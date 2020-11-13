package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ReceivablesType {

	Remuneration(0, "酬金");

	ReceivablesType(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	private int code;
	private String msg;

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static Optional<ReceivablesType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<ReceivablesType> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

}
