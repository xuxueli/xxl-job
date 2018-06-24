package com.xxl.job.admin.util;

import com.xxl.job.admin.core.util.MailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.MessageFormat;

/**
 * email util test
 *
 * @author xuxueli 2017-12-22 17:16:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationcontext-*.xml")
public class MailUtilTest {

    @Test
    public void mailTest() throws Exception {

        String mailBodyTemplate = "<h5>监控告警明细：</span>" +
                "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
                "   <thead style=\"font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" +
                "         <td>执行器</td>\n" +
                "         <td>任务ID</td>\n" +
                "         <td>任务描述</td>\n" +
                "         <td>告警类型</td>\n" +
                "      </tr>\n" +
                "   <thead/>\n" +
                "   <tbody>\n" +
                "      <tr>\n" +
                "         <td>{0}</td>\n" +
                "         <td>{1}</td>\n" +
                "         <td>{2}</td>\n" +
                "         <td>调度失败</td>\n" +
                "      </tr>\n" +
                "   <tbody>\n" +
                "</table>";

        String title = "调度中心监控报警";
        String content = MessageFormat.format(mailBodyTemplate, "执行器A", "01", "任务A1");

        boolean ret = MailUtil.sendMail("931591021@qq.com", title, content);
        System.out.println(ret);
    }

}
