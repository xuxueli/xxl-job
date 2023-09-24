package com.xxl.job.executor.factory.gluetype;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.factory.handler.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Bean 执行处理器
 * @author Rong.Jia
 * @date 2023/09/22
 */
@Slf4j
@Component
public class BeanTypeExecutorProcessor extends AbstractGlueTypeExecutorProcessor {

    @Override
    public Boolean supports(GlueTypeEnum glueType) {
        return GlueTypeEnum.BEAN.equals(glueType);
    }

    @Override
    protected ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam) {

        TriggerHolder triggerHolder = createHolder(triggerParam.getJobId());

        // new jobhandler
        JobHandler newJobHandler = xxlJobRepository.getJobHandler(triggerParam.getExecutorHandler());

        // valid old jobThread

        if (ObjectUtil.isNotNull(triggerHolder.getThread()) && triggerHolder.getHandler() != newJobHandler) {
            // change handler, need kill old thread
            triggerHolder.setRemoveOldReason("change job-handler or glue type, and terminate the old job thread.");
            triggerHolder.setThread(null);
            triggerHolder.setHandler(null);
        }

        // valid handler
        if (ObjectUtil.isEmpty(triggerHolder.getHandler())) {
            triggerHolder.setHandler(newJobHandler);
            if (ObjectUtil.isEmpty(triggerHolder.getHandler())) {
                return ResponseVO.error("job handler [" + triggerParam.getExecutorHandler() + "] not found.");
            }
        }
        return ResponseVO.success(triggerHolder);
    }
}
