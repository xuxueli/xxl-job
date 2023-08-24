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
 * 任务配置明细DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("任务配置明细参数")
public class JobConfigDetailsDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细名称
     */
    @NotBlank(message = "明细名称 不能为空")
    @ApiModelProperty(value = "明细名称", required = true)
    private String name;

    /**
     * 任务配置ID
     */
    @NotNull(message = "任务配置ID 不能为空")
    @ApiModelProperty(value = "任务配置ID", required = true)
    private Long configId;

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID 不能为空")
    @ApiModelProperty(value = "任务ID", required = true)
    private Long jobId;

    /**
     * 源数据源ID(优先级高于主表)
     */
    @ApiModelProperty(value = "源数据源ID(优先级高于主表)")
    private Long srcSourceId;

    /**
     * 源表名(优先级高于主表)
     */
    @ApiModelProperty(value = "源表名(优先级高于主表)")
    private String srcTable;

    /**
     * 目标表名(优先级高于主表)
     */
    @ApiModelProperty(value = "目标表名(优先级高于主表)")
    private String targetTable;

    /**
     * 状态(0:禁用,1:启用)
     */
    @NotNull(message = "状态 不能为空")
    @ApiModelProperty(value = "状态(0:禁用,1:启用)", required = true)
    private Integer status;


}
