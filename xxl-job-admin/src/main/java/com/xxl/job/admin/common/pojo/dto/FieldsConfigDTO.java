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
 * 字段映射配置DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("字段映射配置参数")
@Data
public class FieldsConfigDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务配置ID
     */
    @NotNull(message = "任务配置ID 不能为空")
    @ApiModelProperty(value = "任务配置ID", required = true)
    private Long configId;

    /**
     * 任务配置明细ID
     */
    @ApiModelProperty(value = "任务配置明细ID")
    private Long configDetailsId;

    /**
     * 源字段名
     */
    @NotBlank(message = "源字段名 不能为空")
    @ApiModelProperty(value = "源字段名", required = true)
    private String srcName;

    /**
     * 源字段类型
     */
    @NotBlank(message = "源字段类型 不能为空")
    @ApiModelProperty(value = "源字段类型", required = true)
    private String srcType;

    /**
     * 源字段默认值
     */
    @ApiModelProperty(value = "源字段默认值")
    private String srcDefaultValue;

    /**
     * 目标字段名
     */
    @NotBlank(message = "目标字段名 不能为空")
    @ApiModelProperty(value = "目标字段名", required = true)
    private String targetName;

    /**
     * 目标字段类型
     */
    @NotBlank(message = "目标段类型 不能为空")
    @ApiModelProperty(value = "目标段类型", required = true)
    private String targetType;

    /**
     * 目标字段默认值
     */
    @ApiModelProperty(value = "目标段默认值")
    private String targetDefaultValue;


}
