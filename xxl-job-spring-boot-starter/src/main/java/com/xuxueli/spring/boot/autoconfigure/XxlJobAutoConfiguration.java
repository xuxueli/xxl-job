package com.xuxueli.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job 自动配置
 *
 * @author Rong.Jia
 * @date 2023/01/11
 */
@ComponentScan("com.xuxueli.xxl.job")
@Configuration
@EnableConfigurationProperties({XxlJobProperties.class})
@ConditionalOnProperty(prefix = "xxl.job", name = "enabled", havingValue = "true")
public class XxlJobAutoConfiguration {

    private final XxlJobProperties xxlJobProperties;

    public XxlJobAutoConfiguration(XxlJobProperties xxlJobProperties) {
        this.xxlJobProperties = xxlJobProperties;
    }




}
