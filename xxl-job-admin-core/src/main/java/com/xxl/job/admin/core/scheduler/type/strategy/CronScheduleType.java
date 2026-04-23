package com.xxl.job.admin.core.scheduler.type.strategy;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.scheduler.cron.CronExpression;
import com.xxl.job.admin.core.scheduler.type.ScheduleType;

import java.util.Date;

public class CronScheduleType extends ScheduleType {

    @Override
    public Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception {
        // generate next trigger time, with cron
        return new CronExpression(jobInfo.getScheduleConf()).getNextValidTimeAfter(fromTime);
    }

}