package com.xxl.job.admin.common.mail;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 电子邮件属性
 *
 * @author Rong.Jia
 * @date 2021/07/26 11:22:30
 */
@Data
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    /**
     * 是否开启，默认不开启
     */
    private boolean enabled = false;

    /**
     * 服务器地址
     */
    private String host;

    /**
     * 端口, 默认：465
     */
    private Integer port = 465;

    /**
     *  是否免认证， 默认：false
     */
    private Boolean avoidAuthEnable = Boolean.FALSE;

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
     * 是否检查RFC 2595指定的服务器标识， 默认：false
     */
    private Boolean sslCheckServerIdentity = Boolean.FALSE;

    /**
     * 是否报告失败发送， 默认：false
     */
    private Boolean sendPartial = Boolean.FALSE;

    /**
     * 开启tls保护连接 ， 默认：false
     */
    private Boolean startTlsEnabled = Boolean.FALSE;

    /**
     * 如果服务器没有支持STARTTLS命令，或命令失败，连接方法将会失败。， 默认：false
     *
     */
    private Boolean startTlsRequired = Boolean.FALSE;

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

}
