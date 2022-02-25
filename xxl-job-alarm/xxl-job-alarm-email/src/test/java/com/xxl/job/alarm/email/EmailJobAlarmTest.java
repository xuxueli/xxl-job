package com.xxl.job.alarm.email;

import com.xxl.job.alarm.AlarmConstants;
import org.junit.jupiter.api.Assertions;

import java.util.Properties;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
class EmailJobAlarmTest {

    @org.junit.jupiter.api.Test
    void doAlarm() {
        Properties properties = new Properties();
        properties.setProperty(EmailConstants.EMAIL_HOST, "smtp.xx.com");
        properties.setProperty(EmailConstants.EMAIL_PORT, "25");
        properties.setProperty(EmailConstants.EMAIL_SMTP_USER, "xx");
        properties.setProperty(EmailConstants.EMAIL_SMTP_PASSWORD, "x");
        properties.setProperty(EmailConstants.EMAIL_SMTP_FROM, "xx");
        properties.setProperty(AlarmConstants.ALARM_TARGET, "xx");
        properties.setProperty(EmailConstants.EMAIL_SUBJECT, "分布式任务告警");

        String message = "<html>\n" +
                "    <body>\n" +
                "        <div id=\"mailContentContainer\" class=\"qmbox qm_con_body_content qqmail_webmail_only\" style=\"opacity: 1;\"><h5></h5>监控告警明细：<span></span><table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\">\n" +
                "                <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\">      <tr>\n" +
                "                    <td width=\"20%\">执行器</td>\n" +
                "                    <td width=\"10%\">任务ID</td>\n" +
                "                    <td width=\"20%\">任务描述</td>\n" +
                "                    <td width=\"10%\">告警类型</td>\n" +
                "                    <td width=\"40%\">告警内容</td>\n" +
                "                </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n" +
                "                <tr>\n" +
                "                    <td>1</td>\n" +
                "                    <td>1</td>\n" +
                "                    <td>测试任务1</td>\n" +
                "                    <td>任务触发类型：手动触发<br>调度机器：172.18.75.119<br>执行器-注册方式：自动注册<br>执行器-地址列表：null<br>路由策略：随机<br>阻塞处理策略：单机串行<br>任务超时时间：1<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>调度失败：执行器地址为空<br><br></td>\n" +
                "                    <td>Alarm Job LogId=50700<br>TriggerMsg=<br>任务触发类型：Cron触发<br>调度机器：172.18.75.119<br>执行器-注册方式：自动注册<br>执行器-地址列表：null<br>路由策略：随机<br>阻塞处理策略：单机串行<br>任务超时时间：1<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\"> &gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;&gt;触发调度&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt; </span><br>调度失败：执行器地址为空<br><br></td>\n" +
                "                </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "            <style type=\"text/css\">.qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta {display: none !important;}</style>\n" +
                "        </div>\n" +
                "    </body>\n" +
                "</html>\n";

        boolean alarm = new EmailJobAlarm().doAlarm(properties, message);
        //Assertions.assertTrue(alarm);
        Assertions.assertFalse(alarm);
    }
}
