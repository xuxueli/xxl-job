package com.xxl.job.admin.business.scheduler.route;

import com.xxl.job.admin.business.model.XxlJobGroup;
import com.xxl.job.core.openapi.executor.dto.TriggerRequest;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * route address
     *
     * @param triggerRequest trigger request
     * @param jobGroup  executor group
     * @return  ReturnT.content=address
     */
    public abstract Response<String> route(TriggerRequest triggerRequest, XxlJobGroup jobGroup);

}
