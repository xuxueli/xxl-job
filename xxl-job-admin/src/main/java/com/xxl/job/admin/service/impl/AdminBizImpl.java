package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackRequest;
import com.xxl.job.core.biz.model.RegistryRequest;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {


    @Override
    public ReturnT<String> callback(List<HandleCallbackRequest> handleCallbackRequestList) {
        return XxlJobAdminBootstrap.getInstance().getJobCompleteHelper().callback(handleCallbackRequestList);
    }

    @Override
    public ReturnT<String> registry(RegistryRequest registryRequest) {
        return XxlJobAdminBootstrap.getInstance().getJobRegistryHelper().registry(registryRequest);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryRequest registryRequest) {
        return XxlJobAdminBootstrap.getInstance().getJobRegistryHelper().registryRemove(registryRequest);
    }

}
