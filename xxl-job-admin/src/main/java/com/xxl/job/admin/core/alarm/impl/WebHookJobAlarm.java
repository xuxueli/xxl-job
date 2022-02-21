package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.alarm.msg.FeiShuTextMsgReq;
import com.xxl.job.admin.core.alarm.msg.TextMsgReq;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * job alarm by webhook
 *
 * @author hcmyfs@163.com 2022-02-20
 */
@Component
public class WebHookJobAlarm implements JobAlarm {

    private RestTemplate restTemplate;
    private static Logger log = LoggerFactory.getLogger(WebHookJobAlarm.class);

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;
        String br = "\r\n";
        restTemplate = new RestTemplate();
        // send monitor webhook
        if (info != null && info.getAlarmType() == AlarmTypeEnum.WEBHOOK.getAlarmType() &&
                info.getAlarmUrl() != null && info.getAlarmUrl().trim().length() > 0) {

            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += br + "TriggerMsg=" + br + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += br + "HandleCode=" + jobLog.getHandleMsg();
            }

            // email info
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            //String personal = I18nUtil.getString("admin_name_full");
            String title = I18nUtil.getString("jobconf_monitor");
            String content = MessageFormat.format(loadEmailJobAlarmTemplate(),
                    group != null ? group.getTitle() : "null",
                    info.getId(),
                    info.getJobDesc(),
                    alarmContent);
            content=content.replace("<br>","\n");
            content=content.replace("<span style=\"color:#00c0ef;\" >","");
            content=content.replace("</span>","");

            Set<String> urlList = new HashSet<String>(Arrays.asList(info.getAlarmUrl().split(",")));
            for (String webHookUrl : urlList) {
                // make mail
                try {
                    sendRobot(webHookUrl, content, null);
                } catch (Exception e) {
                    log.error(">>>>>>>>>>> xxl-job, job fail alarm webhook send error, JobLogId:{}", jobLog.getId(), e);
                    alarmResult = false;
                }

            }
        }

        return alarmResult;
    }

    /**
     * load webhook job alarm template
     *
     * @return
     */
    private static final String loadEmailJobAlarmTemplate() {
        String mailBodyTemplate = I18nUtil.getString("jobconf_monitor_detail") + "\n" +
                "" + I18nUtil.getString("jobinfo_field_jobgroup") + ":{0}\n" +
                "" + I18nUtil.getString("jobinfo_field_id") + ":{1}\n" +
                "" + I18nUtil.getString("jobinfo_field_jobdesc") + ":{2}\n" +
                "" + I18nUtil.getString("jobconf_monitor_alarm_title") + ":" + I18nUtil.getString("jobconf_monitor_alarm_type") + "\n" +
                "" + I18nUtil.getString("jobconf_monitor_alarm_content") + ":{3}\n";

        return mailBodyTemplate;
    }


    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    public boolean sendRobot(String url, String msg, List<String> usersList) {
        if (!StringUtils.hasText(url)) {
            log.error("sendRobot url 格式错误！");
            throw new RuntimeException("sendRobot url 格式错误！");
        }
        if (!url.startsWith("http")) {
            throw new RuntimeException("sendRobot url 格式错误！");
        }
        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = null;
        if (url.contains("open.feishu.cn")) {
            FeiShuTextMsgReq textMsgReq = new FeiShuTextMsgReq();
            textMsgReq.withContent(msg);
            HttpEntity<FeiShuTextMsgReq> entity = new HttpEntity<>(textMsgReq, headers);
            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        } else {
            TextMsgReq textMsgReq = new TextMsgReq();
            textMsgReq.withContent(msg);
            if (!CollectionUtils.isEmpty(usersList)) {
                textMsgReq.withUserList(usersList);
            }
            HttpEntity<TextMsgReq> entity = new HttpEntity<>(textMsgReq, headers);
            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        }

        String body = responseEntity.getBody();
        log.info("sendRobot={}", body);
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }
}
