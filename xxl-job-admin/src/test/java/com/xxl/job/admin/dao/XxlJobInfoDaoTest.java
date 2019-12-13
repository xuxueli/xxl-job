package com.xxl.job.admin.dao;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.JobInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobInfoDaoTest {

	@Autowired
	private JobInfoService jobInfoService;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	
	@Test
	public void pageList(){
		List<XxlJobInfo> list = jobInfoService.select(new Page<XxlJobInfo>(1, 20), new XxlJobInfo().setJobGroup(0).setTriggerStatus(-1));
		System.out.println(list);

		List<XxlJobInfo> list2 = xxlJobInfoDao.getJobsByGroup(1);
		System.out.println(list2);
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

		info.setAddTime(new Date());
		info.setUpdateTime(new Date());
		info.setGlueUpdatetime(new Date());

		int count = xxlJobInfoDao.save(info);
		System.out.println(count);

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

		info2.setUpdateTime(new Date());
		int item2 = xxlJobInfoDao.update(info2);
		System.out.println(item2);

		xxlJobInfoDao.delete(info2.getId());

		List<XxlJobInfo> list2 = xxlJobInfoDao.getJobsByGroup(1);

		int ret3 = xxlJobInfoDao.findAllCount();
		System.out.println(list2);
		System.out.println(ret3);
	}

}
