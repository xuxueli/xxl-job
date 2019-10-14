package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

/**
 * @author greenman0007
 * @time 2019/10/14 14:55
 */
public class DingDingAlarm implements IAlarm {

    @Override
    public AlarmWay getAlarmWay() {
        return AlarmWay.DINGDING;
    }

    @Override
    public boolean failAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        return false;
    }
}
