package com.xxl.job.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {
    private AdminProperties admin = new AdminProperties();
    private ExecutorProperties executor = new ExecutorProperties();

    @Data
    static public class AdminProperties {
        private String addresses;
    }

    @Data
    static public class ExecutorProperties {
        private String appname;
        private String ip;
        private Integer port = 9999;
        private String accessToken;
        private String logPath;
        private Integer logRetentionDays = 30;
        private boolean integratedSpringBoot;
        private String address;
    }
}
