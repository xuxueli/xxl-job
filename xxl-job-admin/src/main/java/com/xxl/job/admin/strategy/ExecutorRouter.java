package com.xxl.job.admin.strategy;

import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;

/**
 * 执行器路由策略
 * @author Rong.Jia
 * @date 2023/05/15
 */
public interface ExecutorRouter {

    /**
     * 支持
     *
     * @param executorRouteStrategyEnum 路由策略
     * @return {@link Boolean}
     */
    Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum);

    /**
     * 路由地址
     *
     * @param param 参数
     * @return {@link String} 地址
     */
    String route(RouterParam param);


}
