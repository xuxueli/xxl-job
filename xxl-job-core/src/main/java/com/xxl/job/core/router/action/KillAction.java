package com.xxl.job.core.router.action;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.router.HandlerRouter;
import com.xxl.job.core.router.IAction;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.router.thread.JobThread;

/**
 * Created by xuxueli on 16/7/22.
 */
public class KillAction extends IAction {

    @Override
    public ResponseModel execute(RequestModel requestModel) {

        // generate jobKey
        String jobKey = requestModel.getJobGroup().concat("_").concat(requestModel.getJobName());

        // kill handlerThread, and create new one
        JobThread jobThread = HandlerRouter.loadJobThread(jobKey);

        if (jobThread != null) {
            IJobHandler handler = jobThread.getHandler();
            jobThread.toStop("人工手动终止");
            jobThread.interrupt();
            HandlerRouter.registJobThread(jobKey, handler);
            return new ResponseModel(ResponseModel.SUCCESS, "job thread kill success.");
        }

        return new ResponseModel(ResponseModel.FAIL, "job thread not found.");
    }

}
