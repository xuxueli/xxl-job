package com.xxl.job.admin.core.id;

public class SnowflakeIdWorker {

    // ==============================Fields===========================================
    /** 开始时间截 (2019-01-01) */
    private final long twepoch = 1546272000000L;

    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;

    /** 数据标识id所占的位数 */
    private final long datacenterIdBits = 5L;

    private final long maxMachineId = 1023;

    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final long getMachineIdShift = sequenceBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 机器Id */
    private long machineId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     *
     * @param machineId     工作ID (0~31)
     */
    public SnowflakeIdWorker(long machineId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker machineId can't be greater than %d or less than 0", maxMachineId));
        }
        this.machineId = machineId;
    }

    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果是同一时间生成的，则进行毫秒内序列
        if (timestamp <= lastTimestamp) {

            timestamp = lastTimestamp;
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp++;
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (machineId << getMachineIdShift) //
                | sequence;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
