package com.xxl.job.executor.core.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 * @author xuxueli 2017-04-28
 */
@Configuration
public class XxlJobConfig {
    private static Logger log = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.admin.addresses}")
    private String addresses;

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.pool.core}")
    private int poolCore;

    @Value("${xxl.job.pool.max}")
    private int poolMax;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    @Bean
    public XxlJobExecutor xxlJobExecutor() {
        XxlJobExecutor exec = new XxlJobSpringExecutor();
        exec.setIp(ip).setPort(port).setAppName(appName)
                .setAccessToken(accessToken).setAdminAddresses(addresses)
                .setPoolCore(poolCore).setPoolMax(poolMax)
                .setLogPath(logPath).setLogRetentionDays(logRetentionDays);
        log.info(">>>>>>>>>>> xxl-job config init: " + exec);
        return exec;
    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     *
     *      1、引入依赖：
     *          <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-commons</artifactId>
     *             <version>${version}</version>
     *         </dependency>
     *
     *      2、配置文件，或者容器启动变量
     *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     *
     *      3、获取IP
     *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */
}
