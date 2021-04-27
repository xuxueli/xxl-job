package com.xxl.job.admin.core.scheduler;

import com.xxl.job.admin.core.util.I18nUtil;

/**
 * @author xuxueli 2020-10-29 21:11:23  调度过期策略
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    //调度过期后，忽略过期的任务，从当前时间开始重新计算下次触发时间；
    DO_NOTHING(I18nUtil.getString("misfire_strategy_do_nothing")),

    /**
     * fire once now
     */
    //立即执行一次：调度过期后，立即执行一次，并从当前时间开始重新计算下次触发时间；
    FIRE_ONCE_NOW(I18nUtil.getString("misfire_strategy_fire_once_now"));

    private String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem){
        for (MisfireStrategyEnum item: MisfireStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
