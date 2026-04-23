package com.xxl.job.admin.schedule;

import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.type.strategy.CronScheduleType;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

public class CronScheduleTypeTest {

    private final CronScheduleType cronScheduleType = new CronScheduleType();

    @Test
    public void testGenerateNextTriggerTime_noTimeZone() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 0 8 * * ?");
        jobInfo.setScheduleTimeZone(null);

        Date fromTime = new Date();
        Date result = cronScheduleType.generateNextTriggerTime(jobInfo, fromTime);

        assertNotNull(result);
        assertTrue(result.after(fromTime));
    }

    @Test
    public void testGenerateNextTriggerTime_emptyTimeZone() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 0 8 * * ?");
        jobInfo.setScheduleTimeZone("");

        Date fromTime = new Date();
        Date result = cronScheduleType.generateNextTriggerTime(jobInfo, fromTime);

        assertNotNull(result);
        assertTrue(result.after(fromTime));
    }

    @Test
    public void testGenerateNextTriggerTime_withTimeZone_shanghai() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 0 8 * * ?");
        jobInfo.setScheduleTimeZone("Asia/Shanghai");

        Date fromTime = new Date();
        Date result = cronScheduleType.generateNextTriggerTime(jobInfo, fromTime);

        assertNotNull(result);
        assertTrue(result.after(fromTime));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String timeStr = sdf.format(result);
        assertTrue(timeStr.startsWith("08:00:00"), "Expected 08:00:00 in Asia/Shanghai, got " + timeStr);
    }

    @Test
    public void testGenerateNextTriggerTime_withTimeZone_moscow() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 0 8 * * ?");
        jobInfo.setScheduleTimeZone("Europe/Moscow");

        Date fromTime = new Date();
        Date result = cronScheduleType.generateNextTriggerTime(jobInfo, fromTime);

        assertNotNull(result);
        assertTrue(result.after(fromTime));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        String timeStr = sdf.format(result);
        assertTrue(timeStr.startsWith("08:00:00"), "Expected 08:00:00 in Europe/Moscow, got " + timeStr);
    }

    @Test
    public void testDifferentTimeZones_produceDifferentResults() throws Exception {
        XxlJobInfo jobInfoShanghai = new XxlJobInfo();
        jobInfoShanghai.setScheduleConf("0 0 8 * * ?");
        jobInfoShanghai.setScheduleTimeZone("Asia/Shanghai");

        XxlJobInfo jobInfoMoscow = new XxlJobInfo();
        jobInfoMoscow.setScheduleConf("0 0 8 * * ?");
        jobInfoMoscow.setScheduleTimeZone("Europe/Moscow");

        Date fromTime = new Date();
        Date resultShanghai = cronScheduleType.generateNextTriggerTime(jobInfoShanghai, fromTime);
        Date resultMoscow = cronScheduleType.generateNextTriggerTime(jobInfoMoscow, fromTime);

        assertNotNull(resultShanghai);
        assertNotNull(resultMoscow);

        assertNotEquals(resultShanghai.getTime(), resultMoscow.getTime(),
                "Same cron expression with different time zones should produce different absolute trigger times");
    }

    @Test
    public void testUtcTimeZone() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 30 14 * * ?");
        jobInfo.setScheduleTimeZone("UTC");

        Date fromTime = new Date();
        Date result = cronScheduleType.generateNextTriggerTime(jobInfo, fromTime);

        assertNotNull(result);
        assertTrue(result.after(fromTime));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timeStr = sdf.format(result);
        assertTrue(timeStr.startsWith("14:30:00"), "Expected 14:30:00 in UTC, got " + timeStr);
    }

    @Test
    public void testMultipleTriggers_sameTimeZone() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 0/30 * * * ?");
        jobInfo.setScheduleTimeZone("Asia/Tokyo");

        Date lastTime = new Date();
        for (int i = 0; i < 5; i++) {
            Date nextTime = cronScheduleType.generateNextTriggerTime(jobInfo, lastTime);
            assertNotNull(nextTime);
            assertTrue(nextTime.after(lastTime));
            lastTime = nextTime;
        }
    }
}
