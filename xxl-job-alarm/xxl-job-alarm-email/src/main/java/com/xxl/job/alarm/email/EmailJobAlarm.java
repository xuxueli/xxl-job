package com.xxl.job.alarm.email;

import com.xxl.job.alarm.JobAlarm;
import com.xxl.job.alarm.SPI;

import java.util.Properties;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
@SPI("email")
public class EmailJobAlarm implements JobAlarm {
    @Override
    public boolean doAlarm(Properties config, String message) {
        return new EmailSender(config).sendMsg(message);
    }
}
