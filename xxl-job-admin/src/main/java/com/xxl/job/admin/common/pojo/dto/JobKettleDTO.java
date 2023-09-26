package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 任务 Kettle模型 DTO
 *
 * @author Rong.Jia
 * @date 2023/09/26
 */
@Data
@ApiModel("任务-kettle模型修改参数")
public class JobKettleDTO implements Serializable {

    /**
     * 任务ID集合
     */
    @NotEmpty(message = "任务ID集合 不能为空")
    @ApiModelProperty(value = "任务ID集合", required = true)
    private List<Long> jobIds;

    /**
     * kettle信息ID
     */
    @NotNull(message = "kettle信息ID 不能为空")
    @ApiModelProperty(value = "kettle信息ID", required = true)
    private Long kettleId;


}
