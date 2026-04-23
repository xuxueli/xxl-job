package com.xxl.job.admin.core.scheduler.trigger;

/**
 * trigger type enum
 *
 * @author xuxueli 2018-09-16 04:56:41
 */
public enum TriggerTypeEnum {

    MANUAL("Manual"),
    CRON("Cron"),
    RETRY("Retry"),
    PARENT("Parent"),
    API("API"),
    MISFIRE("Misfire");

    private TriggerTypeEnum(String title){
        this.title = title;
    }
    private String title;
    public String getTitle() {
        return title;
    }

}