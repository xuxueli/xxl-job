package com.xxl.job.admin.strategy;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分片广播
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ExecutorRouteBroadcast extends BaseExecutorRouter {

    @Override
    public Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum) {
        return ExecutorRouteStrategyEnum.SHARDING_BROADCAST.equals(executorRouteStrategyEnum);
    }

    @Override
    protected String doRoute(Long jobId, List<String> registries) {
       return StrUtil.EMPTY;
    }



  
}
