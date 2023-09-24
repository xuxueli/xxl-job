package com.xxl.job.executor.factory.gluetype;

import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.pojo.dto.TriggerParam;
import com.xxl.job.core.pojo.vo.ResponseVO;

/**
 * 运行模式 执行处理器
 * @author Rong.Jia
 * @date 2023/09/22
 */
public interface GlueTypeExecutorProcessor {

    /**
     * 是否支持
     * @param glueType 运行模式
     * @return {@link Boolean}
     */
    Boolean supports(GlueTypeEnum glueType);

    /**
     * 调度
     * @param triggerParam 调度参数
     * @return {@link ResponseVO}
     */
    ResponseVO<Void> trigger(TriggerParam triggerParam);












}
