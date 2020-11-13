package com.dahantc.erp.util.businessCard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.util.StringUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * 名片扫描工具类
 *
 * @author 8520
 */
@Component
public class ScanBusinessCardUtil {

    private static Logger logger = LoggerFactory.getLogger(ScanBusinessCardUtil.class);

    /**
     * 所有的文件都压缩到2M大小后编码上传
     **/
    private static final long MAX_IMG_SIZE = 2 * 1024 * 1024;

    private AccessTokenUtil accessTokenUtil;

    /**
     * 扫描卡片信息
     *
     * @return 识别出来的信息
     */
    public BaseResponse<BusinessCardInfo> scanBusinessCard(byte[] imgData) throws IOException {
        logger.info("压缩前的文件大小" + imgData.length + "bit");
        long srcSize = imgData.length;
        double accuracy = getAccuracy(srcSize / 1024);
        while (imgData.length > MAX_IMG_SIZE) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imgData);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imgData.length);
            Thumbnails.of(inputStream)
                    .scale(1f)
                    .outputQuality(accuracy)
                    .toOutputStream(outputStream);
            imgData = outputStream.toByteArray();
        }
        logger.info("压缩后的文件大小" + imgData.length + "bit");
        String accessToken = accessTokenUtil.getAccessToken();
        try {
            String imgStr = new String(Base64.getEncoder().encode(imgData));
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            logger.info("编码完成后的文件大小" + imgParam.getBytes().length + "bit");
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_card";
            String param = "image=" + imgParam;
            String result = post(url, accessToken, param);
            logger.info("百度名片识别接口返回内容:" + result);
            if (StringUtil.isBlank(result)) {
                logger.error("请求百度的接口返回内容为空");
                return BaseResponse.error("名片识别失败");
            }
            JSONObject resultObj = JSON.parseObject(result);
            // 检测失败的内容
            JSONObject wordsResult = resultObj.getJSONObject("words_result");
            if (wordsResult == null) {
                String errorCode = resultObj.getString("error_code");
                return BaseResponse.error(ScanErrorCode.getErrorDesc(errorCode));
            }

            BusinessCardInfo cardInfo = new BusinessCardInfo();
            JSONArray addr = wordsResult.getJSONArray("ADDR");
            if (addr != null && !addr.isEmpty()) {
                cardInfo.setAddress(addr.toString());
            }
            JSONArray fax = wordsResult.getJSONArray("FAX");
            if (fax != null && !fax.isEmpty()) {
                cardInfo.setFax(fax.toString());
            }
            JSONArray mobile = wordsResult.getJSONArray("MOBILE");
            if (mobile != null && !mobile.isEmpty()) {
                cardInfo.setMobile(mobile.toString());
            }
            JSONArray name = wordsResult.getJSONArray("NAME");
            if (name != null && !name.isEmpty()) {
                cardInfo.setName(name.toString());
            }
            // 邮编
            JSONArray pc = wordsResult.getJSONArray("PC");
            if (pc != null && !pc.isEmpty()) {
                cardInfo.setPc(pc.toString());
            }
            JSONArray webUrl = wordsResult.getJSONArray("URL");
            if (webUrl != null && !webUrl.isEmpty()) {
                cardInfo.setUrl(webUrl.toString());
            }
            JSONArray tel = wordsResult.getJSONArray("TEL");
            if (tel != null && !tel.isEmpty()) {
                cardInfo.setTel(tel.toString());
            }
            JSONArray company = wordsResult.getJSONArray("COMPANY");
            if (company != null && !company.isEmpty()) {
                cardInfo.setCompany(company.toString());
            }
            JSONArray title = wordsResult.getJSONArray("TITLE");
            if (title != null && !title.isEmpty()) {
                cardInfo.setTitle(title.toString());
            }
            JSONArray email = wordsResult.getJSONArray("EMAIL");
            if (email != null && !email.isEmpty()) {
                cardInfo.setEmail(email.toString());
            }
            return BaseResponse.success(cardInfo);
        } catch (Exception e) {
            logger.error("请求百度异常", e);
        }
        return BaseResponse.error("名片扫描失败");
    }

    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2047) {
            accuracy = 0.6;
        } else if (size < 3275) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }

    private String post(String requestUrl, String accessToken, String params)
            throws Exception {
        String contentType = "application/x-www-form-urlencoded";
        return post(requestUrl, accessToken, contentType, params);
    }

    private String post(String requestUrl, String accessToken, String contentType, String params)
            throws Exception {
        String encoding = "UTF-8";
        if (requestUrl.contains("nlp")) {
            encoding = "GBK";
        }
        return post(requestUrl, accessToken, contentType, params, encoding);
    }

    private String post(String requestUrl, String accessToken, String contentType, String params, String encoding)
            throws Exception {
        String url = requestUrl + "?access_token=" + accessToken;
        return postGeneralUrl(url, contentType, params, encoding);
    }

    private String postGeneralUrl(String generalUrl, String contentType, String params, String encoding) throws Exception {
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(params.getBytes(encoding));
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
            String getLine;
            while ((getLine = in.readLine()) != null) {
                result.append(getLine);
            }
        }catch (Exception e){
            logger.error("解析返回的数据异常",e);
        }finally {
            if (in != null) {
                in.close();
            }
        }
        return result.toString();
    }

    @Autowired
    public void setAccessTokenUtil(AccessTokenUtil accessTokenUtil) {
        this.accessTokenUtil = accessTokenUtil;
    }
}