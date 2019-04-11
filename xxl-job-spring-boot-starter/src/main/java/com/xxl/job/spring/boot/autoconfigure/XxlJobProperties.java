package com.xxl.job.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yangyanju
 * @version 1.0
 * @date 2019-04-11
 */
@ConfigurationProperties("xxl.job")
public class XxlJobProperties {

    private Boolean enabled = false;
    private String accessToken;
    private AdminProperties admin;
    private ExecutorProperties executor;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        if (enabled != null) {
            this.enabled = enabled;
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        if (accessToken != null && accessToken.trim().length() > 0) {
            this.accessToken = accessToken;
        }
    }

    public AdminProperties getAdmin() {
        return admin;
    }

    public void setAdmin(AdminProperties admin) {
        this.admin = admin;
    }

    public ExecutorProperties getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorProperties executor) {
        this.executor = executor;
    }

    /**
     * AdminProperties
     */
    public static class AdminProperties {

        private String addresses;

        public String getAddresses() {
            return addresses;
        }

        public void setAddresses(String addresses) {
            this.addresses = addresses;
        }
    }

    /**
     * ExecutorProperties
     */
    public static class ExecutorProperties {

        private static final int MAX_PORT = 65535;

        private String appName;
        private String logPath;
        private String ip;
        private Integer port = 10019;
        private Integer logRetentionDays = -1;

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

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            if (ip != null && ip.trim().length() > 0) {
                this.ip = ip;
            }
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            if (port != null && port > 0 && port <= MAX_PORT) {
                this.port = port;
            }
        }

        public Integer getLogRetentionDays() {
            return logRetentionDays;
        }

        public void setLogRetentionDays(Integer logRetentionDays) {
            if (logRetentionDays != null && logRetentionDays >= 0) {
                this.logRetentionDays = logRetentionDays;
            }
        }
    }
}
