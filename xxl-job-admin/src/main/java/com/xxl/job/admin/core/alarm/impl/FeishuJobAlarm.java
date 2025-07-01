package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.alarm.msg.FeiShuTextMsgReq;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.stereotype.Component;

/**
 * job alarm by feishu
 *
 * @author hcmyfs@163.com 2022-02-20
 */
@Component
public class FeishuJobAlarm extends BaseAlarm implements JobAlarm {

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {

        String content = parseContent(info, jobLog);
        FeiShuTextMsgReq textMsgReq = new FeiShuTextMsgReq();
        textMsgReq.withTitle(getTitle());
        textMsgReq.withContent(content);
        return sendToAll(jobLog, info, textMsgReq.toJson());

    }

    @Override
    public boolean accept(XxlJobInfo info) {
        return info != null && info.getAlarmType() == AlarmTypeEnum.FEI_SHU.getAlarmType() &&
               info.getAlarmUrl() != null && !info.getAlarmUrl().trim().isEmpty();
    }
}
