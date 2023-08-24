package com.xxl.job.admin.strategy;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 执行器路由器策略
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Component
public class ExecutorRouterStrategy {

    @Autowired
    private List<ExecutorRouter> routers;

    /**
     * 路线
     *
     * @param param 参数
     * @return {@link String}
     */
    public String route(RouterParam param) {
        ExecutorRouter executorRouter = routers.stream().filter(a -> a.supports(param.getExecutorRouteStrategy()))
                .findAny().orElse(null);
        Assert.notNull(executorRouter, String.format("暂不支持 【%s】该路由策略", param.getExecutorRouteStrategy().name()));
        return executorRouter.route(param);
    }









}
