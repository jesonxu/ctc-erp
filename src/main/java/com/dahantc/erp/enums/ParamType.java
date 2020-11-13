package com.dahantc.erp.enums;

/**
 * 系统参数类型<br>
 * 用于标识参数类型
 * 
 * @author 8541
 */
public enum ParamType {

	/**
	 * 系统环境变量
	 **/
	SYSTEM_PARAMETER("系统环境变量"), // 0

	/**
	 * 产品协议
	 */
	PRODUCT_PROTOCOL("产品协议") , // 1

	/**
	 * 提成比例
	 */
	COMMISSION_RATION("提成比例"), // 2
	

	/**
	 * 流程阈值
	 */
	FLOW_THRESHOLD("流程阈值"), // 3

	
	/**
	 * 提成比例
	 */
	OLD_CUSTOMER_COMMISSION_RATION("老客户提成比例"), // 4
	
	/**
	 * 客户变更规则
	 */
	CUSTOMER_CHANGE_RULE("客户变更规则"), // 5
	;

	private String desc;

	private static String[] descs;

	ParamType() {

	}

	ParamType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs() {
		ParamType[] values = values();
		descs = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
