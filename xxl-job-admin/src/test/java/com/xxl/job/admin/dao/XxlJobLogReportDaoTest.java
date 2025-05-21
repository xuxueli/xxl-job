package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XxlJobLogReportDaoTest {

    @Resource
    private XxlJobLogReportDao xxlJobLogReportDao;
    @Test
    void update() {
        XxlJobLogReport logReport = new XxlJobLogReport();
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        logReport.setTriggerDay(today);
        List<XxlJobLogReport> reports = xxlJobLogReportDao.queryLogReport(today, today);
        if (reports.isEmpty()){
            xxlJobLogReportDao.save(logReport);
        }
        int update = xxlJobLogReportDao.insertUpdate(logReport);

        assert update > 0;
    }
}