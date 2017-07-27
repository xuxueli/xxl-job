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
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList) {
        StringBuilder idleBeatResultSB = new StringBuilder();
        for (String address : addressList) {
            // beat
            ReturnT<String> beatResult = null;
            try {
                ExecutorBizImpl executorBiz = (ExecutorBizImpl) new NetComClientProxy(ExecutorBizImpl.class, address).getObject();
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = ReturnT.error("" + e);
            }
            idleBeatResultSB.append( (idleBeatResultSB.length()>0)?"<br><br>":"")

                    .append("心跳检测：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode())
                    .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ReturnT.SUCCESS_CODE) {

                ReturnT<String> runResult = runExecutor(triggerParam, address);
                idleBeatResultSB.append("<br><br>").append(runResult.getMsg());
                // result
                runResult.setMsg(idleBeatResultSB.toString());
                runResult.setContent(address);
                return runResult;
            }
        }
        return ReturnT.error(idleBeatResultSB.toString());
    }
}
