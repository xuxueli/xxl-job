package com.xuxueli.job.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Luo Bao Ding
 * @since 2019/5/27
 */
public class AccessTokenPutRequestInterceptor implements RequestInterceptor {
    public static final String H_ACCESS_TOKEN = "ACCESS_TOKEN";

    @Value("${xxl.job.accessToken:}")
    private String accessToken;

    @Override
    public void apply(RequestTemplate template) {
        template.header(H_ACCESS_TOKEN, accessToken);

    }
}
