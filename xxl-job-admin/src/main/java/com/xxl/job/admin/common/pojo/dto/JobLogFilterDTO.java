package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 任务日志过滤查询DTO
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("任务日志过滤查询参数")
public class JobLogFilterDTO extends PageDTO implements Serializable {

    /**
     * 执行器ID
     */
    @ApiModelProperty("执行器ID")
    private Long groupId;

    /**
     * 任务ID集合
     */
    @ApiModelProperty("任务ID集合")
    private List<Long> jobIds;

    /**
     * 状态，成功：1，失败：2，运行中：3
     */
    @ApiModelProperty("状态，成功：1，失败：2，运行中：3")
    private Integer status;









}
