package com.xxl.job.admin.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.xxl.job.admin.core.model.XxlJobRegistry;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationcontext-*.xml")
public class XxlJobRegistryDaoTest {

    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    @Test
    public void test() {
        int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1");
        if (ret < 1) {
            ret = xxlJobRegistryDao.registrySave("g1", "k1", "v1");
        }

        List<XxlJobRegistry> list = xxlJobRegistryDao
                .findAll(Date.from(LocalDateTime.now().minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant()));

        int ret2 = xxlJobRegistryDao
                .removeDead(Date.from(LocalDateTime.now().minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant()));
    }

}
