package com.xxl.tool.response;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:33
 */

public enum ResponseCode {
    CODE_200(200, "成功"),
    CODE_201(201, "未知错误"),
    CODE_202(202, "业务异常"),
    CODE_203(203, "系统异常");

    private final int code;
    private final String msg;

    private ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
