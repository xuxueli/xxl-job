package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.msg.WebHookMsg;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BaseAlarm {

    private static final Logger log = LoggerFactory.getLogger(BaseAlarm.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    private static final String br = "\n";

    private String title;

    public boolean sendMsg(String url, String json) {
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        String body = responseEntity.getBody();
        log.info("sendRobot  url={} body={}", url, body);
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }


    public String parseContent(XxlJobInfo info, XxlJobLog jobLog) {

        // alarmContent
        String alarmContent = br + "日志ID:" + jobLog.getId();
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += br + "触发信息:" + br + jobLog.getTriggerMsg();
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += br + "调度失败信息:" + br + jobLog.getHandleMsg();
        }
        // email info
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());
        // String personal = I18nUtil.getString("admin_name_full");
        title = I18nUtil.getString("jobconf_monitor");
        String content = MessageFormat.format(loadJobAlarmTemplate(),
                group != null ? group.getTitle() : "null",
                info.getId(),
                info.getJobDesc(),
                alarmContent);
        content = content.replaceAll("<br><br>", "\n");
        content = content.replaceAll("<span style=\"color:#00c0ef;\" >", "");
        content = content.replaceAll("</span>", "");
        content = content.replaceAll("<br>", "\n");
        return content.trim();
    }

    public String getTitle() {
        return title;
    }

    /**
     * load webhook job alarm template
     *
     * @return
     */
    private static String loadJobAlarmTemplate() {
        String mailBodyTemplate = I18nUtil.getString("jobconf_monitor_detail") + br +
                                  I18nUtil.getString("jobinfo_field_jobgroup") + ":{0}" + br +
                                  I18nUtil.getString("jobinfo_field_id") + ":{1}" + br +
                                  I18nUtil.getString("jobinfo_field_jobdesc") + ":{2}" + br +
                                  I18nUtil.getString("jobconf_monitor_alarm_title") + ":" + I18nUtil.getString("jobconf_monitor_alarm_type") + br +
                                  I18nUtil.getString("jobconf_monitor_alarm_content") + ":{3}";
        return mailBodyTemplate;
    }

    /**
     * @param info
     * @param json
     * @return
     */

    public boolean sendToAll(XxlJobLog jobLog, XxlJobInfo info, String json) {
        Set<String> urlList = new HashSet<String>(Arrays.asList(info.getAlarmUrl().split(",")));
        for (String webHookUrl : urlList) {
            // send message
            try {
                sendMsg(webHookUrl, json);
            } catch (Exception e) {
                log.error(">>>>>>>>>>> xxl-job, job fail alarm webhook send error, JobLogId:{}", jobLog.getId(), e);
                return false;
            }
        }
        return true;
    }

    /**
     * webhook 消息，发给对应的第三方接口去接受，如短信，其他系统
     *
     * @param info
     * @param jobLog
     * @return
     */
    public WebHookMsg parseWebHookMsg(XxlJobInfo info, XxlJobLog jobLog) {
        WebHookMsg webHookMsg = new WebHookMsg();
        webHookMsg.setJobDesc(info.getJobDesc());
        webHookMsg.setJobId(jobLog.getJobId());
        webHookMsg.setTriggerMsg(jobLog.getTriggerMsg());
        webHookMsg.setTriggerCode(jobLog.getTriggerCode());
        webHookMsg.setHandleMsg(jobLog.getHandleMsg());
        webHookMsg.setId(jobLog.getId());
        webHookMsg.setAlarmStatus(jobLog.getAlarmStatus());
        webHookMsg.setTriggerTime(jobLog.getTriggerTime());
        webHookMsg.setHandleTime(jobLog.getHandleTime());
        webHookMsg.setJobGroup(info.getJobGroup());
        webHookMsg.setExecutorAddress(jobLog.getExecutorAddress());
        webHookMsg.setExecutorHandler(jobLog.getExecutorHandler());
        webHookMsg.setExecutorParam(jobLog.getExecutorParam());
        webHookMsg.setExecutorShardingParam(jobLog.getExecutorShardingParam());
        webHookMsg.setHandleCode(jobLog.getHandleCode());
        return webHookMsg;
    }

}
