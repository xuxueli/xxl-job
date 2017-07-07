package com.xxl.job.spring.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Author: Antergone
 * Date: 2017/6/30
 */
@Data
@ConfigurationProperties(prefix = XxlJobProperties.XXL_SERVER_PREFIX)
public class XxlJobProperties {

    public static final String XXL_SERVER_PREFIX = "xxl.server";

    private String ip = "";
    private int port = 9999;
    private String appName;
    private String addresses;
    private String logPath;
}
