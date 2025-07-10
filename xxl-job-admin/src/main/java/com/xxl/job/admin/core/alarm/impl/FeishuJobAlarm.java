package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.msg.BaseMsg;
import com.xxl.job.admin.core.alarm.msg.FeiShuTextMsgReq;
import org.springframework.stereotype.Component;

/**
 * job alarm by feishu
 *
 * @author: Dao-yang.
 * @date: Created in 2025/7/10 18:22
 */
@Component
public class FeishuJobAlarm extends AbstractJobAlarm {


    @Override
    protected AlarmTypeEnum getAlarmType() {
        return AlarmTypeEnum.FEI_SHU;
    }
    @Override
    protected BaseMsg createMsgRequest(String content) {
        FeiShuTextMsgReq msgReq = new FeiShuTextMsgReq();
        msgReq.withTitle(getTitle());
        msgReq.withContent(content);
        return msgReq;
    }

}
