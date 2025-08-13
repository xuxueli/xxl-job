package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JobAlarmer implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JobAlarmer.class);

    private ApplicationContext applicationContext;
    private List<JobAlarm> jobAlarmList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JobAlarm> serviceBeanMap = applicationContext.getBeansOfType(JobAlarm.class);
        if (!serviceBeanMap.isEmpty()) {
            jobAlarmList = new ArrayList<JobAlarm>(serviceBeanMap.values());
        }
    }

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean alarm(XxlJobInfo info, XxlJobLog jobLog) {

        boolean result = false;
        if (jobAlarmList != null && !jobAlarmList.isEmpty()) {
            result = true;  // success means all-success
            for (JobAlarm alarm : jobAlarmList) {
                if (alarm.accept(info)) {
                    logger.info(">>>>>>>>>>> xxl-job try to alarm, alarmType={}", alarm.getClass().getSimpleName());
                    boolean resultItem = false;
                    try {
                        resultItem = alarm.doAlarm(info, jobLog);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (!resultItem) {
                        result = false;
                    }
                }
            }
        }

        return result;
    }

}
