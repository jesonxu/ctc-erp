package com.dahantc.erp.util.businessCard;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 名片扫描配置参数
 *
 * @author 8520
 */
@Component
@ConfigurationProperties(prefix = "card")
public class BusinessCardConfig {

    private BaiduProperty baidu;

    public BaiduProperty getBaidu() {
        return baidu;
    }

    public void setBaidu(BaiduProperty baidu) {
        this.baidu = baidu;
    }

    public static class BaiduProperty {
        private String grantType;
        private String clientId;
        private String clientSecret;

        public String getGrantType() {
            return grantType;
        }

        public void setGrantType(String grantType) {
            this.grantType = grantType;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }
}
