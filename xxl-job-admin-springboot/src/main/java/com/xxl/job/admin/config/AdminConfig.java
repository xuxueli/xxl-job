package com.xxl.job.admin.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author linzehua
 */
@Configuration
public class AdminConfig {
    @Bean("propertyConfigurer")
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setFileEncoding("utf-8");
        propertyPlaceholderConfigurer.setLocation(new ClassPathResource("xxl-job-admin.properties"));
        return propertyPlaceholderConfigurer;
    }
    //********************************* part 1 :for datasource *********************************
//    @Value("${xxl.job.db.driverClass}")
//    private String driverClass;
//    @Value("${xxl.job.db.url}")
//    private String jdbcUrl;
//    @Value("${xxl.job.db.user}")
//    private String user;
//    @Value("${xxl.job.db.password}")
//    private String password;

    @Bean("dataSource")
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        Properties properties = getProperties();
        dataSource.setDriverClass(properties.getProperty("xxl.job.db.driverClass"));
        dataSource.setJdbcUrl(properties.getProperty("xxl.job.db.url"));
        dataSource.setUser(properties.getProperty("xxl.job.db.user"));
        dataSource.setPassword(properties.getProperty("xxl.job.db.password"));
        dataSource.setInitialPoolSize(3);
        dataSource.setMinPoolSize(2);
        dataSource.setMinPoolSize(10);
        dataSource.setMaxIdleTime(60);
        dataSource.setAcquireRetryDelay(1000);
        dataSource.setAcquireRetryAttempts(10);
        dataSource.setPreferredTestQuery("SELECT 1");
        return dataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Autowired DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        Resource[] mapperLocations = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mybatis-mapper/*.xml");
        sqlSessionFactoryBean.setMapperLocations(mapperLocations);
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.xxl.job.admin.dao");
        return mapperScannerConfigurer;
    }

    //********************************* part 2 :for tx *********************************

    @Bean("transactionManager")
    public DataSourceTransactionManager transactionManager(@Autowired DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    //********************************* part 3 :for xxl-job scheduler *********************************

    @Bean("quartzScheduler")
    public SchedulerFactoryBean schedulerFactoryBean(@Autowired DataSource dataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setStartupDelay(20);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        return schedulerFactoryBean;
    }

//    @Bean("quartzScheduler")
//    public Scheduler quartzScheduler(@Autowired DataSource dataSource,@Autowired SchedulerFactoryBean schedulerFactoryBean){
////        SchedulerFactoryBean schedulerFactoryBean = schedulerFactoryBean(dataSource);
//        return schedulerFactoryBean.getObject();
//    }


//
//    @Value("${xxl.job.accessToken}")
//    private String accessToken;

    @Bean(name = "xxlJobDynamicScheduler", initMethod = "init", destroyMethod = "destroy")
    public XxlJobDynamicScheduler xxlJobDynamicScheduler(@Qualifier("quartzScheduler") Scheduler quartzScheduler) throws Exception {
        XxlJobDynamicScheduler xxlJobDynamicScheduler = new XxlJobDynamicScheduler();
        Properties properties = getProperties();
        xxlJobDynamicScheduler.setScheduler(quartzScheduler);
        xxlJobDynamicScheduler.setAccessToken(properties.getProperty("xxl.job.accessToken"));
        return xxlJobDynamicScheduler;
    }

    private static Properties properties;

    private Properties getProperties() throws Exception {
        if (properties == null) {
            ClassPathResource classPathResource = new ClassPathResource("xxl-job-admin.properties");
            properties = new Properties();
            PropertiesLoaderUtils.fillProperties(properties, classPathResource);
        }

        return properties;
    }
}
