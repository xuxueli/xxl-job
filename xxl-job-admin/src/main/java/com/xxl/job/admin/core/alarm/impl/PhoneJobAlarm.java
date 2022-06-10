package com.xxl.job.admin.core.alarm.impl;

import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhengcc
 */
@Component
public class PhoneJobAlarm implements JobAlarm {
    private static Logger logger = LoggerFactory.getLogger(PhoneJobAlarm.class);
    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        boolean alarmResult = true;
        if (info!=null && StringUtils.hasText(info.getAlarmPhone())){
            String alarmContent = "Alarm Job Desc=" + info.getJobDesc() + ",LogId=" + jobLog.getId();
            if (jobLog.getTriggerCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>调度信息=<br>" + jobLog.getTriggerMsg();
            }
            if (jobLog.getHandleCode()>0 && jobLog.getHandleCode() != ReturnT.SUCCESS_CODE) {
                alarmContent += "<br>执行信息=" + jobLog.getHandleMsg();
            }
            XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(Integer.valueOf(info.getJobGroup()));
            String personal = I18nUtil.getString("admin_name_full");
            String title = I18nUtil.getString("jobconf_monitor");

            Set<String> phoneSet = new HashSet<String>(Arrays.asList(info.getAlarmPhone().split(",")));
            for (String phone: phoneSet) {

                // make mail
                try {
                    //TODO  待发送到行内电脑，完成短信平台的接入
                } catch (Exception e) {
                    logger.error(">>>>>>>>>>> job fail alarm phone send error, JobLogId:{}", jobLog.getId(), e);
                    alarmResult = false;
                }
            }
        }
        return alarmResult;
    }
}
