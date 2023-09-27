package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 任务日志信息DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Data
@ApiModel("任务日志参数")
public class JobLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务组主键ID
     */
    @NotNull(message = "任务组主键ID 不能为空")
    @ApiModelProperty(value = "任务组主键ID", required = true)
    private Long groupId;

    /**
     * 任务主键ID
     */
    @NotNull(message = "任务主键ID 不能为空")
    @ApiModelProperty(value = "任务主键ID", required = true)
    private Long jobId;

    /**
     * 执行器地址，本次执行的地址
     */
    @ApiModelProperty("执行器地址，本次执行的地址")
    private String executorAddress;

    /**
     * 执行器任务handler
     */
    @ApiModelProperty("执行器任务handler")
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    @ApiModelProperty("执行器任务参数")
    private String executorParam;

    /**
     * 执行器任务分片参数，格式如 1/2
     */
    @ApiModelProperty("执行器任务分片参数，格式如 1/2")
    private String executorShardingParam;

    /**
     * 失败重试次数
     */
    @NotNull(message = "失败重试次数 不能为空")
    @ApiModelProperty(value = "失败重试次数",required = true)
    private Integer executorFailRetryCount;

    /**
     * 调度-时间
     */
    @ApiModelProperty("调度-时间")
    private Long triggerTime;

    /**
     * 调度-结果 (-1: 无效, 0:成功, 其他:失败)
     */
    @NotNull(message = "调度-结果 不能为空")
    @ApiModelProperty(value = "调度-结果 (-1: 无效, 0:成功, 其他:失败)",required = true)
    private Integer triggerCode;

    /**
     * 调度-日志
     */
    @ApiModelProperty("调度-日志")
    private String triggerMessage;

    /**
     * 执行-时间
     */
    @ApiModelProperty("执行-时间")
    private Long handleTime;

    /**
     * 执行-状态(-1: 运行中,0:成功,其他:失败)
     */
    @NotNull(message = "执行-状态 不能为空")
    @ApiModelProperty(value = "执行-状态(-1: 运行中,0:成功,其他:失败)", required = true)
    private Integer handleCode;

    /**
     * 执行-日志
     */
    @ApiModelProperty("执行-日志")
    private String handleMessage;

    /**
     * 告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败
     */
    @NotNull(message = "告警状态 不能为空")
    @ApiModelProperty(value = "告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败", required = true)
    private Integer alarmStatus;


}
