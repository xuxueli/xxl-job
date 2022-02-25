package com.xxl.job.alarm;

import java.util.Properties;

/**
 * Created on 2022/2/22.
 *
 * @author lan
 */
public interface JobAlarm {

    boolean doAlarm(Properties config, String message);
}
