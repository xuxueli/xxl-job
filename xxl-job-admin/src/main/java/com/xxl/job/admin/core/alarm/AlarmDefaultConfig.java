package com.xxl.job.admin.core.alarm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认配置，前缀为alarm的
 * <p>
 * Created on 2022/2/25.
 *
 * @author lan
 */
@Component
@ConfigurationProperties(prefix = AlarmDefaultConfig.PREFIX)
public class AlarmDefaultConfig extends HashMap<String, String> implements Map<String, String> {

    public final static String PREFIX = "alarm";
}
