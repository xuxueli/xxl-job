package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 日志报告DTO
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Data
@ApiModel("日志报告参数")
public class LogReportDTO implements Serializable {

    private static final long serialVersionUID = 6998406303528781530L;

    /**
     * 调度-时间
     */
    @NotBlank(message = "调度时间 不能为空")
    @ApiModelProperty(value = "调度-时间(天)", required = true)
    private Date triggerDay;

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
