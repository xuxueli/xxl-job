package com.xxl.job.admin.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * i18n util
 *
 * @author xuxueli 2018-01-17 20:39:06
 */
public class I18nUtil {
    private static Logger logger = LoggerFactory.getLogger(I18nUtil.class);

    private static final String i18n_file = "i18n/message.properties";
    private static Properties prop = null;
    private static boolean prop_cache = false;

    public static Properties loadI18nProp(){
        if (prop_cache && prop != null) {
            return prop;
        }

        try {
            Resource resource = new ClassPathResource(i18n_file);
            EncodedResource encodedResource = new EncodedResource(resource,"UTF-8");
            prop = PropertiesLoaderUtils.loadProperties(encodedResource);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return prop;
    }

    public static String getString(String key) {
        return loadI18nProp().getProperty(key);
    }

}
