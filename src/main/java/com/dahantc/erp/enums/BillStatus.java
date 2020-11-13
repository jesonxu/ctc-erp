package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 关联(销账)状态
 */
public enum BillStatus {
	
	NO_RECONCILE("未对账"),

	RECONILING("对账中"),
	
	RECONILED("已对账（未销账）"), // 已对账,未销账

	WRITING_OFF("销账中"),
	
	WRITED_OFF("销账完成");
	
	private String desc;

	private static String[] descs;

	BillStatus() {

	}

	BillStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		BillStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<BillStatus> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static String getBillStatus(int code) {
		String result = "";
		Optional<BillStatus> billStatus = BillStatus.getEnumsByCode(code);
		if (billStatus.isPresent()) {
			result = billStatus.get().getDesc();
		}
		return result;
	}
}
