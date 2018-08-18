package com.xxl.job.admin.core.enums;

/**
 * Created by xuxueli on 17/5/9.
 */

@Deprecated
public enum ExecutorFailStrategyEnum {

    NULL("NULL"),

    FAIL_TRIGGER_RETRY("FAIL_TRIGGER_RETRY"),

    FAIL_HANDLE_RETRY("FAIL_HANDLE_RETRY");

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
