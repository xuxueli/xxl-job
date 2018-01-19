package com.xxl.job.executor.register;

/**
 * Created by Adam on 2018/1/9.
 */
public enum RouterPolicy {
    /**
     * 第一个
     */
    FIRST,

    /**
     *  最后一个
     */
    LAST,

    /**
     * 轮询
     */
    ROUND,

    /**
     * 随机
     */
    RANDOM,

    /**
     * 一致性hash
     */
    CONSISTENT_HASH,

    /**
     * 最不经常使用
     */
    LEAST_FREQUENTLY_USED,

    /**
     * 最近最久未使用
     */
    LEAST_RECENTLY_USED,

    /**
     * 故障转移
     */
    FAILOVER

}
