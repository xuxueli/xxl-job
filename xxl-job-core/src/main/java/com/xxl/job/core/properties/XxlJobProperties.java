package com.xxl.job.core.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author SongLongKuan
 * @time 2021/2/22 10:47 上午
 */
@Component
public class XxlJobProperties {
    @Value("${server.port}")
    private Integer port;

    @Value("${xxl.job.admin.addresses:}")
    private String adminAddress;


    @Value("${xxl.job.accessToken}")
    private String accesstoken;

    @Value("${xxl.job.executor.appname}")
    private String appname;


    @Value("${xxl.job.executor.address:}")
    private String address;

    @Value("${xxl.job.executor.ip}")
    private String ip;


    @Value("${xxl.job.executor.logpath}")
    private String logpath;

    @Value("${xxl.job.executor.logretentiondays}")
    private Integer logretentiondays;


    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public void setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
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

    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }

    public Integer getLogretentiondays() {
        return logretentiondays;
    }

    public void setLogretentiondays(Integer logretentiondays) {
        this.logretentiondays = logretentiondays;
    }
}
