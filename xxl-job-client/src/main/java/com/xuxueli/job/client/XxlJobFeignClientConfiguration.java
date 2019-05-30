package com.xuxueli.job.client;

import org.springframework.context.annotation.Bean;

/**
 * @author Luo Bao Ding
 * @since 2019/5/27
 */
public class XxlJobFeignClientConfiguration {

    @Bean
    public AccessTokenPutRequestInterceptor accessTokenPutRequestInterceptor(XxlJobProperties xxlJobProperties) {
        return new AccessTokenPutRequestInterceptor(xxlJobProperties);
    }
}
