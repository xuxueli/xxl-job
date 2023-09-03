package com.xxl.job.executor.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.enums.KettleLogLevel;
import com.xxl.job.core.pojo.dto.IdleBeatParam;
import com.xxl.job.core.pojo.dto.KillParam;
import com.xxl.job.core.pojo.dto.LogParam;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.factory.glue.GlueProcessor;
import com.xxl.job.executor.factory.handler.GlueJobHandler;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.handler.KettleKtrJobHandler;
import com.xxl.job.executor.factory.handler.ScriptJobHandler;
import com.xxl.job.executor.factory.repository.XxlJobRepository;
import com.xxl.job.executor.factory.thread.JobThread;
import com.xxl.job.executor.service.ExecutorService;
import com.xxl.job.executor.utils.JobLogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 执行器service实现类
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
@Service
public class ExecutorServiceImpl implements ExecutorService {

    @Autowired
    private XxlJobRepository xxlJobRepository;

    @Autowired
    private GlueProcessor glueProcessor;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ResponseVO beat() {
        return ResponseVO.success();
    }

    @Override
    public ResponseVO idleBeat(IdleBeatParam idleBeatParam) {

        // isRunningOrHasQueue
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = xxlJobRepository.getJob(idleBeatParam.getJobId());
        if (ObjectUtil.isNotNull(jobThread) && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }

        if (isRunningOrHasQueue) {
            return ResponseVO.error("job thread is running or has trigger queue.");
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO run(TriggerParam triggerParam) {
        // load old：jobHandler + jobThread
        JobThread jobThread = xxlJobRepository.getJob(triggerParam.getJobId());
        JobHandler jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;

        // valid：jobHandler + jobThread
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        if (GlueTypeEnum.BEAN.equals(glueTypeEnum)) {

            // new jobhandler
            JobHandler newJobHandler = xxlJobRepository.getJobHandler(triggerParam.getExecutorHandler());

            // valid old jobThread

            if (ObjectUtil.isNotNull(jobThread) && jobHandler != newJobHandler) {
                // change handler, need kill old thread
                removeOldReason = "change job-handler or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (ObjectUtil.isEmpty(jobHandler)) {
                jobHandler = newJobHandler;
                if (ObjectUtil.isEmpty(jobHandler)) {
                    return ResponseVO.error("job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }

        } else if (GlueTypeEnum.GLUE_GROOVY.equals(glueTypeEnum)) {

            // valid old jobThread
            if (ObjectUtil.isNotNull(jobThread)
                    && !(jobThread.getHandler() instanceof GlueJobHandler
                            && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdateTime() == triggerParam.getGlueUpdatedTime())) {
                // change handler or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (ObjectUtil.isEmpty(jobHandler)) {
                try {
                    JobHandler originJobHandler = glueProcessor.loadNewInstance(triggerParam.getGlueSource());
                    jobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatedTime());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return ResponseVO.error(e.getMessage());
                }
            }
        } /*else if (GlueTypeEnum.GLUE_GROOVY_CLASS.equals(glueTypeEnum)) {
            try {
                Map<String, GlueGroovyClass> beansOfType = applicationContext.getBeansOfType(GlueGroovyClass.class);
                if (CollectionUtil.isNotEmpty(beansOfType)) {
                    beansOfType.forEach((key, value) -> value.execute(triggerParam.getJobId()));
                }
            }catch (Exception e) {
                log.error("{}, ", GlueTypeEnum.GLUE_GROOVY_CLASS.name(), e);
            }
        } */else if (glueTypeEnum.equals(GlueTypeEnum.KETTLE_KTR)) {

            // valid old jobThread
            if (ObjectUtil.isNotNull(jobThread) && !(jobThread.getHandler() instanceof KettleKtrJobHandler)) {
                removeOldReason = "change job or kettle ktr, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (ObjectUtil.isEmpty(jobHandler)) {
                jobHandler = new KettleKtrJobHandler(triggerParam.getJobId(),triggerParam.getJobName(),
                        triggerParam.getKtrs().get(0), KettleLogLevel.match(triggerParam.getKettleLogLevel()));
            }

        } else if (glueTypeEnum.equals(GlueTypeEnum.KETTLE_KJB)) {
            log.info("kettle(kjb) {}", triggerParam.toString());

        } else if (glueTypeEnum.isScript()) {

            // valid old jobThread
            if (jobThread != null &&
                    !(jobThread.getHandler() instanceof ScriptJobHandler
                            && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdateTime() == triggerParam.getGlueUpdatedTime())) {
                // change script or gluesource updated, need kill old thread
                removeOldReason = "change job source or glue type, and terminate the old job thread.";

                jobThread = null;
                jobHandler = null;
            }

            // valid handler
            if (ObjectUtil.isEmpty(jobHandler)) {
                jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatedTime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            return ResponseVO.error("glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }

        // executor block strategy
        if (ObjectUtil.isNotNull(jobThread)) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy());
            if (ExecutorBlockStrategyEnum.DISCARD_LATER.equals(blockStrategy)) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return ResponseVO.error("block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY.equals(blockStrategy)) {
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
        if (ObjectUtil.isEmpty(jobThread)) {
            jobThread = xxlJobRepository.registerJob(triggerParam.getJobId(), jobHandler, removeOldReason);
        }

        // push data to queue
        return jobThread.pushTriggerQueue(triggerParam);
    }

    @Override
    public ResponseVO kill(KillParam killParam) {
        // kill handlerThread, and create new one
        JobThread jobThread = xxlJobRepository.getJob(killParam.getJobId());
        if (ObjectUtil.isNotNull(jobThread)) {
            xxlJobRepository.removeJob(killParam.getJobId(), "scheduling center kill job.");
            return ResponseVO.success();
        }
        return ResponseVO.success("job thread already killed.");
    }

    @Override
    public ResponseVO<LogResult> log(LogParam logParam) {
        // log filename: logPath/yyyy-MM-dd/9999.log
        String logFileName = JobLogUtils.makeLogFileName(new Date(logParam.getLogDateTim()), logParam.getLogId());

        LogResult logResult = JobLogUtils.readLog(logFileName, logParam.getFromLineNum());
        return ResponseVO.success(logResult);
    }

}
