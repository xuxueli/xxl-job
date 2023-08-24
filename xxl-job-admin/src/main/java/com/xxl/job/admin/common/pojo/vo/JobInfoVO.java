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
 * 任务信息
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@ApiModel("任务信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class JobInfoVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务组信息
     */
    @ApiModelProperty("任务组信息")
    private JobGroupVO jobGroup;

    /**
     * 任务名
     */
    @ApiModelProperty("任务名")
    private String name;

    /**
     * 作者
     */
    @ApiModelProperty("作者")
    private String author;

    /**
     * 报警邮件
     */
    @ApiModelProperty("报警邮件")
    private String alarmEmail;

    /**
     * 调度类型
     */
    @ApiModelProperty("调度类型")
    private String scheduleType;

    /**
     * 调度配置，值含义取决于调度类型
     */
    @ApiModelProperty("调度配置")
    private String scheduleConf;

    /**
     * 调度过期策略
     */
    @ApiModelProperty("调度过期策略")
    private String misfireStrategy;

    /**
     * 执行器路由策略
     */
    @ApiModelProperty("执行器路由策略")
    private String executorRouteStrategy;

    /**
     * 执行器任务handler
     */
    @ApiModelProperty("执行器任务handler")
    private String executorHandler;

    /**
     * 执行器任务参数
     */
    @ApiModelProperty("执行器任务参数")
    private String executorParam;

    /**
     * 阻塞处理策略
     */
    @ApiModelProperty("阻塞处理策略")
    private String executorBlockStrategy;

    /**
     * 任务执行超时时间，单位秒
     */
    @ApiModelProperty("任务执行超时时间，单位秒")
    private Integer executorTimeout;

    /**
     * 失败重试次数
     */
    @ApiModelProperty("失败重试次数")
    private Integer executorFailRetryCount;

    /**
     * GLUE类型
     */
    @ApiModelProperty("GLUE类型")
    private String glueType;

    /**
     * GLUE源代码
     */
    @ApiModelProperty("GLUE源代码")
    private String glueSource;

    /**
     * GLUE备注
     */
    @ApiModelProperty("GLUE备注")
    private String glueDescription;

    /**
     * GLUE更新时间
     */
    @ApiModelProperty("GLUE更新时间")
    private Long glueUpdatedTime;

    /**
     * 子任务ID
     */
    @ApiModelProperty("子任务ID")
    private List<Long> childJobIds;

    /**
     * 调度状态：0-停止，1-运行
     */
    @ApiModelProperty("调度状态：0-停止，1-运行")
    private Integer triggerStatus;

    /**
     * 上次调度时间
     */
    @ApiModelProperty("上次调度时间")
    private Long triggerLastTime;

    /**
     * 下次调度时间
     */
    @ApiModelProperty("下次调度时间")
    private Long triggerNextTime;


}
