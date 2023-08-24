package com.xxl.job.admin.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.xxl.job.admin.common.enums.ExecutorRouteStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 最不经常使用
 * <p>
 * 单个JOB对应的每个执行器，使用频率最低的优先被选举
 * a(*)、LFU(Least Frequently Used)：最不经常使用，频率/次数
 * b、LRU(Least Recently Used)：最近最久未使用，时间
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Component
public class ExecutorRouteLFU extends BaseExecutorRouter {

    private final ConcurrentMap<Long, HashMap<String, Integer>> jobLfuMap = new ConcurrentHashMap<>();
    private static volatile long CACHE_VALID_TIME = 0;

    @Override
    public Boolean supports(ExecutorRouteStrategyEnum executorRouteStrategyEnum) {
        return ExecutorRouteStrategyEnum.LEAST_FREQUENTLY_USED.equals(executorRouteStrategyEnum);
    }

    @Override
    protected String doRoute(Long jobId, List<String> registries) {
        return routeAddress(jobId, registries);
    }

    public String routeAddress(Long jobId, List<String> addressList) {

        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLfuMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        // lfu item init, // Key排序可以用TreeMap+构造入参Compare；Value排序暂时只能通过ArrayList；
        HashMap<String, Integer> lfuItemMap = jobLfuMap.get(jobId);
        if (CollectionUtil.isEmpty(lfuItemMap)) {
            lfuItemMap = MapUtil.newHashMap();

            // 避免重复覆盖
            jobLfuMap.putIfAbsent(jobId, lfuItemMap);
        }

        // put new
        for (String address : addressList) {
            if (!lfuItemMap.containsKey(address) || lfuItemMap.get(address) > 1000000) {

                // 初始化时主动Random一次，缓解首次压力
                lfuItemMap.put(address, new Random().nextInt(addressList.size()));
            }
        }
        // remove old
        List<String> delKeys = new ArrayList<>();
        for (String existKey : lfuItemMap.keySet()) {
            if (!addressList.contains(existKey)) {
                delKeys.add(existKey);
            }
        }
        if (delKeys.size() > 0) {
            for (String delKey : delKeys) {
                lfuItemMap.remove(delKey);
            }
        }

        // load least userd count address
        List<Map.Entry<String, Integer>> lfuItemList = new ArrayList<>(lfuItemMap.entrySet());
        lfuItemList.sort(Comparator.comparingInt(Map.Entry::getValue));

        Map.Entry<String, Integer> addressItem = lfuItemList.get(0);
        String minAddress = addressItem.getKey();
        addressItem.setValue(addressItem.getValue() + 1);

        return addressItem.getKey();
    }


}
