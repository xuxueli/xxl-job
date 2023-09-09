package com.xxl.job.admin.service;

import com.xxl.job.admin.XxlJobAdminApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class JobInfoServiceTest extends XxlJobAdminApplicationTest {

    @Autowired
    private JobInfoService jobInfoService;

    @Test
    void cronLatestExecutionTime() {

        List<String> crons = jobInfoService.cronLatestExecutionTime("0/20 * * * * ?");
        for (String cron : crons) {
            System.out.println(cron);
        }

    }
}