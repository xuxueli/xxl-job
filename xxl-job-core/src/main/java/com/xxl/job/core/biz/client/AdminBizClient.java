package com.xxl.job.core.biz.client;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.XxlJobRemotingUtil;

import java.util.List;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class AdminBizClient implements AdminBiz {

    public AdminBizClient() {
    }

    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    private String addressUrl;
    private String accessToken;
    private int timeout = 3;


    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/callback", accessToken, timeout, callbackParamList, String.class);
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registry", accessToken, timeout, registryParam, String.class);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registryRemove", accessToken, timeout, registryParam, String.class);
    }

    public ReturnT<String> triggerJob(TriggerSimpleParam triggerParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/job/trigger", accessToken, timeout, triggerParam, String.class);
    }

    public ReturnT<String> addJob(JobSaveParam jobSaveParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/job/add", accessToken, timeout, jobSaveParam, String.class);
    }

    public ReturnT<String> updateJob(JobSaveParam jobSaveParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/job/update", accessToken, timeout, jobSaveParam, String.class);
    }

    public ReturnT<String> removeJob(int id) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/job/remove/" + id, accessToken, timeout, null, String.class);
    }

    public ReturnT<String> stopJob(int id) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/job/stop/" + id, accessToken, timeout, null, String.class);
    }

    public ReturnT<String> startJob(int id) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/job/start/" + id, accessToken, timeout, null, String.class);
    }

}
