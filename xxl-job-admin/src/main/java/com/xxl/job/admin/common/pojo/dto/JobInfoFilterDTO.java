package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 任务信息过滤查询DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@ApiModel("任务信息过滤查询参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobInfoFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行器主键ID
     */
    @ApiModelProperty(value = "执行器主键ID")
    private Long groupId;

    /**
     * kettle主键ID
     */
    @ApiModelProperty("kettle主键ID")
    private Long kettleId;

    /**
     * 任务名
     */
    @ApiModelProperty(value = "任务名")
    private String name;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    private String author;

    /**
     * 调度类型
     */
    @ApiModelProperty(value = "调度类型")
    private String scheduleType;

    /**
     * 执行器任务handler
     */
    @ApiModelProperty(value = "执行器任务handler")
    private String executorHandler;

    /**
     * 调度状态：0-停止，1-运行
     */
    @ApiModelProperty(value = "调度状态：0-停止，1-运行")
    private Integer triggerStatus;






}
