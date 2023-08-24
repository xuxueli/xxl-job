package com.xxl.job.admin.common.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 邮件配置
 *
 * @author Rong.Jia
 * @date 2023/08/24
 */
@Configuration
@ConditionalOnClass({EmailProperties.class})
@EnableConfigurationProperties({EmailProperties.class})
public class EmailConfiguration {

    private final EmailProperties emailProperties;

    public EmailConfiguration(EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    @Bean
    public EmailFactoryBean emailFactoryBean() {
        return new EmailFactoryBean(emailProperties);
    }






















}
