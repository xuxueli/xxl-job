package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * @author xuxueli 2020-10-29 21:11:23  调度类型
 */
public enum ScheduleTypeEnum {

    //该类型不会主动触发调度；
    NONE(I18nUtil.getString("schedule_type_none")),

    /**
     * schedule by cron
     */
    //该类型将会通过CRON，触发任务调度；
    CRON(I18nUtil.getString("schedule_type_cron")),

    /**
     * schedule by fixed rate (in seconds)
     */
    //固定速度：该类型将会以固定速度，触发任务调度；按照固定的间隔时间，周期性触发；
    FIX_RATE(I18nUtil.getString("schedule_type_fix_rate")),

    /**
     * schedule by fix delay (in seconds)， after the last time 固定延迟：该类型将会以固定延迟，触发任务调度；按照固定的延迟时间，从上次调度结束后开始计算延迟时间，到达延迟时间后触发下次调度；
     */
    /*FIX_DELAY(I18nUtil.getString("schedule_type_fix_delay"))*/;

    private String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem){
        for (ScheduleTypeEnum item: ScheduleTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
