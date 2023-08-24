package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 工作日志清理DTO
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@Data
@ApiModel("工作日志清理参数")
public class JobLogCleanDTO implements Serializable {

    private static final long serialVersionUID = -2585132736165743607L;

    /**
     * 执行器ID
     */
    @ApiModelProperty(value = "执行器ID")
    private Long groupId;

    /**
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long jobId;

    /**
     * 清理方式
     * 1:清理一个月之前日志数据
     * 2:清理三个月之前日志数据
     * 3:清理六个月之前日志数据
     * 4:清理一年之前日志数据
     * 5:清理一千条以前日志数据
     * 6:清理一万条以前日志数据
     * 7:清理三万条以前日志数据
     * 8:清理十万条以前日志数据
     * 9:清理所有日志数据
     */
    @NotNull(message = "清理方式 不能为空")
    @ApiModelProperty(value = "清理方式 1:清理一个月之前日志数据\n" +
                                "     2:清理三个月之前日志数据\n" +
                                "     3:清理六个月之前日志数据\n" +
                                "     4:清理一年之前日志数据\n" +
                                "     5:清理一千条以前日志数据\n" +
                                "     6:清理一万条以前日志数据\n" +
                                "     7:清理三万条以前日志数据\n" +
                                "     8:清理十万条以前日志数据\n" +
                                "     9:清理所有日志数据", required = true)
    private Integer type;









}
