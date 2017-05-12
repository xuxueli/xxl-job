package com.xxl.job.core.thread;

import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.util.AdminApiUtil;
import com.xxl.job.core.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by xuxueli on 17/3/2.
 */
public class ExecutorRegistryThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();
    public static ExecutorRegistryThread getInstance(){
        return instance;
    }

    private Thread registryThread;
    private boolean toStop = false;
    public void start(final int port, final String ip, final String appName){

        // valid
        if ( !(AdminApiUtil.allowCallApi() && (appName!=null && appName.trim().length()>0)) ) {
            logger.warn(">>>>>>>>>>>> xxl-job, executor registry config fail");
            return;
        }

        // executor address (generate addredd = ip:port)
        final String executorAddress;
        if (ip != null && ip.trim().length()>0) {
            executorAddress = ip.trim().concat(":").concat(String.valueOf(port));
        } else {
            executorAddress = IpUtil.getIpPort(port);
        }

        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appName, executorAddress);
                        ReturnT<String> registryResult = AdminApiUtil.callApiFailover(AdminApiUtil.REGISTRY, registryParam);
                        logger.info(">>>>>>>>>>> xxl-job registry, RegistryParam:{}, registryResult:{}", new Object[]{registryParam.toString(), registryResult.toString()});
                    } catch (Exception e) {
                        logger.error(">>>>>>>>>>> xxl-job ExecutorRegistryThread Exception:", e);
                    }

                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        registryThread.setDaemon(true);
        registryThread.start();
    }

    public void toStop() {
        toStop = true;
    }

}
