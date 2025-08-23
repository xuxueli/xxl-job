package com.xxl.job.admin.scheduler.route.strategy;

import com.xxl.job.admin.scheduler.route.ExecutorRouter;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLast extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return ReturnT.ofSuccess(addressList.get(addressList.size()-1));
    }

}
