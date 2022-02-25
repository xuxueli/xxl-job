package com.xxl.job.alarm.email;

import com.xxl.job.alarm.AlarmConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created on 2022/2/22.
 *
 * @author lan
 */
public class EmailSender {

    private final static String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

    private final String subject;

    private final String host;

    private final String port;

    private final String username;

    private final String password;

    private final String from;

    private final String[] receivers;

    public EmailSender(Properties config) {
        subject = config.getProperty(EmailConstants.EMAIL_SUBJECT);
        host = config.getProperty(EmailConstants.EMAIL_HOST);
        port = config.getProperty(EmailConstants.EMAIL_PORT);
        username = config.getProperty(EmailConstants.EMAIL_SMTP_USER);
        password = config.getProperty(EmailConstants.EMAIL_SMTP_PASSWORD);
        from = config.getProperty(EmailConstants.EMAIL_SMTP_FROM);

        String receiversProperty = config.getProperty(AlarmConstants.ALARM_TARGET);
        if (StringUtils.isNotBlank(receiversProperty)) {
            receivers = receiversProperty.split(",");
        } else {
            throw new IllegalArgumentException("email alarm receivers cannot be empty");
        }
    }

    public boolean sendMsg(String message) {
        // Create the email message
        try {
            HtmlEmail email = new HtmlEmail();
            email.setCharset(DEFAULT_CHARSET);
            email.setHostName(host);
            if (StringUtils.isNotBlank(port)) {
                email.setSmtpPort(Integer.parseInt(port));
            }
            for (String receiver : receivers) {
                email.addTo(receiver, receiver);
            }
            email.setAuthentication(username, password);
            email.setFrom(from, from);
            email.setSubject(subject);

            // set the html message
            email.setHtmlMsg(message);

            // send the email
            email.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return false;
    }
}
