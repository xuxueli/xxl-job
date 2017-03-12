package com.xxl.job.admin.core.route;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {

    public abstract String route(int jobId, ArrayList<String> addressList);

    public static String route(int jobId, ArrayList<String> addressList, String executorRouteStrategy){
        if (CollectionUtils.isEmpty(addressList)) {
            return null;
        }
        ExecutorRouteStrategyEnum strategy = ExecutorRouteStrategyEnum.match(executorRouteStrategy, ExecutorRouteStrategyEnum.FIRST);
        String routeAddress = strategy.getRouter().route(jobId, addressList);
        return routeAddress;
    }

    public static void main(String[] args) {


        for (int i = 0; i < 100; i++) {
            String ret = ExecutorRouter.route(666, new ArrayList<String>(Arrays.asList("127.0.0.1:0000", "127.0.0.1:2222", "127.0.0.1:3333")), ExecutorRouteStrategyEnum.LEAST_FREQUENTLY_USED.name());
            System.out.println(ret);
        }

    }

}
