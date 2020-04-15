package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
@Repository
public interface XxlJobLogDao extends JpaRepository<XxlJobLog, Long>, JpaSpecificationExecutor<XxlJobLog> {

	@Query("select t from XxlJobLog t where t.id = :id")
	public XxlJobLog load(@Param("id") long id);

	@Transactional
	@Modifying
	@Query("update XxlJobLog t set t.triggerTime = :#{#xxlJobLog.triggerTime}, " +
			"t.triggerCode = :#{#xxlJobLog.triggerCode}, t.triggerMsg = :#{#xxlJobLog.triggerMsg}, t.executorAddress = :#{#xxlJobLog.executorAddress}, " +
			"t.executorHandler = :#{#xxlJobLog.executorHandler}, t.executorParam = :#{#xxlJobLog.executorParam}, " +
			"t.executorShardingParam = :#{#xxlJobLog.executorShardingParam}, t.executorFailRetryCount = :#{#xxlJobLog.executorFailRetryCount} " +
			"where t.id = :#{#xxlJobLog.id}")
	public int updateTriggerInfo(@Param("xxlJobLog") XxlJobLog xxlJobLog);

	@Transactional
	@Modifying
	@Query("update XxlJobLog t set t.handleTime = :#{#xxlJobLog.handleTime}, " +
			"t.handleCode = :#{#xxlJobLog.handleCode}, t.handleMsg = :#{#xxlJobLog.handleMsg} " +
			"where t.id = :#{#xxlJobLog.id}")
	public int updateHandleInfo(@Param("xxlJobLog") XxlJobLog xxlJobLog);

	@Transactional
	@Modifying
	@Query("delete from XxlJobLog t where t.jobId = :jobId")
	public int delete(@Param("jobId") long jobId);

	@Query("select count(handleCode) as triggerDayCount, " +
			"sum(case when (triggerCode in (0, 200) and handleCode = 0) then 1 else 0 end) as triggerDayCountRunning, " +
			"sum(case when handleCode = 200 then 1 else 0 end) as triggerDayCountSuc " +
			"from XxlJobLog where triggerTime between :fromDate and :toDate")
	public Map<String, Object> findLogReport(@Param("fromDate") Date from,
											 @Param("toDate") Date to);

	@Transactional
	@Modifying
	@Query("delete from XxlJobLog t where t.id in :logIds")
	public int clearLog(@Param("logIds") List<Long> logIds);

	@Query("select t.id from XxlJobLog t " +
			"where ((t.triggerCode not in (0, 200) or t.handleCode <> 0) and (t.handleCode <> 200)) " +
			"and t.alarmStatus = 0")
	public List<Long> findFailJobLogIds(Pageable pageable);

	@Transactional
	@Modifying
	@Query("update XxlJobLog t set t.alarmStatus = :newAlarmStatus " +
			"where t.id = :logId and t.alarmStatus = :oldAlarmStatus")
	public int updateAlarmStatus(@Param("logId") long logId,
								 @Param("oldAlarmStatus") int oldAlarmStatus,
								 @Param("newAlarmStatus") int newAlarmStatus);

	public List<Long> findLostJobIds(@Param("losedTime") Date losedTime);

}
