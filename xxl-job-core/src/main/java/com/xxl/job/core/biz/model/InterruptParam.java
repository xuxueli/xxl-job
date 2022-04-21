package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * @author cszxyang
 * @date 2022-04-20
 */
public class InterruptParam implements Serializable {
    private static final long serialVersionUID = 42L;

    public InterruptParam() {
    }
    public InterruptParam(int jobId) {
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