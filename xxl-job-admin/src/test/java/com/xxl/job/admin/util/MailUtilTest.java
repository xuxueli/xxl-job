package com.xxl.job.admin.util;

import com.xxl.job.admin.core.util.MailUtil;
import com.xxl.job.admin.service.XxlJobService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.MessageFormat;
import java.util.List;

/**
 * email util test
 *
 * @author xuxueli 2017-12-22 17:16:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationcontext-*.xml")
public class MailUtilTest {
	
	@Autowired
	private XxlJobService xxlJobService;

    //@Test
    public void registryTest() throws Exception {

        String mailBodyTemplate = "<h5>任务周期性监控异常明细：</span>" +
                "<table border=\"1\" cellpadding=\"3\" style=\"border-collapse:collapse; width:80%;\" >\n" +
                "   <thead style=\"font-family:微软雅黑; font-weight: bold;color: #ffffff;background-color: #ff8c00;\" >" +
                "      <tr>\n" +
                "         <td>任务ID</td>\n" +
                "         <td>任务描述</td>\n" +
                "         <td>成功数</td>\n" +
                "         <td>失败数</td>\n" +
                "         <td>进行中</td>\n" +
                "      </tr>\n" +
                "   <thead/>\n" +
                "   <tbody>\n<tr>\n<td colspan=\"6\" style=\"font-family:微软雅黑;font-weight: bold;color: #ffffff;background-color: #4bccb4;\">昨天执行情况</td>\n</tr>\n<tbody>\n" +
                "   <tbody>\n" +
                "      <tr>\n" +
                "         <td>{1}</td>\n" +
                "         <td>{2}</td>\n" +
                "         <td>{3}</td>\n" +
                "         <td>{4}</td>\n" +
                "         <td>{5}</td>\n" +
                "      </tr>\n" +
                "   <tbody>\n" +
                "   <tbody>\n<tr>\n<td colspan=\"6\"></td>\n</tr>\n<tbody>\n" +
                "</table>";

        String title = "调度中心监控报警";
        String content = MessageFormat.format(mailBodyTemplate, "01", "测试",30,20,5);

        boolean ret = MailUtil.sendMail("jeromeliu@can-dao.com", title, content);
        System.out.println(ret);
    }
    
    @Test
    public void test(){
    	xxlJobService.monitor("jeromeliu@can-dao.com");
    }

}
