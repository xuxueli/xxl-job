package com.xxl.job.admin.common.pojo.dto;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * kettle信息DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("kettle信息参数")
public class KettleInfoDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @NotBlank(message = "名称 不能为空")
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    /**
     * 模型类型(ktr,kjb)
     */
    @NotBlank(message = "模型类型 不能为空")
    @ApiModelProperty(value = "模型类型(ktr,kjb)", required = true)
    private String type;

    /**
     * kettle文件
     */
    @ApiModelProperty("kettle文件")
    private MultipartFile file;

    /**
     * kjb引导文件，模型类型为kjb有效
     */
    @ApiModelProperty("kjb引导文件，模型类型为kjb有效")
    private String guideKjb;

    /**
     * 日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,
     * DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))
     */
    @NotBlank(message = "日志级别 不能为空")
    @ApiModelProperty(value = "日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志," +
            "BASIC:基本日志,DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))", required = true)
    private String logLevel;

    /**
     * 状态, 1: 启用, 0:禁用
     */
    @NotNull(message = "状态 不能为空")
    @ApiModelProperty(value = "状态, 1: 启用, 0:禁用", required = true)
    private Integer status;



 

}
