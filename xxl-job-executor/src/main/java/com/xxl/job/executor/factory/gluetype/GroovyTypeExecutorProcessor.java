package com.xxl.job.executor.factory.gluetype;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.factory.glue.GlueProcessor;
import com.xxl.job.executor.factory.handler.GlueJobHandler;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.thread.JobThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * groovy  执行处理器
 *
 * @author Rong.Jia
 * @date 2023/09/24
 */
@Slf4j
@Component
public class GroovyTypeExecutorProcessor extends AbstractGlueTypeExecutorProcessor {

    @Autowired
    private GlueProcessor glueProcessor;

    @Override
    public Boolean supports(GlueTypeEnum glueType) {
        return GlueTypeEnum.GLUE_GROOVY.equals(glueType);
    }

    @Override
    protected ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam) {

        TriggerHolder triggerHolder = createHolder(triggerParam.getJobId());
        JobHandler jobHandler = triggerHolder.getHandler();
        JobThread jobThread = triggerHolder.getThread();

        // valid old jobThread
        if (ObjectUtil.isNotNull(jobThread)
                && !(jobThread.getHandler() instanceof GlueJobHandler
                && ((GlueJobHandler) jobThread.getHandler()).getGlueUpdateTime() == triggerParam.getGlueUpdatedTime())) {

            // change handler or gluesource updated, need kill old thread
            triggerHolder.setRemoveOldReason("change job source or glue type, and terminate the old job thread.");
            triggerHolder.setThread(null);
            triggerHolder.setHandler(null);
        }

        // valid handler
        if (ObjectUtil.isNull(jobHandler)) {
            try {
                JobHandler originJobHandler = glueProcessor.loadNewInstance(triggerParam.getGlueSource());
                triggerHolder.setHandler(new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatedTime()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseVO.error(e.getMessage());
            }
        }
        return ResponseVO.success(triggerHolder);
    }
}
