package com.xxl.job.spring.boot.autoconfigure;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.spring.handler.ISpringJobHandler;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Map;

@Configuration
@ConditionalOnClass(XxlJobExecutor.class)
@EnableConfigurationProperties(XxlJobProperties.class)
@Order
public class XxlJobAutoConfiguration {

    private final XxlJobProperties properties;

    public XxlJobAutoConfiguration(XxlJobProperties properties) {
        this.properties = properties;
    }


    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor(ApplicationContext context) {

        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();

        Map<String, IJobHandler> serviceBeanMap = context.getBeansOfType(IJobHandler.class);

        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (IJobHandler handler : serviceBeanMap.values()) {
                if (handler.getClass().isAnnotationPresent(JobHander.class)) {
                    String name = handler.getClass().getAnnotation(JobHander.class).value();
                    if (handler instanceof ISpringJobHandler) {
                        factory.autowireBean(handler);
                    }
                    XxlJobExecutor.registJobHandler(name, handler);
                }
            }
        }

        GlueFactory.init(factory);
        return new XxlJobExecutor(
                this.properties.getIp(),
                this.properties.getPort(),
                this.properties.getAppName(),
                this.properties.getAddresses(),
                this.properties.getLogPath());
    }
}