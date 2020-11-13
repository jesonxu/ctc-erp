package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ProductType {
	SMS("短信"), // 0

	@Deprecated
	VOICE_STRIP("语音(按条计费)"), // 1 

	MMS("彩信"), // 2

	SUPER_MMS("超级短信"), // 3

	INTER_SMS("国际短信"), // 4

	MOBILE_AUTH("一键登录"), // 5

	VOICE_TIME("语音(按时计费)"), // 6

	TECH_SERVICE("技术服务、软件"), // 7

	OTHERS("其他"); // 8

	private String desc;

	private static String[] descs;

	ProductType() {

	}

	ProductType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		ProductType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<ProductType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<ProductType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getProductType(int type) {
		String result = "";
		Optional<ProductType> productType = ProductType.getEnumsByCode(type);
		if (productType.isPresent()) {
			result = productType.get().getDesc();
		}
		return result;
	}
	
	public static ProductType[] getAllValues() {
		return new ProductType[] {SMS, MMS, SUPER_MMS, INTER_SMS, MOBILE_AUTH, VOICE_TIME, TECH_SERVICE, OTHERS};
	}
}
