package com.xxl.job.executor.factory.glue;

import com.xxl.job.executor.factory.handler.JobHandler;

/**
 * Glue处理器
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
public interface GlueProcessor {

    /**
     * 加载新实例
     *
     * @param codeSource 代码源
     * @return {@link JobHandler}
     * @throws Exception 异常
     */
    JobHandler loadNewInstance(String codeSource) throws Exception;











}
