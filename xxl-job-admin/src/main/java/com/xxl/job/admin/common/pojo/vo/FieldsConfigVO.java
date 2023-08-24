package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 字段映射配置VO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("字段映射配置信息")
@Data
public class FieldsConfigVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务配置ID
     */
    @ApiModelProperty(value = "任务配置ID")
    private Long configId;

    /**
     * 任务配置明细ID
     */
    @ApiModelProperty(value = "任务配置明细ID")
    private Long configDetailsId;

    /**
     * 源字段名
     */
    @ApiModelProperty(value = "源字段名")
    private String srcName;

    /**
     * 源字段类型
     */
    @ApiModelProperty(value = "源字段类型")
    private String srcType;

    /**
     * 源字段默认值
     */
    @ApiModelProperty(value = "源字段默认值")
    private String srcDefaultValue;

    /**
     * 目标字段名
     */
    @ApiModelProperty(value = "目标字段名")
    private String targetName;

    /**
     * 目标字段类型
     */
    @ApiModelProperty(value = "目标段类型")
    private String targetType;

    /**
     * 目标字段默认值
     */
    @ApiModelProperty(value = "目标段默认值")
    private String targetDefaultValue;


}
