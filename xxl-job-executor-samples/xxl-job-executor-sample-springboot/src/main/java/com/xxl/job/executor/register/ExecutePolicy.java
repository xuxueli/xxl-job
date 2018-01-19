package com.xxl.job.executor.register;

/**
 * Created by Adam on 2018/1/9.
 */
public enum ExecutePolicy {

    /**
     * 单机串行
     */
    SERIAL_EXECUTION,

    /**
     * 丢弃后续调度
     */
    DISCARD_LATER,

    /**
     * 覆盖之前调度
     */
    COVER_EARLY
}
