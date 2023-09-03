package com.xxl.job.admin.common.pojo.dto;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 任务信息DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@ApiModel("任务参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobInfoDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行器主键ID
     */
    @NotNull(message = "执行器主键ID 不能为空")
    @ApiModelProperty(value = "执行器主键ID", required = true)
    private Long groupId;

    /**
     * 任务名
     */
    @NotBlank(message = "任务名 不能为空")
    @ApiModelProperty(value = "任务名", required = true)
    private String name;

    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    private String author;

    /**
     * 报警邮件
     */
    @ApiModelProperty(value = "报警邮件")
    private String alarmEmail;

    /**
     * 调度类型
     */
    @NotBlank(message = "调度类型 不能为空")
    @ApiModelProperty(value = "调度类型", required = true)
    private String scheduleType;

    /**
     * 调度配置，值含义取决于调度类型
     */
    @ApiModelProperty(value = "调度配置，值含义取决于调度类型")
    private String scheduleConf;

    /**
     * 调度过期策略
     */
    @NotBlank(message = "调度过期策略 不能为空")
    @ApiModelProperty(value = "调度过期策略", required = true)
    private String misfireStrategy;

    /**
     * 执行器路由策略
     */
    @NotBlank(message = "执行器路由策略 不能为空")
    @ApiModelProperty(value = "执行器路由策略", required = true)
    private String executorRouteStrategy;

    /**
     * 执行器任务handler
     */
    @ApiModelProperty(value = "执行器任务handler")
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    @ApiModelProperty(value = "执行器任务参数")
    private String executorParam;

    /**
     * 阻塞处理策略
     */
    @NotBlank(message = "阻塞处理策略 不能为空")
    @ApiModelProperty(value = "阻塞处理策略", required = true)
    private String executorBlockStrategy;

    /**
     * 调度状态：0-停止，1-运行
     */
    @NotNull(message = "调度状态 不能为空")
    @ApiModelProperty(value = "调度状态：0-停止，1-运行", required = true)
    private Integer triggerStatus;

    /**
     * 任务执行超时时间，单位秒
     */
    @ApiModelProperty(value = "任务执行超时时间，单位秒")
    private Integer executorTimeout;

    /**
     * 失败重试次数
     */
    @ApiModelProperty(value = "失败重试次数")
    private Integer executorFailRetryCount;

    /**
     * GLUE类型
     */
    @NotBlank(message = "GLUE类型 不能为空")
    @ApiModelProperty(value = "GLUE类型", required = true)
    private String glueType;

    /**
     * GLUE源代码
     */
    @ApiModelProperty(value = "GLUE源代码")
    private String glueSource;

    /**
     * GLUE备注
     */
    @ApiModelProperty(value = "GLUE备注")
    private String glueDescription;

    /**
     * GLUE更新时间
     */
    @ApiModelProperty(value = "GLUE更新时间")
    private Long glueUpdatedTime;

    /**
     * 子任务ID
     */
    @ApiModelProperty("子任务ID")
    private List<Long> childJobIds;


}
