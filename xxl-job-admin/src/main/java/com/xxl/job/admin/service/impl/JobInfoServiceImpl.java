/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:03:21
 * @Description:
 * @Copyright: 2019 版权所有：
 */
package com.xxl.job.admin.service.impl;

import javax.annotation.Resource;

import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.service.JobInfoService;

/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:03:21
 * @Description:
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {

	@Resource
	private XxlJobInfoDao xxlJobInfoDao;

	@Override
	public XxlJobInfo query(XxlJobInfo j) {
		Page<XxlJobInfo> beans = this.select(new Page<XxlJobInfo>(1, 2), j);
		if (beans == null || beans.size() < 1)
			return null;
		int sz = beans.size();
		if (sz == 1)
			return beans.get(0);
		throw new TooManyResultsException("Expected one (or null) result, but found: " + sz);
	}

	@Override
	public Page<XxlJobInfo> select(Page<XxlJobInfo> pg, XxlJobInfo j) {
		if (pg != null)
			PageHelper.startPage(pg.getPageNum(), pg.getPageSize());
		return xxlJobInfoDao.select(j);
	}
}
