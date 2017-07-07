package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.jetty.NetComClientProxy;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.ArrayList;

/**
 * Author: Antergone
 * Date: 2017/6/29
 */
public abstract class AbstractExecutorOverRouter extends ExecutorRouter {

    public String route(int jobId, ArrayList<String> addressList) {
        return addressList.get(0);
    }

    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList, XxlJobLog jobLog) {
        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {
            // beat
            ReturnT<String> beatResult = null;
            try {
                ExecutorBizImpl executorBiz = (ExecutorBizImpl) new NetComClientProxy(ExecutorBizImpl.class, address).getObject();
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT<String>(ReturnT.FAIL_CODE, ""+e );
            }
            beatResultSB.append("<br>----------------------<br>")
                    .append("心跳检测：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode())
                    .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {
                jobLog.setExecutorAddress(address);

                ReturnT<String> runResult = runExecutor(triggerParam, address);
                beatResultSB.append("<br>----------------------<br>").append(runResult.getMsg());

                return new ReturnT<String>(runResult.getCode(), beatResultSB.toString());
            }
        }
        return new ReturnT<String>(ReturnT.FAIL_CODE, beatResultSB.toString());
    }
}
