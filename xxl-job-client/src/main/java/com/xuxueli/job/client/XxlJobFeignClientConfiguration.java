package com.xuxueli.job.client;

import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Luo Bao Ding
 * @since 2019/5/27
 */
@Configuration
@Import(FeignClientsConfiguration.class)
public class XxlJobFeignClientConfiguration {


    @Bean
    public AccessTokenPutRequestInterceptor accessTokenPutRequestInterceptor() {
        return new AccessTokenPutRequestInterceptor();
    }
}
