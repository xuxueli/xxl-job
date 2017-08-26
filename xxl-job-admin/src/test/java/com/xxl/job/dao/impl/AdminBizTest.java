package com.xxl.job.dao.impl;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import org.junit.Assert;
import org.junit.Test;

/**
 * admin-api client, test
 * @author xuxueli 2017-07-28 22:14:52
 */
public class AdminBizTest {

    @Test
    public void registryTest() throws Exception {

        // admin-client
        String addressUrl = "http://127.0.0.1:8080/xxl-job-admin".concat(AdminBiz.MAPPING);
        String accessToken = null;
        AdminBiz adminBiz = (AdminBiz) new NetComClientProxy(AdminBiz.class, addressUrl, accessToken).getObject();

        // test executor registry
        RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), "xxl-job-executor-example", "127.0.0.1:9999");
        ReturnT<String> returnT = adminBiz.registry(registryParam);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);



        // test executor registry remove
        registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), "xxl-job-executor-example", "127.0.0.1:9999");
        returnT = adminBiz.registryRemove(registryParam);
        Assert.assertTrue(returnT.getCode() == ReturnT.SUCCESS_CODE);

    }

}
