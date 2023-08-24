package com.xxl.job.admin.common.pojo.dto;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 执行器组DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("执行器组参数")
public class JobGroupDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行器AppName
     */
    @NotBlank(message = "执行器AppName 不能为空")
    @ApiModelProperty(value = "执行器AppName", required = true)
    private String appName;

    /**
     * 执行器名称
     */
    @NotBlank(message = "执行器名称 不能为空")
    @ApiModelProperty(value = "执行器名称", required = true)
    private String title;

    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    @NotNull(message = "执行器地址类型 不能为空")
    @ApiModelProperty(value = "执行器地址类型：0=自动注册、1=手动录入", required = true)
    private Integer addressType;

    /**
     * 执行器地址列表
     */
    @ApiModelProperty("执行器地址列表 (地址类型->1 不能为空)")
    private List<String> addresses;

}
