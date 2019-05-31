package com.xuxueli.job.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Luo Bao Ding
 * @since 2019/5/18
 */
@ConfigurationProperties("xxl.job")
public class XxlJobProperties {
    public static final String DEFAULT_LOG_PATH = "./logs/applogs/xxl-job/jobhandler";

    private String serverAddresses = "http://localhost:8080/xxl-job-admin";

    private String clientAppName;

    private String clientIp;

    private int clientPort;

    private String accessToken = "";

    private String logPath = DEFAULT_LOG_PATH;

    private int logRetentionDays = 4;

    public String getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(String serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public String getClientAppName() {
        return clientAppName;
    }

    public void setClientAppName(String clientAppName) {
        this.clientAppName = clientAppName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }
}
