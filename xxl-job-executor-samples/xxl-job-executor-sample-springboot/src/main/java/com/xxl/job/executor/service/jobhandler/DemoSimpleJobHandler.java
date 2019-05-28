package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@JobHandler(value = "DemoSimpleJobHandler")
@Component
public class DemoSimpleJobHandler extends IJobHandler {
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public ReturnT<String> execute(String param) {
        String log = "DemoSimpleJobHandler = [" + param + "]:" + count.getAndIncrement();
        System.out.println(log);
        XxlJobLogger.log(log);
        return SUCCESS;
    }

}