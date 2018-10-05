package com.xxl.job.admin.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;

/**
 * @author linzehua
 */
@Configuration
public class BaseConfig {


    @Bean("freemarkerConfig")
    public FreeMarkerConfigurer freeMarkerConfigurer() throws IOException {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:webapp/WEB-INF/template/");

        //freemarker配置
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("freemarker.properties"));
        propertiesFactoryBean.afterPropertiesSet();

        freeMarkerConfigurer.setFreemarkerSettings(propertiesFactoryBean.getObject());

        return freeMarkerConfigurer;
    }
}
