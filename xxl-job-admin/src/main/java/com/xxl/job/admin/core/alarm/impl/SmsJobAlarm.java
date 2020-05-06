package com.xxl.job.admin.core.alarm.impl;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//tencent cloud api
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
// 导入 SMS 模块的 client
import com.tencentcloudapi.sms.v20190711.SmsClient;
// 导入要请求接口对应的 request response 类
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;

/**
 * job alarm by sms
 * @author gavin
 */
@Component
public class SmsJobAlarm implements JobAlarm {
    private static Logger logger = LoggerFactory.getLogger(SmsJobAlarm.class);
    /**
     * fail alarm
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // send sms
        if (info != null && (info.getAlarmPhone() != null
                && info.getAlarmPhone().trim().length() > 0)) {
            // make phone
            try {
                /*
                String content_dingtalk = MessageFormat.format(loadDingDingJobAlarmTemplate(),
                        group != null ? group.getTitle() : "null",
                        info.getId(),
                        info.getJobDesc(),
                        alarmContent);
                 */


            } catch (Exception e) {
                logger.error("xxl-job,job fail alarm sms send error, JobLogId:{}",
                        jobLog.getId(), e);
                alarmResult = false;
            }

        }
        return alarmResult;
    }

}