package com.xxl.job.alarm.email;

/**
 * Created on 2022/2/22.
 *
 * @author lan
 */
public final class EmailConstants {

    /** The host name of the mail server. */
    public static final String EMAIL_HOST = "alarm.email.smtp.host";

    /** The port number of the mail server. */
    public static final String EMAIL_PORT = "alarm.email.smtp.port";

    /** The email address to use for SMTP MAIL command. */
    public static final String EMAIL_SMTP_FROM = "alarm.email.smtp.from";

    /** The SMTP username. */
    public static final String EMAIL_SMTP_USER = "alarm.email.smtp.user";

    /** The SMTP password. */
    public static final String EMAIL_SMTP_PASSWORD = "alarm.email.smtp.password";

    public static final String EMAIL_RECEIVERS = "alarm.email.receivers";

    public static final String EMAIL_SUBJECT = "alarm.email.subject";


    private EmailConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
