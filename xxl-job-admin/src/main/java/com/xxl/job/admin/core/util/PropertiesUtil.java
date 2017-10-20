package com.xxl.job.admin.core.util;

import org.springframework.core.env.Environment;

/**
 * properties util
 *
 * @author xuxueli 2015-8-28 10:35:53
 */
public class PropertiesUtil {

    private static PropertiesUtil instance = new PropertiesUtil();
    private Environment environment;

    public static String getString(String key) {
        return instance.environment.getProperty(key);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static PropertiesUtil getInstance() {
        return instance;
    }
}
