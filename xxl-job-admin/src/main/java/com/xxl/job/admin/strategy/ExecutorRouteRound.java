package com.xxl.job.admin.strategy;

import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ExecutorRouteRound extends BaseExecutorRouter {

    private final ConcurrentMap<Long, AtomicInteger> routeCountEachJob = new ConcurrentHashMap<>();
    private static volatile long cacheValidTime = 0;

    @Override
    public Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum) {
        return ExecutorRouteStrategyEnum.ROUND.equals(executorRouteStrategyEnum);
    }

    @Override
    protected String doRoute(Long jobId, List<String> registries) {
        return registries.get(count(jobId) % registries.size());
    }

    private int count(Long jobId) {
        // cache clear
        if (System.currentTimeMillis() > cacheValidTime) {
            routeCountEachJob.clear();
            cacheValidTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        AtomicInteger count = routeCountEachJob.get(jobId);
        if (count == null || count.get() > 1000000) {
            // 初始化时主动Random一次，缓解首次压力
            count = new AtomicInteger(new Random().nextInt(100));
        } else {
            // count++
            count.addAndGet(1);
        }
        routeCountEachJob.put(jobId, count);
        return count.get();
    }

}


