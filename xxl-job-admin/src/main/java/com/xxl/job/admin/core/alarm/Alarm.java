package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;

/**
 * Alarm interface.
 *
 * @author yuan.cheng
 */
public interface Alarm {
    /**
     * send alarm
     * @param jobInfo {@link XxlJobInfo}
     * @param xxlJobLog {@link XxlJobLog}
     * @param jobGroup {@link XxlJobGroup}
     * @return true if send alarm success
     */
    boolean sendAlarm(XxlJobInfo jobInfo, XxlJobLog xxlJobLog, XxlJobGroup jobGroup);
}
