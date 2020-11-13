package com.dahantc.erp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;

public class UAParserUtil {

	private static final Logger logger = LogManager.getLogger(UAParserUtil.class);

	private static Invocable invoke;

	public static Invocable getInvoke() throws Exception {

		if (null == invoke) {
			logger.info("初始化请求源解析器");
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
			InputStreamReader streamReader = null;
			File uaParseJS = new File(ResourceUtils.getURL("classpath:").getPath(), "static/common/js/ua-parser.js");
			if (uaParseJS.exists()) {
				streamReader = new InputStreamReader(new FileInputStream(uaParseJS), "UTF-8");
			} else {
				InputStream inputStream = UAParserUtil.class.getResourceAsStream("/static/common/js/ua-parser.js");
				streamReader = new InputStreamReader(inputStream);
			}
			engine.eval(streamReader);
			if (engine instanceof Invocable) {
				invoke = (Invocable) engine; // 调用merge方法，并传入两个参数
			}
		}
		return invoke;
	}

	public static String getReqMsg(String agenct) throws Exception {
		Invocable invokeInstence = getInvoke();
		try {
			return (String) invokeInstence.invokeFunction("getMsg", agenct);
		} catch (Exception e) {
			invoke = null;
			invokeInstence = getInvoke();
			return (String) invokeInstence.invokeFunction("getMsg", agenct);
		}
	}

	public static String getBrower(String agenct) throws Exception {
		String reqMsg = getReqMsg(agenct);
		if (StringUtils.isNotBlank(reqMsg)) {
			JSONObject userAgentInfo = JSONObject.parseObject(reqMsg);
			JSONObject browser = userAgentInfo.getJSONObject("browser");
			if (browser != null) {
				return browser.getString("name");
			}
		}
		return "";
	}
}
