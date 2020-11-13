package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum AuditResult {

	CREATED(1, "创建"),

	PASS(2, "通过"),

	REJECTED(3, "驳回"),

	CANCLE(4, "取消"),

	SAVE(5, "保存"),

	REVOKE(6, "撤销");

	AuditResult(int code, String msg) {
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

	public static Optional<AuditResult> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<AuditResult> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

}
