package com.xxl.job.alarm.dingtalk;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Created on 2022/2/22.
 *
 * @author lan
 */
class DingtalkJobAlarmTest {

    @org.junit.jupiter.api.Test
    void doAlarm() throws IOException {
        Properties config = new Properties();
        config.put(DingtalkConstants.DINGTALK_KEYWORD, "#");
        config.put(DingtalkConstants.DINGTALK_WEBHOOK, "https://oapi.dingtalk.com/robot/send?access_token=xxx");
        config.put(DingtalkConstants.DINGTALK_AT_MOBILES, "159xxxx");

        boolean alarm = new DingtalkJobAlarm().doAlarm(config, readFile("alarm-dingtalk.ftl"));
        //Assertions.assertTrue(alarm);
        Assertions.assertFalse(alarm);
    }

    private String readFile(String filename) throws IOException {
        InputStream inputStream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename), "no such file");

        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int read = -1;

        while ((read = inputStream.read(buf)) != -1) {
            sb.append(new String(buf, 0, read));
        }

        return sb.toString();
    }
}
