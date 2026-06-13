package com.xxl.job.core.thread;

import com.xxl.job.core.constant.RegistType;
import com.xxl.job.core.openapi.AdminBiz;
import com.xxl.job.core.openapi.model.RegistryRequest;
import com.xxl.job.core.constant.Const;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuxueli on 17/3/2.
 */
public class ExecutorRegistryThreadHelper {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorRegistryThreadHelper.class);


    /**
     * registry thread
     */
    private CyclicThread registryThread;

    /**
     * start
     */
    public void start(final XxlJobExecutor xxlJobExecutor){

        /**
         * valid
          */
        if (StringTool.isBlank(xxlJobExecutor.getAppname())) {
            logger.warn(">>>>>>>>>>> xxl-job, executor registry config fail, appname is null.");
            return;
        }
        if (CollectionTool.isEmpty(xxlJobExecutor.getAdminBizList())) {
            logger.warn(">>>>>>>>>>> xxl-job, executor registry config fail, adminAddresses is null.");
            return;
        }


        /**
         * registry thread
          */
        registryThread = new CyclicThread("ExecutorRegistryThread#registryThread", true, new Runnable() {
            @Override
            public void run() {
                RegistryRequest registryParam = new RegistryRequest(RegistType.EXECUTOR.name(), xxlJobExecutor.getAppname(), xxlJobExecutor.getAddress());
                for (AdminBiz adminBiz: xxlJobExecutor.getAdminBizList()) {
                    try {
                        Response<String> registryResult = adminBiz.registry(registryParam);
                        if (registryResult!=null && registryResult.isSuccess()) {
                            registryResult = Response.ofSuccess();
                            logger.debug(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            break;
                        } else {
                            logger.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                        }
                    } catch (Throwable e) {
                        logger.info(">>>>>>>>>>> xxl-job registry error, registryParam:{}", registryParam, e);
                    }

                }
            }
        }, Const.BEAT_TIMEOUT * 1000L, true);
        registryThread.start();

    }

    /**
     * stop
     */
    public void stop(final XxlJobExecutor xxlJobExecutor) {

        /**
         * 1、stop registryThread
         */
        registryThread.stop();

        /**
         * 2、registry remove
         */
        registryRemove(xxlJobExecutor);
    }

    private void registryRemove(final XxlJobExecutor xxlJobExecutor){
        RegistryRequest registryParam = new RegistryRequest(RegistType.EXECUTOR.name(), xxlJobExecutor.getAppname(), xxlJobExecutor.getAddress());
        for (AdminBiz adminBiz: xxlJobExecutor.getAdminBizList()) {
            try {
                Response<String> registryResult = adminBiz.registryRemove(registryParam);
                if (registryResult!=null && registryResult.isSuccess()) {
                    registryResult = Response.ofSuccess();
                    logger.info(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                    break;
                } else {
                    logger.info(">>>>>>>>>>> xxl-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                }
            } catch (Throwable e) {
                logger.warn(">>>>>>>>>>> xxl-job registry-remove error, registryParam:{}, error:{}", registryParam, e.getMessage());
            }
        }
    }

}
