package com.xxl.job.core.openapi.admin;

import com.xxl.job.core.openapi.admin.dto.CallbackRequest;
import com.xxl.job.core.openapi.admin.dto.RegistryRequest;
import com.xxl.tool.response.Response;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {


    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackRequest callback request
     * @return response
     */
    public Response<String> callback(CallbackRequest callbackRequest);


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
