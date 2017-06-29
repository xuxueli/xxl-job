package com.xxl.job.executor.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: felix.
 * @createTime: 2017/6/29.
 */
@ConfigurationProperties(prefix = "xxl.job.config")
public class XxlJobProperties {

    private int port;
    private String appName;
    private String logPath;
    private String adminAddresses;
    private String ip;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
