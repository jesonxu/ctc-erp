package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 开票状态
 */
public enum InvoiceStatus {
	INVOICED(0, "已开票"),

	INVALID(1, "已作废"),

	;

	private int code;

	private String msg;

	InvoiceStatus(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static String getInvoiceStatusMsg(int code) {
		String result = "未知";
		Optional<InvoiceStatus> invoiceStatusOptional = getEnumsByCode(code);
		if (invoiceStatusOptional.isPresent()) {
			result = invoiceStatusOptional.get().getMsg();
		}
		return result;
	}

	public int getCode() {
		return code;
	}

	public static Optional<InvoiceStatus> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(e -> e.code == code).findFirst();
	}

	public static Optional<InvoiceStatus> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(e -> e.msg.equals(msg)).findFirst();
	}

	public String getMsg() {
		return msg;
	}
}
