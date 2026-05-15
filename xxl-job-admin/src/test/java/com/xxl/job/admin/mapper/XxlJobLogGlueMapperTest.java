package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobLogGlue;
import com.xxl.job.admin.service.JobLogGlueService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogGlueMapperTest {

    @Resource
    private JobLogGlueService xxlJobLogGlueService;

    @Test
    public void test(){
        XxlJobLogGlue logGlue = new XxlJobLogGlue();
        logGlue.setJobId(1);
        logGlue.setGlueType("1");
        logGlue.setGlueSource("1");
        logGlue.setGlueRemark("1");

        logGlue.setAddTime(new Date());
        logGlue.setUpdateTime(new Date());
        boolean ret = xxlJobLogGlueService.save(logGlue);

        List<XxlJobLogGlue> list = xxlJobLogGlueService.findByJobId(1);

        xxlJobLogGlueService.removeOld(1, 1);

        xxlJobLogGlueService.deleteByJobId(1);
    }

}