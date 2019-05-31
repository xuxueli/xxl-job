package com.xuxueli.job.client;

/**
 * @author Luo Bao Ding
 * @since 2019/5/30
 */
public class XxlJobClientException extends RuntimeException {

    public XxlJobClientException() {
        super();
    }

    public XxlJobClientException(String message) {
        super(message);

    }

    public XxlJobClientException(String message, Throwable throwable) {
        super(message,throwable);
    }

}
