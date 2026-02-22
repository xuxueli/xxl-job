package com.xxl.job.admin.scheduler.alarm.impl;

import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.scheduler.alarm.JobAlarm;
import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.context.XxlJobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉告警通知
 *
 * @author zrk on 2026/2/17
 */
@Component
public class DingtalkJobAlarm implements JobAlarm {

    private static Logger logger = LoggerFactory.getLogger(DingtalkJobAlarm.class);

    @Value("${xxl.job.alarm.dingtalk.webhook:}")
    private String dingtalkWebhook;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // check dingtalk webhook config
        if (dingtalkWebhook == null || dingtalkWebhook.trim().isEmpty()) {
            logger.warn(">>>>>>>>>>> xxl-job, dingtalk webhook is not configured, skip alarm.");
            return true;
        }

        try {
            // build alarm content
            String alarmContent = buildAlarmContent(info, jobLog);

            // build dingtalk message
            Map<String, Object> message = new HashMap<>();
            message.put("msgtype", "text");

            Map<String, String> textContent = new HashMap<>();
            textContent.put("content", alarmContent);
            message.put("text", textContent);

            // send to dingtalk
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(dingtalkWebhook, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error(">>>>>>>>>>> xxl-job, dingtalk alarm send failed, status: {}, body: {}",
                        response.getStatusCode(), response.getBody());
                alarmResult = false;
            } else {
                logger.info(">>>>>>>>>>> xxl-job, dingtalk alarm send success, response: {}", response.getBody());
            }

        } catch (Exception e) {
            logger.error(">>>>>>>>>>> xxl-job, dingtalk alarm send error, JobLogId:{}", jobLog.getId(), e);
            alarmResult = false;
        }

        return alarmResult;
    }

    /**
     * build alarm content
     */
    private String buildAlarmContent(XxlJobInfo info, XxlJobLog jobLog) {
        StringBuilder content = new StringBuilder();

        // title
        content.append(I18nUtil.getString("jobconf_monitor")).append("\n");
        content.append("====================\n");

        // job info
        XxlJobGroup group = XxlJobAdminBootstrap.getInstance().getXxlJobGroupMapper().load(Integer.valueOf(info.getJobGroup()));
        content.append(I18nUtil.getString("jobinfo_field_jobgroup")).append(": ")
                .append(group != null ? group.getTitle() : "null").append("\n");
        content.append(I18nUtil.getString("jobinfo_field_id")).append(": ")
                .append(info.getId()).append("\n");
        content.append(I18nUtil.getString("jobinfo_field_jobdesc")).append(": ")
                .append(info.getJobDesc()).append("\n");

        // alarm type
        content.append(I18nUtil.getString("jobconf_monitor_alarm_title")).append(": ")
                .append(I18nUtil.getString("jobconf_monitor_alarm_type")).append("\n");

        // alarm content
        content.append(I18nUtil.getString("jobconf_monitor_alarm_content")).append(":\n");
        content.append("Job LogId=").append(jobLog.getId()).append("\n");

        if (jobLog.getTriggerCode() != XxlJobContext.HANDLE_CODE_SUCCESS) {
            content.append("TriggerMsg: ").append(jobLog.getTriggerMsg()).append("\n");
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != XxlJobContext.HANDLE_CODE_SUCCESS) {
            content.append("HandleMsg: ").append(jobLog.getHandleMsg()).append("\n");
        }

        return content.toString();
    }

}
