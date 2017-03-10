package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;

import java.util.ArrayList;

/**
 * 单个JOB对应的每个执行器，使用频率最低的优先被选举
 *      a(*)、LFU(Least Frequently Used)：最不经常使用，频率/次数
 *      b、LRU(Least Recently Used)：最近最久未使用，时间
 *
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLFU extends ExecutorRouter {

    @Override
    public String route(int jobId, ArrayList<String> addressList) {

        // TODO

        return addressList.get(0);
    }

}
