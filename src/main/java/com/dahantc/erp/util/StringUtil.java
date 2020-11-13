/**
 * 
 */
package com.dahantc.erp.util;

import com.dahantc.erp.commom.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.chinatricom.util.Tools;

/**
 * @author 8536
 * 
 */
public class StringUtil {
	private static final char[] HEX_CODE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final long[] byteTable = createLookupTable();
	private static final long HSTART = 0xBB40E64DA205B064L;
	private static final long HMULT = 7664345821815920749L;
	private static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '+', '/', };
	// 匹配至多6位小数的正则，且可匹配千分位
	private static final String MONEY_REGULAR = "^(([1-9][0-9]{0,2}(,?\\d{3})*)|0)(\\.\\d{1,6})?$";

	/**
	 * 转半角的函数(DBC case) 全角空格为12288，半角空格为32
	 * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 * 
	 * @param input
	 * @return
	 */
	public static String toDBC(String input) {

		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String listAsString(List<String> list) {
		if (list != null && list.size() > 0) {
			StringBuffer sb = new StringBuffer();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				if (i + 1 == len) {
					sb.append(list.get(i));
				} else {
					sb.append(list.get(i) + ",");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static List<String> listAsString(String[] arrays, int size) {
		if (arrays != null && arrays.length > 0) {
			List<String> strings = new ArrayList<String>();
			StringBuffer sb = new StringBuffer();
			int len = arrays.length;
			for (int i = 0; i < len; i++) {
				if (i + 1 == len || ((i + 1) % size == 0)) {
					sb.append(arrays[i]);
					strings.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(arrays[i] + ",");
				}
			}
			sb.setLength(0);
			sb = null;
			return strings;
		} else {
			return null;
		}
	}

	public static String[] listAsArray(List<String> list) {
		if (list != null && list.size() > 0) {
			String[] objs = new String[list.size()];
			list.toArray(objs);
			return objs;
		} else {
			return null;
		}
	}

	public static String arrayAsString(String[] arrays) {
		if (arrays != null && arrays.length > 0) {
			StringBuffer sb = new StringBuffer();
			int len = arrays.length;
			for (int i = 0; i < len; i++) {
				if (i + 1 == len) {
					sb.append(arrays[i]);
				} else {
					sb.append(arrays[i] + ",");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static List<String> ListAsString(String[] arrays, int size) {
		if (arrays != null && arrays.length > 0) {
			List<String> strings = new ArrayList<String>();
			StringBuffer sb = new StringBuffer();
			int len = arrays.length;
			for (int i = 0; i < len; i++) {
				if (i + 1 == len || ((i + 1) % size == 0)) {
					sb.append(arrays[i]);
					strings.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(arrays[i] + ",");
				}
			}
			sb.setLength(0);
			sb = null;
			return strings;
		} else {
			return null;
		}
	}

	/**
	 * 取短信内容当中是否带有签名格式字符串
	 * 默认签名长度不得超过20
	 */
	public static String[] getSignature(String content) {
		List<String> _signList = null;
		Pattern pattern = Pattern.compile("^\u3010[^\u3011]+\u3011|\u3010[^\u3010]+\u3011$|^\\[[^\\]]+\\]|\\[[^\\[]+\\]$");
		Matcher m = pattern.matcher(content);
		if (m.find()) {
			_signList = new ArrayList<String>();
			String sign = m.group();
			_signList.add(sign);
			while (m.find()) {
				sign = m.group();
				_signList.add(sign);
			}
			return _signList.size() == 0 ? null : _signList.toArray(new String[] {});
		}
		return null;
	}

	/**
	 * 去掉字符串中的回车
	 * 
	 * @param str
	 * @return
	 */
	public static String cleanEnter(String str) {
		Pattern p = Pattern.compile("[\n-\r]");
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	// 计算字符串有多少字符(中文一个字占两个字符)
	public static int getStringLength(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		// 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < value.length(); i++) {
			// 获取一个字符
			String temp = value.substring(i, i + 1);
			// 判断是否为中文字符
			if (temp.matches(chinese)) {
				// 中文字符长度为1
				valueLength += 2;
			} else {
				// 其他字符长度为0.5
				valueLength += 1;
			}
		}
		// 进位取整
		return valueLength;
	}

	/**
	 * 解析直连协议内容编码
	 * 
	 * @param content
	 * @return
	 */
	/* 
	public static String transcoding(String content) {
		String tranScodContent = null;
		int n = Integer.valueOf(content.substring(1, 2));
		byte[] _byeContent = Tools.hexStringToByteArray(content);
		if (n == 5) {
			byte[] _byte = new byte[_byeContent.length - 6];
			System.arraycopy(_byeContent, 6, _byte, 0, _byte.length);
			tranScodContent = Tools.decodeSmsMsg(_byte, 8);
		}
		if (n == 7) {
			byte[] _byte = new byte[_byeContent.length - 8];
			System.arraycopy(_byeContent, 8, _byte, 0, _byte.length);
			tranScodContent = Tools.decodeSmsMsg(_byte, 8);
		}
		if (n == 6) {
			byte[] _byte = new byte[_byeContent.length - 7];
			System.arraycopy(_byeContent, 7, _byte, 0, _byte.length);
			tranScodContent = Tools.decodeSmsMsg(_byte, 7);
		}
		return tranScodContent;
	}*/

	public static String decodeCMPPMsgID(byte abyte0[]) {
		StringBuffer stringbuffer = new StringBuffer();
		String msgid_pre = byteArrayToHexString(abyte0, 0, abyte0.length - 2);
		stringbuffer.append(msgid_pre);
		stringbuffer.append("_");
		int msgid_seq = abyte0[abyte0.length - 1] & 0xff | (abyte0[abyte0.length - 2] & 0xff) << 8;
		stringbuffer.append(msgid_seq);
		return stringbuffer.toString();

	}

	private static String byteArrayToHexString(byte b[], int offset, int length) {
		StringBuffer result = new StringBuffer(length * 2);
		for (int i = 0; i < length; i++) {
			int n = b[offset + i];
			if (n < 0)
				n = 256 + n;
			result.insert(2 * i, HEX_CODE[(n >> 4)]);
			result.insert(2 * i + 1, HEX_CODE[n & 0x0f]);
		}
		return result.toString();
	}

	public static String genRandomNum(int pwd_len) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[][] str = { { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' },
				{ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' },
				{ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' } };

		String pwd = "";
		Random r = new Random();
		char[] tmpStr = new char[str.length];
		for (int j = 0; j < str.length; j++) {
			tmpStr[j] = str[j][r.nextInt(str[j].length)];
		}
		count = tmpStr.length;
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止生成负数，
			int number = r.nextInt(str.length);
			i = Math.abs(r.nextInt(str[number].length)); // 生成的数最大为36-1
			pwd += str[number][i];
			count++;
		}

		for (char c : tmpStr) {
			int randInt = r.nextInt(pwd.length());
			pwd = pwd.substring(0, randInt) + c + pwd.substring(randInt);
		}

		return pwd;
	}

	private static final long[] createLookupTable() {
		long[] byteTable = new long[256];
		long h = 0x544B2FBACAAF1684L;
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 31; j++) {
				h = (h >>> 7) ^ h;
				h = (h << 11) ^ h;
				h = (h >>> 10) ^ h;
			}
			byteTable[i] = h;
		}
		return byteTable;
	}

	public static long hashCode(CharSequence cs) {
		long h = HSTART;
		final long hmult = HMULT;
		final long[] ht = byteTable;
		final int len = cs.length();
		for (int i = 0; i < len; i++) {
			char ch = cs.charAt(i);
			h = (h * hmult) ^ ht[ch & 0xff];
			h = (h * hmult) ^ ht[(ch >>> 8) & 0xff];
		}
		return h;
	}

	public static boolean isBlank(String str) {
		return (str == null || "".equals(str.trim()));
	}

	public static boolean isNotBlank(String str) {
		return (str != null && !"".equals(str.trim()));
	}

	/**
	 * 把10进制的数字转换成64进制
	 * 
	 * @param number
	 * @param shift
	 * @return
	 */
	public static String hex10To64(long number) {
		char[] buf = new char[64];
		int charPos = 64;
		int radix = 1 << 6;
		long mask = radix - 1;
		do {
			buf[--charPos] = digits[(int) (number & mask)];
			number >>>= 6;
		} while (number != 0);
		return new String(buf, charPos, (64 - charPos));
	}

	/**
	 * 把64进制的字符串转换成10进制
	 * 
	 * @param decompStr
	 * @return
	 */
	public static long hex64To10(String decompStr) {
		long result = 0;
		for (int i = decompStr.length() - 1; i >= 0; i--) {
			if (i == decompStr.length() - 1) {
				result += getCharIndexNum(decompStr.charAt(i));
				continue;
			}
			for (int j = 0; j < digits.length; j++) {
				if (decompStr.charAt(i) == digits[j]) {
					result += ((long) j) << 6 * (decompStr.length() - 1 - i);
				}
			}
		}
		return result;
	}

	private static long getCharIndexNum(char ch) {
		int num = ((int) ch);
		if (num >= 48 && num <= 57) {
			return num - 48;
		} else if (num >= 97 && num <= 122) {
			return num - 87;
		} else if (num >= 65 && num <= 90) {
			return num - 29;
		} else if (num == 43) {
			return 62;
		} else if (num == 47) {
			return 63;
		}
		return 0;
	}

	/**
	 * 检测是否有emoji字符
	 * 
	 * @param source
	 * @return 一旦含有就抛出
	 */
	public static boolean containsEmoji(String source) {
		if (StringUtil.isBlank(source)) {
			return false;
		}
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
	}

	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {

		if (StringUtil.isNotBlank(source)) {
			StringBuilder buf = new StringBuilder(source.length());
			try {
				int len = source.length();
				for (int i = 0; i < len; i++) {
					char codePoint = source.charAt(i);
					if (!isEmojiCharacter(codePoint)) {
						buf.append(codePoint);
					}
				}

				source = buf.toString();
				return source;
			} finally {
				buf = null;
			}

		}
		return null;
	}



	/**
	 * 获取关系的名称
	 *
	 * @param relationship
	 *            关系
	 */
	public static String getMatchName(String relationship) {
		if (Constants.ROP_EQ.equals(relationship)) {
			return "等于";
		} else if (Constants.ROP_NE.equals(relationship)) {
			return "不等于";
		} else if (Constants.ROP_LT.equals(relationship)) {
			return "小于";
		} else if (Constants.ROP_GT.equals(relationship)) {
			return "大于";
		} else if (Constants.ROP_LE.equals(relationship)) {
			return "小于等于";
		} else if (Constants.ROP_GE.equals(relationship)) {
			return "大于等于";
		}
		return "";
	}

	/**
	 * 判断strs中是否包含str
	 * 
	 * @param strs
	 * @param str
	 * @return
	 */
	public static boolean isContains(String strs, String str) {
		if (strs != null) {
			if ("*".equals(strs.trim())) {
				return true;
			}
			String[] strList = strs.split(",");
			return Arrays.asList(strList).contains(str);
		}
		return false;
	}

	/**
	 * 判断str是否是金额的数字，可匹配1~4位小数，和千分位
	 * @param str
	 * @return
	 */
	public static boolean isMoneyNumber(String str){
		Pattern pattern = Pattern.compile(MONEY_REGULAR);
		Matcher match=pattern.matcher(str);
		return match.matches();
	}

	/**
	 * 比较2个字符串内容相等
	 * @param first
	 * @param second
	 * @return
	 */
	public static Boolean equals(Object first, Object second) {
		if (first == null && second == null) {
			return Boolean.TRUE;
		} else {
			return first != null && second != null ? first.toString().equals(second.toString()) : Boolean.FALSE;
		}
	}
}
