package com.xxl.job.core.biz.impl;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.rpc.remoting.invoker.call.CallType;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.invoker.route.LoadBalance;
import com.xxl.rpc.remoting.net.NetEnum;
import com.xxl.rpc.serialize.Serializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class ExecutorBizImplTest {

    public XxlJobExecutor xxlJobExecutor = null;
    public ExecutorBiz executorBiz = null;

    @Before
    public void before() throws Exception {

        // init executor
        xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setAdminAddresses(null);
        xxlJobExecutor.setAppName("xxl-job-executor-sample");
        xxlJobExecutor.setIp(null);
        xxlJobExecutor.setPort(9999);
        xxlJobExecutor.setAccessToken(null);
        xxlJobExecutor.setLogPath("/data/applogs/xxl-job/jobhandler");
        xxlJobExecutor.setLogRetentionDays(-1);

        // start executor
        xxlJobExecutor.start();

        TimeUnit.SECONDS.sleep(3);

        // init executor biz proxy
        executorBiz = (ExecutorBiz) new XxlRpcReferenceBean(
                NetEnum.NETTY_HTTP,
                Serializer.SerializeEnum.HESSIAN.getSerializer(),
                CallType.SYNC,
                LoadBalance.ROUND,
                ExecutorBiz.class,
                null,
                3000,
                "127.0.0.1:9999",
                null,
                null,
                null).getObject();
    }

    @After
    public void after(){
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }


    @Test
    public void beat() {
        // Act
        final ReturnT<String> retval = executorBiz.beat();

        // Assert result
        Assert.assertNotNull(retval);
        Assert.assertNull(((ReturnT<String>) retval).getContent());
        Assert.assertEquals(200, retval.getCode());
        Assert.assertNull(retval.getMsg());
    }

    @Test
    public void idleBeat(){
        final int jobId = 0;

        // Act
        final ReturnT<String> retval = executorBiz.idleBeat(jobId);

        // Assert result
        Assert.assertNotNull(retval);
        Assert.assertNull(((ReturnT<String>) retval).getContent());
        Assert.assertEquals(500, retval.getCode());
        Assert.assertEquals("job thread is running or has trigger queue.", retval.getMsg());
    }

    @Test
    public void kill(){
        final int jobId = 0;

        // Act
        final ReturnT<String> retval = executorBiz.kill(jobId);

        // Assert result
        Assert.assertNotNull(retval);
        Assert.assertNull(((ReturnT<String>) retval).getContent());
        Assert.assertEquals(200, retval.getCode());
        Assert.assertNull(retval.getMsg());
    }

    @Test
    public void log(){
        final long logDateTim = 0L;
        final long logId = 0;
        final int fromLineNum = 0;

        // Act
        final ReturnT<LogResult> retval = executorBiz.log(logDateTim, logId, fromLineNum);

        // Assert result
        Assert.assertNotNull(retval);
    }

    @Test
    public void run(){
        // trigger data
        final TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1);
        triggerParam.setExecutorHandler("demoJobHandler");
        triggerParam.setExecutorParams(null);
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.COVER_EARLY.name());
        triggerParam.setGlueType(GlueTypeEnum.BEAN.name());
        triggerParam.setGlueSource(null);
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        triggerParam.setLogId(1);
        triggerParam.setLogDateTim(System.currentTimeMillis());

        // Act
        final ReturnT<String> retval = executorBiz.run(triggerParam);

        // Assert result
        Assert.assertNotNull(retval);
    }

}
