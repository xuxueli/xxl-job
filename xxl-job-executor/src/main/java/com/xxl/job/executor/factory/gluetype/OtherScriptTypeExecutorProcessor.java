package com.xxl.job.executor.factory.gluetype;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.handler.ScriptJobHandler;
import com.xxl.job.executor.factory.thread.JobThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 其他脚本类型  执行处理器
 *
 * @author Rong.Jia
 * @date 2023/09/24
 */
@Slf4j
@Component
public class OtherScriptTypeExecutorProcessor extends AbstractGlueTypeExecutorProcessor {

    @Override
    public Boolean supports(GlueTypeEnum glueType) {
        return glueType.isScript();
    }

    @Override
    protected ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam) {

        TriggerHolder triggerHolder = createHolder(triggerParam.getJobId());
        JobHandler jobHandler = triggerHolder.getHandler();
        JobThread jobThread = triggerHolder.getThread();

        // valid old jobThread
        if (ObjectUtil.isNotNull(jobThread) &&
                !(jobThread.getHandler() instanceof ScriptJobHandler
                        && ((ScriptJobHandler) jobThread.getHandler()).getGlueUpdateTime() == triggerParam.getGlueUpdatedTime())) {
            // change script or gluesource updated, need kill old thread
            triggerHolder.setRemoveOldReason("change job source or glue type, and terminate the old job thread.");
            triggerHolder.setThread(null);
            triggerHolder.setHandler(null);
        }

        // valid handler
        if (ObjectUtil.isNull(jobHandler)) {
            jobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatedTime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            triggerHolder.setHandler(jobHandler);
        }
        return ResponseVO.success(triggerHolder);
    }

}
