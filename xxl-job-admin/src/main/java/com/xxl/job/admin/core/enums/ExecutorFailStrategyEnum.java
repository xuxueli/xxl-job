package com.xxl.job.admin.core.enums;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorFailStrategyEnum {

    FAIL_ALARM(I18nUtil.getString("jobconf_fail_alarm")),

    FAIL_RETRY(I18nUtil.getString("jobconf_fail_retry"));

    private final String title;
    private ExecutorFailStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ExecutorFailStrategyEnum match(String name, ExecutorFailStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorFailStrategyEnum item: ExecutorFailStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}
