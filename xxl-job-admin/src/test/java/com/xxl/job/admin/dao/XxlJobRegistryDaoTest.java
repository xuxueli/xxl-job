package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobRegistryDaoTest {

    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    @Test
    public void test(){
        int ret = xxlJobRegistryDao.registrySaveOrUpdate("g1", "k1", "v1", new Date());
        /*int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
        if (ret < 1) {
            ret = xxlJobRegistryDao.registrySave("g1", "k1", "v1", new Date());
        }*/

        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(1, new Date());

        int ret2 = xxlJobRegistryDao.removeDead(Arrays.asList(1));
    }

    @Test
    public void test2() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                int ret = xxlJobRegistryDao.registrySaveOrUpdate("g1", "k1", "v1", new Date());
                System.out.println(ret);

                /*int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
                if (ret < 1) {
                    ret = xxlJobRegistryDao.registrySave("g1", "k1", "v1", new Date());
                }*/
            }).start();
        }

        TimeUnit.SECONDS.sleep(10);
    }

}
