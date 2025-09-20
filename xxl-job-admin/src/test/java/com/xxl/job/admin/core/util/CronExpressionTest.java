package com.xxl.job.admin.core.util;

import com.xxl.job.admin.scheduler.cron.CronExpression;
import com.xxl.job.core.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

public class CronExpressionTest {

    @Test
    public void shouldWriteValueAsString() throws ParseException {
        CronExpression cronExpression = new CronExpression("0 0 0 ? * 1");
        Date lastTriggerTime = new Date();
        for (int i = 0; i < 5; i++) {
            Date nextTriggerTime = cronExpression.getNextValidTimeAfter(lastTriggerTime);
            System.out.println(DateUtil.formatDateTime(nextTriggerTime));

            lastTriggerTime = nextTriggerTime;
        }
    }
}
