/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:02:38
 * @Description:
 * @Copyright: 2019 版权所有：
 */
package com.xxl.job.admin.service;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.model.XxlJobInfo;

/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:02:38
 * @Description:
 */
public interface JobInfoService {
	/**
	 * 查询
	 * @param j {@link XxlJobInfo}
	 * @return {@link XxlJobInfo}
	 * @author Haining.Liu
	 * @date 2019年12月5日 下午1:57:45
	 */
	public XxlJobInfo query(XxlJobInfo j);
	/**
	 * 查询列表
	 * @param j {@link XxlJobInfo}
	 * @return {@link Page}<{@link XxlJobInfo}>
	 * @author Haining.Liu
	 * @date 2019年12月5日 下午1:57:45
	 */
	public Page<XxlJobInfo> select(Page<XxlJobInfo> pg, XxlJobInfo j);
}
