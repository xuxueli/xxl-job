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
 * 任务配置DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@ApiModel("任务配置参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobConfigDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @NotBlank(message = "编码 不能为空")
    @ApiModelProperty(value = "编码", required = true)
    private String code;

    /**
     * 名称
     */
    @NotBlank(message = "名称 不能为空")
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    /**
     * 源数据源ID
     */
    @NotNull(message = "源数据源ID 不能为空")
    @ApiModelProperty(value = "源数据源ID", required = true)
    private Long srcSourceId;

    /**
     * 目标数据源ID
     */
    @NotNull(message = "目标数据源ID 不能为空")
    @ApiModelProperty(value = "目标数据源ID", required = true)
    private Long targetSourceId;

    /**
     * 源表名
     */
    @NotBlank(message = "源表名 不能为空")
    @ApiModelProperty(value = "源表名", required = true)
    private String srcTable;

    /**
     * 目标表名
     */
    @NotBlank(message = "目标表名 不能为空")
    @ApiModelProperty(value = "目标表名", required = true)
    private String targetTable;

    /**
     * 同步依据字段名,增量同步时有效
     */
    @ApiModelProperty(value = "同步依据字段名,增量同步时有效")
    private String syncBasis;

    /**
     * 同步类型(1:全量,2:增量),默认:2
     */
    @NotNull(message = "同步类型 不能为空")
    @ApiModelProperty(value = "同步类型(1:全量,2:增量),默认:2", required = true)
    private Integer syncType;

    /**
     * 增量方式(0:不是时间,1:是时间)，默认：0
     */
    @NotNull(message = "增量方式 不能为空")
    @ApiModelProperty(value = "增量方式(0:不是时间,1:是时间)，默认：0", required = true)
    private Integer incrementType;

    /**
     * 增量初始值(同步依据是时间有效)
     */
    @ApiModelProperty(value = "增量初始值")
    private Long incrementStart;

    /**
     * 状态(0:禁用,1:启用)
     */
    @NotNull(message = "状态 不能为空")
    @ApiModelProperty(value = "状态(0:禁用,1:启用)", required = true)
    private Integer status;
















}
