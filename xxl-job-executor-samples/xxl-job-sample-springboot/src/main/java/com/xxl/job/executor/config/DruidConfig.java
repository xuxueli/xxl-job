package com.xxl.job.executor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.xa.DruidXADataSource;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
@ComponentScan(basePackages = "com.xxl.job.executor.service")
public class DruidConfig {
    private Logger logger = LoggerFactory.getLogger(DruidConfig.class);
    @Value("${job.db.driverName}")
    private String driverName;
    @Value("${job.db.url}")
	private String url;
	@Value("${job.db.username}")
	private String username;
	@Value("${job.db.password}")
	private String password;
	@Value("${job.db.max_pool_size}")
	private int max_pool_size=10;
	@Value("${job.db.init_pool_size}")
	private int init_pool_size=2;

    @Bean(name="connection",destroyMethod = "close")
    public DruidPooledConnection init() throws Exception {
        logger.info("-----database config init.-------");
        @SuppressWarnings("resource")
		DruidXADataSource dataSource = new DruidXADataSource();
		if(!StringUtils.isEmpty(driverName)){
			dataSource.setDriverClassName(driverName);
		}
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
//		配置初始化大小、最小、最大
		if(max_pool_size>0){
			dataSource.setMaxActive(max_pool_size);//最大连接池数量
			dataSource.setMinIdle(Double.valueOf(max_pool_size/5).intValue());
		}
		if(init_pool_size>0){
			dataSource.setInitialSize(init_pool_size);//初始化时建立物理连接的个数
		}
//		配置获取连接等待超时的时间
		dataSource.setMaxWait(600000l);
//		配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		dataSource.setTimeBetweenEvictionRunsMillis(600000l);
//		配置一个连接在池中最小生存的时间，单位是毫秒
		dataSource.setMinEvictableIdleTimeMillis(600000l);
		dataSource.setValidationQuery("SELECT 'x'");
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
//		打开PSCache，并且指定每个连接上PSCache的大小
		dataSource.setPoolPreparedStatements(false);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
//		配置监控统计拦截的filters
		dataSource.setFilters("stat,log4j");
//		打开removeAbandoned功能,如果连接超过30分钟未关闭，就会被强行回收，并且日志记录连接申请时输出错误日志
		dataSource.setRemoveAbandoned(true);//打开removeAbandoned功能
		dataSource.setRemoveAbandonedTimeout(1800);//1800秒，也就是30分钟
		dataSource.setLogAbandoned(true);//关闭abanded连接时输出错误日志
//		合并多个DruidDataSource的监控数据
		dataSource.setUseGlobalDataSourceStat(true);
//		dataSource.setConnectionProperties("autoReconnect=true");
		dataSource.init();
        return dataSource.getConnection();
    }

}