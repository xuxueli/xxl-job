package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.alarm.msg.BaseMsg;
import com.xxl.job.admin.core.alarm.msg.EmailMsg;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.core.biz.model.ReturnT;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * job alarm by email
 *
 * @author: Dao-yang.
 * @date: Created in 2025/7/10 18:22
 */
@Component
public class EmailJobAlarm extends AbstractJobAlarm {

    private static final Logger logger = LoggerFactory.getLogger(EmailJobAlarm.class);


    @Override
    protected AlarmTypeEnum getAlarmType() {
        return AlarmTypeEnum.EMAIL;
    }

    @Override
    protected BaseMsg createMsgRequest(String content) {
        return null;
    }

    @Override
    protected BaseMsg createMsgRequest(XxlJobInfo info, XxlJobLog jobLog) {
        EmailMsg emailMsg = new EmailMsg();
        emailMsg.setTitle(I18nUtil.getString("jobconf_monitor"));
        emailMsg.setContent(buildEmailContent(info, jobLog));
        emailMsg.setRecipients(info.getAlarmUrl().split(","));
        return emailMsg;
    }

    private String buildEmailContent(XxlJobInfo info, XxlJobLog jobLog) {
        String alarmContent = getAlarmContent(jobLog);
        XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());
        return MessageFormat.format(loadEmailJobAlarmTemplate(),
                group != null ? group.getTitle() : "null",
                info.getId(),
                info.getJobDesc(),
                alarmContent);
    }

    private String getAlarmContent(XxlJobLog jobLog) {
        String alarmContent = "告警日志ID:" + jobLog.getId();
        alarmContent += "<br/><b>触发信息:</b><br/>" + jobLog.getTriggerMsg();
        if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmContent += "<br/><br><b>调度失败信息:</b><br>" + jobLog.getHandleMsg();
        }
        return alarmContent;
    }


    /**
     * load email job alarm template
     *
     * @return
     */
    private String loadEmailJobAlarmTemplate() {
        return "<h5>" + I18nUtil.getString("jobconf_monitor_detail") + "：</span>" +
               "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
               "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
               "      <tr>\n" +
               "         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobgroup") + "</td>\n" +
               "         <td width=\"10%\" >" + I18nUtil.getString("jobinfo_field_id") + "</td>\n" +
               "         <td width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobdesc") + "</td>\n" +
               "         <td width=\"10%\" >" + I18nUtil.getString("jobconf_monitor_alarm_title") + "</td>\n" +
               "         <td width=\"40%\" >" + I18nUtil.getString("jobconf_monitor_alarm_content") + "</td>\n" +
               "      </tr>\n" +
               "   </thead>\n" +
               "   <tbody>\n" +
               "      <tr>\n" +
               "         <td>{0}</td>\n" +
               "         <td>{1}</td>\n" +
               "         <td>{2}</td>\n" +
               "         <td>" + I18nUtil.getString("jobconf_monitor_alarm_type") + "</td>\n" +
               "         <td>{3}</td>\n" +
               "      </tr>\n" +
               "   </tbody>\n" +
               "</table>";
    }


    @Override
    public boolean sendToAll(XxlJobLog jobLog, XxlJobInfo info, String jsonMsg) {
        try {
            EmailMsg emailMsg = JacksonUtil.readValue(jsonMsg, EmailMsg.class);
            if (emailMsg == null || emailMsg.getRecipients() == null) {
                logger.error("Email send error, email recipients is null, JobLogId:{}", jobLog.getId());
                return false;
            }
            MimeMessage mimeMessage = XxlJobAdminConfig.getAdminConfig().getMailSender().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(XxlJobAdminConfig.getAdminConfig().getEmailFrom(), I18nUtil.getString("admin_name_full"));
            helper.setTo(emailMsg.getRecipients());
            helper.setSubject(emailMsg.getTitle());
            helper.setText(emailMsg.getContent(), true);
            XxlJobAdminConfig.getAdminConfig().getMailSender().send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("Email send error, JobLogId:{}", jobLog.getId(), e);
            return false;
        }
    }

}
