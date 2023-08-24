package com.xxl.job.executor.exceptions;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * xxl-job执行器异常
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@SuppressWarnings("ALL")
public class XxlJobExecutorException extends RuntimeException implements Serializable {

    public XxlJobExecutorException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public XxlJobExecutorException(String message) {
        super(message);
    }

    public XxlJobExecutorException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public XxlJobExecutorException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public XxlJobExecutorException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public XxlJobExecutorException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }

}
