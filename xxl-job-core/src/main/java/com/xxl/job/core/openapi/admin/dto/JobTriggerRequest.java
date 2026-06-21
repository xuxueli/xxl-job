package com.xxl.job.core.openapi.admin.dto;

import java.io.Serializable;

/**
 * request DTO for manually triggering a job once
 */
public class JobTriggerRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    private int id;
    private String executorParam;
    private String addressList;

    public JobTriggerRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
