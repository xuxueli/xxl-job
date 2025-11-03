package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.core.openapi.AdminBiz;
import com.xxl.job.core.openapi.model.HandleCallbackRequest;
import com.xxl.job.core.openapi.model.RegistryRequest;
import com.xxl.tool.response.Response;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {

    @Override
    public Response<String> callback(List<HandleCallbackRequest> handleCallbackRequestList) {
        return XxlJobAdminBootstrap.getInstance().getJobCompleteHelper().callback(handleCallbackRequestList);
    }

    @Override
    public Response<String> registry(RegistryRequest registryRequest) {
        return XxlJobAdminBootstrap.getInstance().getJobRegistryHelper().registry(registryRequest);
    }

    @Override
    public Response<String> registryRemove(RegistryRequest registryRequest) {
        return XxlJobAdminBootstrap.getInstance().getJobRegistryHelper().registryRemove(registryRequest);
    }

}
