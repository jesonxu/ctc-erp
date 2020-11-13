package com.dahantc.erp.enums;

/**
 * 实体的状态<br>
 * 用于标识实体数据的状态是删除还是正常
 * 
 * @author 8541
 */
public enum EntityStatus {

	/** 已删除，逻辑删除 */
	DELETED("已删除"),

	/** 正常 */
	NORMAL("正常");

	private String desc;

	private static String[] descs;

	EntityStatus() {

	}

	EntityStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		EntityStatus[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
