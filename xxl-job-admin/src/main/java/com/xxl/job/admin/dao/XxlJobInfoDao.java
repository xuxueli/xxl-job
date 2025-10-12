package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.platform.data.LogBatchOperateDto;
import com.xxl.job.admin.platform.pageable.data.PageDto;
import javafx.print.Collation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

	public List<XxlJobInfo> pageList(@Param("page")PageDto page,
									 @Param("jobGroup") int jobGroup,
									 @Param("triggerStatus") int triggerStatus,
									 @Param("jobDesc") String jobDesc,
									 @Param("executorHandler") String executorHandler,
									 @Param("author") String author);
	public int pageListCount(
							 @Param("jobGroup") int jobGroup,
							 @Param("triggerStatus") int triggerStatus,
							 @Param("jobDesc") String jobDesc,
							 @Param("executorHandler") String executorHandler,
							 @Param("author") String author);

	public int save(XxlJobInfo info);

	public XxlJobInfo loadById(@Param("id") int id);

	public int update(XxlJobInfo xxlJobInfo);

	public int delete(@Param("id") long id);

	public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

	public int findAllCount();

	/**
	 * find schedule job, limit "trigger_status = 1"
	 *
	 * @param maxNextTime
	 * @param pagesize
	 * @return
	 */
	public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize );

	/**
	 * update schedule job
	 *
	 * 	1、can only update "trigger_status = 1", Avoid stopping tasks from being opened
	 * 	2、valid "triggerStatus gte 0", filter illegal state
	 *
	 * @param xxlJobInfo
	 * @return
	 */
	public int scheduleUpdate(XxlJobInfo xxlJobInfo);


    public int batchChangeTriggerStatus(@Param("post")LogBatchOperateDto post,
								 @Param("triggerStatus")int triggerStatus,
								 @Param("loginUser")XxlJobUser loginUser);

	public int batchUpdateScheduleConf(@Param("post")LogBatchOperateDto post,
									   @Param("loginUser")XxlJobUser loginUser);
}
