package com.xxl.job.admin.business.scheduler.route.strategy;

import com.xxl.job.admin.business.model.XxlJobGroup;
import com.xxl.job.admin.business.scheduler.route.ExecutorRouter;
import com.xxl.job.core.openapi.executor.dto.TriggerRequest;
import com.xxl.tool.response.Response;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLast extends ExecutorRouter {

    @Override
    public Response<String> route(TriggerRequest triggerParam, XxlJobGroup jobGroup) {
        return Response.ofSuccess(jobGroup.getRegistryList().get(jobGroup.getRegistryList().size()-1));
    }

}
