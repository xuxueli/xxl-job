package com.xxl.job.core.config;

import com.xxl.job.core.biz.AdminAddressesResolver;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.DefaultAdminAddressesResolver;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.endpoint.ExecutorEndpoint;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration(proxyBeanMethods = false)
@Import({ExecutorEndpoint.class})
@EnableConfigurationProperties({
        XxlJobProperties.class,
        ServerProperties.class
})
public class XxlJobAutoConfiguration {

    @Bean
    public ExecutorBiz executorBiz() {
        return new ExecutorBizImpl();
    }

    @Bean
    public ExecutorEndpoint executorEndpoint(ExecutorBiz executorBiz) {
        return new ExecutorEndpoint(executorBiz);
    }

    @Bean
    public XxlJobSpringExecutor xxlJobSpringExecutor(ServerProperties serverProperties,
                                                     XxlJobProperties xxlJobProperties,
                                                     ObjectProvider<AdminAddressesResolver> objectProvider) {
        XxlJobProperties.AdminProperties admin = xxlJobProperties.getAdmin();
        String adminAddresses = admin.getAddresses();
        XxlJobProperties.ExecutorProperties executor = xxlJobProperties.getExecutor();
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        AdminAddressesResolver addressesResolver = objectProvider.getIfAvailable(() -> new DefaultAdminAddressesResolver(adminAddresses));
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAddressesResolver(addressesResolver);
        xxlJobSpringExecutor.setAppname(executor.getAppName());
        xxlJobSpringExecutor.setAddress(executor.getAddress());
        xxlJobSpringExecutor.setIp(executor.getIp());
        xxlJobSpringExecutor.setPort(serverProperties.getPort());
        xxlJobSpringExecutor.setAccessToken(admin.getAccessToken());
        xxlJobSpringExecutor.setLogPath(executor.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(executor.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

}
