package com.dahantc.erp.enums;

public enum GoalType {

	DeptMonth("部门月目标"), // 0部门月目标

	SelfMonth("个人月目标"); // 1销售个人月目标

	private String desc;

	private static String[] descs;

	GoalType() {

	}

	GoalType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		GoalType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
