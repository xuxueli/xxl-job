package com.xxl.job.executor.core.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    private static final int CONNECT_TIMEOUT = 3 * 1000;
    private static final int READ_TIMEOUT = 5 * 1000;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.setConnectTimeout(CONNECT_TIMEOUT).setReadTimeout(READ_TIMEOUT).build();
    }
}
