package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 任务配置明细VO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("任务配置明细信息")
public class JobConfigDetailsVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细名称
     */
    @ApiModelProperty(value = "明细名称")
    private String name;

    /**
     * 任务配置
     */
    @ApiModelProperty(value = "任务配置")
    private JobConfigVO config;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long jobId;

    /**
     * 源数据源(优先级高于主表)
     */
    @ApiModelProperty(value = "源数据源(优先级高于主表)")
    private DatasourceInfoVO srcSource;

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
    @ApiModelProperty(value = "状态(0:禁用,1:启用)")
    private Integer status;

    /**
     * 字段配置
     */
    @ApiModelProperty(value = "字段配置")
    private List<FieldsConfigVO> fieldsConfigs;


}
