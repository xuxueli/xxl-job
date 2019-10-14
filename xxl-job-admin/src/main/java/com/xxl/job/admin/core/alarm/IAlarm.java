package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

/**
 * @author greenman0007
 * @time 2019/10/14 14:20
 */
public interface IAlarm {
    /**
     * 告警方式
     * @see com.xxl.job.admin.core.alarm.AlarmWay
     * @return
     */
    AlarmWay getAlarmWay();

    /**
     * 失败告警
     * @param info
     * @param jobLog
     * @return
     */
    boolean failAlarm(XxlJobInfo info, XxlJobLog jobLog);
}
