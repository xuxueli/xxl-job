package com.xxl.job.autoconfigure;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linyunlong
 */
@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
@ConditionalOnProperty(value = "xxl.job.enabled", havingValue = "true", matchIfMissing = true)
public class XxlJobAutoConfiguration {
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties properties) {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(properties.getAdminAddress());
        executor.setAccessToken(properties.getAccessToken());

        executor.setAppname(properties.getAppName());
        executor.setAddress(properties.getAddress());
        executor.setIp(properties.getIp());
        executor.setPort(properties.getPort());
        executor.setLogPath(properties.getLogPath());
        executor.setLogRetentionDays(properties.getLogRetentionDays());

        return executor;
    }
}
