package com.dahantc.erp.commom;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.util.StringUtil;

/**
 * 企业微信相关接口
 * 
 * @author 8501
 *
 */
public class WeixinMessage {

	private static final Logger logger = LogManager.getLogger(WeixinMessage.class);
	private static WeixinParam weixinParam = null;
	// 根据appid获取code
	public static String QY_WEIXIN_OAUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=CORPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&agentid=AGENTID&state=STATE#wechat_redirect";
	public static boolean isTestModle = false;
	private static JSONObject corpsecret_access_token = null;
	private static Map<String, JSONObject> mobile_userInfo_token = new HashMap<String, JSONObject>();
	private static Map<String, JSONObject> access_token_map = new HashMap<String, JSONObject>();

	/**
	 * 初始化企业微信参数
	 */
	public static WeixinParam initwxParam(Environment ev) {
		if (null == weixinParam) {
			WeixinParam _weixinParam = new WeixinParam();
			_weixinParam.setCorpid(ev.getProperty("wx.corpid"));
			_weixinParam.setCorpsecret(ev.getProperty("wx.corpsecret"));
			_weixinParam.setAgentid(ev.getProperty("wx.agentid"));
			_weixinParam.setRedirect_uri(ev.getProperty("wx.redirect_uri"));
			_weixinParam.setTest(ev.getProperty("wx.isTest"));
			_weixinParam.setCheckinSecret(ev.getProperty("wx.checkinSecret"));
			String v = ev.getProperty("wx.userListSize");
			if (StringUtil.isNotBlank(v)) {
				_weixinParam.setUserListSize(Integer.parseInt(v));
			}
			weixinParam = _weixinParam;
		}
		return weixinParam;
	}

	/**
	 * 获取应用信息
	 * 
	 * @return
	 */
	public static String getAgentInfo() {
		boolean needCleanToken = false;
		String agentInfo = getAgentInfo(needCleanToken);
		if (StringUtils.isNotBlank(agentInfo)) {
			JSONObject agentInfoJson = JSONObject.parseObject(agentInfo);
			if (null != agentInfoJson) {
				String errmsg = agentInfoJson.getString("errmsg");
				String agentid = agentInfoJson.getString("agentid");
				if (!"ok".equalsIgnoreCase(errmsg) || StringUtils.isBlank(agentid)) {
					needCleanToken = true;
				}
			}

		} else {
			needCleanToken = true;
		}
		if (needCleanToken) {
			agentInfo = getAgentInfo(needCleanToken);
		}

		return agentInfo;

	}

	/**
	 * 获取应用信息 needcleanToken 是否需要重新获Token
	 * 
	 * @return
	 */
	private static String getAgentInfo(boolean needCleanToken) {

		if (needCleanToken) {
			corpsecret_access_token = null;
		}
		String corpsecretToken = getAccessToken();
		if (StringUtils.isBlank(corpsecretToken)) {
			logger.error("corpsecretToken is null ");
			return null;
		}
		String uri = "agent/get?access_token=" + corpsecretToken + "&agentid=" + weixinParam.getAgentid();
		return doPost(weixinParam.getBaseUrl() + uri, "");
	}

	/**
	 * 获取部门信息 departmentId 部门id。获取指定部门及其下的子部门。 如果不填，默认获取全量组织架构
	 * 
	 * @return
	 */
	public static String getDepartmentList(String departmentId) {

		String corpsecretToken = getAccessToken();
		if (StringUtils.isBlank(corpsecretToken)) {
			return null;
		}
		String uri = "department/list?access_token=" + corpsecretToken + "&id=" + departmentId;
		return doPost(weixinParam.getBaseUrl() + uri, "");

	}

	/**
	 * 获取用户信息
	 * 
	 * @param userid
	 * @return
	 */
	public static String getUserInfo(String userid) {
		String sendMsgRes = getUserInfo(userid, false);
		if (StringUtils.isBlank(sendMsgRes)) {
			sendMsgRes = getUserInfo(userid, true);
		}
		return sendMsgRes;

	}

	/**
	 * 获取用户信息
	 * 
	 * @param inMobile
	 * @return
	 */
	public static JSONObject getUserInfoByMobile(String inMobile) {
		if (mobile_userInfo_token.containsKey(inMobile)) {
			return mobile_userInfo_token.get(inMobile);
		}
		return getUserList().get(inMobile);
	}

