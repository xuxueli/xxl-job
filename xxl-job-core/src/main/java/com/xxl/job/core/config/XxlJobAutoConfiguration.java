package com.xxl.job.core.config;

import com.xxl.job.core.endpoint.ExecuctorEndpoint;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({ExecuctorEndpoint.class})
@EnableConfigurationProperties({
        XxlJobProperties.class,
        ServerProperties.class
})
public class XxlJobAutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobSpringExecutor(ServerProperties serverProperties,
                                                     XxlJobProperties xxlJobProperties) {
        XxlJobProperties.AdminProperties admin = xxlJobProperties.getAdmin();
        XxlJobProperties.ExecutorProperties executor = xxlJobProperties.getExecutor();
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(executor.getAppname());
        xxlJobSpringExecutor.setAddress(executor.getAddress());
        xxlJobSpringExecutor.setIp(executor.getIp());
        xxlJobSpringExecutor.setPort(serverProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(admin.getAccessToken());
        xxlJobSpringExecutor.setLogPath(executor.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(executor.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

}
