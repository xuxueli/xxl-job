package com.xxl.job.admin.core.util;

import com.xxl.job.admin.config.AppConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.Properties;

/**
 * 邮件发送.Util
 *
 * @author xuxueli 2016-3-12 15:06:20
 */
@Component
public class MailUtil {
    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);


    @Autowired
    private AppConfig.EmailConfig emailConfig;

    /**
     <!-- spring mail sender -->
     <bean id="javaMailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl"  scope="singleton" >
     <property name="host" value="${mail.host}" />			<!-- SMTP发送邮件的服务器的IP和端口 -->
     <property name="port" value="${mail.port}" />
     <property name="username" value="${mail.username}" />	<!-- 登录SMTP邮件发送服务器的用户名和密码 -->
     <property name="password" value="${mail.password}" />
     <property name="javaMailProperties">					<!-- 获得邮件会话属性,验证登录邮件服务器是否成功 -->
     <props>
     <prop key="mail.smtp.auth">true</prop>
     <prop key="prop">true</prop>
     <!-- <prop key="mail.smtp.timeout">25000</prop> -->
     </props>
     </property>
     </bean>
     */
    /**
     * 发送邮件 (完整版)(结合Spring)
     * <p>
     * //@param javaMailSender: 发送Bean
     * //@param sendFrom		: 发送人邮箱
     * //@param sendNick		: 发送人昵称
     *
     * @param toAddress       : 收件人邮箱
     * @param mailSubject     : 邮件主题
     * @param mailBody        : 邮件正文
     * @param mailBodyIsHtml: 邮件正文格式,true:HTML格式;false:文本格式
     * @param attachments     : 附件
     */
    @SuppressWarnings("null")
    public boolean sendMailSpring(String toAddress, String mailSubject, String mailBody, boolean mailBodyIsHtml, File[] attachments) {
        JavaMailSender javaMailSender = null;//ResourceBundle.getInstance().getJavaMailSender();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ArrayUtils.isNotEmpty(attachments), "UTF-8"); // 设置utf-8或GBK编码，否则邮件会有乱码;multipart,true表示文件上传

            helper.setFrom(emailConfig.getFrom(), emailConfig.getNick());
            helper.setTo(toAddress);

            // 设置收件人抄送的名片和地址(相当于群发了)
            //helper.setCc(InternetAddress.parse(MimeUtility.encodeText("邮箱001") + " <@163.com>," + MimeUtility.encodeText("邮箱002") + " <@foxmail.com>"));

            helper.setSubject(mailSubject);
            helper.setText(mailBody, mailBodyIsHtml);

            // 添加附件
            if (ArrayUtils.isNotEmpty(attachments)) {
                for (File file : attachments) {
                    helper.addAttachment(MimeUtility.encodeText(file.getName()), file);
                }
            }

            // 群发
            //MimeMessage[] mailMessages = { mimeMessage };

            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.info("{}", e);
        }
        return false;
    }

    /**
     * 发送邮件 (完整版) (纯JavaMail)
     *
     * @param toAddress       : 收件人邮箱
     * @param mailSubject     : 邮件主题
     * @param mailBody        : 邮件正文
     * @param mailBodyIsHtml: 邮件正文格式,true:HTML格式;false:文本格式
     *                        //@param inLineFile	: 内嵌文件
     * @param attachments     : 附件
     */
    public boolean sendMail(String toAddress, String mailSubject, String mailBody,
                            boolean mailBodyIsHtml, File[] attachments) {
        try {
            // 创建邮件发送类 JavaMailSender (用于发送多元化邮件，包括附件，图片，html 等    )
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(emailConfig.getHost());            // 设置邮件服务主机
            mailSender.setUsername(emailConfig.getUsername());    // 发送者邮箱的用户名
            mailSender.setPassword(emailConfig.getPassword());    // 发送者邮箱的密码

            //配置文件，用于实例化java.mail.session
            Properties pro = new Properties();
            pro.put("mail.smtp.auth", "true");        // 登录SMTP服务器,需要获得授权 (网易163邮箱新近注册的邮箱均不能授权,测试 sohu 的邮箱可以获得授权)
            pro.put("mail.smtp.socketFactory.port", emailConfig.getPort());
            pro.put("mail.smtp.socketFactory.fallback", "false");
            mailSender.setJavaMailProperties(pro);

            //创建多元化邮件 (创建 mimeMessage 帮助类，用于封装信息至 mimeMessage)
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ArrayUtils.isNotEmpty(attachments), "UTF-8");

            helper.setFrom(emailConfig.getFrom(), emailConfig.getNick());
            helper.setTo(toAddress);

            helper.setSubject(mailSubject);
            helper.setText(mailBody, mailBodyIsHtml);

            // 添加内嵌文件，第1个参数为cid标识这个文件,第2个参数为资源
            //helper.addInline(MimeUtility.encodeText(inLineFile.getName()), inLineFile);

            // 添加附件
            if (ArrayUtils.isNotEmpty(attachments)) {
                for (File file : attachments) {
                    helper.addAttachment(MimeUtility.encodeText(file.getName()), file);
                }
            }

            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
