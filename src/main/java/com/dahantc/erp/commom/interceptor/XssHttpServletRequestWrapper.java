package com.dahantc.erp.commom.interceptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	HttpServletRequest orgRequest = null;
	private static final Logger logger = LogManager.getLogger(XssHttpServletRequestWrapper.class);

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		if (orgRequest.getAttribute("HTTP-CONTEXT-XSSENCODE") == null) {
			return xssEncode(super.getParameter(xssEncode(name)));
		} else {
			return super.getParameter(xssEncode(name));
		}

	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {

		String value = super.getHeader(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 
	 * @param s
	 * @return
	 */
	public static String xssEncode(String value) {
		if (value == null) {
			return null;
		}
		StringBuffer result = new StringBuffer(value.length());
		for (int i = 0; i < value.length(); ++i) {
			switch (value.charAt(i)) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&#39;");
				break;
			case '%':
				result.append("&#37;");
				break;
			case '(':
				result.append("&#40;");
				break;
			case ')':
				result.append("&#41;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '+':
				result.append("&#43;");
				break;
			default:
				result.append(value.charAt(i));
				break;
			}
		}
		return result.toString();

	}

	public static String xssDecode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		try {
			s = StringEscapeUtils.unescapeHtml4(s);
		} catch (Exception e) {
			logger.error("转译内容:" + s + ",异常:", e);
		}
		logger.info("转译后内容:" + s);
		return s;
	}

	public static String contentDecode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		try {
			String string = s.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			if (string.endsWith("\u2028")) {
				string = string.substring(0, string.lastIndexOf("\u2028"));
			}
			string = string.replace("\u2028", " ");
			s = StringEscapeUtils.unescapeHtml4(string);
		} catch (Exception e) {
			logger.error("转译内容:" + s + ",异常:", e);
		}
		return s;
	}

	/**
	 * 获取最原始的request
	 * 
	 * @return
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XssHttpServletRequestWrapper) {
			return ((XssHttpServletRequestWrapper) req).getOrgRequest();
		}
		return req;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = null;
		Map<String, String[]> map = orgRequest.getParameterMap();
		if (orgRequest.getAttribute("HTTP-CONTEXT-XSSENCODE") == null) {
			if (map != null) {
				parameterMap = new HashMap<String, String[]>();
				Iterator<Map.Entry<String, String[]>> iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String[]> entry = iter.next();
					String[] values = entry.getValue();
					if (values != null) {
						for (int i = 0; i < values.length; i++) {
							values[i] = xssEncode(values[i]);
						}
					}
					parameterMap.put(entry.getKey(), values);
				}
				orgRequest.setAttribute("HTTP-CONTEXT-XSSENCODE", true);
			}
			return parameterMap;
		} else {
			return map;
		}

	}
}
