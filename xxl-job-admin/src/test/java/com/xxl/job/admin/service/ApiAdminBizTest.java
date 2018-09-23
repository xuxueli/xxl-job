package com.xxl.job.admin.service;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import org.junit.Assert;
import org.junit.Test;

public class ApiAdminBizTest {
    // admin-client
    private static String addressUrl = "http://127.0.0.1:8080".concat(AdminBiz.MAPPING);
    private static String accessToken = null;

    @Test
    public void add() throws Exception {
        ApiAdminBiz apiAdminBiz = (ApiAdminBiz) new NetComClientProxy(ApiAdminBiz.class, addressUrl, accessToken)
                .getObject();

        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setAlarmEmail("caiyihua@wcansoft.com");
        xxlJobInfo.setAuthor("Cai Yihua");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorHandler("demoJobHandler");
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setExecutorParam("");
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setJobCron("0 0/3 * * * ? ");
        xxlJobInfo.setJobGroup(13);
        xxlJobInfo.setJobDesc("API测试任务");
        xxlJobInfo.setGlueType("BEAN");
        ReturnT<String> returnT = apiAdminBiz.add(xxlJobInfo);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);
    }

    @Test
    public void update() throws Exception {
        ApiAdminBiz apiAdminBiz = (ApiAdminBiz) new NetComClientProxy(ApiAdminBiz.class, addressUrl, accessToken)
                .getObject();
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(34);
        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setJobCron("0 0/3 * * * ? ");
        xxlJobInfo.setJobDesc("我是修改备注");
        xxlJobInfo.setAuthor("Cai Yihua");
        ReturnT<String> returnT = apiAdminBiz.update(xxlJobInfo);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);
    }

    @Test
    public void remove() throws Exception {
        ApiAdminBiz apiAdminBiz = (ApiAdminBiz) new NetComClientProxy(ApiAdminBiz.class, addressUrl, accessToken)
                .getObject();
        ReturnT<String> returnT = apiAdminBiz.remove(34);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);
    }

    @Test
    public void pause() throws Exception {
        ApiAdminBiz apiAdminBiz = (ApiAdminBiz) new NetComClientProxy(ApiAdminBiz.class, addressUrl, accessToken)
                .getObject();
        ReturnT<String> returnT = apiAdminBiz.pause(34);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);
    }

    @Test
    public void resume() throws Exception {
        ApiAdminBiz apiAdminBiz = (ApiAdminBiz) new NetComClientProxy(ApiAdminBiz.class, addressUrl, accessToken)
                .getObject();
        ReturnT<String> returnT = apiAdminBiz.resume(34);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);
    }

    @Test
    public void triggerJob() throws Exception {
        ApiAdminBiz apiAdminBiz = (ApiAdminBiz) new NetComClientProxy(ApiAdminBiz.class, addressUrl, accessToken)
                .getObject();

        int jobId = 34;
        ReturnT<String> returnT = apiAdminBiz.triggerJob(jobId);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);
    }
}