	/**
	 * 获取用戶ID
	 * 
	 * @param code
	 * @return
	 */
	public static String getUserID(String code) {
		String corpsecretToken = getAccessToken();
		if (StringUtils.isBlank(corpsecretToken)) {
			return null;
		}
		String uri = "user/getuserinfo?access_token=" + corpsecretToken + "&code=" + code;
		String sendMsgRes = doPost(weixinParam.getBaseUrl() + uri, "");
		logger.info("getUserID响应数据:" + sendMsgRes);
		return sendMsgRes;

	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public static String getUserList(String department_id, int fetch_child) {
		String corpsecretToken = getAccessToken();
		if (StringUtils.isBlank(corpsecretToken)) {
			return null;
		}
		String uri = "user/list?access_token=" + corpsecretToken + "&fetch_child=" + fetch_child + "&department_id=" + department_id;
		return doPost(weixinParam.getBaseUrl() + uri, "");

	}

	/**
	 * 获取用户信息
	 * 
	 * @return mobile_userInfo_token
	 */
	public static Map<String, JSONObject> getUserList() {
		Map<String, JSONObject> mobile_userInfo = new HashMap<String, JSONObject>();
		String agentInfo = getAgentInfo();
		JSONObject agentInfoJson = JSONObject.parseObject(agentInfo);
		if (null != agentInfoJson) {
			JSONObject allow_userinfossObj = agentInfoJson.getJSONObject("allow_userinfos");
			try {
				JSONArray userJSONArray = allow_userinfossObj.getJSONArray("user");
				for (Object object : userJSONArray) {
					String userInfo = getUserInfo(((JSONObject) object).getString("userid"));
					JSONObject userInfoJson = JSONObject.parseObject(userInfo);
					if (null != userInfoJson) {
						String mobile = userInfoJson.getString("mobile");
						mobile_userInfo.put(mobile, userInfoJson);
					}
				}
			} catch (Exception e) {
				logger.error("get allow_userinfos error :", e);
				e.printStackTrace();
			}

			JSONObject allow_partysObj = agentInfoJson.getJSONObject("allow_partys");
			try {
				JSONArray partyidJSONArray = allow_partysObj.getJSONArray("partyid");
				for (int index = 0; index < partyidJSONArray.size(); index++) {
					String userList = getUserList(partyidJSONArray.getString(index), 1);
					JSONObject userListJson = JSONObject.parseObject(userList);
					if (null != userListJson) {
						JSONArray userlistJSONArray = userListJson.getJSONArray("userlist");
						for (Object object : userlistJSONArray) {
							String mobile = ((JSONObject) object).getString("mobile");
							mobile_userInfo.put(mobile, (JSONObject) object);
						}
					}
				}
			} catch (Exception e) {
				logger.error("get allow_partys error :", e);
				e.printStackTrace();
			}
		} else {
			logger.error("getAgentInfo empty ");
		}
		mobile_userInfo_token = mobile_userInfo;
		return mobile_userInfo;
	}

	/**
	 * 获取打卡数据
	 *
	 * @param postData	提交数据JSON字符串
	 *
	 * @return	响应结果
	 */
	public static String getCheckInData(String postData) {
		JSONObject accessToken = getAccessToken(weixinParam.getCorpid(), weixinParam.getCheckinSecret());
		String access_token = accessToken.getString("access_token");
		String uri = "checkin/getcheckindata?access_token=" + access_token;
		logger.info("请求数据：" + postData);
		String result = doPost(weixinParam.getBaseUrl() + uri, postData);
		logger.info("响应数据：" + result);
		return result;
	}

	/**
	 * 获取素材
	 *
	 * @param mediaId	媒体文件id
	 *
	 * @return	响应结果
	 */
	public static byte[] getMedia(String mediaId) {
		JSONObject accessToken = getAccessToken(weixinParam.getCorpid(), weixinParam.getCheckinSecret());
		String access_token = accessToken.getString("access_token");
		String uri = "media/get?access_token=" + access_token + "&media_id=" + mediaId;
		logger.info("请求媒体文件：" + mediaId);
		FileInputStream inputStream = null;
		byte[] buff = null;
		try {
			buff = doGet(weixinParam.getBaseUrl() + uri, null);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		return buff;
	}

	/**
	 * 发送消息
	 * 
	 * @param mobile
	 * @param content
	 * @return
	 */
	public static String sendMessageByMobile(String mobile, String content) {
		if (null != mobile) {
			String[] mobileArray = mobile.split(",");
			for (String string : mobileArray) {
				JSONObject userInfo = getUserInfoByMobile(string);
				if (null != userInfo) {
					return sendMessage("", userInfo.getString("userid"), content);
				} else {
					logger.info("在企业微信上，手机号没有找到对对应的用户信息：" + mobile);
				}
			}
		}
		return "";

	}

	/**
	 * 发送消息
	 * 
	 * @param toparty
	 * @param touser
	 * @param content
	 * @return
	 */
	public static String sendMessage(String toparty, String touser, String content) {
		String corpsecretToken = getAccessToken();

		if (StringUtils.isBlank(corpsecretToken)) {
			return null;
		}

		JSONObject param = new JSONObject();
		if (StringUtils.isBlank(toparty) && StringUtils.isBlank(touser)) {
			touser = "@all";
		}
		param.put("touser", touser);
		param.put("toparty", toparty);
		param.put("totag", "");
		param.put("msgtype", "text");
		param.put("agentid", weixinParam.getAgentid());
		JSONObject textJson = new JSONObject();
		textJson.put("content", content);
		param.put("text", textJson);
		param.put("safe", "0");
		String requestData = param.toString();
		logger.info("sendMsg请求数据：" + requestData);
		String uri = "message/send?access_token=" + corpsecretToken;
		String sendMsgRes = doPost(weixinParam.getBaseUrl() + uri, requestData);
		logger.info("sendMsg响应数据:" + sendMsgRes);
		return sendMsgRes;
	}

	private static String getAccessToken() {
		if (null == corpsecret_access_token || (StringUtils.isBlank(corpsecret_access_token.getString("access_token")))
				|| (corpsecret_access_token.getLong("creatTime") == 0)
				|| (System.currentTimeMillis() - corpsecret_access_token.getLong("creatTime") > 1000 * 60 * 60)) {
			try {
				String uri = "gettoken?corpid=" + weixinParam.getCorpid() + "&corpsecret=" + weixinParam.getCorpsecret();
				String token = doPost(weixinParam.getBaseUrl() + uri, "");
				JSONObject tokenJson = JSONObject.parseObject(token);
				if (null != tokenJson) {
					JSONObject corpsecretToken = new JSONObject();
					corpsecretToken.put("access_token", tokenJson.getString("access_token"));
					corpsecretToken.put("creatTime", System.currentTimeMillis());
					corpsecretToken.put("expires_in", tokenJson.getInteger("expires_in"));
					corpsecret_access_token = corpsecretToken;
				}
			} catch (Exception e) {
				logger.error("gettoken error " + e.getMessage());
				e.printStackTrace();
			}

		}

		if (null == corpsecret_access_token || StringUtils.isBlank(corpsecret_access_token.getString("access_token"))) {
			logger.info("获取access_token失败！");
			return null;
		}
		return corpsecret_access_token.getString("access_token");
	}

	/**
	 * 获取AccessToken
	 * @param corpid		企业id
	 * @param corpsecret	应用密钥
	 * @return
	 */
	private static JSONObject getAccessToken(String corpid, String corpsecret) {
		JSONObject corpsecretToken = access_token_map.get(corpsecret);
		if (null == corpsecretToken || (StringUtils.isBlank(corpsecretToken.getString("access_token"))) || (corpsecretToken.getLong("creatTime") == 0)
				|| (System.currentTimeMillis() - corpsecretToken.getLong("creatTime") > (corpsecretToken.getLong("expires_in") * 1000))) {
			try {
				String uri = "gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;
				String token = doPost(weixinParam.getBaseUrl() + uri, null);
				JSONObject tokenJson = JSONObject.parseObject(token);
				if (null != tokenJson) {
					corpsecretToken = new JSONObject();
					corpsecretToken.put("access_token", tokenJson.getString("access_token"));
					corpsecretToken.put("creatTime", System.currentTimeMillis());
					// 过期时间，单位秒
					corpsecretToken.put("expires_in", tokenJson.getLongValue("expires_in"));
					access_token_map.put(corpsecret, corpsecretToken);
				}
			} catch (Exception e) {
				logger.info("gettoken error " + e.getMessage());
			}

		}
		return corpsecretToken;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param userid
	 * @return
	 */
	private static String getUserInfo(String userid, boolean needCleanToken) {

		if (needCleanToken) {
			corpsecret_access_token = null;
		}
		String corpsecretToken = getAccessToken();
		if (StringUtils.isBlank(corpsecretToken)) {
			return null;
		}
		String uri = "user/get?access_token=" + corpsecretToken + "&userid=" + userid;
		String sendMsgRes = doPost(weixinParam.getBaseUrl() + uri, "");
		logger.info("getUserInfo响应数据:" + sendMsgRes);
		return sendMsgRes;

	}

	/**
	 * 发送https POST请求
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            提交的数据
	 * @return
	 */
	private static String doPost(String url, String data) {
		String res = null;
		HttpsURLConnection conns = null;
		try {
			// 创建SSLContext
			SSLContext sslContext = SSLContext.getInstance("SSL");
			TrustManager[] tm = { new TrustAnyTrustManager() };
			// 初始化
			sslContext.init(null, tm, new java.security.SecureRandom());

			HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
				public boolean verify(String s, SSLSession sslsession) {
					return true;
				}
			};
			// 设置使用的SSLSoctetFactory
			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			URL _url = new URL(url);
			conns = (HttpsURLConnection) _url.openConnection();
			conns.setRequestMethod("POST");
			conns.setConnectTimeout(6000);
			conns.setReadTimeout(6000);
			conns.setRequestProperty("Content-Type", "application/json");
			conns.setRequestProperty("Charset", "UTF-8");
			conns.setUseCaches(false);
			conns.setDoOutput(true);
			conns.setDoInput(true);
			conns.connect();

			// 提交数据
			DataOutputStream dos = new DataOutputStream(conns.getOutputStream());
			if (StringUtils.isNotBlank(data)) {
				dos.write(data.getBytes());
			}
			dos.flush();
			dos.close();

			// 读取返回内容
			BufferedReader br = new BufferedReader(new InputStreamReader(conns.getInputStream(), "utf-8"));
			StringBuffer response = new StringBuffer();
			String line = null;
			while (null != (line = br.readLine())) {
				response.append(line);
			}
			res = response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != conns) {
				conns.disconnect();
			}
		}
		logger.debug("weixin request data = " + data + "---,response data= " + res);
		return res;
	}

	/**
	 * 发送https POST请求
	 *
	 * @param url
	 *            请求地址
	 * @param data
	 *            提交的数据
	 * @return
	 */
	private static byte[] doGet(String url, String data) {
		byte[] getData = null;
		HttpsURLConnection conns = null;
		InputStream inputStream = null;
		try {
			// 创建SSLContext
			SSLContext sslContext = SSLContext.getInstance("SSL");
			TrustManager[] tm = { new TrustAnyTrustManager() };
			// 初始化
			sslContext.init(null, tm, new java.security.SecureRandom());

			HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
				public boolean verify(String s, SSLSession sslsession) {
					return true;
				}
			};
			// 设置使用的SSLSoctetFactory
			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			URL _url = new URL(url);
			conns = (HttpsURLConnection) _url.openConnection();
			conns.setRequestMethod("GET");
			conns.setConnectTimeout(6000);
			conns.setReadTimeout(6000);
			conns.setRequestProperty("Content-Type", "application/json");
			conns.setRequestProperty("Charset", "UTF-8");
			conns.setUseCaches(false);
			conns.setDoOutput(true);
			conns.setDoInput(true);
			conns.connect();

			// 得到输入流
			inputStream = conns.getInputStream();
			getData = readInputStream(inputStream);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			if (null != conns) {
				conns.disconnect();
			}
		}
		return getData;
	}

	public static byte[] readInputStream(InputStream inputStream)
			throws IOException {
		byte[] b = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(b)) != -1) {
			bos.write(b, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}


	public static WeixinParam getWeixinParam() {
		return weixinParam;
	}
}
