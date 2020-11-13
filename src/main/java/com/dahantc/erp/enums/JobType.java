package com.dahantc.erp.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public enum JobType {
	Sales("销售"),

	IT("技术"),

	HR("人事"),

	Admin("行政"),

	OP("运营"),

	Resource("资源");

	private String desc;

	private static String[] descs;

	JobType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		JobType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}

	/**
	 * 获取枚举名和描述
	 * @return Sales -> 销售 ……
	 */
	public static HashMap<String, String> getNameMap() {
		HashMap<String, String> nameMap = new HashMap<>();
		JobType[] values = values();
		for (int i = 0; i < values.length; i++) {
			nameMap.put(values[i].name(), values[i].getDesc());
		}
		return nameMap;
	}

	public static Optional<JobType> getEnumsByCode(int code) {
		return Arrays.stream(values()).filter(p -> p.ordinal() == code).findFirst();
	}

	public static Optional<JobType> getEnumsByMsg(String desc) {
		return Arrays.stream(values()).filter(p -> p.desc.equals(desc)).findFirst();
	}

	public static Optional<JobType> getEnumsByName(String name) {
		return Arrays.stream(values()).filter(p -> p.name().equals(name)).findFirst();
	}

	public static String getJobType(String jobType) {
		String result = "未知";
		Optional<JobType> opt = getEnumsByName(jobType);
		if (opt.isPresent()) {
			result = opt.get().getDesc();
		}
		return result;
	}

	public static String getJobTypes(String jobTypes) {
		String result = "";
		if (StringUtils.isNotBlank(jobTypes)) {
			String[] arr = jobTypes.split(",");
			for (String str : arr) {
				result += getJobType(str);
			}
		}
		return result;
	}
}
