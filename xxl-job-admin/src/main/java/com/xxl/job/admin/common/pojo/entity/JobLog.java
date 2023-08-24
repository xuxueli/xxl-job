package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 任务日志信息
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Data
@TableName("XXL_JOB_LOG")
public class JobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 执行器主键ID
     */
    private Long groupId;

    /**
     * 任务，主键ID
     */
    private Long jobId;

    /**
     * 执行器地址，本次执行的地址
     */
    private String executorAddress;

    /**
     * 执行器任务handler
     */
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    private String executorParam;

    /**
     * 执行器任务分片参数，格式如 1/2
     */
    private String executorShardingParam;

    /**
     * 失败重试次数
     */
    private Integer executorFailRetryCount;

    /**
     * 调度-时间
     */
    private Long triggerTime;

    /**
     * 调度-结果 (-1: 无效, 0:成功, 其他:失败)
     */
    private Integer triggerCode;

    /**
     * 调度-日志
     */
    private String triggerMessage;

    /**
     * 执行-时间
     */
    private Long handleTime;

    /**
     * 执行-状态(-1: 运行中,0:成功,其他:失败)
     */
    private Integer handleCode;

    /**
     * 执行-日志
     */
    private String handleMessage;

    /**
     * 告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败
     */
    private Integer alarmStatus;


}
