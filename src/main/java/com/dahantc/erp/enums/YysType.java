package com.dahantc.erp.enums;

import com.dahantc.erp.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * 通信云的遗留问题。语音产品类型和其他类型有差别 除了语音之外的其他产品类型，012分别是移动联通电信 语音产品类型，123是移动联通电信
 * 同步到erp统计明细表的统计数据的运营商类型，也是存在差异
 */
public enum YysType {

	/**
	 * 中国移动 （语音的中国移动是1）
	 */
	CMCC(0, "中国移动"),

	/**
	 * 中国联通（语音的中国联通是2）
	 */
	UNICOM(1, "中国联通"),

	/**
	 * 中国电信（语音的中国电信是3）
	 */
	TELECOM(2, "中国电信"),

	/**
	 * 国际短信
	 */
	INTER(99, "国际短信"),

	ALL(1000, "全部"),

	;

	private int value;

	private String desc;

	private static String[] descs;

	YysType() {

	}

	private YysType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public String getDesc() {
		return this.desc;
	}

	public static Optional<YysType> getOptionalByValue(int value) {
		return Arrays.stream(values()).filter(yys -> value == yys.getValue()).findFirst();
	}

	/**
	 * 获取一般的运营商类型
	 *
	 * @return 运营商类型
	 */
	public static ArrayList<YysType> getNormalYysType() {
		YysType[] pre = values();
		ArrayList<YysType> result = new ArrayList<>();
		for (YysType yysType : pre) {
			if (yysType != YysType.ALL && yysType != YysType.INTER) {
				result.add(yysType);
			}
		}
		return result;
	}

	/**
	 * 获取运营商类型名称
	 *
	 * @param productType
	 *            产品类型
	 * @param value
	 *            运营商类型值
	 * @return 运营商类型名称
	 */
	public static String getNormalYysTypeName(int productType, String value) {
		if (null == value || value.length() == 0) {
			return "无";
		}
		// 语音产品的运营商跟其他产品有错位，要先转换
/*		if (productType == 6) {
			value = toNormalYysType(value);
		}*/
		String[] yysTypes = value.split(",");
		for (int i = 0; i < yysTypes.length; i++) {
			Optional<YysType> yysTypeOpt = getOptionalByValue(Integer.parseInt(yysTypes[i]));
			if (yysTypeOpt.isPresent()) {
				yysTypes[i] = yysTypeOpt.get().getDesc();
			} else {
				yysTypes[i] = "未知";
			}
		}

		return String.join(",", yysTypes);
	}

	/**
	 * 从页面展示的运营商类型转为真实的运营商类型
	 *
	 * @param yysTypeStr
	 *            逗号分隔字符串
	 * @return 语音0, 1, 2 -> 1,2,3
	 */
	public static String toVoiceYysType(String yysTypeStr) {
		// 全部
		if ((YysType.ALL.getValue() + "").equals(yysTypeStr)) {
			return yysTypeStr;
		}
		String[] yysTypes = yysTypeStr.split(",");
		// 其他，由于一般的类型值是012，语音的类型值是123，需要加1才是语音的类型值
		for (int i = 0; i < yysTypes.length; i++) {
			int yysType = Integer.parseInt(yysTypes[i]);
			if (yysType != YysType.INTER.getValue()) {
				// 0 -> 1, 1 -> 2, 2 -> 3
				yysTypes[i] = yysType + 1 + "";
			}
		}
		return String.join(",", yysTypes);
	}

	/**
	 * 从真实的运营商类型转为展示用的运营商类型
	 *
	 * @param yysTypeStr
	 *            逗号分隔字符串
	 * @return 语音1, 2, 3 -> 0,1,2
	 */
	public static String toNormalYysType(String yysTypeStr) {
		// 全部
		if ((YysType.ALL.getValue() + "").equals(yysTypeStr)) {
			return yysTypeStr;
		}
		String[] yysTypes = yysTypeStr.split(",");
		// 其他，由于一般的类型值是012，语音的类型值是123，语音需要减1才是一般的类型值
		for (int i = 0; i < yysTypes.length; i++) {
			int yysType = Integer.parseInt(yysTypes[i]);
			if (yysType != YysType.INTER.getValue()) {
				// 3 -> 2, 2 -> 1, 1 -> 0
				yysTypes[i] = yysType - 1 + "";
			}
		}
		return String.join(",", yysTypes);
	}
}