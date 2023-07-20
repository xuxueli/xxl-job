package com.xxl.job.admin.core.alarm.impl;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.RemotingUtil;
import com.xxl.job.core.biz.model.ReturnT;



/**
 * job alarm by webhook
 *
 * @author xuxueli 2020-01-19
 */
@Component
public class HookJobAlarm implements JobAlarm {
    private static Logger logger = LoggerFactory.getLogger(HookJobAlarm.class);

    @Value("${xxl.job.alarm.hook.enable}")
    private boolean enable;
    
    @Value("${xxl.job.alarm.hook.msgHead}")
    private String msgHead="";

    @Value("${xxl.job.alarm.hook.timeout}")
    private int timeout;

    /**
     * fail alarm
     *
     * @param jobLog
     */
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog){
        boolean alarmResult = true;
        String alarmHookType = info.getAlarmHookType();
        if(enable){
            switch(alarmHookType){
                case "telegram":
                    sendTelegramMessage(info, jobLog);
                break;
            }
        }
        
        
        return alarmResult;
    }

    private void sendTelegramMessage(XxlJobInfo info, XxlJobLog jobLog){

        String url = info.getAlarmHookUrl();

        if(url!=null && url.trim().length()!=0){
            // alarmContent
            String alarmContent = "  Alarm Job LogId=" + jobLog.getId() + "\n\n";
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "  <b>TriggerMsg=</b>\n    " + 
                    jobLog.getTriggerMsg().replaceAll("<br>", "\n    ").replaceAll("\\<.*?\\>", "") + "\n";
            }
            if (jobLog.getHandleCode()>0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "  <b>HandleCode=</b>\n    " + 
                    jobLog.getHandleMsg().replaceAll("<br>", "\n    ").replaceAll("\\<.*?\\>", "");
            }

            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            String content = MessageFormat.format(loadTelebotJobAlarmTemplate(),
                        msgHead!=null?msgHead:"",
                        group!=null?group.getTitle():"null",
                        info.getId(),
                        info.getJobDesc(),
                        alarmContent);
            TelegramRequest requestBody = new TelegramRequest(content, "HTML");
            RemotingUtil.postBody(url, timeout, requestBody);
        }
        
    }

    /**
     * load telebot job alarm template
     *
     * @return
     */
    private static final String loadTelebotJobAlarmTemplate(){
        String mailBodyTemplate = 
                "<code><b>{0}" + I18nUtil.getString("jobconf_monitor_detail") + "</b>\n"  +
                "<b>"+ I18nUtil.getString("jobinfo_field_jobgroup") +":</b>{1}\n" +
                "<b>"+ I18nUtil.getString("jobinfo_field_id") +":</b>{2}\n" +
                "<b>"+ I18nUtil.getString("jobinfo_field_jobdesc") +":</b>{3}\n" +
                "<b>"+ I18nUtil.getString("jobconf_monitor_alarm_title") +":</b>"+ I18nUtil.getString("jobconf_monitor_alarm_type") +"\n" +
                "<b>"+ I18nUtil.getString("jobconf_monitor_alarm_content") +":</b>\n{4}</code>";

        return mailBodyTemplate;
    }

}
