package com.xxl.job.admin.service;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLock;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLockDao;
import com.xxl.job.admin.dao.XxlJobUserDao;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author dudiao
 * @date 2020/3/23 下午 04:59
 */
@Component
public class InitDataService implements ApplicationRunner {

    @Resource
    private HibernateProperties hibernateProperties;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobUserDao xxlJobUserDao;
    @Resource
    private XxlJobLockDao xxlJobLockDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // insert xxl_job_group, xxl_job_info, xxl_job_user, xxl_job_lock
        if ("create".equals(hibernateProperties.getDdlAuto())) {
            insertInitData();
        }
    }

    protected void insertInitData() {
        insertXxlJobGroup();

        insertXxlJobInfo();

        insertXxlJobUser();

        XxlJobLock xxlJobLock = new XxlJobLock();
        xxlJobLock.setLockName("schedule_lock");
        xxlJobLockDao.save(xxlJobLock);
    }

    private void insertXxlJobUser() {
        XxlJobUser xxlJobUser = new XxlJobUser();
        xxlJobUser.setId(1L);
        xxlJobUser.setUsername("admin");
        xxlJobUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
        xxlJobUser.setRole(1);
        xxlJobUserDao.save(xxlJobUser);
    }

    private void insertXxlJobGroup() {
        XxlJobGroup xxlJobGroup = new XxlJobGroup();
        xxlJobGroup.setId(1L);
        xxlJobGroup.setAppname("xxl-job-executor-sample");
        xxlJobGroup.setTitle("示例执行器");
        xxlJobGroup.setAddressType(0);
        xxlJobGroup.setAddressList(null);
        xxlJobGroupDao.save(xxlJobGroup);
    }

    private void insertXxlJobInfo() {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(1L);
        xxlJobInfo.setJobGroup(1L);
        xxlJobInfo.setJobCron("0 0 0 * * ? *");
        xxlJobInfo.setJobDesc("测试任务1");
        xxlJobInfo.setAddTime(new Date());
        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfo.setAuthor("XXL");

        xxlJobInfo.setExecutorRouteStrategy("FIRST");
        xxlJobInfo.setExecutorHandler("demoJobHandler");
        xxlJobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
        xxlJobInfo.setExecutorTimeout(0);
        xxlJobInfo.setExecutorFailRetryCount(0);

        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setGlueRemark("GLUE代码初始化");
        xxlJobInfo.setGlueUpdatetime(new Date());
        xxlJobInfoDao.save(xxlJobInfo);
    }
}
