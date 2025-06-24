package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.alarm.msg.WebHookMsg;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.stereotype.Component;

/**
 * job alarm by webhook
 *
 * @author hcmyfs@163.com 2022-02-20
 */
@Component
public class WebHookJobAlarm extends BaseAlarm implements JobAlarm {

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        if (info != null && info.getAlarmType() == AlarmTypeEnum.WEBHOOK.getAlarmType() &&
            info.getAlarmUrl() != null && !info.getAlarmUrl().trim().isEmpty()) {
            WebHookMsg webHookMsg = parseWebHookMsg(info, jobLog);
            return sendToAll(jobLog, info, webHookMsg.toJson());
        }
        return true;
    }

    @Override
    public boolean accept(XxlJobInfo info) {
        return info != null && info.getAlarmType() == AlarmTypeEnum.WEBHOOK.getAlarmType() &&
               info.getAlarmUrl() != null && !info.getAlarmUrl().trim().isEmpty();
    }
}
