package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 价格类型
 * 
 * @author wangyang
 *
 */
public enum PriceType {

	UNIFORM_PRICE(1, "统一价"),

	STAGE_PRICE(2, "阶段价"),

	STEPPED_PRICE(3, "阶梯价");

	PriceType(int code, String msg) {
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

	public static Optional<PriceType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<PriceType> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

}
