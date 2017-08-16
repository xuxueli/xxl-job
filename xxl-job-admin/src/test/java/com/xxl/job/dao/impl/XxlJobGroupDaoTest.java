package com.xxl.job.dao.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationcontext-*.xml")
public class XxlJobGroupDaoTest {

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @Test
    public void test(){
        List<XxlJobGroup> list = xxlJobGroupDao.findAll();

        List<XxlJobGroup> list2 = xxlJobGroupDao.findByAddressType(0);

        XxlJobGroup group = new XxlJobGroup();
        group.setAppName("setAppName");
        group.setTitle("setTitle");
        group.setOrder(1);
        group.setAddressType(0);
        group.setAddressList("setAddressList");

        int ret = xxlJobGroupDao.save(group);

        XxlJobGroup group2 = xxlJobGroupDao.load(group.getId());
        group2.setAppName("setAppName2");
        group2.setTitle("setTitle2");
        group2.setOrder(2);
        group2.setAddressType(2);
        group2.setAddressList("setAddressList2");

        int ret2 = xxlJobGroupDao.update(group2);

        int ret3 = xxlJobGroupDao.remove(group.getId());
    }

}
