package com.xxl.job.core.biz.model;

public enum HandleCodeEnum {
    CHILD_RUNNING(444),IGNORE(666);

    int code;

    HandleCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
