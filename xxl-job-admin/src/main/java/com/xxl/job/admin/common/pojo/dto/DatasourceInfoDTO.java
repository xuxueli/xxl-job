package com.xxl.job.admin.common.pojo.dto;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 数据源配置DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@ApiModel("数据源配置参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceInfoDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称 不能为空")
    @ApiModelProperty(value = "数据源名称", required = true)
    private String name;

    /**
     * 数据源类型
     */
    @NotBlank(message = "数据源类型 不能为空")
    @ApiModelProperty(value = "数据源类型", required = true)
    private String sourceType;

    /**
     * 是否DB类型（0:否，1：是）
     */   
    @NotNull(message = "是否DB类型 不能为空")
    @ApiModelProperty(value = "是否DB类型（0:否，1：是）", required = true)
    private Integer db;

    /**
     * 数据库名
     */
    @ApiModelProperty(value = "数据库名")
    private String databaseName;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * url
     */
    @NotBlank(message = "URL 不能为空")
    @ApiModelProperty(value = "url", required = true)
    private String url;

    /**
     * jdbc驱动类
     */
    @ApiModelProperty("jdbc驱动类")
    private String driverClass;

    /**
     * 初始连接数（默认：30）
     */
    @ApiModelProperty("初始连接数（默认：30）")
    private Integer initialSize;

    /**
     * 最小空闲（默认：10）
     */
    @ApiModelProperty("最小空闲（默认：10）")
    private Integer minIdle;

    /**
     * 最大连接数(默认：100)
     */
    @ApiModelProperty("最大连接数(默认：100)")
    private Integer maxActive;

    /**
     * 状态： 1启用 0禁用
     */
    @ApiModelProperty("状态： 1启用 0禁用")
    private Integer status;



}
