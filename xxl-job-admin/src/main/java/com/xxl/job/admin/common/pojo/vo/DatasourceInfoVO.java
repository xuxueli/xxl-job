package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 数据源配置VO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@ApiModel("数据源配置信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceInfoVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @ApiModelProperty("编码")
    private String code;

    /**
     * 数据源名称
     */
    @ApiModelProperty("数据源名称")
    private String name;

    /**
     * 数据源类型
     */
    @ApiModelProperty("数据源类型")
    private String sourceType;

    /**
     * 是否DB类型（0:否，1：是）
     */
    @ApiModelProperty("是否DB类型（0:否，1：是）")
    private Integer db;

    /**
     * 数据库名
     */
    @ApiModelProperty("数据库名")
    private String databaseName;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * url
     */
    @ApiModelProperty("url")
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
    private Boolean status;



}
