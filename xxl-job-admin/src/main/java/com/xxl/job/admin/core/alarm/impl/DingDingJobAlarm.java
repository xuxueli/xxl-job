package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.alarm.msg.DingDingTextMsgReq;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.stereotype.Component;

/**
 * job alarm by ding ding
 *
 * @author hcmyfs@163.com 2022-02-20
 */
@Component
public class DingDingJobAlarm extends BaseAlarm implements JobAlarm {

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        String content = parseContent(info, jobLog);
        DingDingTextMsgReq dingDingTextMsgReq = new DingDingTextMsgReq();
        dingDingTextMsgReq.withContent(content);
        return sendToAll(jobLog, info, dingDingTextMsgReq.toJson());
    }

    @Override
    public boolean accept(XxlJobInfo info) {
        return (info != null && info.getAlarmType() == AlarmTypeEnum.DING_DING.getAlarmType() &&
                info.getAlarmUrl() != null && !info.getAlarmUrl().trim().isEmpty());
    }
}
