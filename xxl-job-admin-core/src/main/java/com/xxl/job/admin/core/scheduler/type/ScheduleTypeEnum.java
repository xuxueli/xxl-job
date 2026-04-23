package com.xxl.job.admin.core.scheduler.type;

import com.xxl.job.admin.core.scheduler.type.strategy.CronScheduleType;
import com.xxl.job.admin.core.scheduler.type.strategy.FixRateScheduleType;
import com.xxl.job.admin.core.scheduler.type.strategy.NoneScheduleType;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum ScheduleTypeEnum {

    NONE("None", new NoneScheduleType()),

    /**
     * schedule by cron
     */
    CRON("Cron", new CronScheduleType()),

    /**
     * schedule by fixed rate (in seconds)
     */
    FIX_RATE("Fix Rate", new FixRateScheduleType()),

    /**
     * schedule by fix delay (in seconds)， after the last time
     */
    /*FIX_DELAY("Fix Delay")*/;

    private final String title;
    private final ScheduleType scheduleType;;

    ScheduleTypeEnum(String title, ScheduleType scheduleType) {
        this.title = title;
        this.scheduleType = scheduleType;
    }

    public String getTitle() {
        return title;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    /**
     * match by name
     *
     * @param name          name of ScheduleTypeEnum
     * @param defaultItem   default item
     * @return ScheduleTypeEnum
     */
    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem){
        for (ScheduleTypeEnum item: ScheduleTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}