package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.alarm.msg.WechatTextMsgReq;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * job alarm by wechat
 *
 * @author hcmyfs@163.com 2022-02-20
 */
@Component
public class WebChatJobAlarm  extends BaseAlarm implements JobAlarm {

    private RestTemplate restTemplate;
    private static Logger log = LoggerFactory.getLogger(WebChatJobAlarm.class);

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        restTemplate = new RestTemplate();

        if (info != null && info.getAlarmType() == AlarmTypeEnum.ENT_WECHAT.getAlarmType() &&
                info.getAlarmUrl() != null && info.getAlarmUrl().trim().length() > 0) {
            String content = parseContent(info, jobLog);
            WechatTextMsgReq textMsgReq = new WechatTextMsgReq();
            textMsgReq.withContent(content);
            return sendToAll(jobLog, info, textMsgReq.toJson());
        }
        return false;
    }

}
