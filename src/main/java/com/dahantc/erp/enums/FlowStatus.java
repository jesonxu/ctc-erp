package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FlowStatus {
	/**
	 * 未审核（创建）
	 */
	NOT_AUDIT("未审核"),

	/**
	 * 归档
	 */
	FILED("归档"),

	/**
	 * 待审核（通过/驳回）
	 */
	NO_PASS("待审核"),

	/**
	 * 取消
	 */
	CANCLE("取消");

	private String desc;

	private static String[] descs;

	FlowStatus() {

	}

	FlowStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		FlowStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<FlowStatus> getEnumsByOrdinal(int ordinal) {
		return Arrays.stream(values()).filter(flowStatus -> flowStatus.ordinal() == ordinal).findFirst();
	}
}
