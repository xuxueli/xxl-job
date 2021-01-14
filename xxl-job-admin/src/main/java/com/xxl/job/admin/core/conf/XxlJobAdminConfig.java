package com.xxl.job.admin.core.conf;

import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.core.biz.AdminBiz;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
public class XxlJobAdminConfig implements InitializingBean{
    private static XxlJobAdminConfig adminConfig = null;
    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;
    }

    // conf

    @Value("${xxl.job.login.username}")
    private String loginUsername;

    @Value("${xxl.job.login.password}")
    private String loginPassword;

    @Value("${xxl.job.i18n}")
    private String i18n;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${spring.mail.username}")
    private String emailUserName;


    @Value("${web_hook.url}")
    private String webHookUrl;

    @Value("${web_hook.method}")
    private String webHookMethod;

    @Value("${web_hook.data}")
    private String webHookData;
    // dao, service

    @Resource
    private XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private AdminBiz adminBiz;
    @Resource
    private JavaMailSender mailSender;

    public String getLoginUsername() {
        return loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public String getI18n() {
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailUserName() {
        return emailUserName;
    }

    public XxlJobLogDao getXxlJobLogDao() {
        return xxlJobLogDao;
    }

    public XxlJobInfoDao getXxlJobInfoDao() {
        return xxlJobInfoDao;
    }

    public XxlJobRegistryDao getXxlJobRegistryDao() {
        return xxlJobRegistryDao;
    }

    public XxlJobGroupDao getXxlJobGroupDao() {
        return xxlJobGroupDao;
    }

    public AdminBiz getAdminBiz() {
        return adminBiz;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void invokeWebHook(String... params) {
        if (webHookUrl==null || webHookUrl.isEmpty() || webHookMethod == null || webHookMethod.isEmpty()) {
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if ("POST".equalsIgnoreCase(webHookMethod)) {
            String data = (webHookData==null || webHookData.isEmpty())? "{}" : format(webHookData, params);
            HttpEntity<String> entity = new HttpEntity<>(data, headers);
            restTemplate().postForObject(webHookUrl, entity, String.class);
        }
        else if ("GET".equalsIgnoreCase(webHookMethod)) {
            restTemplate().getForObject(webHookUrl, String.class, params);
        }
    }

    private RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
       return new RestTemplate(requestFactory);
    }

    private String format(String data, String... params) {
        if (params == null || params.length <= 0) {
            return data;
        }
        for (int i =0 ; i < params.length; i++) {
            data = data.replaceFirst("\\{"+i+"\\}", params[i]);
        }
        return data;
    }


}

