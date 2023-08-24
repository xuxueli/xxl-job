package com.xxl.job.admin.strategy;

import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路由器参数
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Data
public class RouterParam implements Serializable {

    private static final long serialVersionUID = 3028302717281104519L;

    /**
     * 执行器策略
     */
    private ExecutorRouteStrategyEnum executorRouteStrategy;

    /**
     * 任务ID
     */
    private Long jobId;

    /**
     * 执行器地址列表
     */
    private List<String> registries;









}
