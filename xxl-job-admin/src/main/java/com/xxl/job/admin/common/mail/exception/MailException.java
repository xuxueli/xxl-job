package com.xxl.job.admin.common.mail.exception;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * 电子邮件异常
 *
 * @author Rong.Jia
 * @date 2021/07/26 13:32:01
 */
public class MailException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 8543112636271431069L;

    public MailException(String message) {
        super(message);
    }

    public MailException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public MailException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public MailException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }

}
