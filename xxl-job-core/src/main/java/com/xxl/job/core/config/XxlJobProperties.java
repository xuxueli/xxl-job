package com.xxl.job.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {
    private AdminProperties admin = new AdminProperties();
    private ExecutorProperties executor = new ExecutorProperties();

    @Getter
    @Setter
    public static class AdminProperties {
        private String addresses;
        private String accessToken;
    }

    @Getter
    @Setter
    public static class ExecutorProperties {
        private String appname;
        private String ip;
        private String logPath;
        private Integer logRetentionDays = 30;
        private Boolean integratedSpringBoot;
        private String address;
    }
}
