package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * kettle信息过滤查询DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("kettle信息过滤查询参数")
public class KettleInfoFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 模型类型(ktr,kjb)
     */
    @ApiModelProperty(value = "模型类型(ktr,kjb)")
    private String type;

    /**
     * 日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,
     * DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))
     */
    @ApiModelProperty(value = "日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志," +
            "BASIC:基本日志,DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))")
    private String logLevel;

    /**
     * 状态, 1: 启用, 0:禁用
     */
    @ApiModelProperty(value = "状态, 1: 启用, 0:禁用")
    private Integer status;


 

}
