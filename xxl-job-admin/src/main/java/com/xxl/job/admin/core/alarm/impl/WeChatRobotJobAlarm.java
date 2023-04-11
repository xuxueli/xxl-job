package com.xxl.job.admin.core.alarm.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.core.biz.model.ReturnT;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * job alarm by WeChat robot
 *
 * @author leon.qi 2023-04-11
 */
@Component
public class WeChatRobotJobAlarm implements JobAlarm {

    private static final Logger logger = LoggerFactory.getLogger(WeChatRobotJobAlarm.class);

    @Value("${xxl.job.alarm.wechat-robot-webhook:}")
    private String weChatRobotWebhook;


    /**
     * fail alarm
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        if (Objects.isNull(weChatRobotWebhook) || weChatRobotWebhook.isEmpty()) {
            return true;
        }

        // alarmContent
        String alarmContent = "Alarm Job LogId=" + jobLog.getId();
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "\nTriggerMsg=" + jobLog.getTriggerMsg();
        }
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "\nHandleCode=" + jobLog.getHandleMsg();
        }

        // message info
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());
        String content = MessageFormat.format(loadWeChatRobotJobAlarmTemplate(),
                group != null ? group.getTitle() : "null",
                info.getId(),
                info.getJobDesc(),
                alarmContent);
        String markdownMessage = formatWeChatRobotMarkdownMessage(content);

        HttpPost httpPost = constructHttpPost(weChatRobotWebhook, markdownMessage);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return checkWeChatRobotResult(resp);
        } catch (IOException e) {
            logger.error(">>>>>>>>>>> xxl-job, job fail alarm WeChat robot message send error, JobLogId:{}",
                    jobLog.getId(), e);
            return false;
        }
    }

    private boolean checkWeChatRobotResult(String result) throws JsonProcessingException {
        if (Objects.isNull(result)) {
            throw new RuntimeException("send WeChat Robot message error, return null");
        }

        Map map = JacksonUtil.readValue(result, Map.class);
        if (Objects.isNull(map)) {
            throw new RuntimeException("send WeChat Robot message parse error");
        }
        if (!Objects.equals(map.get("errcode"), 0)) { // WeChat Robot def
            throw new RuntimeException("send WeChat Robot message error, " + map);
        }
        return true;
    }

    private static HttpPost constructHttpPost(String url, String msg) {
        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(msg, StandardCharsets.UTF_8);
        post.setEntity(entity);
        post.addHeader("Content-Type", "application/json; charset=utf-8");
        return post;
    }

    /**
     * load WeChat Robot job alarm template
     */
    private static String loadWeChatRobotJobAlarmTemplate() {
        return "XXL-JOB <font color=\"warning\">" + I18nUtil.getString("jobconf_monitor_detail") + "</font>\n"
                + I18nUtil.getString("jobinfo_field_jobgroup") + "：{0}\n"
                + I18nUtil.getString("jobinfo_field_id") + "：{1}\n"
                + I18nUtil.getString("jobinfo_field_jobdesc") + "：{2}\n"
                + I18nUtil.getString("jobconf_monitor_alarm_title") + "："
                + I18nUtil.getString("jobconf_monitor_alarm_type") + "\n"
                + I18nUtil.getString("jobconf_monitor_alarm_content") + "：\n> {3}";
    }

    private String formatWeChatRobotMarkdownMessage(String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", "markdown");
        Map<String, Object> text = new HashMap<>();
        text.put("content", content);
        body.put("markdown", text);
        return JacksonUtil.writeValueAsString(body);
    }

}
