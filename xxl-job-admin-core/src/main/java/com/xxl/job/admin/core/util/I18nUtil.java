package com.xxl.job.admin.core.util;

import com.xxl.tool.core.PropTool;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * i18n util for core module
 *
 * @author xuxueli 2018-01-17 20:39:06
 */
public class I18nUtil {

    private static final String DEFAULT_I18N = "zh_CN";

    // ---------------------- for i18n config ----------------------

    private static String i18n = DEFAULT_I18N;

    /**
     * set i18n locale
     */
    public static void setI18n(String locale) {
        if (locale != null && (locale.equals("zh_CN") || locale.equals("zh_TC") || locale.equals("en"))) {
            i18n = locale;
        } else {
            i18n = DEFAULT_I18N;
        }
    }

    /**
     * get i18n
     */
    public static String getI18n() {
        return i18n;
    }

    // ---------------------- tool ----------------------

    private static Properties prop = null;
    public static Properties loadI18nProp(){
        if (prop != null) {
            return prop;
        }
        // build i18n filepath
        String i18nFile = MessageFormat.format("i18n/message_{0}.properties", i18n);

        // load prop
        prop = PropTool.loadProp(i18nFile);
        return prop;
    }

    /**
     * get val of i18n key
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        return loadI18nProp().getProperty(key);
    }

    /**
     * get mult val of i18n mult key, as json
     *
     * @param keys
     * @return
     */
    public static String getMultString(String... keys) {
        Properties prop = loadI18nProp();
        if (keys!=null && keys.length>0) {
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < keys.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(keys[i]).append("\":\"").append(prop.getProperty(keys[i])).append("\"");
            }
            sb.append("}");
            return sb.toString();
        }
        return "{}";
    }

}