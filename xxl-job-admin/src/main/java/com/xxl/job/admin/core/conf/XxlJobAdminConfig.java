package com.xxl.job.admin.core.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobAdminConfig implements InitializingBean{
    private static XxlJobAdminConfig adminConfig = null;
    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;
    }

    private String mailHost = "smtp.163.com";

    private String mailPort = "25";

    private boolean mailSSL = false;

    private String mailUsername = "";

    private String mailPassword = "";

    private String mailSendNick = "";

    private String loginUsername = "";

    private String loginPassword = "";

    private String i18n ="";


    public String getMailHost() {
        return mailHost;
    }

    public String getMailPort() {
        return mailPort;
    }

    public boolean isMailSSL() {
        return mailSSL;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public String getMailSendNick() {
        return mailSendNick;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public String getI18n() {
        return i18n;
    }

}
