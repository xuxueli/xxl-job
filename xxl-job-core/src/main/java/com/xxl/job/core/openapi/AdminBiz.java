package com.xxl.job.core.openapi;

import com.xxl.job.core.openapi.model.HandleCallbackRequest;
import com.xxl.job.core.openapi.model.RegistryRequest;
import com.xxl.tool.response.Response;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {


    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param handleCallbackRequestList
     * @return
     */
    public Response<String> callback(List<HandleCallbackRequest> handleCallbackRequestList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryRequest
     * @return
     */
    public Response<String> registry(RegistryRequest registryRequest);

    /**
     * registry remove
     *
     * @param registryRequest
     * @return
     */
    public Response<String> registryRemove(RegistryRequest registryRequest);


    // ---------------------- biz (custome) ----------------------
    // group、job ... manage

}
