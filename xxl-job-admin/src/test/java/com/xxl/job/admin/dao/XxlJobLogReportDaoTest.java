package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author dudiao
 * @date 2020/3/22 下午 10:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogReportDaoTest {

    @Resource
    private XxlJobLogReportDao xxlJobLogReportDao;

    @Test
    public void test(){
        XxlJobLogReport report = new XxlJobLogReport();
        report.setFailCount(1);
        report.setRunningCount(1);
        report.setSucCount(1);
        report.setTriggerDay(new Date());
        xxlJobLogReportDao.save(report);

        report.setSucCount(2);
        xxlJobLogReportDao.update(report);

        xxlJobLogReportDao.queryLogReport(new Date(System.currentTimeMillis() - 60 * 1000), new Date());

        xxlJobLogReportDao.queryLogReportTotal();
    }
}
