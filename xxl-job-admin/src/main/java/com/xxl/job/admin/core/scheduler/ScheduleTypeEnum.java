package com.xxl.job.admin.core.scheduler;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum ScheduleTypeEnum {

    NONE,

    /**
     * schedule by cron
     */
    CRON,

    /**
     * schedule by fixed rate (in seconds)
     */
    FIX_RATE,

    /**
     * schedule by fix delay (in seconds)， after the last time
     */
    FIX_DELAY;

    public static ScheduleTypeEnum match(String name){
        for (ScheduleTypeEnum item: ScheduleTypeEnum.values()) {
            if (item.equals(name)) {
                return item;
            }
        }
        return NONE;
    }

}
