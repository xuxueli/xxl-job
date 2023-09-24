package com.xxl.job.executor.factory.gluetype;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.enums.KettleLogLevel;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.executor.factory.handler.JobHandler;
import com.xxl.job.executor.factory.handler.KettleKtrJobHandler;
import com.xxl.job.executor.factory.thread.JobThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * kettle-ktr  执行处理器
 *
 * @author Rong.Jia
 * @date 2023/09/24
 */
@Slf4j
@Component
public class KtrTypeExecutorProcessor extends AbstractGlueTypeExecutorProcessor {

    @Override
    public Boolean supports(GlueTypeEnum glueType) {
        return GlueTypeEnum.KETTLE_KTR.equals(glueType);
    }

    @Override
    protected ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam) {

        TriggerHolder triggerHolder = createHolder(triggerParam.getJobId());

        JobHandler jobHandler = triggerHolder.getHandler();
        JobThread jobThread = triggerHolder.getThread();

        // valid old jobThread
        if (ObjectUtil.isNotNull(jobThread) && !(jobThread.getHandler() instanceof KettleKtrJobHandler)) {
            triggerHolder.setRemoveOldReason("change job or kettle ktr, and terminate the old job thread.");
            triggerHolder.setThread(null);
            triggerHolder.setHandler(null);
        }

        // valid handler
        if (ObjectUtil.isNull(jobHandler)) {
           triggerHolder.setHandler(new KettleKtrJobHandler(triggerParam.getJobId(),triggerParam.getJobName(),
                   triggerParam.getKtrs().get(0), KettleLogLevel.match(triggerParam.getKettleLogLevel())));
        }
        return ResponseVO.success(triggerHolder);
    }

}
