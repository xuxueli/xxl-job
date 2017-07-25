package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.route.strategy.*;

/**
 * Created by xuxueli on 17/3/10.
 */
public enum ExecutorRouteStrategyEnum {

    FIRST("第一个", new ExecutorRouteFirst()),
    LAST("最后一个", new ExecutorRouteLast()),
    ROUND("轮询", new ExecutorRouteRound()),
    RANDOM("随机", new ExecutorRouteRandom()),
    CONSISTENT_HASH("一致性HASH", new ExecutorRouteConsistentHash()),
    LEAST_FREQUENTLY_USED("最不经常使用", new ExecutorRouteLFU()),
    LEAST_RECENTLY_USED("最近最久未使用", new ExecutorRouteLRU()),
    FAILOVER("故障转移", new ExecutorRouteFailover()),
    BUSYOVER("忙碌转移", new ExecutorRouteBusyover()),
    SHARDING_BROADCAST("分片广播", null);

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
