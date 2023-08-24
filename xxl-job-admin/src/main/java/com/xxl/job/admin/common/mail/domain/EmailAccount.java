package com.xxl.job.admin.common.mail.domain;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.mail.exception.ExceptionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 邮件配置
 *
 * @author Rong.Jia
 * @date 2021/07/26 13:19:08
 */
@ToString
@NoArgsConstructor
public class EmailAccount implements Serializable {

    public static final long serialVersionUID = 2088567017789003169L;

    /**
     * 服务器地址
     */
    private String host;

    /**
     * 端口, 默认：465
     */
    private Integer port = 465;

    /**
     * 发件人账号
     */
    private String username;

    /**
     * 发件人账号-授权码、密码
     */
    private String password;

    /**
     * 发件人名称
     */
    private String name;

    /**
     * 是否开启SSL协议, 默认开启
     */
    private Boolean sslEnable = Boolean.TRUE;

    /**
     *  是否开启邮件退回， 默认关闭
     */
    private Boolean bounceEnable = Boolean.FALSE;

    /**
     * 超时时间，默认： 3000
     */
    private Integer timeout = 3000;

    /**
     * 连接超时时间， 默认： 3000
     */
    private Integer connectionTimeout = 3000;

    /**
     * 字符集， 默认：UTF-8
     */
    private String charset = CharsetUtil.UTF_8;

    /**
     * debug， 默认：false
     */
    private Boolean debug = Boolean.FALSE;

    /**
     * pop3
     */
    private Pop3 pop3 = new Pop3();

    /**
     * pop3
     *
     * @author Rong.Jia
     * @date 2021/07/26 13:04:39
     */
    @Data
    public static class Pop3 {

        /**
         * 确定是否在SMTP之前使用pop3, 默认：fasle
         */
        private Boolean popBeforeSmtp = Boolean.FALSE;

        /**
         * pop3服务器地址.
         */
        private String popHost;

        /**
         * pop3 账号
         */
        private String popUsername;

        /**
         * pop3 密码
         */
        private String popPassword;

    }

    public String getCharset() {
        return StrUtil.isBlank(charset) ? CharsetUtil.UTF_8 : charset;
    }

    public EmailAccount(String host, Integer port, String username,
                        String password, String name, Boolean sslEnable,
                        Boolean bounceEnable, Integer timeout,
                        Integer connectionTimeout, String charset,
                        Boolean debug, Pop3 pop3) {

        this(host, port, username, password);

        this.name = name;
        this.sslEnable = sslEnable;
        this.bounceEnable = bounceEnable;
        this.timeout = timeout;
        this.connectionTimeout = connectionTimeout;
        this.charset = charset;
        this.debug = debug;
        this.pop3 = pop3;
    }

    public EmailAccount(String host, Integer port, String username, String password) {

        Assert.notBlank(host, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "host"));
        Assert.notNull(port, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "port"));
        Assert.notBlank(username, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "username"));
        Assert.notBlank(password, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "password"));

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        Assert.notBlank(host, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "host"));
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        Assert.notNull(port, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "port"));
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Assert.notBlank(username, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "username"));
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Assert.notBlank(password, String.format(ExceptionEnum.THE_PROPERTY_CANNOT_BE_EMPTY.getValue(), "password"));
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSslEnable() {
        return sslEnable;
    }

    public void setSslEnable(Boolean sslEnable) {
        this.sslEnable = sslEnable;
    }

    public Boolean getBounceEnable() {
        return bounceEnable;
    }

    public void setBounceEnable(Boolean bounceEnable) {
        this.bounceEnable = bounceEnable;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Pop3 getPop3() {
        return pop3;
    }

    public void setPop3(Pop3 pop3) {
        this.pop3 = pop3;
    }
}
