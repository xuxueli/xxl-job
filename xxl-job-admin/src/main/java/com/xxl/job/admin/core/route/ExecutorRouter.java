package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
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
     * @param addressList
     * @return ReturnT.content=address
     */
    public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);


    /**
     * route address within group
     * @param group
     * @param triggerParam
     * @param addressList
     * @return
     */
    public ReturnT<String> route(XxlJobGroup group, TriggerParam triggerParam, List<String> addressList) {
        return route(triggerParam, addressList);
    }

}
