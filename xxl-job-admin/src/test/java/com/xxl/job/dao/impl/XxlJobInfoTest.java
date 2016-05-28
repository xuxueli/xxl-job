package com.xxl.job.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.IXxlJobInfoDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationcontext-*.xml")
public class XxlJobInfoTest {
	
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	
	@Test
	public void pageList(){
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(0, 20, null, null);
		int list_count = xxlJobInfoDao.pageListCount(0, 20, null, null);
		
		System.out.println(list);
		System.out.println(list_count);
	}
	
	@Test
	public void save_load(){
		XxlJobInfo info = new XxlJobInfo();
		info.setJobName("job_name");
		info.setJobCron("jobCron");
		info.setJobClass("jobClass");
		int count = xxlJobInfoDao.save(info);
		System.out.println(count);
		System.out.println(info.getId());
		
		XxlJobInfo item = xxlJobInfoDao.load(null ,"job_name");
		System.out.println(item);
	}
	
	@Test
	public void update(){
		XxlJobInfo item = xxlJobInfoDao.load(null ,"job_name");
		
		item.setJobCron("jobCron2");
		xxlJobInfoDao.update(item);
	}
	
}
