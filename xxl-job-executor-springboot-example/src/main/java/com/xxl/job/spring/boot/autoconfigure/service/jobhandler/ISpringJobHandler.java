package com.xxl.job.spring.boot.autoconfigure.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

/**
 * Author: Antergone
 * Date: 2017/7/7
 */
public abstract class ISpringJobHandler extends IJobHandler {
    @Override
    public abstract ReturnT<String> execute(String... params) throws Exception;
}
