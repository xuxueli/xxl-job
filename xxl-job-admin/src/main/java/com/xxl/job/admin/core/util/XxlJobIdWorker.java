package com.xxl.job.admin.core.util;

/**
 * 主键生成器，基于Snowflake实现64位自增ID算法
 *
 * @author dudiao
 * @date 2020/3/21 下午 04:15
 */
public class XxlJobIdWorker {

    /**
     * 主机和进程的机器码
     */
    private static Sequence WORKER = new Sequence();

    public static long getId() {
        return WORKER.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(WORKER.nextId());
    }

    /**
     * <p>
     * 有参构造器
     * </p>
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public static void initSequence(long workerId, long datacenterId) {
        WORKER = new Sequence(workerId, datacenterId);
    }
}
