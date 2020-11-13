package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum CostPriceType {
	AUTO("平台同步"), // 0

	MANUAL("手动配置"); // 8

	private String desc;

	private static String[] descs;

	CostPriceType() {

	}

	CostPriceType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		CostPriceType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	public static Optional<CostPriceType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<CostPriceType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static String getCostPriceType(int type) {
		String result = "";
		Optional<CostPriceType> productType = CostPriceType.getEnumsByCode(type);
		if (productType.isPresent()) {
			result = productType.get().getDesc();
		}
		return result;
	}
	
	public Map<Integer, String> getCostPriceTypeMap() {
		CostPriceType[] values = values();
		Map<Integer, String> typeMap = new HashMap<>();
		for (CostPriceType type : values) {
			typeMap.put(type.ordinal(), type.getDesc());
		}
		return typeMap;
	}
}
