package com.xxl.job.core.openapi;

import com.xxl.job.core.openapi.model.CallbackRequest;
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
     * @param callbackRequestList callback request list
     * @return response
     */
    public Response<String> callback(List<CallbackRequest> callbackRequestList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryRequest registry request
     * @return  response
     */
    public Response<String> registry(RegistryRequest registryRequest);

    /**
     * registry remove
     *
     * @param registryRequest registry request
     * @return  response
     */
    public Response<String> registryRemove(RegistryRequest registryRequest);


    // ---------------------- job operate ----------------------

    // jobAdd

    // jobUpdate

    // jobDelete

    // jobQuery

    // jobStart

    // jobStop

    // jobTrigger

}
