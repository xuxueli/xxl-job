package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.service.JobGroupService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobGroupMapperTest {

    @Resource
    private JobGroupService xxlJobGroupService;

    @Test
    public void test(){
        List<XxlJobGroup> list = xxlJobGroupService.findAll();

        List<XxlJobGroup> list2 = xxlJobGroupService.findByAddressType(0);

        XxlJobGroup group = new XxlJobGroup();
        group.setAppname("setAppName");
        group.setTitle("setTitle");
        group.setAddressType(1);
        group.setAddressList("http://127.0.0.1:8080");
        group.setUpdateTime(new Date());

        int ret = xxlJobGroupService.add(group);

        XxlJobGroup group2 = xxlJobGroupService.load(group.getId());
        group2.setAppname("setAppName2");
        group2.setTitle("setTitle2");
        group2.setAddressType(1);
        group2.setAddressList("http://127.0.0.1:8080");
        group2.setUpdateTime(new Date());

        int ret2 = xxlJobGroupService.update(group2);

        int ret3 = xxlJobGroupService.remove(java.util.List.of(group.getId()));
    }

}