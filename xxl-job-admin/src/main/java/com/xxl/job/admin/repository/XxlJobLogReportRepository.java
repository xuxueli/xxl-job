package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * @author YunSongLiu
 */
public interface XxlJobLogReportRepository extends JpaRepository<XxlJobLogReport, Integer>, JpaSpecificationExecutor<XxlJobLogReport> {
    List<XxlJobLogReport> queryXxlJobLogReportsByTriggerDayBetweenOrderByTriggerDayAsc(Date from, Date to);

    XxlJobLogReport findByTriggerDayEquals(Date date);
}