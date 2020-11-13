package com.dahantc.erp.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

	private static final Logger logger = LogManager.getLogger(HttpUtil.class);
	private static OkHttpClient mOkHttpClient;

	static {
		OkHttpClient.Builder ClientBuilder = new OkHttpClient.Builder();
		ClientBuilder.readTimeout(20, TimeUnit.SECONDS);// 读取超时
		ClientBuilder.connectTimeout(6, TimeUnit.SECONDS);// 连接超时
		ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);// 写入超时
		// 支持HTTPS请求，跳过证书验证
		ClientBuilder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts());
		ClientBuilder.hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		mOkHttpClient = ClientBuilder.build();
	}

	/**
	 * 发送json请求
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static String postMethod(String url, JSONObject json) throws Exception {
		return httpPost(url, null, json.toString());
	}

	/**
	 * 生成安全套接字工厂，用于https请求的证书跳过
	 * 
	 * @return
	 */
	public static SSLSocketFactory createSSLSocketFactory() {
		SSLSocketFactory ssfFactory = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAllCerts() }, new SecureRandom());
			ssfFactory = sc.getSocketFactory();
		} catch (Exception e) {
			logger.error("", e);
		}
		return ssfFactory;
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @return
	 */
	public static String httpGet(String url) {
		String result = null;
		Request request = new Request.Builder().url(url).build();
		try {
			Response response = mOkHttpClient.newCall(request).execute();
			result = response.body().string();
		} catch (Exception e) {
			logger.error(url, e);
		}
		return result;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param data
	 *            提交的参数为json字符串
	 */
	public static String httpPost(String url, Map<String, String> head, String data) {
		String result = null;
		if (null == head) {
			head = new HashMap<>();
		}
		try {
			RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), data);
			Request request = new Request.Builder().url(url).headers(Headers.of(head)).post(requestBody).build();
			Response response = mOkHttpClient.newCall(request).execute();
			result = response.body().string();
		} catch (IOException e) {
			logger.error("", e);
		}
		return result;
	}

}

/**
 * 用于信任所有证书
 */
class TrustAllCerts implements X509TrustManager {
	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}
}