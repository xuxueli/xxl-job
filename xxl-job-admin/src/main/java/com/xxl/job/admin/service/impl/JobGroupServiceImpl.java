/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:03:21
 * @Description:
 * @Copyright: 2019 版权所有：
 */
package com.xxl.job.admin.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.JobGroupService;

/**
 * @author Haining.Liu
 * @date 2019年12月5日 下午2:03:21
 * @Description:
 */
@Service
public class JobGroupServiceImpl implements JobGroupService {

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;

	@Override
	public XxlJobGroup query(XxlJobGroup jg) {
		List<XxlJobGroup> beans = this.select(new Page<XxlJobGroup>(1, 2), jg);
		if (beans == null || beans.size() < 1)
			return null;
		int sz = beans.size();
		if (sz == 1)
			return beans.get(0);
		throw new TooManyResultsException("Expected one (or null) result, but found: " + sz);
	}

	@Override
	public Page<XxlJobGroup> select(Page<XxlJobGroup> pg, XxlJobGroup jg) {
		if (pg != null)
			PageHelper.startPage(pg.getPageNum(), pg.getPageSize());
		return xxlJobGroupDao.select(jg);
	}
}
