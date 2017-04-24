package com.xxl.job.core.biz.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by xuxueli on 17/3/2.
 */
public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private int logId;
    private Set<String> logAddress;

    private ReturnT<String> executeResult;

    public HandleCallbackParam(int logId, Set<String> logAddress, ReturnT<String> executeResult) {
        this.logId = logId;
        this.logAddress = logAddress;
        this.executeResult = executeResult;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Set<String> getLogAddress() {
        return logAddress;
    }

    public void setLogAddress(Set<String> logAddress) {
        this.logAddress = logAddress;
    }

    public ReturnT<String> getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(ReturnT<String> executeResult) {
        this.executeResult = executeResult;
    }
}
