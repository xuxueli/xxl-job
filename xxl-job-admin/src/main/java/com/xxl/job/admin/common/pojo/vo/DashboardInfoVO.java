package com.xxl.job.admin.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 仪表盘信息VO
 *
 * @author Rong.Jia
 * @date 2023/05/16
 */
@Data
@ApiModel("仪表盘信息")
public class DashboardInfoVO implements Serializable {

    /**
     * 任务数量
     */
    private Long jobInfoCount;

    /**
     * 任务日志数量
     */
    private Long jobLogCount;

    /**
     * 任务日志成功数量
     */
    private Long jobLogSuccessCount;

    /**
     * 执行器数量
     */
    private Integer executorCount;

    /**
     * 用户数量
     */
    private Long userInfoCount;


}
