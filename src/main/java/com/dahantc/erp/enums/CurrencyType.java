package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum CurrencyType {

	CNR(0, "人民币"),

	USD(1, "美元"),

	EUR(2, "欧元"),

	HKD(3, "港币"),

	GBP(4, "英镑"),

	JPY(5, "日元"),

	KRW(6, "韩元"),

	CAD(7, "加元"),

	AUD(8, "澳元"),

	CHF(9, "瑞郎"),

	SGD(10, "新加坡元"),

	MYR(11, "马来西亚币"),

	IDR(12, "印尼卢比"),

	NZD(13, "新西兰元"),

	VND(13, "越南盾"),

	THB(14, "泰铢"),

	PHP(15, "其他");

	CurrencyType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	private int code;

	private String name;

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public static Optional<CurrencyType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<CurrencyType> getEnumsByName(String name) {
		return Arrays.stream(values()).filter(p -> p.name.equals(name)).findFirst();
	}

}
