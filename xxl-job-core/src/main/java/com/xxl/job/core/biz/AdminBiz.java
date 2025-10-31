package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.HandleCallbackRequest;
import com.xxl.job.core.biz.model.RegistryRequest;
import com.xxl.job.core.biz.model.ReturnT;

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
    public ReturnT<String> callback(List<HandleCallbackRequest> handleCallbackRequestList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryRequest
     * @return
     */
    public ReturnT<String> registry(RegistryRequest registryRequest);

    /**
     * registry remove
     *
     * @param registryRequest
     * @return
     */
    public ReturnT<String> registryRemove(RegistryRequest registryRequest);


    // ---------------------- biz (custome) ----------------------
    // group、job ... manage

}
