package com.xxl.job.core.util;

public class ResponseResultInfoEntity<T> {

    private String code; //状态码, 0：成功

    private String msg; //消息

    private T data;  //数据信息

    private String token;

    public ResponseResultInfoEntity() {
    }

    public ResponseResultInfoEntity(String code) {
        this.code = code;
    }

    public ResponseResultInfoEntity(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResultInfoEntity(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
