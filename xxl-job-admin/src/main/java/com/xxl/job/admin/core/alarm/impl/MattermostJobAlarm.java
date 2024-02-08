package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;


/**
 * job alarm by mattermost
 *
 * @author lostsnow 2020-04-12
 */
@Component
public class MattermostJobAlarm implements JobAlarm {
    private static Logger logger = LoggerFactory.getLogger(MattermostJobAlarm.class);

    /**
     * fail alarm
     *
     * @param jobLog
     */
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;
        String webhookUrl = XxlJobAdminConfig.getAdminConfig().getMattermostWebhookUrl();
        ;

        // send monitor email
        if (info != null && webhookUrl != "") {

            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "\nTriggerMsg:\n    <br>" + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "\nHandleCode=" + jobLog.getHandleMsg();
            }

            alarmContent = alarmContent.replaceAll("(?i)<br */?>", "\n    ");
            alarmContent = Jsoup.parse(alarmContent).wholeText();
            // alarm info
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            String title = I18nUtil.getString("jobconf_monitor");
            String content = MessageFormat.format(loadMattermostJobAlarmTemplate(),
                    group != null ? group.getTitle() : "null",
                    info.getId(),
                    info.getJobDesc(),
                    alarmContent);

            content = title + "\n\n" + content;

            JsonObject payload = new JsonObject();
            payload.addProperty("text", content);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(payload.toString(), headers);
            try {
                restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> xxl-job, job fail alarm mattermost send error, JobLogId:{}", jobLog.getId(), e);

                alarmResult = false;
            }
        }

        return alarmResult;
    }

    /**
     * load email job alarm template
     *
     * @return
     */
    private static final String loadMattermostJobAlarmTemplate() {
        String template = "* " + I18nUtil.getString("jobinfo_field_jobgroup") + ": {0}\n" +
                "* " + I18nUtil.getString("jobinfo_field_id") + ": {1}\n" +
                "* " + I18nUtil.getString("jobinfo_field_jobdesc") + ": {2}\n" +
                "* " + I18nUtil.getString("jobconf_monitor_alarm_title") + ": " +
                I18nUtil.getString("jobconf_monitor_alarm_type") + "\n\n" +
                I18nUtil.getString("jobconf_monitor_alarm_content") + "\n{3}";

        return template;
    }

}
