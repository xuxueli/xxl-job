package com.xxl.job.admin.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author dudiao
 * @date 2020/3/22 下午 10:04
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobServiceTest {

    @Resource
    private XxlJobService xxlJobService;

    @Test
    public void test() {
        xxlJobService.pageList(0, 10, 1, 1, null, null, null);

        xxlJobService.dashboardInfo();

        xxlJobService.findClearLogIds(1, 1, new Date(), 10, 10);

        xxlJobService.jobLogPageList(0, 10, 1, 1, new Date(), new Date(), 10);

        xxlJobService.removeOldLogGlue(-1, 10);
    }
}
