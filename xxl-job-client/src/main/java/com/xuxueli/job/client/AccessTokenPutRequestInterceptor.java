package com.xuxueli.job.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author Luo Bao Ding
 * @since 2019/5/27
 */
public class AccessTokenPutRequestInterceptor implements RequestInterceptor {
    public static final String H_ACCESS_TOKEN = "ACCESS_TOKEN";


    private final XxlJobProperties xxlJobProperties;

    public AccessTokenPutRequestInterceptor(XxlJobProperties xxlJobProperties) {
        this.xxlJobProperties = xxlJobProperties;
    }


    @Override
    public void apply(RequestTemplate template) {
        template.header(H_ACCESS_TOKEN, xxlJobProperties.getAccessToken());

    }
}
