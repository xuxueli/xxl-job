package com.xxl.job.admin.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MachineUtils {

    private static final Logger logger = LoggerFactory.getLogger(MachineUtils.class);

    private static String machineIp = null;

    public static String get() {
        if(machineIp == null){
            try {
                //获取本机IP地址
                machineIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.error("get host error：",e);
                throw new RuntimeException("get host error");
            }
        }
        return machineIp;
    }
}
