package com.xxl.job.core.biz.model;

public enum HandleCodeEnum {
    IGNORE(666),CONTAINS_SUCCESS(433)//有成功，也有失败
    ;

    int code;

    HandleCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
