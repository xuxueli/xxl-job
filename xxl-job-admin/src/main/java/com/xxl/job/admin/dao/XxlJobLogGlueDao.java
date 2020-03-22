package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
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
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
@Repository
public interface XxlJobLogGlueDao extends JpaRepository<XxlJobLogGlue, Long>, JpaSpecificationExecutor<XxlJobLogGlue> {

	@Query("select t from XxlJobLogGlue t where t.jobId = :jobId order by t.id desc")
	public List<XxlJobLogGlue> findByJobId(@Param("jobId") long jobId);

	@Transactional
	@Modifying
	@Query("delete from XxlJobLogGlue t where t.jobId = :jobId and t.id not in :excludeLogGlueIds")
	public int removeOld(@Param("jobId") long jobId, @Param("excludeLogGlueIds") List<Long> excludeLogGlueIds);

	@Query("select t.id from XxlJobLogGlue t")
	public List<Long> findJobGlueIds(Pageable pageable);

	@Transactional
	@Modifying
	@Query("delete from XxlJobLogGlue t where t.jobId = :jobId")
	public int deleteByJobId(@Param("jobId") long jobId);
	
}
