/**
 * com.ctc.common.Stat.java
 * 2011-4-28
 */
package com.dahantc.erp.enums;

/**
 * 短信发送结果
 * @author 8541
 */
public enum Stat {

	/** 成功 */
	Succeed("成功"),

	/** 长短信部分成功 */
	SucceedOfPart("长短信部分成功"),

	/** 等待发送 */
	ReadyTOSend("等待发送"),

	/** 发送失败 */
	ReportErr("发送失败"),

	/** 提交成功 */
	RespSucceed("未知"),

	/** 提交失败 */
	RespErr("提交失败");

	private String desc;

	private static String[] descs;

	Stat(){
		
	}
	
	Stat(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}

	public static String[] getDescs(){
		Stat[] values = values();
		descs = new String[values.length];
		for(int i = 0; i<values.length; i++){
			descs[i] = values[i].getDesc();
		}
		values = null;
		return descs;
	}
}
