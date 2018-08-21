package com.xxl.job.core.biz.model;

public enum HandleCodeEnum {
    CHILD_RUNNING(444),IGNORE(666),CONTAINS_ERROR(434)
    ,CONTAINS_SUCCESS(433)//有成功，也有失败
    ;

    int code;

    HandleCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
