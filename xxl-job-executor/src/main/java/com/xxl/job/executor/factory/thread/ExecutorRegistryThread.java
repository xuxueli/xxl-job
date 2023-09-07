package com.xxl.job.executor.factory.thread;

import cn.hutool.core.thread.ThreadUtil;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.executor.service.AdminClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * 执行器注册线程
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class ExecutorRegistryThread extends BaseTaskThread {

    @Autowired
    private AdminClient adminClient;

    private Thread registryThread;
    private volatile boolean toStop = false;

    @Override
    public void start(){
        registryThread = newThread(() -> {

            log.info(">>>>>>>>>>> xxl-job, executor registry thread running.");

            while (!toStop) {
                adminClient.registry();

                try {
                    if (!toStop) {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT.getValue());
                    }
                } catch (InterruptedException e) {
                    if (!toStop) {
                        log.warn(">>>>>>>>>>> xxl-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }
            }

            // registry remove
            adminClient.unRegistry();
            log.info(">>>>>>>>>>> xxl-job, executor registry thread destroy.");
        }, "xxl-job, executor ExecutorRegistryThread");
    }

    @Override
    public void stop() {
        toStop = true;

        // interrupt and wait
        ThreadUtil.interrupt(registryThread, Boolean.TRUE);
    }

}
