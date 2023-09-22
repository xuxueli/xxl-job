package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 触发任务DTO
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@Data
@ApiModel("触发任务参数")
public class TriggerJobDTO implements Serializable {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID 不能为空")
    @ApiModelProperty(value = "任务ID", required = true)
    private Long jobInfoId;

    /**
     * 执行参数
     */
    @ApiModelProperty(value = "执行参数")
    private String executorParam;

    /**
     * 机器地址(为空：自动从执行器中获取)
     */
    @ApiModelProperty(value = "机器地址(为空：自动从执行器中获取)")
    private String addresses;





}
