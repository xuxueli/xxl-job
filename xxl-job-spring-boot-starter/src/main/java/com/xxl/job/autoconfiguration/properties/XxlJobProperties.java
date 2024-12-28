package com.xxl.job.autoconfiguration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Ice2Faith
 * @date 2024/6/26 17:41
 * @desc
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    private AdminProperties admin;

    private String accessToken;

    private ExecutorProperties executor;

    @Data
    public static class AdminProperties {
        private String addresses;
    }

    @Data
    public static class ExecutorProperties {

        private String appname;

        private String address;

        private String ip;

        private int port;

        private String logPath;

        private int logRetentionDays;
    }
}
