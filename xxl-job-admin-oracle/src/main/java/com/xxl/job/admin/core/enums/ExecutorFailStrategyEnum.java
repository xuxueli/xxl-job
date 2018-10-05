package com.xxl.job.admin.core.enums;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorFailStrategyEnum {

    FAIL_ALARM("失败告警"),

    FAIL_RETRY("失败重试");

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
