package com.xxl.job.alarm.dingtalk;


import com.xxl.job.alarm.JobAlarm;
import com.xxl.job.alarm.SPI;

import java.util.Properties;

/**
 * Created on 2022/2/22.
 *
 * @author lan
 */
@SPI("dingtalk")
public class DingtalkJobAlarm implements JobAlarm {


    @Override
    public boolean doAlarm(Properties config, String message) {
        return new DingtalkSender(config).sendMsg(message);
    }
}
