package com.xxl.job.core.openapi.executor.dto;

import java.io.Serializable;

/**
 * @author xuxueli 2020-04-11 22:27
 */
public class LogRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    public LogRequest() {
    }
    public LogRequest(long logDateTime, long logId, int fromLineNum) {
        this.logDateTime = logDateTime;
        this.logId = logId;
        this.fromLineNum = fromLineNum;
    }

    private long logDateTime;
    private long logId;
    private int fromLineNum;

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