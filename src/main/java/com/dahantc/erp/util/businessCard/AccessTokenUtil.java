package com.dahantc.erp.util.businessCard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.util.HttpUtil;
import com.dahantc.erp.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * token时间
 *
 * @author 8520
 */
@Component
public class AccessTokenUtil {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenUtil.class);

    private BusinessCardConfig businessCardConfig;

    /**
     * token
     */
    private String token;

    /**
     * 过期时间
     */
    private Long expireTime;

    public String getAccessToken() {
        if (expireTime == null || StringUtil.isBlank(token)) {
            getRemoteAccessToken();
        } else {
            // 是否过期（提前1个小时过期 ）
            boolean hasExpired = System.currentTimeMillis() - expireTime > 3600000;
            if (hasExpired) {
                getRemoteAccessToken();
            }
        }
        if (StringUtil.isBlank(token)) {
            logger.error("获取token信息失败");
        }
        return token;
    }

    /**
     * 向百度获取授权的token
     */
    private void getRemoteAccessToken() {
        BusinessCardConfig.BaiduProperty baiduProperty = businessCardConfig.getBaidu();
        if (baiduProperty == null) {
            logger.error("百度卡片识别参数为配置");
            return;
        }
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=" + baiduProperty.getGrantType()
                + "&client_id=" + baiduProperty.getClientId()
                + "&client_secret=" + baiduProperty.getClientSecret();
        String tokenResult = HttpUtil.httpGet(getAccessTokenUrl);
        logger.info(tokenResult);
        if (StringUtil.isNotBlank(tokenResult)) {
            JSONObject tokenObj = JSON.parseObject(tokenResult);
            this.token = tokenObj.getString("access_token");
            this.expireTime = System.currentTimeMillis() + tokenObj.getLongValue("expires_in")*1000;
        }
    }

    @Autowired
    public void setBusinessCardConfig(BusinessCardConfig businessCardConfig) {
        this.businessCardConfig = businessCardConfig;
    }
}
