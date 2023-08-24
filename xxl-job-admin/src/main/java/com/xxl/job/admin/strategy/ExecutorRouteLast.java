package com.xxl.job.admin.strategy;

import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 最后一个
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ExecutorRouteLast extends BaseExecutorRouter {

    @Override
    public Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum) {
        return ExecutorRouteStrategyEnum.LAST.equals(executorRouteStrategyEnum);
    }

    @Override
    protected String doRoute(Long jobId, List<String> registries) {
        return registries.get(registries.size() - 1);
    }


}
