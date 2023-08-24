package com.xxl.job.admin.common.mail;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.mail.domain.EmailAccount;
import com.xxl.job.admin.common.mail.domain.EmailTo;
import com.xxl.job.admin.common.mail.exception.ExceptionEnum;
import org.apache.commons.mail.DataSourceResolver;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.resolver.DataSourceFileResolver;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;


/**
 * 电子邮件工具类
 *
 * @author Rong.Jia
 * @date 2021/07/26 13:11:22
 */
public class EmailUtil {

    private static final String PROTOCOL_REG = "^(http|https|ftp)://.*$";
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    /**
     * 获取数据源解析器
     *
     * @return {@link DataSourceResolver[]} 数据源解析器
     * @throws MalformedURLException URL  异常
     */
    protected static DataSourceResolver[] getDataSourceResolvers() throws MalformedURLException {

        return new DataSourceResolver[]{
                new DataSourceFileResolver(),
                new DataSourceUrlResolver(new URL("http://")),
                new DataSourceUrlResolver(new URL("https://"))};
    }

    /**
     * 设置端口
     *
     * @param email     电子邮件对象
     * @param sslEnable 是否开启SSL协议
     * @param port      端口
     */
    protected static void setPort(Email email, Boolean sslEnable, Integer port) {
        if (sslEnable) {
            email.setSslSmtpPort(Convert.toStr(port));
        } else {
            email.setSmtpPort(port);
        }
    }

    /**
     * 设置消息
     *
     * @param email   电子邮件
     * @param message 消息
     * @throws EmailException 添加异常
     */
    protected static void setMsg(Email email, String message) throws EmailException {
        if (!(email instanceof HtmlEmail)) {
            Assert.notBlank(message, ExceptionEnum.THE_MESSAGE_CANNOT_BE_EMPTY.getValue());
            email.setMsg(message);
        }
    }

    /**
     * 添加接收者
     *
     * @param email    电子邮件
     * @param toEmails 接收者
     * @throws EmailException 添加异常
     */
    protected static void addTo(Email email, List<EmailTo> toEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(toEmails)) {
            int index = 0;
            for (EmailTo toEmail : toEmails) {
                if (StrUtil.isNotBlank(toEmail.getMail())) {
                    email.addTo(toEmail.getMail(), toEmail.getName(), toEmail.getCharset());
                    index++;
                }
            }
            Assert.isFalse(index == 0, ExceptionEnum.THE_RECEIVER_CANNOT_BE_EMPTY.getValue());
        }
    }

    /**
     * 添加抄送者
     *
     * @param email    电子邮件
     * @param ccEmails 抄送者
     * @throws EmailException 添加异常
     */
    protected static void addCc(Email email, List<EmailTo> ccEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(ccEmails)) {
            for (EmailTo ccEmail : ccEmails) {
                if (StrUtil.isNotBlank(ccEmail.getMail())) {
                    email.addCc(ccEmail.getMail(), ccEmail.getName(), ccEmail.getCharset());
                }
            }
        }
    }

    /**
     * 添加密送者
     *
     * @param email     电子邮件
     * @param bccEmails 密送者
     * @throws EmailException 添加异常
     */
    protected static void addBcc(Email email, List<EmailTo> bccEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(bccEmails)) {
            for (EmailTo bccEmail : bccEmails) {
                if (StrUtil.isNotBlank(bccEmail.getMail())) {
                    email.addBcc(bccEmail.getMail(), bccEmail.getName(), bccEmail.getCharset());
                }
            }
        }
    }

    /**
     * 添加回复者
     *
     * @param email       电子邮件
     * @param replyEmails 回复者
     */
    protected static void addReply(Email email, List<EmailTo> replyEmails) throws EmailException {
        if (CollectionUtil.isNotEmpty(replyEmails)) {
            for (EmailTo replyEmail : replyEmails) {
                if (StrUtil.isNotBlank(replyEmail.getMail())) {
                    email.addReplyTo(replyEmail.getMail(), replyEmail.getName(), replyEmail.getCharset());
                }
            }
        }
    }

    /**
     * 添加pop3
     *
     * @param email 电子邮件
     * @param pop3  pop3
     */
    protected static void addPop3(Email email, EmailProperties.Pop3 pop3) {
        if (pop3.getPopBeforeSmtp()) {
            email.setPopBeforeSmtp(pop3.getPopBeforeSmtp(),
                    pop3.getPopHost(), pop3.getPopUsername(),
                    pop3.getPopPassword());
        }
    }

    /**
     * 设置退回
     *
     * @param email        电子邮件
     * @param bounceEnable 是否开启邮件退回
     * @param from         退回人
     */
    protected static void setBounce(Email email, Boolean bounceEnable, String from) {
        if (bounceEnable) {
            email.setBounceAddress(from);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return boolean 是否存在
     */
    protected static boolean checkFileExists(String filePath) {
        try {

            if (ReUtil.isMatch(PROTOCOL_REG, filePath)) {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(filePath).openConnection();
                con.setRequestMethod("HEAD");

                long totalSize = Long.parseLong(con.getHeaderField("Content-Length"));

                return (con.getResponseCode() == HttpURLConnection.HTTP_OK) && totalSize > 0L;
            } else {
                return FileUtil.exist(filePath);
            }
        } catch (Exception e) {
            logger.error("checkFileExists {}", e.getMessage());
        }

        return false;
    }

    /**
     * 检查在线文件
     *
     * @param filePath 文件路径
     * @return boolean 是否是在线文件
     */
    protected static boolean checkOnlineFile(String filePath) {
        return ReUtil.isMatch(PROTOCOL_REG, filePath);
    }

    /**
     * 获取邮箱操作
     *
     * @param emailAccount 电子邮件帐户信息
     * @return {@link EmailTemplate} 邮箱操作
     */
    private static EmailTemplate getTemplate(EmailAccount emailAccount) {

        Assert.notNull(emailAccount, ExceptionEnum.THE_EMAIL_ACCOUNT_INFORMATION_CANNOT_BE_EMPTY.getValue());

        EmailProperties emailProperties = new EmailProperties();
        BeanUtil.copyProperties(emailAccount, emailProperties);
        EmailProperties.Pop3 pop3 = new EmailProperties.Pop3();
        BeanUtil.copyProperties(emailAccount.getPop3(), pop3);
        emailProperties.setPop3(pop3);

        EmailFactoryBean emailFactoryBean = new EmailFactoryBean(emailProperties);
        emailFactoryBean.afterPropertiesSet();

        return emailFactoryBean.getObject();
    }

    /**
     * 设置日期
     *
     * @param email 电子邮件
     * @param date  日期
     */
    protected static void setDate(Email email, Date date) {

        if (ObjectUtil.isNotNull(date)) {
            if (DateUtil.compare(date, new Date()) < 0) {
                date = new Date();
            }
            email.setSentDate(date);
        }
    }



}
