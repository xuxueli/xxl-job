package com.xxl.job.admin.scheduler.type.strategy;

import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.cron.CronExpression;
import com.xxl.job.admin.scheduler.type.ScheduleType;

import java.util.Date;
import java.util.TimeZone;

public class CronScheduleType extends ScheduleType {

    @Override
    public Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception {
        CronExpression cronExpression = new CronExpression(jobInfo.getScheduleConf());
        if (jobInfo.getScheduleTimeZone() != null && !jobInfo.getScheduleTimeZone().isEmpty()) {
            cronExpression.setTimeZone(TimeZone.getTimeZone(jobInfo.getScheduleTimeZone()));
        }
        return cronExpression.getNextValidTimeAfter(fromTime);
    }

}
