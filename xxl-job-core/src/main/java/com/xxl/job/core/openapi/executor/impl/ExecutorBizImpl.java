package com.xxl.job.core.openapi.executor.impl;

import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.impl.GlueJobHandler;
import com.xxl.job.core.handler.impl.ScriptJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.openapi.executor.ExecutorBiz;
import com.xxl.job.core.openapi.executor.dto.*;
import com.xxl.job.core.thread.JobThread;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by xuxueli on 17/3/1.
 */
public class ExecutorBizImpl implements ExecutorBiz {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);

    @Override
    public Response<String> beat() {
        return Response.ofSuccess();
    }

    @Override
    public Response<String> idleBeat(IdleBeatRequest idleBeatRequest) {
        XxlJobExecutor executor = XxlJobExecutor.getInstance();
        return executor.executeWithJobThreadLock(idleBeatRequest.getJobId(), () -> {
            JobThread jobThread = executor.loadJobThread(idleBeatRequest.getJobId());
            return jobThread != null && jobThread.isRunningOrHasQueue()
                    ? Response.ofFail("job thread is running or has trigger queue.")
                    : Response.ofSuccess();
        });
    }

    @Override
    public Response<String> trigger(TriggerRequest triggerRequest) {
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerRequest.getGlueType());

        // valid glue (non-BEAN) enabled
        if (glueTypeEnum != null && GlueTypeEnum.BEAN != glueTypeEnum) {
            boolean glueEnabled = XxlJobExecutor.getInstance().getGlueEnabled();
            if (!glueEnabled) {
                logger.warn(">>>>>>>>>>> xxl-job executor not support current glue type[{}], please check executor configuration.", glueTypeEnum.getDesc());
                return Response.of(XxlJobContext.HANDLE_CODE_FAIL, "fail, current glue type ["+ glueTypeEnum.getDesc() +"] not supported.");
            }
        }

        XxlJobExecutor executor = XxlJobExecutor.getInstance();
        // Keep handler selection, thread replacement, and queue insertion atomic for this job ID.
        return executor.executeWithJobThreadLock(triggerRequest.getJobId(),
                () -> triggerWithJobThreadLock(executor, triggerRequest, glueTypeEnum));
    }

    private Response<String> triggerWithJobThreadLock(XxlJobExecutor executor,
                                                       TriggerRequest triggerRequest,
                                                       GlueTypeEnum glueTypeEnum) {
        // load job info：jobHandler + jobThread
        JobThread jobThread = executor.loadJobThread(triggerRequest.getJobId());
        IJobHandler jobHandler = jobThread!=null?jobThread.getHandler():null;
        String removeOldReason = null;

        // dispatch handler
        if (GlueTypeEnum.BEAN == glueTypeEnum) {

            // new jobhandler
            IJobHandler newJobHandler = executor.loadJobHandler(triggerRequest.getExecutorHandler());

            // valid old jobThread
            if (jobThread!=null && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return Response.of(XxlJobContext.HANDLE_CODE_FAIL, "job handler [" + triggerRequest.getExecutorHandler() + "] not found.");
                }
            }

        } else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                        && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime()== triggerRequest.getGlueUpdatetime() )) {
                // change handler or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                try {
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerRequest.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerRequest.getGlueUpdatetime());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return Response.of(XxlJobContext.HANDLE_CODE_FAIL, e.getMessage());
                }
            }
        } else if (glueTypeEnum!=null && glueTypeEnum.isScript()) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdatetime()== triggerRequest.getGlueUpdatetime() )) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (jobHandler == null) {
                jobHandler = new ScriptJobHandler(triggerRequest.getJobId(), triggerRequest.getGlueUpdatetime(), triggerRequest.getGlueSource(), GlueTypeEnum.match(triggerRequest.getGlueType()));
            }
        } else {
            return Response.of(XxlJobContext.HANDLE_CODE_FAIL, "glueType[" + triggerRequest.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerRequest.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return Response.of(XxlJobContext.HANDLE_CODE_FAIL, "block strategy effect："+ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = executor.registJobThread(triggerRequest.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        return jobThread.pushTriggerQueue(triggerRequest);
    }

    @Override
    public Response<String> kill(KillRequest killRequest) {
        JobThread jobThread = XxlJobExecutor.getInstance().removeJobThread(
                killRequest.getJobId(), "scheduling center kill job.");
        return jobThread != null
                ? Response.ofSuccess()
                : Response.ofSuccess("job thread already killed.");
    }

    @Override
    public Response<LogData> log(LogRequest logRequest) {
        // log filename: logPath/yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logRequest.getLogDateTime()), logRequest.getLogId());

        LogData logResult = XxlJobFileAppender.readLog(logFileName, logRequest.getFromLineNum());
        return Response.ofSuccess(logResult);
    }

}
