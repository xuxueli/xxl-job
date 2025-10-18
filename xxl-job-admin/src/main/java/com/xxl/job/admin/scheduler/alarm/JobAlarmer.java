package com.xxl.job.admin.scheduler.alarm;

import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
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

/**
 * xxl-job alarmer
 *
 * @author xuxueli 17/7/13.
 */
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
        if (MapTool.isNotEmpty(serviceBeanMap)) {
            jobAlarmList = new ArrayList<>(serviceBeanMap.values());
        }
    }

    /**
     * job alarm
     */
    public boolean alarm(XxlJobInfo info, XxlJobLog jobLog) {

        boolean result = false;
        if (CollectionTool.isNotEmpty(jobAlarmList)) {
            result = true;  // success means all-success
            for (JobAlarm alarm: jobAlarmList) {
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

        return result;
    }

}
