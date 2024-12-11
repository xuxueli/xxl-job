package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobRegistryDaoTest {

    private Logger logger= LoggerFactory.getLogger(XxlJobRegistryDaoTest.class);

    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;

    @Test
    public void test(){
        int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
        if (ret < 1) {
            try {
                ret = xxlJobRegistryDao.registrySave("g1", "k1", "v1", new Date());
            } catch (Throwable e) {
                logger.warn("registry maybe has registered! {}",e.getMessage(),e);
            }
        }

        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll( new Date());

        int ret2 = xxlJobRegistryDao.removeDead(Arrays.asList(1));
    }

    @Test
    public void test2() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
                if (ret < 1) {
                    try {
                        ret = xxlJobRegistryDao.registrySave("g1", "k1", "v1", new Date());
                    } catch (Throwable e) {
                        logger.warn("registry maybe has registered! {}",e.getMessage(),e);
                    }
                }
                System.out.println(ret);
            }).start();
        }

        TimeUnit.SECONDS.sleep(10);
    }

}
