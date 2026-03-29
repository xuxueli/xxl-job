package com.xxl.job.admin.scheduler.type.strategy;

import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.type.ScheduleType;
import com.xxl.tool.core.DateTool;

import java.util.Date;

public class FixRateScheduleType extends ScheduleType {

    @Override
    public Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception {
        // generate next trigger time, fix rate delay
        Date nextTriggerTime = new Date(jobInfo.getTriggerLastTime() + Long.parseLong(jobInfo.getScheduleConf()) * 1000L);
        if (nextTriggerTime.after(fromTime)) {
            // fix rate delay, after last-trigger-time
            return nextTriggerTime;
        } else {
            // fix rate delay, after fromTime (clear ms, second +1)
            fromTime = DateTool.setMilliseconds(DateTool.addSeconds(fromTime, 1), 0);
            return new Date(fromTime.getTime() + Long.parseLong(jobInfo.getScheduleConf()) * 1000L);
        }
    }

}
