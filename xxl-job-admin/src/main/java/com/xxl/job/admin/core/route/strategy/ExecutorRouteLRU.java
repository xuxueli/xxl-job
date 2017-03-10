package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单个JOB对应的每个执行器，最久为使用的优先被选举
 *      a、LFU(Least Frequently Used)：最不经常使用，频率/次数
 *      b(*)、LRU(Least Recently Used)：最近最久未使用，时间
 *
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLRU extends ExecutorRouter {

    private static ConcurrentHashMap<Integer, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap<Integer, LinkedHashMap<String, String>>();

    @Override
    public String route(int jobId, ArrayList<String> addressList) {

        LinkedHashMap<String, String> lruItem = jobLRUMap.get(jobId);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             *      a、accessOrder：ture=访问顺序排序（get/put时排序）；false=插入顺序排期；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             */
            lruItem = new LinkedHashMap<>(16, 0.75f, true);
            jobLRUMap.put(jobId, lruItem);
        }

        for (String address: addressList) {
            if (!lruItem.containsKey(address)) {
                lruItem.put(address, address);
            }
        }

        String eldestKey = lruItem.entrySet().iterator().next().getKey();
        String eldestValue = lruItem.get(eldestKey);
        return eldestValue;
    }

}
