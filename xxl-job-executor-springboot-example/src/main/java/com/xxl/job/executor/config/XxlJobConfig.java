package com.xxl.job.executor.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.executor.properties.XxlJobProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "com.xxl.job.executor.service.jobhandler")
public class XxlJobConfig {

    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Autowired
    XxlJobProperties properties;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor() {

        logger.error("------------ xxlJobExecutor -----------");
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setPort(properties.getPort());
        xxlJobExecutor.setAppName(properties.getAppName());
        xxlJobExecutor.setLogPath(properties.getLogPath());
        xxlJobExecutor.setAdminAddresses(properties.getAdminAddresses());
        xxlJobExecutor.setIp(properties.getIp());
        return xxlJobExecutor;
    }
}