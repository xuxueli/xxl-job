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
 * 任务配置VO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-17
 */
@ApiModel("任务配置信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobConfigVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private String code;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 源数据源
     */
    @ApiModelProperty(value = "源数据源")
    private DatasourceInfoVO srcSource;

    /**
     * 目标数据源
     */
    @ApiModelProperty(value = "目标数据源")
    private DatasourceInfoVO targetSource;

    /**
     * 源表名
     */
    @ApiModelProperty(value = "源表名")
    private String srcTable;

    /**
     * 目标表名
     */
    @ApiModelProperty(value = "目标表名")
    private String targetTable;

    /**
     * 同步依据字段名,增量同步时有效
     */
    @ApiModelProperty(value = "同步依据字段名,增量同步时有效")
    private String syncBasis;

    /**
     * 同步类型(1:全量,2:增量),默认:2
     */
    @ApiModelProperty(value = "同步类型(1:全量,2:增量),默认:2")
    private Integer syncType;

    /**
     * 增量方式(0:不是时间,1:是时间)，默认：0
     */
    @ApiModelProperty(value = "增量方式(0:不是时间,1:是时间)，默认：0")
    private Integer incrementType;

    /**
     * 增量初始值(同步依据是时间有效)
     */
    @ApiModelProperty(value = "增量初始值")
    private Long incrementStart;

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
