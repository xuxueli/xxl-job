package com.xxl.job.spring.boot.autoconfigure;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@ConditionalOnClass({XxlJobSpringExecutor.class})
@EnableConfigurationProperties(XxlJobProperties.class)
@ConditionalOnProperty(name = "xxl.job.executor.enabled")
public class XxlJobSpringBootAutoConfiguration {
    private final XxlJobProperties xxlJobProperties;
    private Logger logger = LoggerFactory.getLogger(XxlJobSpringBootAutoConfiguration.class);

    public XxlJobSpringBootAutoConfiguration(XxlJobProperties xxlJobProperties) {
        this.xxlJobProperties = xxlJobProperties;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getExecutor().getAppname());
        if (Objects.nonNull(xxlJobProperties.getExecutor().getAddress()) && !xxlJobProperties.getExecutor().getAddress().isEmpty()) {
            xxlJobSpringExecutor.setAddress(xxlJobProperties.getExecutor().getAddress());
        }
        if (Objects.nonNull(xxlJobProperties.getExecutor().getIp()) && !xxlJobProperties.getExecutor().getIp().isEmpty()) {
            xxlJobSpringExecutor.setIp(xxlJobProperties.getExecutor().getIp());
        }
        if (Objects.nonNull(xxlJobProperties.getExecutor().getPort()) && xxlJobProperties.getExecutor().getPort() > 0) {
            xxlJobSpringExecutor.setPort(xxlJobProperties.getExecutor().getPort());
        }
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getExecutor().getLogretentiondays());
        return xxlJobSpringExecutor;
    }
}
