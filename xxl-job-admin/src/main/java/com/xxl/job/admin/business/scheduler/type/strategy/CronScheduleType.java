package com.xxl.job.admin.business.scheduler.type.strategy;

import com.xxl.job.admin.business.model.XxlJobInfo;
import com.xxl.job.admin.business.scheduler.cron.CronExpression;
import com.xxl.job.admin.business.scheduler.type.ScheduleType;
import com.xxl.job.admin.business.scheduler.util.ScheduleTimezoneUtil;

import java.util.Date;

public class CronScheduleType extends ScheduleType {

    @Override
    public Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception {
        // generate next trigger time, with cron
        CronExpression cronExpression = new CronExpression(jobInfo.getScheduleConf());
        cronExpression.setTimeZone(ScheduleTimezoneUtil.resolveTimezone(jobInfo.getScheduleTimezone()));
        return cronExpression.getNextValidTimeAfter(fromTime);
    }

}
