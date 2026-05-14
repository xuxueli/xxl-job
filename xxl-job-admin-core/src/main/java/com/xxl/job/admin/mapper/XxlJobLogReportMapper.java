package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.model.XxlJobLogReport;
import org.apache.ibatis.annotations.*;


/**
 * job log report
 *
 * @author xuxueli 2019-11-22
 */
public interface XxlJobLogReportMapper extends BaseMapper<XxlJobLogReport> {
    @Select("SELECT " +
                "SUM(running_count) AS runningCount, " +
                "SUM(suc_count) AS sucCount, " +
                "SUM(fail_count) AS failCount " +
            "FROM xxl_job_log_report")
    XxlJobLogReport queryLogReportTotal();
}