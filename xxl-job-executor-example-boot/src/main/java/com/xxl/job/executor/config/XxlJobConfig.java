package com.xxl.job.executor.config;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.registry.impl.DbRegistHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


@Configuration
//@ImportResource("classpath:applicationcontext-xxl-job.xml")
public class XxlJobConfig {

    @Value("${xxl.job.executor.ip}")
    private String ip;
    @Value("${xxl.job.executor.port}")
    private Integer port;
    @Value("${xxl.job.executor.appname}")
    private String appName;
    @Value("${xxl.job.db.driverClass}")
    private String driverClass;
    @Value("${xxl.job.db.url}")
    private String jdbcUrl;
    @Value("${xxl.job.db.user}")
    private String user;
    @Value("${xxl.job.db.password}")
    private String password;

    @Bean(destroyMethod = "close", name = "xxlJobDataSource")
    public ComboPooledDataSource getComboPooledDataSource() throws Exception {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(driverClass);
        comboPooledDataSource.setJdbcUrl(jdbcUrl);
        comboPooledDataSource.setUser(user);
        comboPooledDataSource.setPassword(password);
        comboPooledDataSource.setInitialPoolSize(3);
        comboPooledDataSource.setMinPoolSize(2);
        comboPooledDataSource.setMaxPoolSize(10);
        comboPooledDataSource.setMaxIdleTime(60);
        comboPooledDataSource.setAcquireRetryDelay(1000);
        comboPooledDataSource.setAcquireRetryAttempts(10);
        comboPooledDataSource.setPreferredTestQuery("SELECT 1");
        return comboPooledDataSource;
    }

    @Bean
    public DbRegistHelper getDbRegistHelper() throws Exception {
        DbRegistHelper dbRegistHelper = new DbRegistHelper();
        dbRegistHelper.setDataSource(getComboPooledDataSource());
        return dbRegistHelper;
    }

    @Bean(initMethod = "start", destroyMethod = "destroy", name = "xxlJobExecutor")
    public XxlJobExecutor getBeetlGroupUtilConfiguration() throws Exception {
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setIp(ip);
        xxlJobExecutor.setPort(port);
        xxlJobExecutor.setAppName(appName);
        xxlJobExecutor.setRegistHelper(getDbRegistHelper());
        return xxlJobExecutor;
    }

}