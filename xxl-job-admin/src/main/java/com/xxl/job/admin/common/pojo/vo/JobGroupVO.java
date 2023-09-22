package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 执行器组
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobGroupVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行器AppName
     */
    @ApiModelProperty("执行器AppName")
    private String appName;

    /**
     * 执行器名称
     */
    @ApiModelProperty("执行器名称")
    private String title;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    @ApiModelProperty("执行器地址类型：0=自动注册、1=手动录入")
    private Integer addressType;

    /**
     * 执行器地址列表
     */
    @ApiModelProperty("执行器地址列表")
    private String addresses;







}
