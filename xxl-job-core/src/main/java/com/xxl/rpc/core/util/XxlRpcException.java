package com.xxl.rpc.core.util;

/**
 * @author xuxueli 2018-10-20 23:00:40
 */
public class XxlRpcException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    public XxlRpcException(String msg) {
        super(msg);
    }

    public XxlRpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public XxlRpcException(Throwable cause) {
        super(cause);
    }

}