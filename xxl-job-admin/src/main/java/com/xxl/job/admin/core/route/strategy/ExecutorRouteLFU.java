package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 单个JOB对应的每个执行器，使用频率最低的优先被选举
 * a(*)、LFU(Least Frequently Used)：最不经常使用，频率/次数
 * b、LRU(Least Recently Used)：最近最久未使用，时间
 * <p>
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLFU extends ExecutorRouter {

    public static class AppAddressPool {
        private static ConcurrentMap<String, AppAddressPool> APP_LFU_MANAGER = new ConcurrentHashMap();

        /**
         * 每个APP name 实例化一个地址池
         *
         * @param appname
         * @return
         */
        public static AppAddressPool getOrCreate(String appname) {
            return APP_LFU_MANAGER.computeIfAbsent(appname, key -> new AppAddressPool());
        }

        private List<String> freeAddressList = new ArrayList<>();

        //key=address
        private HashMap<String, Integer> usedAddressManager = new HashMap<>();

        private synchronized void clear(String appname) {
            APP_LFU_MANAGER.remove(appname);
            getOrCreate(appname);
        }

        public synchronized void updateAddressStats(String address) {
            usedAddressManager.put(address, usedAddressManager.getOrDefault(address, 0) + 1);
        }

        private synchronized String route(List<String> addressList) {
            HashMap<String, Integer> lfuItemMap = usedAddressManager;

            // remove dead address
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

            // put new address into free list
            freeAddressList.clear();
            for (String address : addressList) {
                if (!lfuItemMap.containsKey(address)) {
                    freeAddressList.add(address);
                }
            }

            if (freeAddressList.size() > 0) {
                String freeAddress = freeAddressList.get(0);
                freeAddressList.remove(0);
                return freeAddress;
            }

            // load least userd count address
            List<Map.Entry<String, Integer>> lfuItemList = new ArrayList<Map.Entry<String, Integer>>(lfuItemMap.entrySet());
            Collections.sort(lfuItemList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            Map.Entry<String, Integer> addressItem = lfuItemList.get(0);

            return addressItem.getKey();
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
