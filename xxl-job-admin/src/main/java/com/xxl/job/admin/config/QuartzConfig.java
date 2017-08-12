package com.xxl.job.admin.config;

import groovy.util.logging.Log4j2;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;

/**
 * Quartz配置类
 *
 * @author: lvhao
 * @since: 2016-6-2 20:09
 */
@Configuration
@Log4j2
public class QuartzConfig {
    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    @Autowired
    private DataSource dataSource;
    
    @Value("${xxl.job.accessToken}")
    private String accessToken;
    
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AutowiringQuartzJobFactory autowiringQuartzJobFactory;

    @PostConstruct
    public void initDone() {
        log.info("Quartz init done...");
    }
    
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
    
    @Bean
    public SchedulerFactoryBean init(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setStartupDelay(20);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        try {
            schedulerFactoryBean.setQuartzProperties(quartzProperties());
        } catch (IOException e) {
            e.printStackTrace();
        }
        schedulerFactoryBean.setJobFactory(autowiringQuartzJobFactory);
        return schedulerFactoryBean;
    }

    @Bean
    public XxlJobDynamicScheduler initXxlJobDynamicScheduler(){
        XxlJobDynamicScheduler schedulerFactoryBean = new XxlJobDynamicScheduler();
        schedulerFactoryBean.setScheduler(init().getScheduler());
        schedulerFactoryBean.setAccessToken(accessToken);
        schedulerFactoryBean.setApplicationContext(applicationContext);
        try {
			schedulerFactoryBean.init();
		} catch (Exception e) {
			log.error("任务调度执行器初始化失败",e);
		}
        return schedulerFactoryBean;
    }
}
