package com.xxl.job.admin.controller.rest;

/**
 * @Author: 63198
 * @Date: 2021/3/2 下午2:12
 * @Version 1.0
 */
public class TriggerOnceInfo {
    private String executorParam;
    private String addressList;


    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }
}
