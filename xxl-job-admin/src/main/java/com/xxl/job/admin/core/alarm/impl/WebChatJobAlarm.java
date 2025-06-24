package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.alarm.msg.WechatTextMsgReq;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.stereotype.Component;

/**
 * job alarm by wechat
 *
 * @author hcmyfs@163.com 2022-02-20
 */
@Component
public class WebChatJobAlarm extends BaseAlarm implements JobAlarm {

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        String content = parseContent(info, jobLog);
        WechatTextMsgReq textMsgReq = new WechatTextMsgReq();
        textMsgReq.withContent(content);
        return sendToAll(jobLog, info, textMsgReq.toJson());
    }

    @Override
    public boolean accept(XxlJobInfo info) {
        return (info != null && info.getAlarmType() == AlarmTypeEnum.ENT_WECHAT.getAlarmType() &&
                info.getAlarmUrl() != null && !info.getAlarmUrl().trim().isEmpty());
    }

}
