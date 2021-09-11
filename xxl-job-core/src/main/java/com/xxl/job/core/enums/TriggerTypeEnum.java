package com.xxl.job.core.enums;


/**
 * trigger type enum
 *
 * @author xuxueli 2018-09-16 04:56:41
 */
public enum TriggerTypeEnum {

    MANUAL,
    CRON,
    RETRY,
    PARENT,
    API,
    MISFIRE;

    private String title;
    public String getTitle() {
        return title;
    }

}
