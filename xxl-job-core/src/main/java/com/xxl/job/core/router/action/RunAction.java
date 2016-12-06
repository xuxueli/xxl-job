package com.xxl.job.core.router.action;

import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.impl.GlueJobHandler;
import com.xxl.job.core.router.HandlerRouter;
import com.xxl.job.core.router.IAction;
import com.xxl.job.core.router.model.RequestModel;
import com.xxl.job.core.router.model.ResponseModel;
import com.xxl.job.core.router.thread.JobThread;

/**
 * Created by xuxueli on 16/7/22.
 */
public class RunAction extends IAction {

    @Override
    public ResponseModel execute(RequestModel requestModel) {

        // generate jobKey
        String jobKey = requestModel.getJobGroup().concat("_").concat(requestModel.getJobName());

        // load old thread
        JobThread jobThread = HandlerRouter.loadJobThread(jobKey);

        if (!requestModel.isGlueSwitch()) {
            // bean model

            // valid handler instance
            IJobHandler jobHandler = HandlerRouter.loadJobHandler(requestModel.getExecutorHandler());
            if (jobHandler==null) {
                return new ResponseModel(ResponseModel.FAIL, "job handler for jobKey=[" + jobKey + "] not found.");
            }

            if (jobThread == null) {
                jobThread = HandlerRouter.registJobThread(jobKey, jobHandler);
            } else {
                // job handler update, kill old job thread
                if (jobThread.getHandler() != jobHandler) {
                    // kill old job thread
                    jobThread.toStop("更换任务模式或JobHandler,终止旧任务线程");
                    jobThread.interrupt();

                    // new thread, with new job handler
                    jobThread = HandlerRouter.registJobThread(jobKey, jobHandler);
                }
            }
        } else {
            // glue model

            // valid glueloader
            if (!GlueFactory.isActive()) {
                return new ResponseModel(ResponseModel.FAIL, "glueLoader for jobKey=[" + jobKey + "] not found.");
            }

            if (jobThread == null) {
                jobThread = HandlerRouter.registJobThread(jobKey, new GlueJobHandler(requestModel.getJobGroup(), requestModel.getJobName()));
            } else {
                // job handler update, kill old job thread
                if (!(jobThread.getHandler() instanceof GlueJobHandler)) {
                    // kill old job thread
                    jobThread.toStop("更换任务模式或JobHandler,终止旧任务线程");
                    jobThread.interrupt();

                    // new thread, with new job handler
                    jobThread = HandlerRouter.registJobThread(jobKey, new GlueJobHandler(requestModel.getJobGroup(), requestModel.getJobName()));
                }
            }
        }

        // push data to queue
        jobThread.pushTriggerQueue(requestModel);
        return new ResponseModel(ResponseModel.SUCCESS, null);
    }

}
