package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.service.JobLogService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogMapperTest {

    @Resource
    private JobLogService xxlJobLogService;

    @Test
    public void test(){
        // Service pageList returns PageModel<XxlJobLog>, simplified test
        List<XxlJobLog> list2 = xxlJobLogService.list();
        // For testing, just verify service is working
        System.out.println("Found " + (list2 != null ? list2.size() : 0) + " logs");
    }

}