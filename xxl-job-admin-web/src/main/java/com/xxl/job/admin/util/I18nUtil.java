package com.xxl.job.admin.util;

import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.tool.freemarker.FtlTool;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * i18n util - web layer wrapper
 *
 * @author xuxueli 2018-01-17 20:39:06
 */
@Component
public class I18nUtil implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(I18nUtil.class);

    // ---------------------- for i18n config ----------------------

    /**
     * i18n config
     */
    @Value("${xxl.job.i18n}")
    private String i18n;

    /**
     * freemarker config
     */
    @Autowired
    private Configuration configuration;

    @Override
    public void afterPropertiesSet() throws Exception {
        // set locale to core I18nUtil
        com.xxl.job.admin.core.util.I18nUtil.setI18n(i18n);

        // init freemarker shared variable
        configuration.setSharedVariable("I18nUtil", FtlTool.generateStaticModel(I18nUtil.class.getName()));

        // init single (for template access)
        single = this;

        // init i18n-enum
        initI18nEnum();
    }

    private static I18nUtil single = null;
    private static I18nUtil getSingle() {
        return single;
    }

    // ---------------------- tool ----------------------

    /**
     * get i18n
     */
    public String getI18n() {
        if (!"zh_CN".equals(i18n) && !"zh_TC".equals(i18n) && !"en".equals(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    /**
     * get val of i18n key (delegate to core)
     */
    public static String getString(String key) {
        return com.xxl.job.admin.core.util.I18nUtil.getString(key);
    }

    /**
     * get mult val of i18n mult key, as json (delegate to core)
     */
    public static String getMultString(String... keys) {
        return com.xxl.job.admin.core.util.I18nUtil.getMultString(keys);
    }

    // ---------------------- init I18n-enum ----------------------

    /**
     * init i18n-enum
     */
    private void initI18nEnum(){
        for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

}