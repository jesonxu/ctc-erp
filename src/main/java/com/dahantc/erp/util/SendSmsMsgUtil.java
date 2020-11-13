package com.dahantc.erp.util;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component
public class SendSmsMsgUtil {
	private static final Logger logger = LogManager.getLogger(SendSmsMsgUtil.class);

	private static String url;

	private static String account;

	private static String password;

	private static String template;

	@Value("${sms.url}")
	public void setUrl(String url) {
		SendSmsMsgUtil.url = url;
	}

	@Value("${sms.account}")
	public void setAccount(String account) {
		SendSmsMsgUtil.account = account;
	}

	@Value("${sms.password}")
	public void setPassword(String password) {
		SendSmsMsgUtil.password = password;
	}

	@Value("${sms.template}")
	public void setTemplate(String template) {
		SendSmsMsgUtil.template = template;
	}

	public static boolean sendRandomCodeMsg(String phones, String randomCode) {
		try {
			JSONObject json = new JSONObject();
			json.put("account", account);
			json.put("password", password);
			json.put("phones", phones);
			json.put("content", template.replace("###*###", randomCode));
			String response = HttpUtil.httpPost(url, new HashMap<>(), json.toJSONString());
			if (StringUtils.isBlank(response) || !"0".equals(JSONObject.parseObject(response).getString("result"))) {
				logger.error("短信提交异常，提交响应：" + response);
				return false;
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return true;
	}

	public String getUrl() {
		return url;
	}

	public String getAccount() {
		return account;
	}

	public String getPassword() {
		return password;
	}

	public String getTemplate() {
		return template;
	}

}
