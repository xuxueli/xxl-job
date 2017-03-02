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

    private int code;
    private String msg;

    public HandleCallbackParam(int logId, Set<String> logAddress, int code, String msg) {
        this.logId = logId;
        this.logAddress = logAddress;
        this.code = code;
        this.msg = msg;
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
