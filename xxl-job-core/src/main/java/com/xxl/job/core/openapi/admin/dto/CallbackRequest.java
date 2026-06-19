package com.xxl.job.core.openapi.admin.dto;

import java.io.Serializable;

/**
 * Created by xuxueli on 17/3/2.
 */
public class CallbackRequest implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logId;
    private long logDateTime;

    private int handleCode;
    private String handleMsg;

    public CallbackRequest(){}
    public CallbackRequest(long logId, long logDateTime, int handleCode, String handleMsg) {
        this.logId = logId;
        this.logDateTime = logDateTime;
        this.handleCode = handleCode;
        this.handleMsg = handleMsg;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public long getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(long logDateTime) {
        this.logDateTime = logDateTime;
    }

    public int getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    @Override
    public String toString() {
        return "CallbackRequest{" +
                "logId=" + logId +
                ", logDateTime=" + logDateTime +
                ", handleCode=" + handleCode +
                ", handleMsg='" + handleMsg + '\'' +
                '}';
    }

}
