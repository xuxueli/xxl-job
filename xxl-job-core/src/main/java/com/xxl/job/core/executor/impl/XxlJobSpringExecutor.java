package com.xxl.job.core.executor.impl;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.rpc.util.IpUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements DisposableBean, ApplicationContextAware {


    @Override
    public void start() throws Exception {

        // init JobHandler Repository
        initJobHandlerRepository(applicationContext);

        // refresh GlueFactory
        GlueFactory.refreshInstance(1);


        // super start
        super.start();
    }

    private void initJobHandlerRepository(ApplicationContext applicationContext){
        if (applicationContext == null) {
            return;
        }

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler){
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("xxl-job jobhandler["+ name +"] naming conflicts.");
                    }
                    registJobHandler(name, handler);
                }
            }
        }
    }

    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void destroy() {
        // 停止时先通信 admin 删除当前执行器
        List<AdminBiz> adminBizList = getAdminBizList();
        if (adminBizList == null || adminBizList.isEmpty()) {
            logger.warn(">>>>>>>>>>> xxl-job stop admin list is empty.");
        } else {
            // registryParam
            RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), super.getAppName(), IpUtil.getIpPort(super.getIp(), super.getPort()));
            for (AdminBiz adminBiz : adminBizList) {
                try {
                    ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                    if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
                        registryResult = ReturnT.SUCCESS;
                        logger.info(">>>>>>>>>>> xxl-job stop registry-remove success, registryParam:{}, registryResult:{}", registryParam, registryResult);
                        break;
                    } else {
                        logger.info(">>>>>>>>>>> xxl-job stop registry-remove fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
                    }
                } catch (Exception e) {
                    logger.info(">>>>>>>>>>> xxl-job stop registry-remove error, registryParam:{}", registryParam, e);
                }
            }
        }
        super.destroy();
    }
}
