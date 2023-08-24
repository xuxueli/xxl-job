package com.xxl.job.admin.common.mail.domain;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 电子邮件接收人信息
 *
 * @author Rong.Jia
 * @date 2021/07/27 10:41:24
 */
@Data
public class EmailTo implements Serializable {

    private static final long serialVersionUID = 8235828969752720172L;

    /**
     * 收件人邮箱
     */
    private String mail;

    /**
     * 发件人名称
     */
    private String name;

    /**
     * 字符集
     */
    private String charset = CharsetUtil.UTF_8;

    public String getCharset() {
        return StrUtil.isBlank(charset) ? CharsetUtil.UTF_8 : charset;
    }

    public EmailTo(String mail, String name, String charset) {
        this.mail = mail;
        this.name = name;
        this.charset = charset;
    }

    public EmailTo(String mail, String name) {
        this.mail = mail;
        this.name = name;
    }

    public EmailTo(String mail) {
        this.mail = mail;
    }

    public EmailTo() {
    }
}
