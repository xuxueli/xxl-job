package com.xxl.job.admin.core.model.dingtalk;

import com.xxl.job.core.util.DateUtil;

import java.util.Date;

public class Token {
    private int errcode;
    private String access_token;
    private String errmsg;
    private int expires_in;
    private long expiresAt;

    public void setExpireAt() {
        this.expiresAt = System.currentTimeMillis() / 1000 + expires_in - 100;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public boolean isExpire() {
        return System.currentTimeMillis() / 1000 > this.expiresAt;
    }
}
