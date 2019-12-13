package com.xxl.job.admin.service;


import java.util.Date;
import java.util.Map;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * core job action for xxl-job
 * 
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

	public Page<XxlJobInfo> select(Page<XxlJobInfo> pg, XxlJobInfo j);

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
	public ReturnT<Integer> update(XxlJobInfo jobInfo);

	/**
	 * remove job
	 * 	 *
	 * @param id
	 * @return
	 */
	public ReturnT<Integer> remove(int id);

	/**
	 * start job
	 *
	 * @param id
	 * @return
	 */
	public ReturnT<String> start(int id);

	/**
	 * stop job
	 *
	 * @param id
	 * @return
	 */
	public ReturnT<String> stop(int id);

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
	 * 添加任务，通过appName
	 * @param jobInfo {@link XxlJobInfo}
	 * @return {@link ReturnT}<{@link Integer}>
	 * @author Haining.Liu
	 * @date 2019年12月5日 上午10:08:54
	 */
	public Integer add4appName(XxlJobInfo jobInfo);
	/**
	 * 修改并重启任务，通过appName
	 * @param jobInfo {@link XxlJobInfo}
	 * @return {@link ReturnT}<{@link Integer}>
	 * @author Haining.Liu
	 * @date 2019年12月5日 上午10:08:54
	 */
	public Integer update4appName(XxlJobInfo jobInfo);
	/**
	 * 删除任务，通过appName
	 * @param jobInfo {@link XxlJobInfo}
	 * @return {@link ReturnT}<{@link Integer}>
	 * @author Haining.Liu
	 * @date 2019年12月5日 上午10:08:54
	 */
	public Integer rm4appName(XxlJobInfo jobInfo);
}
