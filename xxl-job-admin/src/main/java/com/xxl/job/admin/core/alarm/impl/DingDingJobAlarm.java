package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.dingtalk.DingTalkOApiManager;
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

/**
 * job alarm by dingTalk
 *
 * @author wachoo 2020-03-19
 */
@Component
public class DingDingJobAlarm implements JobAlarm {

    private static Logger logger = LoggerFactory.getLogger(DingDingJobAlarm.class);

    @Autowired
    private DingTalkOApiManager dingTalkOApiManager;

    /**
     * fail alarm
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // send dingding
        if (info != null && (info.getAlarmPhone() != null
                && info.getAlarmPhone().trim().length() > 0)) {
            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>TriggerMsg=<br>" + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>HandleCode=" + jobLog.getHandleMsg();
            }

            // content info
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao()
                    .load(Integer.valueOf(info.getJobGroup()));
            // make phone
            try {
                String content_dingtalk = MessageFormat.format(loadDingDingJobAlarmTemplate(),
                        group != null ? group.getTitle() : "null",
                        info.getId(),
                        info.getJobDesc(),
                        alarmContent);
                String accessToken = "3814f4fa7d43db4440e26b4f7346b22f8ebfda60e311917d15323967b4a2661e";
                String secret = "SECe39d9b4e83b78022b3da6d372e2960e6c15c3f5aaaa93634a0372b1d00ee7358";
                alarmResult = dingTalkOApiManager
                        .groupTextSend(accessToken, secret, content_dingtalk, info.getAlarmPhone());
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> xxl-job, job fail alarm phone send error, JobLogId:{}",
                        jobLog.getId(), e);
                alarmResult = false;
            }

        }

        // do something, custom alarm strategy, such as sms

        return alarmResult;
    }

    /**
     * load email job alarm template
     */
    private static final String loadDingDingJobAlarmTemplate() {
        // dingTalk alarm template
        final String dingTalkBodyTemplate =
                I18nUtil.getString("jobconf_monitor_detail") + ">>>>\n\n" +
                        I18nUtil.getString("jobinfo_field_jobgroup") + ":  {0}\n" +
                        I18nUtil.getString("jobinfo_field_id") + ":  {1}\n" +
                        I18nUtil.getString("jobinfo_field_jobdesc") + ":  {2}\n" +
                        I18nUtil.getString("jobconf_monitor_alarm_title") + ":  " + I18nUtil
                        .getString("jobconf_monitor_alarm_type") + "\n" +
                        I18nUtil.getString("jobconf_monitor_alarm_content") + ":  {3}\n";
        return dingTalkBodyTemplate;
    }

}
