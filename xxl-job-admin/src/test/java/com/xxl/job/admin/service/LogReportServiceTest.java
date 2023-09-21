package com.xxl.job.admin.service;

import cn.hutool.core.date.DateUtil;
import com.xxl.job.admin.XxlJobAdminApplicationTest;
import com.xxl.job.admin.common.pojo.dto.LogReportDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LogReportServiceTest extends XxlJobAdminApplicationTest {

    @Autowired
    private LogReportService logReportService;

    @Test
    void syncLogReport() {
        LogReportDTO logReportDTO = new LogReportDTO();
        logReportDTO.setTriggerDay(DateUtil.date());
        logReportDTO.setRunningCount(10L);
        logReportDTO.setSucCount(20L);
        logReportDTO.setFailCount(30L);

        logReportService.syncLogReport(logReportDTO);
    }

    @Test
    void queryLogReportByDay() {
        System.out.println(logReportService.queryLogReportByDay(DateUtil.date()));
    }




}
