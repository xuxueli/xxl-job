package com.xxl.job.openapi;

import com.xxl.job.core.constant.Const;
import com.xxl.job.core.openapi.executor.ExecutorBiz;
import com.xxl.job.core.openapi.executor.dto.*;
import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * executor api test
 *
 * Created by xuxueli on 17/5/12.
 */
public class ExecutorBizTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorBizTest.class);

    private static String addressUrl = "http://127.0.0.1:9999/";
    private static String accessToken = "default_token";
    private static String appname = "xxl-job-executor-sample";

    private ExecutorBiz buildClient(){
        return HttpTool.createClient()
                .url(addressUrl)
                .timeout(3 * 1000)
                .header(Const.XXL_JOB_ACCESS_TOKEN, accessToken)
                .header(Const.XXL_JOB_APPNAME, appname)
                .proxy(ExecutorBiz.class);
    }

    @Test
    public void beat() throws Exception {
        ExecutorBiz executorBiz = buildClient();
        // Act
        final Response<String> retval = executorBiz.beat();
        logger.info("retval:{}", retval);

        // Assert result
        Assertions.assertNotNull(retval);
        Assertions.assertNull(((Response<String>) retval).getData());
        Assertions.assertEquals(200, retval.getCode());
    }

    @Test
    public void idleBeat(){
        ExecutorBiz executorBiz = buildClient();

        final int jobId = 0;

        // Act
        final Response<String> retval = executorBiz.idleBeat(new IdleBeatRequest(jobId));

        // Assert result
        Assertions.assertNotNull(retval);
        Assertions.assertNull(((Response<String>) retval).getData());
        Assertions.assertEquals(500, retval.getCode());
        Assertions.assertEquals("job thread is running or has trigger queue.", retval.getMsg());
    }

    @Test
    public void trigger(){
        ExecutorBiz executorBiz = buildClient();

        // trigger data
        final TriggerRequest triggerParam = new TriggerRequest();
        triggerParam.setJobId(1);
        triggerParam.setExecutorHandler("demoJobHandler");
        triggerParam.setExecutorParams(null);
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.COVER_EARLY.name());
        triggerParam.setGlueType(GlueTypeEnum.BEAN.name());
        triggerParam.setGlueSource(null);
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        triggerParam.setLogId(1);
        triggerParam.setLogDateTime(System.currentTimeMillis());

        // Act
        final Response<String> retval = executorBiz.trigger(triggerParam);

        // Assert result
        Assertions.assertNotNull(retval);

    }

    @Test
    public void kill(){
        ExecutorBiz executorBiz = buildClient();

        final int jobId = 0;

        // Act
        final Response<String> retval = executorBiz.kill(new KillRequest(jobId));

        // Assert result
        Assertions.assertNotNull(retval);
        Assertions.assertNull(((Response<String>) retval).getData());
        Assertions.assertEquals(200, retval.getCode());
        Assertions.assertNull(retval.getMsg());
    }

    @Test
    public void log(){
        ExecutorBiz executorBiz = buildClient();

        final long logId = 2;
        final long logDateTim = System.currentTimeMillis();
        final int fromLineNum = 0;

        // Act
        final Response<LogData> retval = executorBiz.log(new LogRequest(logId, logDateTim, fromLineNum));

        // Assert result
        Assertions.assertNotNull(retval);
    }

}
