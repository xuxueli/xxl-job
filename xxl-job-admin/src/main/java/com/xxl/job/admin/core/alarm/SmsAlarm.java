package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

/**
 * @author greenman0007
 * @time 2019/10/14 14:48
 */
public class SmsAlarm implements IAlarm {

    @Override
    public AlarmWay getAlarmWay() {
        return AlarmWay.SMS;
    }

    @Override
    public boolean failAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        return false;
    }
}
