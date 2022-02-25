package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobAlarm;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.alarm.AlarmConstants;
import com.xxl.job.alarm.SPI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class JobAlarmer implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(JobAlarmer.class);

    private Map<String, com.xxl.job.alarm.JobAlarm> jobAlarmMap;

    @Resource
    private JobAlarmMessageConverter jobAlarmMessageConverter;

    @Resource
    private AlarmDefaultConfig alarmDefaultConfig;

    private Map<String, Properties> alarmDefaultConfigProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, com.xxl.job.alarm.JobAlarm> jobAlarmMap = new HashMap<>();
        ServiceLoader<com.xxl.job.alarm.JobAlarm> serviceLoader = ServiceLoader.load(com.xxl.job.alarm.JobAlarm.class);
        for (com.xxl.job.alarm.JobAlarm jobAlarm : serviceLoader) {
            Class<? extends com.xxl.job.alarm.JobAlarm> alarmClass = jobAlarm.getClass();
            SPI spi = alarmClass.getDeclaredAnnotation(SPI.class);
            if (spi == null || StringUtils.isBlank(spi.value())) {
                logger.warn("alarm plugin {} does not have a name, use the simple name {} of it's class", alarmClass, alarmClass.getSimpleName());
                jobAlarmMap.put(alarmClass.getSimpleName(), jobAlarm);
            } else {
                jobAlarmMap.put(spi.value(), jobAlarm);
            }
        }
        this.jobAlarmMap = Collections.unmodifiableMap(jobAlarmMap);
        initAlarmConfig();
    }

    private void initAlarmConfig() {
        Map<String, Properties> alarmDefaultConfigProperties = new HashMap<>();
        List<String> alarmPluginNames = getAlarmPluginNames();
        for (String alarmPluginName : alarmPluginNames) {
            String propertyConfigPrefix = alarmPluginName.concat(".");
            Properties properties = new Properties();
            alarmDefaultConfig.keySet()
                    .stream()
                    .filter(s -> s.startsWith(propertyConfigPrefix))
                    .forEach(key -> properties.put(AlarmDefaultConfig.PREFIX + "." + key, alarmDefaultConfig.getOrDefault(key, StringUtils.EMPTY)));
            alarmDefaultConfigProperties.put(alarmPluginName, properties);
        }
        this.alarmDefaultConfigProperties = Collections.unmodifiableMap(alarmDefaultConfigProperties);
    }

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean alarm(XxlJobInfo info, List<XxlJobAlarm> jobAlarmList, XxlJobLog jobLog) {
        if (jobAlarmList == null || jobAlarmList.isEmpty()) {
            return true;
        }

        XxlJobGroup jobGroup = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());

        boolean result = false;

        for (XxlJobAlarm xxlAlarmConfig : jobAlarmList) {
            try {
                com.xxl.job.alarm.JobAlarm jobAlarm = jobAlarmMap.get(xxlAlarmConfig.getAlarmType());
                if (jobAlarm != null) {
                    Properties alarmConfigProperties = prepareAlarmConfig(xxlAlarmConfig);
                    String message = jobAlarmMessageConverter.convert(xxlAlarmConfig.getAlarmType(), alarmConfigProperties, jobGroup, info, jobLog);
                    try {
                        result = jobAlarm.doAlarm(alarmConfigProperties, message) || result;
                    } catch (Exception e) {
                        logger.error("plugin {} send alarm message {} error", xxlAlarmConfig.getAlarmType(), message, e);
                    }
                }
            } catch (Exception e) {
                logger.error("parse alarm config error", e);
            }
        }
        return result;
    }


    private Properties prepareAlarmConfig(XxlJobAlarm xxlJobAlarm) throws IOException {
        Properties config = new Properties();
        String alarmConfig = xxlJobAlarm.getAlarmConfig();
        if (StringUtils.isNotBlank(alarmConfig)) {
            config.load(new StringReader(xxlJobAlarm.getAlarmConfig()));
        }
        Properties defaultConfig = alarmDefaultConfigProperties.get(xxlJobAlarm.getAlarmType());
        if (defaultConfig != null) {
            defaultConfig.forEach(config::putIfAbsent);
        }
        if (StringUtils.isNotBlank(xxlJobAlarm.getAlarmTarget())) {
            config.put(AlarmConstants.ALARM_TARGET, xxlJobAlarm.getAlarmTarget());
        }
        return config;
    }

    public List<String> getAlarmPluginNames() {
        return jobAlarmMap.keySet().stream().sorted().collect(Collectors.toList());
    }

}
