package com.xxl.job.executor.factory.gluetype;

import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 运行模式 执行处理器工厂类
 * @author Rong.Jia
 * @date 2023/09/22
 */
@Slf4j
@Component
public class GlueTypeExecutorFactory {

    @Autowired
    private List<GlueTypeExecutorProcessor> executorProcessors;

    @Autowired
    private NotSupportsTypeExecutorProcessor notSupportsTypeExecutorProcessor;

    /**
     * 运行触发器
     *
     * @param triggerParam 触发参数
     * @return {@link ResponseVO}<{@link Void}>
     */
    public ResponseVO<Void> runTrigger(TriggerParam triggerParam){
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        GlueTypeExecutorProcessor executorProcessor = executorProcessors.stream()
                .filter(a -> a.supports(glueTypeEnum))
                .findAny()
                .orElse(notSupportsTypeExecutorProcessor);
        return executorProcessor.trigger(triggerParam);
    }












}
