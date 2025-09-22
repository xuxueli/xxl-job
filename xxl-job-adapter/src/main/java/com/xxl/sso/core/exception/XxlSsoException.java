package com.xxl.sso.core.exception;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:32
 */
public class XxlSsoException extends RuntimeException {
    private static final long serialVersionUID = 42L;
    private int errorCode = 500;

    public XxlSsoException(String msg) {
        super(msg);
    }

    public XxlSsoException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public XxlSsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public XxlSsoException(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
