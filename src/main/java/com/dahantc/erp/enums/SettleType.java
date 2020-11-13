package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @version:
 * @Description: 结算方式
 * @author: 8513
 * @date: 2019年8月14日 下午1:57:20
 */
public enum SettleType {
	/**
	 * 
	 */
	Prepurchase("预购"),

	/**
	 * 预付
	 */
	Advance("预付"),

	/**
	 * 后付
	 */
	After("后付");

	private String desc;

	private static String[] descs;

	SettleType() {

	}

	SettleType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		SettleType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<SettleType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<SettleType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getSettleType(int settleType) {
		String result = "";
		if (settleType >= 0 && settleType <= SettleType.getDescs().length) {
			result = SettleType.getDescs()[settleType];
		}
		return result;
	}

}
