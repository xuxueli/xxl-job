package com.xxl.job.core.biz.impl;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.impl.GlueJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.JobThread;

import java.util.Date;

/**
 * Created by xuxueli on 17/3/1.
 */
public class ExecutorBizImpl implements ExecutorBiz {

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> kill(String jobGroup, String jobName) {

        // generate jobKey
        String jobKey = jobGroup.concat("_").concat(jobName);

        // kill handlerThread, and create new one
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobKey);

        if (jobThread != null) {
            IJobHandler handler = jobThread.getHandler();
            jobThread.toStop("人工手动终止");
            jobThread.interrupt();
            //XxlJobExecutor.registJobThread(jobKey, handler);
            return ReturnT.SUCCESS;
        }

        return new ReturnT<String>(ReturnT.FAIL_CODE, "job thread not found.");
    }

    @Override
    public ReturnT<String> log(long logDateTim, int logId) {
        // log filename: yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logDateTim), logId);

        String logConteng = XxlJobFileAppender.readLog(logFileName);
        return new ReturnT<String>(ReturnT.SUCCESS_CODE, logConteng);
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // generate jobKey
        String jobKey = triggerParam.getJobGroup().concat("_").concat(triggerParam.getJobName());

        // load old thread
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobKey);

        if (!triggerParam.isGlueSwitch()) {
            // bean model

            // valid handler instance
            IJobHandler jobHandler = XxlJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
            if (jobHandler==null) {
                return new ReturnT(ReturnT.FAIL_CODE, "job handler for jobKey=[" + jobKey + "] not found.");
            }

            if (jobThread == null) {
                jobThread = XxlJobExecutor.registJobThread(jobKey, jobHandler);
            } else {
                // job handler update, kill old job thread
                if (jobThread.getHandler() != jobHandler) {
                    // kill old job thread
                    jobThread.toStop("更换任务模式或JobHandler,终止旧任务线程");
                    jobThread.interrupt();

                    // new thread, with new job handler
                    jobThread = XxlJobExecutor.registJobThread(jobKey, jobHandler);
                }
            }
        } else {
            // glue model

            // valid glueloader
            if (!GlueFactory.isActive()) {
                return new ReturnT(ReturnT.FAIL_CODE, "glueLoader for jobKey=[" + jobKey + "] not found.");
            }

            if (jobThread == null) {
                jobThread = XxlJobExecutor.registJobThread(jobKey, new GlueJobHandler(triggerParam.getJobGroup(), triggerParam.getJobName()));
            } else {
                // job handler update, kill old job thread
                if (!(jobThread.getHandler() instanceof GlueJobHandler)) {
                    // kill old job thread
                    jobThread.toStop("更换任务模式或JobHandler,终止旧任务线程");
                    jobThread.interrupt();

                    // new thread, with new job handler
                    jobThread = XxlJobExecutor.registJobThread(jobKey, new GlueJobHandler(triggerParam.getJobGroup(), triggerParam.getJobName()));
                }
            }
        }

        // push data to queue
        jobThread.pushTriggerQueue(triggerParam);
        return ReturnT.SUCCESS;
    }

}
