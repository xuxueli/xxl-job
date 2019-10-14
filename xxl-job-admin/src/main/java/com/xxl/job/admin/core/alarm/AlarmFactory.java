package com.xxl.job.admin.core.alarm;

import com.xxl.job.admin.core.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author greenman0007
 * @time 2019/10/14 14:59
 */
public class AlarmFactory {
    private static Logger logger = LoggerFactory.getLogger(AlarmFactory.class);

    private static Map<AlarmWay, IAlarm> alarmMap = new ConcurrentHashMap();

    public static void creatAlarms() {
        try {
            List<Class<?>> classes = ClassUtil.getAllAssignedClass(IAlarm.class);
            for (Class clz : classes) {
                IAlarm alarm = (IAlarm) clz.newInstance();
                alarmMap.put(alarm.getAlarmWay(), alarm);
            }
        } catch (Exception e) {
            logger.error("初始化告警方式出错：", e);
        }
    }

    public static IAlarm getAlarm(AlarmWay way) {
        return alarmMap.get(way);
    }
}
