package com.xxl.job.executor.core.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.properties.XxlJobProperties;
import com.xxl.job.core.server.SpringServer;
import com.xxl.job.core.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Autowired
    private XxlJobProperties xxlJobProperties;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");

        if (xxlJobProperties.getIp() == null || xxlJobProperties.getIp().isEmpty()) {
            xxlJobProperties.setIp(IpUtil.getIp());
        }
        if (xxlJobProperties.getAddress() == null || xxlJobProperties.getAddress().isEmpty()) {
            String ipPortAddress = IpUtil.getIpPort(xxlJobProperties.getIp(), xxlJobProperties.getPort());
            xxlJobProperties.setAddress("http://{ip_port}/xxlrpc/".replace("{ip_port}", ipPortAddress));
        }
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAbstractServer(new SpringServer());
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdminAddress());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getAppname());
        xxlJobSpringExecutor.setAddress(xxlJobProperties.getAddress());
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccesstoken());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogpath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogretentiondays());
        return xxlJobSpringExecutor;
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