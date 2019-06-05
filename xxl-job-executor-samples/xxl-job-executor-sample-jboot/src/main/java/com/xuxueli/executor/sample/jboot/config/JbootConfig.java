package com.xuxueli.executor.sample.jboot.config;

import com.xuxueli.executor.sample.jboot.jobhandler.CommandJobHandler;
import com.xuxueli.executor.sample.jboot.jobhandler.DemoJobHandler;
import com.xuxueli.executor.sample.jboot.jobhandler.HttpJobHandler;
import com.xuxueli.executor.sample.jboot.jobhandler.ShardingJobHandler;
import com.xxl.job.core.executor.XxlJobExecutor;
import io.jboot.Jboot;
import io.jboot.core.listener.JbootAppListenerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JbootConfig extends JbootAppListenerBase {
    private Logger logger = LoggerFactory.getLogger(JbootConfig.class);

    // ---------------------- xxl-job executor ----------------------
    private XxlJobExecutor xxlJobExecutor = null;

    private void initXxlJobExecutor() {

        // registry jobhandler
        XxlJobExecutor.registJobHandler("demoJobHandler", new DemoJobHandler());
        XxlJobExecutor.registJobHandler("shardingJobHandler", new ShardingJobHandler());
        XxlJobExecutor.registJobHandler("httpJobHandler", new HttpJobHandler());
        XxlJobExecutor.registJobHandler("commandJobHandler", new CommandJobHandler());

        // init executor
        xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setAdminAddresses(Jboot.configValue("xxl.job.admin.addresses"));
        xxlJobExecutor.setAppName(Jboot.configValue("xxl.job.executor.appname"));
        xxlJobExecutor.setIp(Jboot.configValue("xxl.job.executor.ip"));
        xxlJobExecutor.setPort(Integer.valueOf(Jboot.configValue("xxl.job.executor.port")));
        xxlJobExecutor.setAccessToken(Jboot.configValue("xxl.job.accessToken"));
        xxlJobExecutor.setLogPath(Jboot.configValue("xxl.job.executor.logpath"));
        xxlJobExecutor.setLogRetentionDays(Integer.valueOf(Jboot.configValue("xxl.job.executor.logretentiondays")));

        // start executor
        try {
            xxlJobExecutor.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    // ---------------------- jboot ----------------------

    private void destoryXxlJobExecutor() {
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }

    @Override
    public void onStart() {
        initXxlJobExecutor();
        super.onStart();
    }

    @Override
    public void onStop() {
        destoryXxlJobExecutor();
        super.onStop();
    }
}
