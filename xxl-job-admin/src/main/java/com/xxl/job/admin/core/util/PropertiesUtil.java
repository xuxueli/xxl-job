package com.xxl.job.admin.core.util;

import java.util.Properties;

/**
 * properties util
 *
 * @author xuxueli 2015-8-28 10:35:53
 */
public class PropertiesUtil {

    private static PropertiesUtil instance = new PropertiesUtil();
    private Properties properties;

    public static String getString(String key) {
        return instance.properties.getProperty(key);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public static PropertiesUtil getInstance() {
        return instance;
    }
}
