package com.xxl.job.admin.core.scheduler.route;

import com.xxl.job.admin.core.scheduler.route.strategy.*;

/**
 * Created by xuxueli on 17/3/10.
 */
public enum ExecutorRouteStrategyEnum {

    FIRST("First", new ExecutorRouteFirst()),
    LAST("Last", new ExecutorRouteLast()),
    ROUND("Round", new ExecutorRouteRound()),
    RANDOM("Random", new ExecutorRouteRandom()),
    CONSISTENT_HASH("Consistent Hash", new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED("Least Frequently Used", new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED("Least Recently Used", new ExecutorRouteLRU()),
    FAILOVER("Failover", new ExecutorRouteFailover()),
    BUSYOVER("Busy Over", new ExecutorRouteBusyover()),
    SHARDING_BROADCAST("Sharding Broadcast", null);

    ExecutorRouteStrategyEnum(String title, ExecutorRouter router) {
        this.title = title;
        this.router = router;
    }

    private String title;
    private ExecutorRouter router;

    public String getTitle() {
        return title;
    }
    public ExecutorRouter getRouter() {
        return router;
    }

    /**
     * match router
     */
    public static ExecutorRouteStrategyEnum match(String name, ExecutorRouteStrategyEnum defaultItem){
        if (name != null) {
            for (ExecutorRouteStrategyEnum item: ExecutorRouteStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }

}