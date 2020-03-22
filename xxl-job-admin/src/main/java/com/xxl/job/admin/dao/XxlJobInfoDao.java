package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Repository
public interface XxlJobInfoDao extends JpaRepository<XxlJobInfo, Long>, JpaSpecificationExecutor<XxlJobInfo> {

	@Query("select t from XxlJobInfo t where t.id = :id")
	public XxlJobInfo loadById(@Param("id") long id);

	@Transactional
	@Modifying
	@Query("update XxlJobInfo t set t.jobGroup = :#{#xxlJobInfo.jobGroup}, t.jobCron = :#{#xxlJobInfo.jobCron}, " +
			"t.jobDesc = :#{#xxlJobInfo.jobDesc}, t.updateTime = :#{#xxlJobInfo.updateTime}, t.author = :#{#xxlJobInfo.author}, " +
			"t.alarmEmail = :#{#xxlJobInfo.alarmEmail}, t.executorRouteStrategy = :#{#xxlJobInfo.executorRouteStrategy}, t.executorHandler = :#{#xxlJobInfo.executorHandler}, " +
			"t.executorParam = :#{#xxlJobInfo.executorParam}, t.executorBlockStrategy = :#{#xxlJobInfo.executorBlockStrategy}, t.executorTimeout = :#{#xxlJobInfo.executorTimeout}, " +
			"t.executorFailRetryCount = :#{#xxlJobInfo.executorFailRetryCount}, t.glueType = :#{#xxlJobInfo.glueType}, t.glueSource = :#{#xxlJobInfo.glueSource}, " +
			"t.glueRemark = :#{#xxlJobInfo.glueRemark}, t.glueUpdatetime = :#{#xxlJobInfo.glueUpdatetime}, t.childJobId = :#{#xxlJobInfo.childJobId}, " +
			"t.triggerStatus = :#{#xxlJobInfo.triggerStatus}, t.triggerLastTime = :#{#xxlJobInfo.triggerLastTime}, t.triggerNextTime = :#{#xxlJobInfo.triggerNextTime} " +
			"where t.id = :#{#xxlJobInfo.id}")
	public int update(@Param("xxlJobInfo") XxlJobInfo xxlJobInfo);

	@Transactional
	@Modifying
	@Query("delete from XxlJobInfo t where t.id = :id")
	public int delete(@Param("id") long id);

	@Query("select t from XxlJobInfo t where t.jobGroup = :jobGroup")
	public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") long jobGroup);

	@Query("select count(t.id) from XxlJobInfo t")
	public int findAllCount();

	@Query("select t from XxlJobInfo t where t.triggerStatus = 1 and t.triggerNextTime <= :maxNextTime")
	public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, Pageable pageable);

	@Transactional
	@Modifying
	@Query("update XxlJobInfo t set t.triggerStatus = :#{#xxlJobInfo.triggerStatus}, " +
			"t.triggerLastTime = :#{#xxlJobInfo.triggerLastTime}, t.triggerNextTime = :#{#xxlJobInfo.triggerNextTime} " +
			"where t.id = :#{#xxlJobInfo.id}")
	public int scheduleUpdate(@Param("xxlJobInfo") XxlJobInfo xxlJobInfo);


}
