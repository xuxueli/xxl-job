package com.xxl.job.admin.service.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLock;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.util.EntityUtil;
import com.xxl.job.admin.service.SystemService;
import com.xxl.job.core.util.DateUtil;

/**
 * 
 * @author spenggch 2022-09-30 14:46:28
 *
 */
@Component
public class SystemServiceImpl implements SystemService {

	@Autowired(required = false)
	EntityManager em;

	@Override
	@Transactional
	public void initializeData() {
		if (em == null) {
			return;
		}
		Object cn = em.createQuery("select count(*) from " + XxlJobUser.class.getSimpleName()).getSingleResult();
		if (Integer.parseInt(cn.toString()) > 0) {
			return;
		}
		XxlJobGroup xxlJobGroup = new XxlJobGroup();
		xxlJobGroup.setAppname("xxl-job-executor-sample");
		xxlJobGroup.setTitle("示例执行器");
		xxlJobGroup.setAddressType(0);
		xxlJobGroup.setAddressList(null);
		xxlJobGroup.setUpdateTime(DateUtil.parse("2018-11-03 22:21:31", "yyyy-MM-dd HH:mm:ss"));
		em.persist(xxlJobGroup);

		XxlJobInfo xxlJobInfo = new XxlJobInfo();
		xxlJobInfo.setJobGroup(xxlJobGroup.getId());
		xxlJobInfo.setJobDesc("测试任务1");
		xxlJobInfo.setAddTime(DateUtil.parse("2018-11-03 22:21:31", "yyyy-MM-dd HH:mm:ss"));
		xxlJobInfo.setUpdateTime(DateUtil.parse("2018-11-03 22:21:31", "yyyy-MM-dd HH:mm:ss"));
		xxlJobInfo.setAuthor("XXL");
		xxlJobInfo.setAlarmEmail(null);
		xxlJobInfo.setScheduleType("CRON");
		xxlJobInfo.setScheduleConf("0 0 0 * * ? *");
		xxlJobInfo.setMisfireStrategy("DO_NOTHING");
		xxlJobInfo.setExecutorRouteStrategy("FIRST");
		xxlJobInfo.setExecutorHandler("demoJobHandler");
		xxlJobInfo.setExecutorParam(null);
		xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
		xxlJobInfo.setExecutorTimeout(0);
		xxlJobInfo.setExecutorFailRetryCount(0);
		xxlJobInfo.setGlueType("BEAN");
		xxlJobInfo.setGlueSource(null);
		xxlJobInfo.setGlueRemark("GLUE代码初始化");
		xxlJobInfo.setGlueUpdatetime(DateUtil.parse("2018-11-03 22:21:31", "yyyy-MM-dd HH:mm:ss"));
		xxlJobInfo.setChildJobId(null);
		em.persist(xxlJobInfo);

		XxlJobUser xxlJobUser = new XxlJobUser();
		xxlJobUser.setUsername("admin");
		xxlJobUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
		xxlJobUser.setRole(1);
		xxlJobUser.setPermission(null);
		em.persist(xxlJobUser);

		em.createNativeQuery(
				"insert into " + EntityUtil.getFullTablename(XxlJobLock.class) + " values ('schedule_lock') ")
				.executeUpdate();

	}

}
