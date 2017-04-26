package com.xxl.job.core.biz.impl;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.impl.GlueJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.util.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by xuxueli on 17/3/1.
 */
public class ExecutorBizImpl implements ExecutorBiz {
    private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> kill(int jobId) {
        // kill handlerThread, and create new one
        JobThread jobThread = XxlJobExecutor.loadJobThread(jobId);

        if (jobThread != null) {
            IJobHandler handler = jobThread.getHandler();
            jobThread.toStop("人工手动终止");
            jobThread.interrupt();
            XxlJobExecutor.removeJobThread(jobId);
            return ReturnT.SUCCESS;
        }

        return new ReturnT<String>(ReturnT.SUCCESS_CODE, "job thread aleady killed.");
    }

    @Override
    public ReturnT<LogResult> log(long logDateTim, int logId, int fromLineNum) {
        // log filename: yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logDateTim), logId);

        LogResult logResult = XxlJobFileAppender.readLog(logFileName, fromLineNum);
        return new ReturnT<LogResult>(logResult);
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // load old thread
        JobThread jobThread = XxlJobExecutor.loadJobThread(triggerParam.getJobId());

        if (GlueTypeEnum.BEAN==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // valid handler
            IJobHandler jobHandler = XxlJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
            if (jobHandler==null) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "job handler for JobId=[" + triggerParam.getJobId() + "] not found.");
            }

            // valid exists job thread：change handler, need kill old thread
            if (jobThread != null && jobThread.getHandler() != jobHandler) {
                // kill old job thread
                jobThread.toStop("更换任务模式或JobHandler,终止旧任务线程");
                jobThread.interrupt();
                XxlJobExecutor.removeJobThread(triggerParam.getJobId());
                jobThread = null;
            }

            // make thread: new or exists invalid
            if (jobThread == null) {
                jobThread = XxlJobExecutor.registJobThread(triggerParam.getJobId(), jobHandler);
            }

        } else if (GlueTypeEnum.GLUE_GROOVY==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // valid exists job thread：change handler or glue timeout, need kill old thread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof GlueJobHandler
                        && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdatetime()==triggerParam.getGlueUpdatetime() )) {
                // change glue model or glue timeout, kill old job thread
                jobThread.toStop("更换任务模式或JobHandler,终止旧任务线程");
                jobThread.interrupt();
                XxlJobExecutor.removeJobThread(triggerParam.getJobId());
                jobThread = null;
            }

            // make thread: new or exists invalid
            if (jobThread == null) {
                IJobHandler jobHandler = null;
                try {
                    jobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                } catch (Exception e) {
                    logger.error("", e);
                    return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
                }
                jobThread = XxlJobExecutor.registJobThread(triggerParam.getJobId(), new GlueJobHandler(jobHandler, triggerParam.getGlueUpdatetime()));
            }
        } else if (GlueTypeEnum.GLUE_SHELL==GlueTypeEnum.match(triggerParam.getGlueType())) {

            // make path
            String scriptPath = XxlJobFileAppender.filePath + "gluesource/";
            String scriptFileName = triggerParam.getJobId() + "_" + triggerParam.getGlueUpdatetime() + ".sh";

            // valid file
            File scriptFile = new File(scriptPath, scriptFileName);
            if (!scriptFile.exists()) {
                // valid glue source
                if (triggerParam.getGlueSource()==null) {
                    return new ReturnT<String>(ReturnT.FAIL_CODE, "glueSource is null.");
                }

                // .../gluesource/
                File scriptPathDir = new File(scriptPath);
                if (!scriptPathDir.exists()) {
                    scriptPathDir.mkdirs();
                }

                // .../gluesource/666-156465656.sh
                scriptFile = new File(scriptPath, scriptFileName);
                FileOutputStream fos = null;
                try {
                    scriptFile.createNewFile();

                    fos = new FileOutputStream(scriptFile, true);
                    fos.write(triggerParam.getGlueSource().getBytes("utf-8"));
                    fos.flush();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

            }

            // log File
            String logFileName = XxlJobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTim()), triggerParam.getLogId());

            // run script
            ScriptUtil.execToFile("python", scriptFile.getName(), (XxlJobFileAppender.filePath + logFileName) );

            return ReturnT.FAIL;
        } else if (GlueTypeEnum.GLUE_PYTHON==GlueTypeEnum.match(triggerParam.getGlueType())) {
            String scriptFilePath = XxlJobFileAppender.filePath + "gluesource/" + triggerParam.getJobId() + "_" + triggerParam.getGlueUpdatetime() + ".py";


        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // push data to queue
        jobThread.pushTriggerQueue(triggerParam);
        return ReturnT.SUCCESS;
    }

}
