package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.msg.BaseMsg;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.stereotype.Component;

/**
 * job alarm by webhook
 *
 * @author: Dao-yang.
 * @date: Created in 2025/7/10 18:22
 */
@Component
public class WebHookJobAlarm extends AbstractJobAlarm {

    @Override
    protected AlarmTypeEnum getAlarmType() {
        return AlarmTypeEnum.WEBHOOK;
    }

    @Override
    protected BaseMsg createMsgRequest(String content) {
        return null;
    }

    @Override
    protected BaseMsg createMsgRequest(XxlJobInfo info, XxlJobLog jobLog) {
        return parseWebHookMsg(info, jobLog);
    }
}
