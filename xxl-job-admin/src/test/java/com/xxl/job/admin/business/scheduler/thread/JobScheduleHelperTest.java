package com.xxl.job.admin.business.scheduler.thread;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.xxl.job.admin.business.constant.TriggerStatus;
import com.xxl.job.admin.business.model.XxlJobInfo;
import org.junit.jupiter.api.Test;

class JobScheduleHelperTest {

    @Test
    void preReadsSuccessfullyRefreshedJobWithSentinelStatus() {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setTriggerStatus(-1);
        jobInfo.setTriggerNextTime(10_000L);

        assertTrue(JobScheduleHelper.shouldPreReadNextTriggerTime(jobInfo, 6_000L));
    }

    @Test
    void doesNotPreReadJobStoppedByFailedRefresh() {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setTriggerStatus(TriggerStatus.STOPPED.getValue());
        jobInfo.setTriggerNextTime(10_000L);

        assertFalse(JobScheduleHelper.shouldPreReadNextTriggerTime(jobInfo, 6_000L));
    }

    @Test
    void doesNotPreReadTriggerOutsideWindow() {
        XxlJobInfo jobInfo = new XxlJobInfo();
        jobInfo.setTriggerStatus(-1);
        jobInfo.setTriggerNextTime(11_001L);

        assertFalse(JobScheduleHelper.shouldPreReadNextTriggerTime(jobInfo, 6_000L));
    }
}