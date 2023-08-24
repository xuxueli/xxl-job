package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 字段映射配置过滤查询DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("字段映射配置过滤查询参数")
@Data
public class FieldsConfigFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务配置ID
     */
    @ApiModelProperty(value = "任务配置ID")
    private Long configId;

    /**
     * 任务配置明细ID
     */
    @ApiModelProperty(value = "任务配置明细ID")
    private Long configDetailsId;

    /**
     * 源字段名
     */
    @ApiModelProperty(value = "源字段名")
    private String srcName;

    /**
     * 目标字段名
     */
    @ApiModelProperty(value = "目标字段名")
    private String targetName;



}
