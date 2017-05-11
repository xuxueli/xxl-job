package com.xxl.job.core.executor;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.job.core.util.AdminApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor implements ApplicationContextAware, ApplicationListener {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    private String ip;
    private int port = 9999;
    private String appName;
    private String adminAddresses;
    public static String logPath;

    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    // ---------------------------------- job server ------------------------------------
    private NetComServerFactory serverFactory = new NetComServerFactory();
    public void start() throws Exception {
        // admin api util init
        AdminApiUtil.init(adminAddresses);

        // executor start
        NetComServerFactory.putService(ExecutorBiz.class, new ExecutorBizImpl());
        serverFactory.start(port, ip, appName);

        // trigger callback thread start
        TriggerCallbackThread.getInstance().start();
    }
    public void destroy(){
        // 1、executor registry thread stop
        ExecutorRegistryThread.getInstance().toStop();

        // 2、executor stop
        serverFactory.destroy();

        // 3、job thread repository destory
        if (JobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item: JobThreadRepository.entrySet()) {
                JobThread jobThread = item.getValue();
                jobThread.toStop("Web容器销毁终止");
                jobThread.interrupt();

            }
            JobThreadRepository.clear();
        }

        // 4、trigger callback thread stop
        TriggerCallbackThread.getInstance().toStop();
    }

    // ---------------------------------- init job handler ------------------------------------
    public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobExecutor.applicationContext = applicationContext;

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHander.class);

        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler){
                    String name = serviceBean.getClass().getAnnotation(JobHander.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    registJobHandler(name, handler);
                }
            }
        }
	}

    // ---------------------------------- destory job executor ------------------------------------
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if(applicationEvent instanceof ContextClosedEvent){
            // TODO
        }
    }

    // ---------------------------------- job handler repository
    private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<String, IJobHandler>();
    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler){
        logger.info("xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }
    public static IJobHandler loadJobHandler(String name){
        return jobHandlerRepository.get(name);
    }

    // ---------------------------------- job thread repository
    private static ConcurrentHashMap<Integer, JobThread> JobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();
    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason){
        JobThread newJobThread = new JobThread(handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = JobThreadRepository.put(jobId, newJobThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }
    public static void removeJobThread(int jobId, String removeOldReason){
        JobThread oldJobThread = JobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }
    public static JobThread loadJobThread(int jobId){
        JobThread jobThread = JobThreadRepository.get(jobId);
        return jobThread;
    }

}
