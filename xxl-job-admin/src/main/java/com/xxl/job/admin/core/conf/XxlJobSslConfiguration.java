package com.xxl.job.admin.core.conf;

import com.xxl.job.core.ssl.SslConfig;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;

@Configuration
public class XxlJobSslConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "xxl.job.executor.ssl")
    public SslConfig sslConfig() {
        return new SslConfig();
    }

    @Bean
    SslConfigInitializer sslConfigInitializer(SslConfig sslConfig) throws SSLException {
        return new SslConfigInitializer(sslConfig);
    }

    static class SslConfigInitializer {

        public SslConfigInitializer(SslConfig ssl) throws SSLException {
            // 初始化静态变量
            XxlJobRemotingUtil.init(ssl);
        }
    }
}
