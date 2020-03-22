package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * job log
 * @author xuxueli 2019-11-22
 */
@Repository
public interface XxlJobLogReportDao extends JpaRepository<XxlJobLogReport, Long> {

	@Transactional
	@Modifying
	@Query("update XxlJobLogReport t set t.runningCount = :#{#xxlJobLogReport.runningCount}, " +
			"t.sucCount = :#{#xxlJobLogReport.sucCount}, t.failCount = :#{#xxlJobLogReport.failCount} " +
			"where t.triggerDay = :#{#xxlJobLogReport.triggerDay}")
	public int update(@Param("xxlJobLogReport") XxlJobLogReport xxlJobLogReport);

	@Query("select t from XxlJobLogReport t where t.triggerDay between :triggerDayFrom and :triggerDayTo order by t.triggerDay asc ")
	public List<XxlJobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
												@Param("triggerDayTo") Date triggerDayTo);

	@Query(value = "SELECT 0 id, '2020-01-01' trigger_day, SUM(running_count) running_count, SUM(suc_count) suc_count, SUM(fail_count) fail_count FROM xxl_job_log_report", nativeQuery = true)
	public XxlJobLogReport queryLogReportTotal();

}
