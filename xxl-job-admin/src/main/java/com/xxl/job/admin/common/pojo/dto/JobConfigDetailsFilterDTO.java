package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 任务配置明细过滤DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("任务配置明细过滤参数")
public class JobConfigDetailsFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细名称
     */
    @ApiModelProperty(value = "明细名称")
    private String name;

    /**
     * 任务配置ID
     */
    @ApiModelProperty(value = "任务配置ID")
    private Long configId;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long jobId;

    /**
     * 状态(0:禁用,1:启用)
     */
    @ApiModelProperty(value = "状态(0:禁用,1:启用)")
    private Integer status;


}
