package com.xxl.job.core.openapi.model;

import java.io.Serializable;

/**
 * @author xuxueli 2020-04-11 22:27
 */
public class IdleBeatRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    public IdleBeatRequest() {
    }
    public IdleBeatRequest(int jobId) {
        this.jobId = jobId;
    }

    private int jobId;


    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

}