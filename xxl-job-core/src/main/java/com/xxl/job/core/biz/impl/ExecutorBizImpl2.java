package com.xxl.job.core.biz.impl;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.impl.GlueJobHandler;
import com.xxl.job.core.handler.impl.ScriptJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.thread.JobThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by xuxueli on 17/3/1.
 */
public class ExecutorBizImpl2 implements ExecutorBiz {

    @Override
    public ReturnT<String> beat() {
        // 该方法为了实现故障转移路由策略
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {

        // 该方法为了实现忙碌转移路由策略 即如果机器空闲 返回success
        // 是否空闲通过判断该任务的JobThread是否有任务在执行来确定
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = XxlJobExecutor.loadJobThread(idleBeatParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "job thread is running or has trigger queue.");
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        // 先尝试加载oldJobThread/oldJobHandler
        JobThread jobThread = XxlJobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;

        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        // 这里只介绍下Bean模式 其他脚本模式直接略过了  可自行查看源码
        if (GlueTypeEnum.BEAN == glueTypeEnum) {

            // 加载本次执行的JobHandler
            IJobHandler newJobHandler = XxlJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());

            // 判断JobHandler是否被改变(执行间隔期间可以修改同一任务的执行器)
            if (jobThread != null && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";
                // 如果被改变 则在这里赋为null 下面会统一重新注册jobThread
                jobThread = null;
                jobHandler = null;
            }

            // 简单的判断  确保新的jobHandler不为null
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return new ReturnT<>(ReturnT.FAIL_CODE, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }
        } else {
            // 删除了其他的执行模式
            return new ReturnT<>(ReturnT.FAIL_CODE, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // 如果jobThread不为null
        // 说明此时同一任务id 在上一任务未执行完时触发了下一次执行
        // 则检查其阻塞策略
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    // 丢弃策略
                    return new ReturnT<>(ReturnT.FAIL_CODE, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                // 覆盖策略
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();
                    jobThread = null;
                }
            } else {
                // 什么都不干 即串行执行,将本次触发参数放入执行队列
            }
        }

        // put jobThread 如果存在oldThread 会停止其任务
        if (jobThread == null) {
            jobThread = XxlJobExecutor.registJobThread(triggerParam.getJobId(), jobHandler, removeOldReason);
        }
        // 将本次触发参数放入执行队列
        return jobThread.pushTriggerQueue(triggerParam);
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        // kill当前执行的任务
        JobThread jobThread = XxlJobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            XxlJobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return ReturnT.SUCCESS;
        }
        return new ReturnT<>(ReturnT.SUCCESS_CODE, "job thread already killed.");
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        // log filename: logPath/yyyy-MM-dd/9999.log
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(logParam.getLogDateTim()), logParam.getLogId());

        LogResult logResult = XxlJobFileAppender.readLog(logFileName, logParam.getFromLineNum());
        return new ReturnT<>(logResult);
    }
}
