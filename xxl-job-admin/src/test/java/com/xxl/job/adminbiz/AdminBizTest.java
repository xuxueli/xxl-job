package com.xxl.job.adminbiz;

import com.xxl.job.core.openapi.AdminBiz;
import com.xxl.job.core.openapi.client.AdminBizClient;
import com.xxl.job.core.openapi.model.HandleCallbackRequest;
import com.xxl.job.core.openapi.model.RegistryRequest;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class AdminBizTest {

    // admin-client
    private static String addressUrl = "http://127.0.0.1:8080/xxl-job-admin/";
    private static String accessToken = null;
    private static int timeoutSecond = 3;


    @Test
    public void callback() throws Exception {
        AdminBiz adminBiz = new AdminBizClient(addressUrl, accessToken, timeoutSecond);

        HandleCallbackRequest param = new HandleCallbackRequest();
        param.setLogId(1);
        param.setHandleCode(XxlJobContext.HANDLE_CODE_SUCCESS);

        List<HandleCallbackRequest> callbackParamList = Arrays.asList(param);

        Response<String> returnT = adminBiz.callback(callbackParamList);

        assertTrue(returnT.isSuccess());
    }

    /**
     * registry executor
     *
     * @throws Exception
     */
    @Test
    public void registry() throws Exception {
        AdminBiz adminBiz = new AdminBizClient(addressUrl, accessToken, timeoutSecond);

        RegistryRequest registryParam = new RegistryRequest(RegistryConfig.RegistType.EXECUTOR.name(), "xxl-job-executor-example", "127.0.0.1:9999");
        Response<String> returnT = adminBiz.registry(registryParam);

        assertTrue(returnT.isSuccess());
    }

    /**
     * registry executor remove
     *
     * @throws Exception
     */
    @Test
    public void registryRemove() throws Exception {
        AdminBiz adminBiz = new AdminBizClient(addressUrl, accessToken, timeoutSecond);

        RegistryRequest registryParam = new RegistryRequest(RegistryConfig.RegistType.EXECUTOR.name(), "xxl-job-executor-example", "127.0.0.1:9999");
        Response<String> returnT = adminBiz.registryRemove(registryParam);

        assertTrue(returnT.isSuccess());

    }

}
