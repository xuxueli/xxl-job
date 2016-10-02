package com.xxl.job.core.router.model;

import java.util.Set;

/**
 * Created by xuxueli on 16/7/22.
 */
public class RequestModel {

    private long timestamp;
    private String action;

    private String jobGroup;
    private String jobName;

    private String executorHandler;
    private String executorParams;

    private boolean glueSwitch;

    private Set<String> logAddress;
    private int logId;
    private long logDateTim;

    private String status;
    private String msg;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParams() {
        return executorParams;
    }

    public void setExecutorParams(String executorParams) {
        this.executorParams = executorParams;
    }

    public boolean isGlueSwitch() {
        return glueSwitch;
    }

    public void setGlueSwitch(boolean glueSwitch) {
        this.glueSwitch = glueSwitch;
    }

    public Set<String> getLogAddress() {
        return logAddress;
    }

    public void setLogAddress(Set<String> logAddress) {
        this.logAddress = logAddress;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public long getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RequestModel{" +
                "timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", jobName='" + jobName + '\'' +
                ", executorHandler='" + executorHandler + '\'' +
                ", executorParams='" + executorParams + '\'' +
                ", glueSwitch=" + glueSwitch +
                ", logAddress=" + logAddress +
                ", logId=" + logId +
                ", logDateTim=" + logDateTim +
                ", status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
