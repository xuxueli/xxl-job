package com.xxl.job.executor.factory.gluetype;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.core.utils.ResponseUtils;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.repository.XxlJobRepository;
import com.xxl.job.executor.factory.thread.JobThread;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 运行模式 执行处理器抽象类
 * @author Rong.Jia
 * @date 2023/09/22
 */
public abstract class AbstractGlueTypeExecutorProcessor implements GlueTypeExecutorProcessor {

    @Autowired
    protected XxlJobRepository xxlJobRepository;

    /**
     * 执行调度
     *
     * @param triggerParam 调度参数
     * @return {@link ResponseVO}
     */
    protected abstract ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam);

    @Override
    public ResponseVO<Void> trigger(TriggerParam triggerParam) {

        ResponseVO<TriggerHolder> responseVO = doTrigger(triggerParam);
        if (!ResponseUtils.isSuccess(responseVO)) {
            return ResponseVO.error(responseVO.getMessage());
        }

        TriggerHolder triggerHolder = responseVO.getData();
        JobThread jobThread = triggerHolder.getThread();
        String removeOldReason = triggerHolder.getRemoveOldReason();

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
        if (ObjectUtil.isNull(jobThread)) {
            jobThread = xxlJobRepository.registerJob(triggerParam.getJobId(), triggerHolder.getHandler(), removeOldReason);
        }

        // push data to queue
        return jobThread.pushTriggerQueue(triggerParam);
    }

    protected TriggerHolder createHolder(Long jobId) {
        // load old：jobHandler + jobThread
        JobThread jobThread = xxlJobRepository.getJob(jobId);
        JobHandler jobHandler = ObjectUtil.isNotNull(jobThread) ? jobThread.getHandler() : null;

        TriggerHolder triggerHolder = new TriggerHolder();
        triggerHolder.setHandler(jobHandler);
        triggerHolder.setThread(jobThread);
        return triggerHolder;
    }

    @Data
    protected static class TriggerHolder {
        private JobThread thread;
        private JobHandler handler;
        private String removeOldReason;
    }















}
