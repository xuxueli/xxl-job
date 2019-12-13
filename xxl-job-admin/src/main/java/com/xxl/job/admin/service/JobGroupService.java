/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:02:38
 * @Description:
 * @Copyright: 2019 版权所有：
 */
package com.xxl.job.admin.service;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;

/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:02:38
 * @Description:
 */
public interface JobGroupService {
	/**
	 * 查询
	 * @param jg {@link XxlJobInfo}
	 * @return {@link XxlJobGroup}
	 * @author Haining.Liu
	 * @date 2019年12月5日 下午1:57:45
	 */
	public XxlJobGroup query(XxlJobGroup jg);
	/**
	 * 查询列表
	 * @param jg {@link XxlJobInfo}
	 * @return {@link Page}<{@link XxlJobGroup}>
	 * @author Haining.Liu
	 * @date 2019年12月5日 下午1:57:45
	 */
	public Page<XxlJobGroup> select(Page<XxlJobGroup> pg, XxlJobGroup jg);
}
