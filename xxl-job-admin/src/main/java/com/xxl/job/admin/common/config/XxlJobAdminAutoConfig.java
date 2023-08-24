package com.xxl.job.admin.common.config;

import com.xxl.job.admin.thread.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job管理自动配置
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Configuration
public class XxlJobAdminAutoConfig implements InitializingBean, DisposableBean {

    @Autowired
    private TriggerThreadPool jobTriggerThreadPool;

    @Autowired
    private RegistryThread jobRegistryThread;

    @Autowired
    private FailMonitorThread jobFailMonitorThread;

    @Autowired
    private CompleteThread jobCompleteThread;

    @Autowired
    private LogReportThread jobLogReportThread;

    @Autowired
    private ScheduleThread jobScheduleThread;

    @Override
    public void afterPropertiesSet() throws Exception {
        // admin trigger pool start
        jobTriggerThreadPool.start();

        // admin registry monitor run
        jobRegistryThread.start();

        // admin fail-monitor run
        jobFailMonitorThread.start();

        // admin lose-monitor run ( depend on JobTriggerPoolHelper )
        jobCompleteThread.start();

        // admin log report start
        jobLogReportThread.start();

        // start-schedule  ( depend on JobTriggerPoolHelper )
        jobScheduleThread.start();

        log.info(">>>>>>>>> init xxl-job admin success.");
    }

    @Override
    public void destroy() throws Exception {

        // stop-schedule
        jobScheduleThread.stop();

        // admin log report stop
        jobLogReportThread.stop();

        // admin lose-monitor stop
        jobCompleteThread.stop();

        // admin fail-monitor stop
        jobFailMonitorThread.stop();

        // admin registry stop
        jobRegistryThread.stop();

        // admin trigger pool stop
        jobTriggerThreadPool.stop();

        log.info(">>>>>>>>> destroy xxl-job admin success.");

    }


}
