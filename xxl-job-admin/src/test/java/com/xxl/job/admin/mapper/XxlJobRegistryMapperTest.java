package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobRegistry;
import com.xxl.job.admin.service.JobRegistryService;
import com.xxl.job.core.constant.RegistType;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobRegistryMapperTest {

    @Resource
    private JobRegistryService xxlJobRegistryService;

    @Test
    public void test(){
        int ret = xxlJobRegistryService.registrySaveOrUpdate(RegistType.EXECUTOR.name(), "xxl-job-executor-z1", "v1", new Date());

        List<XxlJobRegistry> list = xxlJobRegistryService.findAll(1, new Date());

        int ret2 = xxlJobRegistryService.removeDead(Arrays.asList(1));
    }

    @Test
    public void test2() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(() -> {
                int ret = xxlJobRegistryService.registrySaveOrUpdate("g1", "k1", "v1", new Date());
                System.out.println(ret);
            }).start();
        }

        TimeUnit.SECONDS.sleep(10);
    }

}