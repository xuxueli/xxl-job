package com.xxl.job.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {
    private boolean enabled;

    private Admin admin;

    private String accessToken;

    private Executor executor;

    public static class Admin {
        private String addresses; // xxl.job.admin.addresses

        public Admin() {
        }

        public Admin(String addresses) {
            this.addresses = addresses;
        }

        public String getAddresses() {
            return addresses;
        }

        public void setAddresses(String addresses) {
            this.addresses = addresses;
        }
    }

    public static class Executor {
        private String appname; // xxl.job.executor.app-name
        private String address;
        private String ip;
        private Integer port;
        private String logPath;
        private int logretentiondays;


        public Executor() {
        }

        public Executor(String appname, String address, String ip, Integer port, String logPath, int logretentiondays) {
            this.appname = appname;
            this.address = address;
            this.ip = ip;
            this.port = port;
            this.logPath = logPath;
            this.logretentiondays = logretentiondays;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getLogPath() {
            return logPath;
        }

        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }

        public int getLogretentiondays() {
            return logretentiondays;
        }

        public void setLogretentiondays(int logretentiondays) {
            this.logretentiondays = logretentiondays;
        }
    }

    public XxlJobProperties() {
    }


    public XxlJobProperties(boolean enabled, Admin admin, String accessToken, Executor executor) {
        this.enabled = enabled;
        this.admin = admin;
        this.accessToken = accessToken;
        this.executor = executor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
