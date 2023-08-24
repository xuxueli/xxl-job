package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 处理日志DTO
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Data
@ApiModel("处理日志参数")
public class HandleLogDTO implements Serializable {

    private static final long serialVersionUID = -7373885778138814893L;

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空")
    @ApiModelProperty(value = "主键", required = true)
    private Long id;

    /**
     * 执行-时间
     */
    @ApiModelProperty("执行-时间")
    private Long handleTime;

    /**
     * 执行-状态(-1: 运行中,0:成功,其他:失败)
     */
    @NotNull(message = "执行-状态 不能为空")
    @ApiModelProperty(value = "执行-状态(-1: 运行中,0:成功,其他:失败)", required = true)
    private Integer handleCode;

    /**
     * 执行-日志
     */
    @ApiModelProperty("执行-日志")
    private String handleMessage;

}
