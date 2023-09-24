package com.xxl.job.executor.factory.gluetype;

import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 不支持类型执行器处理器
 *
 * @author Rong.Jia
 * @date 2023/09/24
 */
@Slf4j
@Component
public class NotSupportsTypeExecutorProcessor extends AbstractGlueTypeExecutorProcessor {

    @Override
    public Boolean supports(GlueTypeEnum glueType) {
        return GlueTypeEnum.NULL.equals(glueType);
    }

    @Override
    protected ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam) {
        return ResponseVO.error("glueType[" + triggerParam.getGlueType() + "] is not valid.");
    }

}
