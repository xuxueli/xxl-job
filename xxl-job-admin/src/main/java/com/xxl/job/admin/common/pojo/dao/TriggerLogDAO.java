package com.xxl.job.admin.common.pojo.dao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 触发日志DAO
 *
 * @author Rong.Jia
 * @date 2023/09/21
 */
@Data
@ApiModel("触发日志参数")
public class TriggerLogDAO implements Serializable {

    private static final long serialVersionUID = -7114492492682222531L;

    /**
     * 主键
     */
    private Long id;

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
    private String triggerTime;

    /**
     * 调度-结果 (-1: 无效, 0:成功, 其他:失败)
     */
    private Integer triggerCode;

    /**
     * 调度-日志
     */
    private String triggerMessage;




}
