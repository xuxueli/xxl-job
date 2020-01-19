package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.alarm.impl.EmailJobAlarm;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class JobAlarmer {

    @Resource
    private EmailJobAlarm emailJobAlarm;

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean alarm(XxlJobInfo info, XxlJobLog jobLog) {

        // alarm by email
        boolean emailResult = emailJobAlarm.doAlarm(info, jobLog);

        // do something, custom alarm strategy, such as sms
        // ...

        return emailResult;
    }



}
