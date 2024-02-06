package com.xxl.job.admin.core.alarm.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.model.dingtalk.Token;
import com.xxl.job.admin.core.model.dingtalk.UserIdRet;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;

/**
 * job alarm by dingTalk
 *
 * @author Caisin 2022-07-26
 */
@Component
public class DingTalkJobAlarm implements JobAlarm {
    private static Logger logger = LoggerFactory.getLogger(DingTalkJobAlarm.class);
    // conf
    @Value("${xxl.job.dingtalk.appKey}")
    private String appKey;
    @Value("${xxl.job.dingtalk.appSecret}")
    private String appSecret;
    @Value("${xxl.job.dingtalk.robotCode}")
    private String robotCode;

    @Autowired
    ObjectMapper objectMapper;
    RestTemplate template = new RestTemplate();

    Token token;

    //在构造函数执行之后执行
    @PostConstruct
    public void init() {
        template.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().set("Host", "api.dingtalk.com");
            request.getHeaders().set("Content-Type", "application/json");
            request.getHeaders().set("x-acs-dingtalk-access-token", getAccessToken());
            return execution.execute(request, body);
        }));

        token = getToken();
    }

    private synchronized String getAccessToken() {
        if (this.token != null) {
            if (token.isExpire()) {
                token = getToken();
            }
            if (token.getErrcode() == 0) {
                return token.getAccess_token();
            }
        }
        return "";
    }

    public Token getToken() {
        String url = String.format("https://oapi.dingtalk.com/gettoken?appkey=%s&appsecret=%s", appKey, appSecret);
        Token tk = template.getForObject(url, Token.class);
        tk.setExpireAt();
        return tk;

    }

    public void sendMsg(List<String> userIds, String msgParam) {
        String url = String.format("https://api.dingtalk.com/v1.0/robot/oToMessages/batchSend");
        HashMap<String, Object> param = new HashMap<>();
        //{
        //  "robotCode" : "String",
        //  "userIds" : [ "String" ],
        //  "msgKey" : "String",
        //  "msgParam" : "String"
        //}
        param.put("robotCode", robotCode);
        param.put("userIds", userIds);
        param.put("msgKey", "sampleMarkdown");
        param.put("msgParam", msgParam);
        template.postForObject(url, param, String.class);

    }

    public String getUserIdByMobile(String mobile) {
        HashMap<String, String> param = new HashMap<>();
        param.put("mobile", mobile);
        UserIdRet rest = template.postForObject("https://oapi.dingtalk.com/topapi/v2/user/getbymobile?access_token=" + getAccessToken(), param, UserIdRet.class);
        UserIdRet.Result result = rest.getResult();
        if (result == null) {
            return "";
        }
        return result.getUserid();
    }

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;

        // send monitor email
        if (info != null && info.getAlarmEmail() != null && info.getAlarmEmail().trim().length() > 0) {

            // alarmContent
            String alarmContent = "Alarm Job LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "  \n  TriggerMsg=  \n  " + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "  \n  HandleCode=" + jobLog.getHandleMsg();
            }

            // email info
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());
            String personal = I18nUtil.getString("admin_name_full");
            String title = I18nUtil.getString("jobconf_monitor");
            String content = MessageFormat.format(loadEmailJobAlarmTemplate(),
                    group != null ? group.getTitle() : "null",
                    info.getId(),
                    info.getJobDesc(),
                    alarmContent);

            String alarmTel = info.getAlarmTel();
            if (alarmTel == null || "".equals(alarmTel.trim())) {
                return false;
            }
            Set<String> telSet = new HashSet<>(Arrays.asList(alarmTel.split(",")));
            try {
                ArrayList<String> userIds = new ArrayList<>();
                for (String tel : telSet) {
                    String id = getUserIdByMobile(tel);
                    userIds.add(id);
                    // make mail
                }
                HashMap<String, Object> msgParam = new HashMap<>();
                msgParam.put("title", title);
                msgParam.put("text", content);
                sendMsg(userIds, objectMapper.writeValueAsString(msgParam));
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> xxl-job, job fail alarm email send error, JobLogId:{}", jobLog.getId(), e);

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
    private static String loadEmailJobAlarmTemplate() {
        return "# " + I18nUtil.getString("jobconf_monitor_detail") + "\n" +
                "## " + I18nUtil.getString("jobinfo_field_jobgroup") + ": {0}\n" +
                "## " + I18nUtil.getString("jobinfo_field_id") + ": {1}\n" +
                "## " + I18nUtil.getString("jobinfo_field_jobdesc") + ":\n" +
                "> {2}\n" +
                "## " + I18nUtil.getString("jobconf_monitor_alarm_title") + ": " + I18nUtil.getString("jobconf_monitor_alarm_type") + "\n" +
                "## " + I18nUtil.getString("jobconf_monitor_alarm_content") + ":\n" +
                "> {3}";
    }

}
