package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 单个JOB对应的每个执行器，最久为使用的优先被选举
 * a、LFU(Least Frequently Used)：最不经常使用，频率/次数
 * b(*)、LRU(Least Recently Used)：最近最久未使用，时间
 * <p>
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLRU extends ExecutorRouter {

    public static class AppAddressPool {
        private static ConcurrentMap<String, AppAddressPool> APP_LRU_MANAGER = new ConcurrentHashMap();

        /**
         * 每个APP name 实例化一个地址池
         *
         * @param appname
         * @return
         */
        public static AppAddressPool getOrCreate(String appname) {
            return APP_LRU_MANAGER.computeIfAbsent(appname, key -> new AppAddressPool());
        }

        private List<String> freeAddressList = new ArrayList<>();

        /**
         * LinkedHashMap
         * a、accessOrder：true=访问顺序排序（get/put时排序）；false=插入顺序排期；
         * b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
         */
        private LinkedHashMap<String, String> usedAddressManager = new LinkedHashMap<String, String>(16, 0.75f, true);

        private synchronized void clear(String appname) {
            APP_LRU_MANAGER.remove(appname);
            getOrCreate(appname);
        }

        public synchronized void updateAddressStats(String address) {
            usedAddressManager.put(address, address);
        }

        private synchronized String route(List<String> addressList) {
            LinkedHashMap<String, String> lruItem = usedAddressManager;

            // remove dead address
            List<String> delKeys = new ArrayList<>();
            for (String existKey : lruItem.keySet()) {
                if (!addressList.contains(existKey)) {
                    delKeys.add(existKey);
                }
            }
            if (delKeys.size() > 0) {
                for (String delKey : delKeys) {
                    lruItem.remove(delKey);
                }
            }

            // put new address into free list
            freeAddressList.clear();
            for (String address : addressList) {
                if (!lruItem.containsKey(address)) {
                    freeAddressList.add(address);
                }
            }

            if (freeAddressList.size() > 0) {
                String freeAddress = freeAddressList.get(0);
                freeAddressList.remove(0);
                return freeAddress;
            }

            // load
            String eldestKey = lruItem.entrySet().iterator().next().getKey();
            String eldestValue = lruItem.get(eldestKey);
            return eldestValue;

        }

    }

    private String route(String appname, List<String> addressList) {
        AppAddressPool appAddressPool = AppAddressPool.getOrCreate(appname);
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            appAddressPool.clear(appname);
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        return appAddressPool.route(addressList);
    }

    private static long CACHE_VALID_TIME = 0;

    @Override
    public ReturnT<String> route(XxlJobGroup group, TriggerParam triggerParam, List<String> addressList) {
        String address = route(group.getAppname(), addressList);
        return new ReturnT<String>(address);
    }


    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        throw new RuntimeException("remove");
    }

}
