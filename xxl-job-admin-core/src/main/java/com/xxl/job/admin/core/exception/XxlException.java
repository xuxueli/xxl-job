package com.xxl.job.admin.core.exception;

/**
 * XxlException - core exception for xxl-job
 *
 * @author xuxueli 2026-04-22
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