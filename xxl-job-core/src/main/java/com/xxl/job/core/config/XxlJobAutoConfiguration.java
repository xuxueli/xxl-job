package com.xxl.job.core.config;

import com.xxl.job.core.endpoint.ExecuctorEndpoint;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({ExecuctorEndpoint.class})
@EnableConfigurationProperties({XxlJobProperties.class})
public class XxlJobAutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobSpringExecutor(XxlJobProperties prop) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(prop.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(prop.getExecutor().getAppname());
        xxlJobSpringExecutor.setAddress(prop.getExecutor().getAddress());
        xxlJobSpringExecutor.setIp(prop.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(prop.getExecutor().getPort());
        xxlJobSpringExecutor.setAccessToken(prop.getExecutor().getAccessToken());
        xxlJobSpringExecutor.setLogPath(prop.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(prop.getExecutor().getLogRetentionDays());
        xxlJobSpringExecutor.setIntegratedSpringBoot(prop.getExecutor().getIntegratedSpringBoot());
        return xxlJobSpringExecutor;
    }
}
