package com.xxl.job.admin.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 托管QuartzJobFactoryBean到Context
 *
 * @author: lvhao
 * @since: 2016-6-23 15:21
 */
@Component
public class AutowiringQuartzJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    // 使用该beanFactory将context外的bean纳入管理
    private transient AutowireCapableBeanFactory beanFactory;

    // ApplicationContextAware 接口需要实现的方法
    // 在AutowiringSpringBeanJobFactory注册到Spring Context后
    // Spring会自动调用该方法
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    public Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }

    @Override
    public void setIgnoredUnknownProperties(String... ignoredUnknownProperties) {
        List<String> ignoreList = Arrays.asList(ignoredUnknownProperties);
        ignoreList.add("applicationContext");
        super.setIgnoredUnknownProperties(ignoreList.stream().toArray(String[]::new));
    }
}