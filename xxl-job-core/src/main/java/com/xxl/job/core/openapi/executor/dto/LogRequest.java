package com.xxl.job.core.openapi.executor.dto;

import java.io.Serializable;

/**
 * @author xuxueli 2020-04-11 22:27
 */
public class LogRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logId;
    private long logDateTime;
    private int fromLineNum;

    public LogRequest() {
    }
    public LogRequest(long logId, long logDateTime, int fromLineNum) {
        this.logId = logId;
        this.logDateTime = logDateTime;
        this.fromLineNum = fromLineNum;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(long logDateTime) {
        this.logDateTime = logDateTime;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

}