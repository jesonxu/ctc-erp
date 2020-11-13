package com.dahantc.erp.commom;

public class WeixinParam {
	private String baseUrl = "https://qyapi.weixin.qq.com/cgi-bin/";
	private String corpid = "wx8e826d80e23e67b3";
	private String corpsecret = "";
	private String agentid = "";
	private String redirect_uri = "";
	private String test = "false";
	private String checkinSecret = "";
	private int userListSize = 100;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getCorpid() {
		return corpid;
	}

	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}

	public String getCorpsecret() {
		return corpsecret;
	}

	public void setCorpsecret(String corpsecret) {
		this.corpsecret = corpsecret;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getRedirect_uri() {
		return redirect_uri;
	}

	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getCheckinSecret() {
		return checkinSecret;
	}

	public void setCheckinSecret(String checkinSecret) {
		this.checkinSecret = checkinSecret;
	}

	public int getUserListSize() {
		return userListSize;
	}

	public void setUserListSize(int userListSize) {
		this.userListSize = userListSize;
	}
}