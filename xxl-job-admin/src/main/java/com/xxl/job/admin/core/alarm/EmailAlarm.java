package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * send email alarm.
 *
 * @author yuan.cheng
 */
public class EmailAlarm implements Alarm {
    /**
     * email alarm template
     */
    private static final String MAIL_BODY_TEMPLATE = "<h5>" + I18nUtil.getString("jobconf_monitor_detail") + "：</h5>" +
        "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >" +
        "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
        "      <tr>" +
        "         <th width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobgroup") + "</th>" +
        "         <th width=\"5%\" >" + I18nUtil.getString("jobinfo_field_id") + "</th>" +
        "         <th width=\"20%\" >" + I18nUtil.getString("jobinfo_field_jobdesc") + "</th>" +
        "         <th width=\"5%\" >" + I18nUtil.getString("jobconf_monitor_alarm_logid") + "</th>" +
        "         <th width=\"10%\" >" + I18nUtil.getString("jobconf_monitor_alarm_title") + "</th>" +
        "         <th width=\"40%\" >" + I18nUtil.getString("jobconf_monitor_alarm_content") + "</th>" +
        "      </tr>" +
        "   <thead/>" +
        "   <tbody>" +
        "      <tr>" +
        "         <td>{0}</td>" +
        "         <td>{1,number,#}</td>" +
        "         <td>{2}</td>" +
        "         <td>{3,number,#}</td>" +
        "         <td>{4}</td>" +
        "         <td>{5}</td>" +
        "      </tr>" +
        "   <tbody>" +
        "</table>";

    @Override
    public boolean sendAlarm(XxlJobInfo info, XxlJobLog jobLog, XxlJobGroup group) {
        //don't send when mail host not set
        if(StringUtils.isBlank(XxlJobAdminConfig.getAdminConfig().getMailHost())){
            return false;
        }

        String groupTitle = group != null ? group.getTitle() : "null";

        String alarmType;
        String alarmContent;
        if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
            alarmType = I18nUtil.getString("jobconf_monitor_alarm_type_trigger");
            alarmContent = jobLog.getTriggerMsg();
        } else if (jobLog.getHandleCode() > 0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
            alarmType = I18nUtil.getString("jobconf_monitor_alarm_type_handle");
            alarmContent = jobLog.getHandleMsg();
        } else {
            alarmType = StringUtils.EMPTY;
            alarmContent = StringUtils.EMPTY;
        }

        String title = I18nUtil.getString("jobconf_monitor");
        String content = MessageFormat.format(MAIL_BODY_TEMPLATE,
            groupTitle,
            info.getId(),
            info.getJobDesc(),
            jobLog.getId(),
            alarmType,
            alarmContent);

        for (String toAddress : info.getAlarmEmail().split(",")) {
            MailUtil.sendMail(toAddress, title, content);
        }
        return true;
    }
}
