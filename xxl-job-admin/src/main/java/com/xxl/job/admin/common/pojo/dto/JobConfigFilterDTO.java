package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 任务配置过滤查询DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@ApiModel("任务配置过滤查询参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobConfigFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 同步类型(1:全量,2:增量),默认:2
     */
    @ApiModelProperty(value = "同步类型(1:全量,2:增量),默认:2")
    private Integer syncType;

    /**
     * 状态(0:禁用,1:启用)
     */
    @ApiModelProperty(value = "状态(0:禁用,1:启用)")
    private Integer status;
















}
