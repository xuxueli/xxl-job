package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.route.strategy.ExecutorRouteLFU;
import com.xxl.job.admin.core.route.strategy.ExecutorRouteLRU;

public class ExecutorRouterHelper {

    /**
     * 以executor为单位，更新LRU和LFU
     *
     * @param appname
     * @param address
     */
    public static void updateRouteStats(String appname, String address) {

        updateLRUStats(appname, address);

        updateLFUStats(appname, address);
    }

    /**
     * 统计LFU
     *
     * @param appname
     * @param address
     */
    private static void updateLFUStats(String appname, String address) {
        ExecutorRouteLFU.AppAddressPool.getOrCreate(appname).updateAddressStats(address);
    }

    /**
     * @param appname
     * @param address
     */
    private static void updateLRUStats(String appname, String address) {
        ExecutorRouteLRU.AppAddressPool.getOrCreate(appname).updateAddressStats(address);
    }


}
