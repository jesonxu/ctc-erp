package com.dahantc.erp.enums;

/**
 * 报告类型（日报、周报、季报、半年报、年报）
 */
public enum ReportType {

	DAYLY("日报"),

	WEEKLY("周报"),

	MONTHLY_REPORT("月报"),

	QUARTERLY_REPORT("季报"),

	SEMIANNUAL_REPORT("半年报"),

	ANNUAL_REPORT("年报");

	private String desc;

	private static String[] descs;

	ReportType() {

	}

	ReportType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		ReportType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

}
