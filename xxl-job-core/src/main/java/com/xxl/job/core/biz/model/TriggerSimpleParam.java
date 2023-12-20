package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * Created by xuxueli on 16/7/22.
 */
public class TriggerSimpleParam implements Serializable{
    private static final long serialVersionUID = 42L;

    private int jobId;
    private String executorParams;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getExecutorParams() {
        return executorParams;
    }

    public void setExecutorParams(String executorParams) {
        this.executorParams = executorParams;
    }

    @Override
    public String toString() {
        return "TriggerSimpleParam{" +
                "jobId=" + jobId +
                ", executorParams='" + executorParams + '\'' +
                '}';
    }
}
