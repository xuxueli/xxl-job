package com.xxl.job.admin.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 最近最久未使用
 * <p>
 * 单个JOB对应的每个执行器，最久为使用的优先被选举
 * a、LFU(Least Frequently Used)：最不经常使用，频率/次数
 * b(*)、LRU(Least Recently Used)：最近最久未使用，时间
 * <p>
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ExecutorRouteLRU extends BaseExecutorRouter {

    private final ConcurrentMap<Long, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap<>();
    private static volatile long CACHE_VALID_TIME = 0;

    @Override
    public Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum) {
        return ExecutorRouteStrategyEnum.LEAST_RECENTLY_USED.equals(executorRouteStrategyEnum);
    }

    @Override
    protected String doRoute(Long jobId, List<String> registries) {
        return routeAddress(jobId, registries);
    }

    public String routeAddress(Long jobId, List<String> addressList) {

        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;
        }

        // init lru
        LinkedHashMap<String, String> lruItem = jobLRUMap.get(jobId);
        if (CollectionUtil.isEmpty(lruItem)) {
            /*
             * LinkedHashMap
             *      a、accessOrder：true=访问顺序排序（get/put时排序）；false=插入顺序排期；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<>(16, 0.75f, true);
            jobLRUMap.putIfAbsent(jobId, lruItem);
        }

        // put new
        for (String address: addressList) {
            if (!lruItem.containsKey(address)) {
                lruItem.put(address, address);
            }
        }
        // remove old
        List<String> delKeys = new ArrayList<>();
        for (String existKey: lruItem.keySet()) {
            if (!addressList.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size() > 0) {
            for (String delKey: delKeys) {
                lruItem.remove(delKey);
            }
        }

        // load
        String eldestKey = lruItem.entrySet().iterator().next().getKey();
        return lruItem.get(eldestKey);
    }



}
