package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogGlueDaoTest {

    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;

    @Test
    public void test(){
        XxlJobLogGlue logGlue = new XxlJobLogGlue();
        logGlue.setJobId(1);
        logGlue.setGlueType("1");
        logGlue.setGlueSource("1");
        logGlue.setGlueRemark("1");

        logGlue.setAddTime(new Date());
        logGlue.setUpdateTime(new Date());
        int ret = xxlJobLogGlueDao.save(logGlue);

        List<XxlJobLogGlue> list = xxlJobLogGlueDao.findByJobId(1);

        int ret2 = xxlJobLogGlueDao.removeOld(1, 1);

        int ret3 =xxlJobLogGlueDao.deleteByJobId(1);
    }

}
