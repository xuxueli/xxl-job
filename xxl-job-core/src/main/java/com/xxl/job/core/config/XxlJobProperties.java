package com.xxl.job.core.config;

import lombok.Data;


/**
 * Author: Antergone
 * Date: 2017/6/30
 */
@Data
public class XxlJobProperties {

    private String ip;
    private int port = 9999;
    private String appName;
    private String addresses;
    private String logPath;
}
