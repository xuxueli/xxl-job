package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * Created by xuxueli on 17/3/2.
 */
public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logId;
    private long logDateTim;

    private Long planTriggerTime;
    private String planTargetTimeZone;

    private int handleCode;
    private String handleMsg;

    public HandleCallbackParam(){}
    public HandleCallbackParam(long logId, long logDateTim, Long planTriggerTime, String planTargetTimeZone, int handleCode, String handleMsg) {
        this.logId = logId;
        this.logDateTim = logDateTim;
        this.planTriggerTime = planTriggerTime;
        this.planTargetTimeZone = planTargetTimeZone;
        this.handleCode = handleCode;
        this.handleMsg = handleMsg;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public long getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public Long getPlanTriggerTime() {
        return planTriggerTime;
    }

    public String getPlanTargetTimeZone() {
        return planTargetTimeZone;
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
        return "HandleCallbackParam{" +
                "logId=" + logId +
                ", logDateTim=" + logDateTim +
                ", planTriggerTime=" + planTriggerTime +
                ", planTargetTimeZone=" + planTargetTimeZone +
                ", handleCode=" + handleCode +
                ", handleMsg='" + handleMsg + '\'' +
                '}';
    }

}
