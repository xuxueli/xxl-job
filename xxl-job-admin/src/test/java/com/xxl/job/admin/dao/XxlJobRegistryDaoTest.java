package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobRegistryDaoTest {

    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    @Test
    public void test(){
        xxlJobRegistryDao.findDead(new Date(System.currentTimeMillis() - 600 * 1000));

        int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
        if (ret < 1) {
            xxlJobRegistryDao.save(new XxlJobRegistry("g1", "k1", "v1", new Date()));
        }
        xxlJobRegistryDao.registryDelete("g1", "k1", "v1");

        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(new Date(System.currentTimeMillis() - 90 * 1000));

        int ret2 = xxlJobRegistryDao.removeDead(Arrays.asList(1L));
    }

}
