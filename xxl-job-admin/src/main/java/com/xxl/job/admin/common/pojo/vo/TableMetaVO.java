package com.xxl.job.admin.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 表 元信息
 *
 * @author Rong.Jia
 * @date 2023/06/02
 */
@Data
@ApiModel("表 元信息")
public class TableMetaVO implements Serializable {

    private static final long serialVersionUID = 3984619857927632201L;

    /**
     * 表名
     */
    @ApiModelProperty("表名")
    private String tableName;

    /**
     * 列名
     */
    @ApiModelProperty("列名")
    private String name;

    /**
     * 类型，对应java.sql.Types中的类型
     */
    @ApiModelProperty("类型")
    private Integer type;

    /**
     * 类型名称
     */
    @ApiModelProperty("类型名称")
    private String typeName;

    /**
     * 大小或数据长度
     */
    @ApiModelProperty("大小或数据长度")
    private Long size;

    /**
     * 是否为可空
     */
    @ApiModelProperty("是否为可空")
    private Boolean isNullable;

    /**
     * 注释
     */
    @ApiModelProperty("注释")
    private String comment;

    /**
     * 是否自增
     */
    @ApiModelProperty("是否自增")
    private Boolean autoIncrement;

    /**
     * 字段默认值<br>
     *  default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be {@code null})
     */
    @ApiModelProperty("字段默认值")
    private String columnDef;

    /**
     * 是否为主键
     */
    @ApiModelProperty("是否为主键")
    private Boolean isPk;









}
