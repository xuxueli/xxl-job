package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * kettle信息
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("kettle信息")
public class KettleInfoVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 模型类型(ktr,kjb)
     */
    @ApiModelProperty("模型类型(ktr,kjb)")
    private String type;

    /**
     * kettle文件
     */
    @ApiModelProperty("kettle文件")
    private byte[] file;

    /**
     * kjb引导文件，模型类型为kjb有效
     */
    @ApiModelProperty("kjb引导文件，模型类型为kjb有效")
    private String guideKjb;

    /**
     * 日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,
     * DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))
     */
    @ApiModelProperty("日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志," +
            "BASIC:基本日志,DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))")
    private String logLevel;

    /**
     * 文件名
     */
    @ApiModelProperty("文件名")
    private String fileName;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private String version;

    /**
     * 状态, 1: 启用, 0:禁用
     */
    @ApiModelProperty(value = "状态, 1: 启用, 0:禁用")
    private Integer status;

    /**
     * 系列
     */
    @ApiModelProperty("系列")
    private String series;

    /**
     * 编码
     */
    @ApiModelProperty("编码")
    private String code;



}
