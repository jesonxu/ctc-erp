package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum InvoiceType {

	/**
	 * 我司开票信息
	 */
	SelfInvoice(0, "我司开票信息"),

	/**
	 * 对方开票信息
	 */
	OtherInvoice(1, "对方开票信息"),

	/**
	 * 我司银行信息
	 */
	SelfBank(2, "我司银行信息"),

	/**
	 * 对方银行信息
	 */
	OtherBank(3, "对方银行信息");

	private int code;

	private String desc;

	InvoiceType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	InvoiceType() {

	}

	public static Optional<InvoiceType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(e -> e.code == code).findFirst();
	}

	public static Optional<InvoiceType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(e -> e.desc.equals(desc)).findFirst();
	}
}
