package com.xxl.job.admin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 路由策略
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Getter
@AllArgsConstructor
public enum ExecutorRouteStrategyEnum {

    // NULL, 第一个,最后一个，轮询
    NULL(""),
    FIRST("第一个"),
    LAST("最后一个"),
    ROUND("轮询"),
    RANDOM("随机"),
    CONSISTENT_HASH("一致性HASH"),
    LEAST_FREQUENTLY_USED("最不经常使用"),
    LEAST_RECENTLY_USED("最近最久未使用"),
    FAILOVER("故障转移"),
    BUSYOVER("忙碌转移"),
    SHARDING_BROADCAST("分片广播"),


    ;

    private final String value;


    public static ExecutorRouteStrategyEnum match(String name){
        return Arrays.stream(ExecutorRouteStrategyEnum.values())
                .filter(a -> a.name().equals(name))
                .findAny().orElse(NULL);
    }

}
