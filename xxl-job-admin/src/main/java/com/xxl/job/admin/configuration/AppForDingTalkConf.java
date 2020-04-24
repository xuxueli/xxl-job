package com.xxl.job.admin.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author wachoo
 * @Date 18:53 2019/4/17
 * @Desc DingTalk配置
 **/
@Configuration
public class AppForDingTalkConf {

    @Value("${app.agentid}")
    private String agentId;

    @Value("${app.key}")
    private String appKey;

    @Value("${app.secret}")
    private String appSecret;


    @Value("${app.access_token}")
    private String accessToken;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
