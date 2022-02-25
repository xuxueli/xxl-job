package com.xxl.job.alarm.http;

import com.xxl.job.alarm.JobAlarm;
import com.xxl.job.alarm.SPI;

import java.util.Properties;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
@SPI("http")
public class HttpJobAlarm implements JobAlarm {
    @Override
    public boolean doAlarm(Properties config, String message) {
        return new HttpSender(config).sendMsg(message);
    }
}
