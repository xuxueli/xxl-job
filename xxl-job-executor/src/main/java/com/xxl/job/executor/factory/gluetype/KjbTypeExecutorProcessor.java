package com.xxl.job.executor.factory.gluetype;

import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * kettle-kjb  执行处理器
 *
 * @author Rong.Jia
 * @date 2023/09/24
 */
@Slf4j
@Component
public class KjbTypeExecutorProcessor extends AbstractGlueTypeExecutorProcessor {

    @Override
    public Boolean supports(GlueTypeEnum glueType) {
        return GlueTypeEnum.KETTLE_KJB.equals(glueType);
    }

    @Override
    protected ResponseVO<TriggerHolder> doTrigger(TriggerParam triggerParam) {
        return ResponseVO.success();
    }
}
