package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.dao.XxlJobLogReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class XxlJobLogReportService extends ServiceImpl<XxlJobLogReportMapper, XxlJobLogReport> {

    public List<XxlJobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
                                                @Param("triggerDayTo") Date triggerDayTo) {
        QueryWrapper<XxlJobLogReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(XxlJobLogReport::getTriggerDay, triggerDayFrom, triggerDayTo);
        queryWrapper.orderByAsc("trigger_day");
        return this.list(queryWrapper);
    }

    public XxlJobLogReport queryLogReportTotal() {
        List<XxlJobLogReport> list = list();
        int sumRunning = 0;
        int sumSuc = 0;
        int sumFail = 0;
        for (XxlJobLogReport xxlJobLogReport : list) {
            sumRunning += xxlJobLogReport.getRunningCount();
            sumSuc += xxlJobLogReport.getSucCount();
            sumFail += xxlJobLogReport.getFailCount();
        }
        XxlJobLogReport xxlJobLogReport = new XxlJobLogReport();
        xxlJobLogReport.setRunningCount(sumRunning);
        xxlJobLogReport.setSucCount(sumSuc);
        xxlJobLogReport.setFailCount(sumFail);
        return xxlJobLogReport;
    }

    public boolean update(XxlJobLogReport xxlJobLogReport) {
        QueryWrapper<XxlJobLogReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobLogReport::getTriggerDay, xxlJobLogReport.getTriggerDay());
        XxlJobLogReport logReport = new XxlJobLogReport();
        logReport.setFailCount(xxlJobLogReport.getFailCount());
        logReport.setSucCount(xxlJobLogReport.getSucCount());
        logReport.setRunningCount(xxlJobLogReport.getRunningCount());
        return update(logReport, queryWrapper);
    }
}
