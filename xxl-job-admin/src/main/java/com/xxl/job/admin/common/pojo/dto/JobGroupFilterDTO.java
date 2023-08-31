package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 执行器组过滤查询DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@ApiModel("执行器组过滤查询参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobGroupFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String title;

    /**
     * 执行器AppName
     */
    @ApiModelProperty(value = "执行器AppName")
    private String appName;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    @ApiModelProperty(value = "执行器地址类型：0=自动注册、1=手动录入")
    private Integer addressType;




}
