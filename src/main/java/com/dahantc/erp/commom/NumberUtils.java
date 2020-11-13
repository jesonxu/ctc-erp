package com.dahantc.erp.commom;


import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author 8519
 * 
 */
public class NumberUtils {
	/**
	 ** 提供精确的小数位四舍五入处理。 <br>
	 * 
	 * @param v
	 *            需要四舍五入的数字<br>
	 * @return 四舍五入后的结果
	 */
	public static BigDecimal round(BigDecimal v) {
		BigDecimal b = new BigDecimal(v.toString());
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, 3, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 转为百分数，小数点后保留两位
	 * 
	 * @param b
	 * @return
	 */
	public static String getPercentFormat(Double b) {
		DecimalFormat df = new DecimalFormat("0.00%");
		return df.format(b);
	}

	/**
	 * 用ascii码判断字符串是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否为数字（含小数）
	 * 
	 * @param str
	 *            字符串
	 * @return 是/否
	 */
	public static boolean isNumber(String str) {
		String reg = "^-?[0-9]+(\\.)?[0-9]*$";
		return str.matches(reg);
	}
}
