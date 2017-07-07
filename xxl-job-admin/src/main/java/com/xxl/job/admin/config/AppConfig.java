package com.xxl.job.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Antergone
 * Date: 2017/6/29
 */
@Configuration
public class AppConfig {


    @Data
    @Configuration
    @ConfigurationProperties(prefix = "xxl.job.mail")
    public class EmailConfig{
        private String host;

        private Integer port = 25;

        private String username;

        private String password;

        private String from;

        private String nick;
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "xxl.job.login")
    public class LoginConfig {
        private String username;

        private String password;
    }
}
