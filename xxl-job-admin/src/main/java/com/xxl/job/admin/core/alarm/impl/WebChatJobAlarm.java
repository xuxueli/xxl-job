package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.msg.BaseMsg;
import com.xxl.job.admin.core.alarm.msg.WechatTextMsgReq;
import org.springframework.stereotype.Component;

/**
 * job alarm by wechat
 *
 * @author: Dao-yang.
 * @date: Created in 2025/7/10 18:22
 */
@Component
public class WebChatJobAlarm extends AbstractJobAlarm {

    @Override
    protected AlarmTypeEnum getAlarmType() {
        return AlarmTypeEnum.ENT_WECHAT;
    }

    @Override
    protected BaseMsg createMsgRequest(String content) {
        WechatTextMsgReq msgReq = new WechatTextMsgReq();
        msgReq.withContent(content);
        return msgReq;
    }

}
