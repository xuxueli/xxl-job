package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.id.GenerateId;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.core.util.DateUtil;
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
    @Resource
    private GenerateId generateId;

    @Test
    public void test(){
        int ret = xxlJobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
        if (ret < 1) {
            ret = xxlJobRegistryDao.registrySave("g1", "k1", "v1", new Date(), generateId.getId());
        }

        Date time = DateUtil.addSecond(new Date(),1);
        List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(time);

        int ret2 = xxlJobRegistryDao.removeDead(Arrays.asList(1L));
    }

}
