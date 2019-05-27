package com.xuxueli.job.client;

/**
 * @author Luo Bao Ding
 * @since 2019/5/27
 */
public enum ExecutorRouteStrategyEnum {
    FIRST,
    LAST,
    ROUND,
    RANDOM,
    CONSISTENT_HASH,
    LEAST_FREQUENTLY_USED,
    LEAST_RECENTLY_USED,
    FAILOVER,
    BUSYOVER,
    SHARDING_BROADCAST;

    public String getName() {
        return this.name();
    }

}
