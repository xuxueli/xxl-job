package com.xxl.job.admin.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图表信息VO
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@Data
@ApiModel("图表信息")
public class ChartInfoVO implements Serializable {

    private static final long serialVersionUID = -7121057955913660628L;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private List<String> days;

    /**
     * 运行数量
     */
    @ApiModelProperty("运行数量")
    private List<Long> runCount;

    /**
     * 成功数量
     */
    @ApiModelProperty("成功数量")
    private List<Long> sucCount;

    /**
     * 失败数量
     */
    @ApiModelProperty("失败数量")
    private List<Long> failCount;

    /**
     * 运行总数
     */
    @ApiModelProperty("运行总数")
    private Long runTotal;

    /**
     * 成功总数
     */
    @ApiModelProperty("成功总数")
    private Long sucTotal;

    /**
     * 失败总数
     */
    @ApiModelProperty("失败总数")
    private Long failTotal;








}
