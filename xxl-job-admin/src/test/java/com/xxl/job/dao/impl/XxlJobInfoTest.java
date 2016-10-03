package com.xxl.job.dao.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationcontext-*.xml")
public class XxlJobInfoTest {
	
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	
	@Test
	public void pageList(){
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(0, 20, 0, null);
		int list_count = xxlJobInfoDao.pageListCount(0, 20, 0, null);
		
		System.out.println(list);
		System.out.println(list_count);
	}
	
	@Test
	public void save_load(){
		XxlJobInfo info = new XxlJobInfo();
		info.setJobName("job_name");
		info.setJobCron("jobCron");
		int count = xxlJobInfoDao.save(info);
		System.out.println(count);
		System.out.println(info.getId());
		
		XxlJobInfo item = xxlJobInfoDao.load(0 ,"job_name");
		System.out.println(item);
	}
	
	@Test
	public void update(){
		XxlJobInfo item = xxlJobInfoDao.load(0 ,"job_name");
		
		item.setJobCron("jobCron2");
		xxlJobInfoDao.update(item);
	}
	
}
