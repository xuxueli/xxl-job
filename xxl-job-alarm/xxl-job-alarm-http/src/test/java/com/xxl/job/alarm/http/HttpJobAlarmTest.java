package com.xxl.job.alarm.http;

import com.xxl.job.alarm.AlarmConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
class HttpJobAlarmTest {

    @Test
    void doAlarm() {
        Properties config = new Properties();
        config.put(HttpConstants.HTTP_HEADERS, "token=123,a=b,c=d");
        config.put(AlarmConstants.ALARM_TARGET, "http://localhost:8080/hello");

        boolean alarm = new HttpJobAlarm().doAlarm(config, "hello baby");
        //Assertions.assertTrue(alarm);
        Assertions.assertFalse(alarm);
    }
}
