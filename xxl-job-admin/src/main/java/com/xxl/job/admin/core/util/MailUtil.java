package com.xxl.job.admin.core.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 邮件发送.Util
 * @author xuxueli 2016-3-12 15:06:20
 */
public class MailUtil {
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
	
	private static String host;
	private static String port;
	private static String username;
	private static String password;
	private static String sendFrom;
	private static String sendNick;
	static{
		host = PropertiesUtil.getString("xxl.job.mail.host");
		port = PropertiesUtil.getString("xxl.job.mail.port");
		username = PropertiesUtil.getString("xxl.job.mail.username");
		password = PropertiesUtil.getString("xxl.job.mail.password");
		sendFrom = PropertiesUtil.getString("xxl.job.mail.sendFrom");
		sendNick = PropertiesUtil.getString("xxl.job.mail.sendNick");
	}
	
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
	 * 
	 * //@param javaMailSender: 发送Bean
	 * //@param sendFrom		: 发送人邮箱
	 * //@param sendNick		: 发送人昵称
	 * @param toAddress		: 收件人邮箱
	 * @param mailSubject	: 邮件主题
	 * @param mailBody		: 邮件正文
	 * @param mailBodyIsHtml: 邮件正文格式,true:HTML格式;false:文本格式
	 * @param attachments	: 附件
	 */
	@SuppressWarnings("null")
	public static boolean sendMailSpring(String toAddress, String mailSubject, String mailBody, boolean mailBodyIsHtml,File[] attachments) {
		JavaMailSender javaMailSender = null;//ResourceBundle.getInstance().getJavaMailSender();
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ArrayUtils.isNotEmpty(attachments), "UTF-8"); // 设置utf-8或GBK编码，否则邮件会有乱码;multipart,true表示文件上传
			
			helper.setFrom(sendFrom, sendNick);
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
	 * @param toAddress		: 收件人邮箱
	 * @param mailSubject	: 邮件主题
	 * @param mailBody		: 邮件正文
	 * @param mailBodyIsHtml: 邮件正文格式,true:HTML格式;false:文本格式
	 * //@param inLineFile	: 内嵌文件
	 * @param attachments	: 附件
	 */
	public static boolean sendMail (String toAddress, String mailSubject, String mailBody, 
			boolean mailBodyIsHtml, File[] attachments){
        try {
			// 创建邮件发送类 JavaMailSender (用于发送多元化邮件，包括附件，图片，html 等    )
        	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        	mailSender.setHost(host); 			// 设置邮件服务主机    
        	mailSender.setUsername(username); 	// 发送者邮箱的用户名    
        	mailSender.setPassword(password); 	// 发送者邮箱的密码    
        	
			//配置文件，用于实例化java.mail.session    
			Properties pro = new Properties();
			pro.put("mail.smtp.auth", "true");		// 登录SMTP服务器,需要获得授权 (网易163邮箱新近注册的邮箱均不能授权,测试 sohu 的邮箱可以获得授权)
			pro.put("mail.smtp.socketFactory.port", port);
			pro.put("mail.smtp.socketFactory.fallback", "false");
			mailSender.setJavaMailProperties(pro);
			
			//创建多元化邮件 (创建 mimeMessage 帮助类，用于封装信息至 mimeMessage)
			MimeMessage mimeMessage = mailSender.createMimeMessage();						
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ArrayUtils.isNotEmpty(attachments), "UTF-8");
			
			helper.setFrom(sendFrom, sendNick);
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
	
	static int total = 0;
	public static void main(String[] args) {
		
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 20; i++) {
			exec.execute(new Thread(new Runnable() {
				@Override
				public void run() {
					while(total < 10){
						String mailBody = "<html><head><meta http-equiv="
								+ "Content-Type"
								+ " content="
								+ "text/html; charset=gb2312"
								+ "></head><body><h1>新书快递通知</h1>你的新书快递申请已推送新书，请到<a href=''>空间"
								+ "</a>中查看</body></html>";
						
						sendMail("ovono802302@163.com", "测试邮件", mailBody, false, null);
						System.out.println(total);
						total++;
					}
				}
			}));
		}
	}
	
}
