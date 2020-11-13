package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 主体类型(区分供应商还是客户)
 */
public enum DsOrderStatus {

	REVIEWING(0, "审核中"),

	TO_BE_DELIVERED(1, "待发货"),
	
	TO_BE_RECEIVED(2, "待收货"),
	
	FINISH(3, "已完成"),

	CANCELLED(4, "已取消"),
	
	REFUND(5, "已退款");

	private int code;

	private String msg;

	DsOrderStatus(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static Optional<DsOrderStatus> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.code == code).findFirst();
	}

	public static Optional<DsOrderStatus> getEnumsByMsg(String msg) {
		return Arrays.stream(values()).filter(p -> p.msg.equals(msg)).findFirst();
	}

	public static String getDesc(int code) {
		Optional<DsOrderStatus> entityType = getEnumsByCode(code);
		if (entityType.isPresent()) {
			return entityType.get().getMsg();
		}
		return "";
	}
}
