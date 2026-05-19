package com.xxl.job.admin.exception;

/**
 * XxlException - proxy exception for xxl-job
 */
public class XxlException extends RuntimeException {

    public XxlException() {
        super();
    }

    public XxlException(String message) {
        super(message);
    }

    public XxlException(String message, Throwable cause) {
        super(message, cause);
    }

    public XxlException(Throwable cause) {
        super(cause);
    }

}