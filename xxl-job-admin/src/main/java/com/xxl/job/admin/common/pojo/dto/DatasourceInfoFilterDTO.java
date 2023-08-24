package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 数据源配置过滤查询DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@ApiModel("数据源配置过滤参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceInfoFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源名称
     */
    @ApiModelProperty(value = "数据源名称")
    private String name;

    /**
     * 数据源类型
     */
    @ApiModelProperty(value = "数据源类型")
    private String sourceType;

    /**
     * 状态： 1启用 0禁用
     */
    @ApiModelProperty("状态： 1启用 0禁用")
    private Integer status;



}
