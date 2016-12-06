package com.xxl.job.core.router.model;

/**
 * Created by xuxueli on 16/7/22.
 */
public class ResponseModel {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    private String status;
    private String msg;

    public ResponseModel() {
    }

    public ResponseModel(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

}
