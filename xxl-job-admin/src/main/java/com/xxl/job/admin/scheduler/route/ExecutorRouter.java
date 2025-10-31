package com.xxl.job.admin.scheduler.route;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * route address
     *
     * @param addressList  executor address list
     * @return  ReturnT.content=address
     */
    public abstract ReturnT<String> route(TriggerRequest triggerParam, List<String> addressList);

}
