package com.xxl.job.admin.core.scheduler;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING,

    /**
     * fire once now
     */
    FIRE_ONCE_NOW;

    public static MisfireStrategyEnum match(String name){
        for (MisfireStrategyEnum item: MisfireStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return DO_NOTHING;
    }

}
