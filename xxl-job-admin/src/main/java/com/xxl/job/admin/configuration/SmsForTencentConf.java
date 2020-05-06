package com.xxl.job.admin.configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author gavin
 * @Desc 腾讯云短信配置
 **/
@Configuration
public class SmsForTencentConf {
    //CAM 密钥查询：https://console.cloud.tencent.com/cam/capi
    @Value("${sms.secretId}")
    private String secretId;

    @Value("${sms.secretKey}")
    private String secretKey;

    //短信应用 ID: 在 [短信控制台] 添加应用后生成的实际 SDKAppID，查询地址：https://console.cloud.tencent.com/smsv2/app-manage
    @Value("${sms.appId}")
    private String appId;

    //短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，可登录 [短信控制台] 查看签名信息
    @Value("${sms.sign}")
    private String sign;
    //模板 ID: 必须填写已审核通过的模板 ID，可登录 [短信控制台] 查看模板 ID
    @Value("${sms.templateID}")
    private String templateID;

    public String getSecretId() {
        return secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getAppId() {
        return appId;
    }

    public String getSign() {
        return sign;
    }

    public String getTemplateID() {
        return templateID;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }
}