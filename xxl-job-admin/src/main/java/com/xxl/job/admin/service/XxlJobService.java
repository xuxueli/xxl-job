package com.xxl.job.admin.service;


import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

	/**
	 * page list
	 *
	 * @param start
	 * @param length
	 * @param jobGroup
	 * @param jobDesc
	 * @param executorHandler
	 * @param author
	 * @return
	 */
	public Page<XxlJobInfo> pageList(int start, int length, long jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

	/**
	 * add job
	 *
	 * @param jobInfo
	 * @return
	 */
	public ReturnT<String> add(XxlJobInfo jobInfo);

	/**
	 * update job
	 *
	 * @param jobInfo
	 * @return
	 */
	public ReturnT<String> update(XxlJobInfo jobInfo);

	/**
	 * remove job
	 * 	 *
	 * @param id
	 * @return
	 */
	public ReturnT<String> remove(long id);

	/**
	 * start job
	 *
	 * @param id
	 * @return
	 */
	public ReturnT<String> start(long id);

	/**
	 * stop job
	 *
	 * @param id
	 * @return
	 */
	public ReturnT<String> stop(long id);

	/**
	 * dashboard info
	 *
	 * @return
	 */
	public Map<String,Object> dashboardInfo();

	/**
	 * chart info
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public ReturnT<Map<String,Object>> chartInfo(Date startDate, Date endDate);

	/**
	 * find clear job log ids
	 *
	 * @param jobGroup
	 * @param jobId
	 * @param clearBeforeTime
	 * @param clearBeforeNum
	 * @param pagesize
	 * @return
	 * @author dudiao
	 * @date 2020/3/23
	 */
	List<Long> findClearLogIds(long jobGroup, long jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize);

	/**
	 * job log page list
	 *
	 * @param offset
	 * @param pagesize
	 * @param jobGroup
	 * @param jobId
	 * @param triggerTimeStart
	 * @param triggerTimeEnd
	 * @param logStatus
	 * @return
	 * @author dudiao
	 * @date 2020/3/23
	 */
	Page<XxlJobLog> jobLogPageList(int offset, int pagesize, long jobGroup, long jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus);

	/**
	 * remove glue backup
	 *
	 * @param jobId
	 * @param limit
	 * @return
	 * @author songyinyin
	 * @date 2020/3/25
	 */
	int removeOldLogGlue(long jobId, int limit);

}
