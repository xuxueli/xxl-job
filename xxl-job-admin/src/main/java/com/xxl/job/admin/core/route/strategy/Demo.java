package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname Demo
 * @Description TODO
 * @Date 2021/4/21 17:35
 * @Created by wangchao
 */
public class Demo {
    public static void main(String[] args) {
        ExecutorRouter executorRoute = new ExecutorRouteLRU();
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1);
        List<String> addressList = new ArrayList<String>() {
            {
                add("111");
                add("222");
                add("333");
            }
        };
        executorRoute.route(triggerParam,addressList);
        executorRoute.route(triggerParam,addressList);
        executorRoute.route(triggerParam,addressList);
        addressList.remove("111");
        addressList.add("444");
        executorRoute.route(triggerParam,addressList);
    }
}
