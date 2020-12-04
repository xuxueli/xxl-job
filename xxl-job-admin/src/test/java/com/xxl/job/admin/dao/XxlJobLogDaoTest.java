package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogDaoTest {

    @Resource
    private XxlJobLogDao xxlJobLogDao;

    @Test
    public void test(){
        List<XxlJobLog> list = xxlJobLogDao.pageList(0, 10, 1, 1, null, null, 1);
        int list_count = xxlJobLogDao.pageListCount(0, 10, 1, 1, null, null, 1);

        XxlJobLog log = new XxlJobLog();
        log.setJobGroup(1);
        log.setJobId(1);

        long ret1 = xxlJobLogDao.save(log);
        XxlJobLog dto = xxlJobLogDao.load(log.getId());

        log.setTriggerTime(new Date());
        log.setTriggerCode(1);
        log.setTriggerMsg("1");
        log.setExecutorAddress("1");
        log.setExecutorHandler("1");
        log.setExecutorParam("1");
        ret1 = xxlJobLogDao.updateTriggerInfo(log);
        dto = xxlJobLogDao.load(log.getId());


        log.setHandleTime(new Date());
        log.setHandleCode(2);
        log.setHandleMsg("2");
        ret1 = xxlJobLogDao.updateHandleInfo(log);
        dto = xxlJobLogDao.load(log.getId());


        List<Long> ret4 = xxlJobLogDao.findClearLogIds(1, 1, new Date(), 100, 100);

        int ret2 = xxlJobLogDao.delete(log.getJobId());

    }

}
