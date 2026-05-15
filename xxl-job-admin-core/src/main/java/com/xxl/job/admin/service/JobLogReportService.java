package com.xxl.job.admin.service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.model.XxlJobLogReport;

public interface JobLogReportService extends IService<XxlJobLogReport> {

    List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo);

    XxlJobLogReport queryLogReportTotal();

}