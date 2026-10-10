package com.xxl.job.admin.business.schedule;

import com.xxl.job.admin.business.model.XxlJobInfo;
import com.xxl.job.admin.business.scheduler.type.strategy.CronScheduleType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CronScheduleTypeTest {

    @Test
    void calculatesCronInConfiguredTimezoneAcrossDaylightSavingChanges() throws Exception {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setScheduleConf("0 0 9 * * ?");
        jobInfo.setScheduleTimezone("America/New_York");

        CronScheduleType scheduleType = new CronScheduleType();

        Date winterTrigger = scheduleType.generateNextTriggerTime(
                jobInfo, Date.from(Instant.parse("2026-01-15T00:00:00Z")));
        Date summerTrigger = scheduleType.generateNextTriggerTime(
                jobInfo, Date.from(Instant.parse("2026-07-15T00:00:00Z")));

        assertEquals(Instant.parse("2026-01-15T14:00:00Z"), winterTrigger.toInstant());
        assertEquals(Instant.parse("2026-07-15T13:00:00Z"), summerTrigger.toInstant());
    }

    @Test
    void usesSchedulerDefaultTimezoneForExistingJobsWithoutTimezone() throws Exception {
        TimeZone originalTimezone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
            XxlJobInfo jobInfo = new XxlJobInfo();
            jobInfo.setScheduleConf("0 0 9 * * ?");

            Date trigger = new CronScheduleType().generateNextTriggerTime(
                    jobInfo, Date.from(Instant.parse("2026-01-15T00:00:00Z")));

            assertEquals(Instant.parse("2026-01-16T00:00:00Z"), trigger.toInstant());
        } finally {
            TimeZone.setDefault(originalTimezone);
        }
    }
}
