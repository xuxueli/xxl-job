package com.xxl.job.core.executor;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.glue.GlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import com.xxl.job.core.thread.ExecutorRegistryThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.job.core.util.AdminApiUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Antergone
 * Date: 2017/7/6
 */
@Data
public class XxlJobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);

    private String ip;
    private int port = 9999;
    private String appName;
    private String addresses;
    private static String logPath;

    private volatile static boolean isInit = false;

    private static NetComServerFactory serverFactory = new NetComServerFactory();
    private static ConcurrentHashMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, JobThread> JobThreadRepository = new ConcurrentHashMap<>();

    public XxlJobExecutor(String ip, int port, String appName, String addresses, String logDir) {
        this.ip = ip;
        this.port = port;
        this.appName = appName;
        this.addresses = addresses;
        if (!logDir.endsWith("/")) {
            logDir = logDir + "/";
        }
        logPath = logDir;
    }

    public void start() throws Exception {
        // admin api util init
        AdminApiUtil.init(this.getAddresses());
        GlueFactory.init();
        // executor start
        serverFactory.putService(ExecutorBiz.class, new ExecutorBizImpl());
        serverFactory.start(this.getPort(), this.getIp(), this.getAppName());
        // trigger callback thread start
        TriggerCallbackThread.getInstance().start();

        this.isInit = true;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                destroy();
            }
        }));
    }

    public void destroy() {
        // 1、executor registry thread stop
        ExecutorRegistryThread.getInstance().toStop();

        // 2、executor stop
        serverFactory.destroy();

        // 3、job thread repository destroy
        if (JobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : JobThreadRepository.entrySet()) {
                JobThread jobThread = item.getValue();
                jobThread.toStop("Web容器销毁终止");
                jobThread.interrupt();

            }
            JobThreadRepository.clear();
        }
        // 4、trigger callback thread stop
        TriggerCallbackThread.getInstance().toStop();
    }

    public static String getLogPath() {
        if (isInit) {
            return logPath;
        } else {
            throw new IllegalStateException("XxlJobExecutor未初始化！");
        }
    }


    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info("xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    public static void registJobHandler(Map<String, IJobHandler> beanMap) {
        jobHandlerRepository.putAll(beanMap);
    }

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }


    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", new Object[]{jobId, handler});

        JobThread oldJobThread = JobThreadRepository.put(jobId, newJobThread);    // putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static void removeJobThread(int jobId) {
        JobThread oldJobThread = JobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop("人工手动终止");
            oldJobThread.interrupt();
        }
    }

    public static JobThread loadJobThread(int jobId) {
        return JobThreadRepository.get(jobId);
    }


}
