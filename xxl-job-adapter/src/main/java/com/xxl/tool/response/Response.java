package com.xxl.tool.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:33
 */

@Data
@NoArgsConstructor
public class Response<T> implements Serializable {
    public static final long serialVersionUID = 42L;
    private int code;
    private String msg;
    private T data;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String toString() {
        return "Response{code=" + this.code + ", msg='" + this.msg + "', data=" + this.data + "}";
    }

    public boolean isSuccess() {
        return this.code == ResponseCode.CODE_200.getCode();
    }

    public static <T> Response<T> of(int code, String msg, T data) {
        return new Response(code, msg, data);
    }

    public static <T> Response<T> of(int code, String msg) {
        return new Response(code, msg, (Object)null);
    }

    public static <T> Response<T> ofSuccess(T data) {
        return new Response(ResponseCode.CODE_200.getCode(), ResponseCode.CODE_200.getMsg(), data);
    }

    public static <T> Response<T> ofSuccess() {
        return new Response(ResponseCode.CODE_200.getCode(), ResponseCode.CODE_200.getMsg(), (Object)null);
    }

    public static <T> Response<T> ofFail(String msg) {
        return new Response(ResponseCode.CODE_203.getCode(), msg, (Object)null);
    }

    public static <T> Response<T> ofFail() {
        return new Response(ResponseCode.CODE_203.getCode(), ResponseCode.CODE_203.getMsg(), (Object)null);
    }
}
