package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationcontext-*.xml")
public class XxlJobInfoDaoTest {
	
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	
	@Test
	public void pageList(){
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(0, 20, 0, null, null);
		int list_count = xxlJobInfoDao.pageListCount(0, 20, 0, null, null);
		
		System.out.println(list);
		System.out.println(list_count);

		List<XxlJobInfo> list2 = xxlJobInfoDao.getJobsByGroup(1);
	}
	
	@Test
	public void save_load(){
		XxlJobInfo info = new XxlJobInfo();
		info.setJobGroup(1);
		info.setJobCron("jobCron");
		info.setJobDesc("desc");
		info.setAuthor("setAuthor");
		info.setAlarmEmail("setAlarmEmail");
		info.setExecutorRouteStrategy("setExecutorRouteStrategy");
		info.setExecutorHandler("setExecutorHandler");
		info.setExecutorParam("setExecutorParam");
		info.setExecutorBlockStrategy("setExecutorBlockStrategy");
		info.setGlueType("setGlueType");
		info.setGlueSource("setGlueSource");
		info.setGlueRemark("setGlueRemark");
		info.setChildJobId("1");

		int count = xxlJobInfoDao.save(info);

		XxlJobInfo info2 = xxlJobInfoDao.loadById(info.getId());
		info2.setJobCron("jobCron2");
		info2.setJobDesc("desc2");
		info2.setAuthor("setAuthor2");
		info2.setAlarmEmail("setAlarmEmail2");
		info2.setExecutorRouteStrategy("setExecutorRouteStrategy2");
		info2.setExecutorHandler("setExecutorHandler2");
		info2.setExecutorParam("setExecutorParam2");
		info2.setExecutorBlockStrategy("setExecutorBlockStrategy2");
		info2.setGlueType("setGlueType2");
		info2.setGlueSource("setGlueSource2");
		info2.setGlueRemark("setGlueRemark2");
		info2.setGlueUpdatetime(new Date());
		info2.setChildJobId("1");

		int item2 = xxlJobInfoDao.update(info2);

		xxlJobInfoDao.delete(info2.getId());

		List<XxlJobInfo> list2 = xxlJobInfoDao.getJobsByGroup(1);

		int ret3 = xxlJobInfoDao.findAllCount();

	}

}
