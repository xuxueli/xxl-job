package com.xxl.job.spring.boot.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job调度器 自动配置
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(XxlJobExecutorProperties.class)
@ComponentScan("com.xxl.job.executor")
@ConditionalOnProperty(prefix = "xxl.job.executor", name = "enabled", havingValue = "true")
public class XxlJobExecutorAutoConfiguration {























}
