package com.xxl.job.autoconfigure;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author linyunlong
 */
@ConfigurationProperties("xxl.job")
@Data
@Slf4j
public class XxlJobProperties {
    /**
     * xxl-job后台地址
     */
    private String adminAddress;
    /**
     * accessToken，与xxl-job后台配置保持一致
     */
    private String accessToken;
    /**
     * 本执行器名称
     */
    private String appName;
    /**
     * 本执行器地址
     */
    private String address;
    /**
     * 本执行器IP
     */
    private String ip;
    /**
     * 本执行器端口
     */
    private int port;
    /**
     * 日志路径
     */
    private String logPath;
    /**
     * 日志存储天数
     */
    private int logRetentionDays = 7;
}
