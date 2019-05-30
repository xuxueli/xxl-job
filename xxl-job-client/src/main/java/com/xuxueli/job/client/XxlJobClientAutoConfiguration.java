package com.xuxueli.job.client;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Luo Bao Ding
 * @since 2019/5/23
 */
@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
@Import({UtilAutoConfiguration.class})
public class XxlJobClientAutoConfiguration {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor(InetUtils inetUtils, XxlJobProperties xxlJobProperties) {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getServerAddresses());
        xxlJobSpringExecutor.setAppName(xxlJobProperties.getClientAppName());
        String ip = xxlJobProperties.getClientIp();
        if (ip == null || ip.equals("")) {
            ip = inetUtils.findFirstNonLoopbackAddress().getHostAddress();
        }
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(xxlJobProperties.getClientPort());
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());

        return xxlJobSpringExecutor;
    }

    @Bean
    public XxlJobClient xxlJobClient(XxlJobProperties xxlJobProperties) {
        return new XxlJobClientImpl(xxlJobProperties);
    }
}
