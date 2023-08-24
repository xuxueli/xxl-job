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
 * GLUE日志
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@ApiModel("GLUE参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class GlueLogDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务主键ID
     */
    @NotNull(message = "任务主键ID 不能为空")
    @ApiModelProperty(value = "任务主键ID", required = true)
    private Long jobId;

    /**
     * GLUE类型
     */
    @NotBlank(message = "GLUE类型 不能为空")
    @ApiModelProperty(value = "GLUE类型", required = true)
    private String glueType;

    /**
     * GLUE源代码
     */
    @NotBlank(message = "GLUE源代码 不能为空")
    @ApiModelProperty(value = "GLUE源代码", required = true)
    private String glueSource;




}
