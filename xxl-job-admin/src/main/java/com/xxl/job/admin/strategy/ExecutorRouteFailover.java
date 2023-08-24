package com.xxl.job.admin.strategy;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.service.ExecutorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 故障转移
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ExecutorRouteFailover extends BaseExecutorRouter {

    @Autowired
    private ExecutorClient executorClient;

    @Override
    public Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum) {
        return ExecutorRouteStrategyEnum.FAILOVER.equals(executorRouteStrategyEnum);
    }

    @Override
    protected String doRoute(Long jobId, List<String> registries) {
        for (String address : registries) {
            if (executorClient.beat(address)) {
                return address;
            }
        }
        return StrUtil.EMPTY;
    }

}
