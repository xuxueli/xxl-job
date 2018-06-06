package com.xxl.job.core.annotationtask.enums;

public enum ExecutorRouteStrategy {

    FIRST("jobconf_route_first"),
    LAST("jobconf_route_last"),
    ROUND("jobconf_route_round"),
    RANDOM("jobconf_route_random"),
    CONSISTENT_HASH("jobconf_route_consistenthash"),
    LEAST_FREQUENTLY_USED("jobconf_route_lfu"),
    LEAST_RECENTLY_USED("jobconf_route_lru"),
    FAILOVER("jobconf_route_failover"),
    BUSYOVER("jobconf_route_busyover"),
    SHARDING_BROADCAST("jobconf_route_shard");//分片 todo 这个路由策略在代码里面

    ExecutorRouteStrategy(String title) {
        this.title = title;
    }

    private String title;//key

    public String getTitle() {
        return title;
    }

}
