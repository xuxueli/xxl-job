package com.xxl.job.admin.core.thread;

public class RingDetail {

    private final Integer jobId;

    private final Long planTriggerTime;

    private final String planTargetTimeZone;

    public RingDetail(int jobId, Long planTriggerTime, String planTargetTimeZone) {
        this.jobId = jobId;
        this.planTriggerTime = planTriggerTime;
        this.planTargetTimeZone = planTargetTimeZone;
    }

    public Integer getJobId() {
        return jobId;
    }

    public Long getPlanTriggerTime() {
        return planTriggerTime;
    }

    public String getPlanTargetTimeZone() {
        return planTargetTimeZone;
    }
}
