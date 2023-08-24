package com.xxl.job.admin.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 日志报告信息
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Data
@ApiModel("日志报告信息")
public class LogReportVO implements Serializable {

    private static final long serialVersionUID = 6998406303528781530L;

    /**
     * 调度-时间
     */
    @ApiModelProperty(value = "调度-时间(天)")
    private Long triggerDay;

    /**
     * 运行中-日志数量
     */
    @ApiModelProperty(value = "运行中-日志数量")
    private Long runningCount;

    /**
     * 执行成功-日志数量
     */
    @ApiModelProperty(value = "执行成功-日志数量")
    private Long sucCount;

    /**
     * 执行失败-日志数量
     */
    @ApiModelProperty(value = "执行失败-日志数量")
    private Long failCount;



}
