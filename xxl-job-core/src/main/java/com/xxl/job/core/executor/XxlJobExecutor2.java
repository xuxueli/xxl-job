package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.server.EmbedServer;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import com.xxl.job.core.thread.JobThread;
import com.xxl.job.core.thread.TriggerCallbackThread;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xuxueli on 2016/3/2 21:14.
 */
public class XxlJobExecutor2 {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor2.class);

    // ---------------------- param ----------------------
    private String adminAddresses;
    private String accessToken;
    private String appname;
    private String address;
    private String ip;
    private int port;
    private String logPath;
    private int logRetentionDays;

    // ---------------------- start + stop ----------------------
    public void start() throws Exception {

        // 初始化日志记录
        XxlJobFileAppender.initLogPath(logPath);

        // 初始化xxlJob地址 可配置多个 该地址用于向xxlJob注册执行器
        initAdminBizList(adminAddresses, accessToken);

        // 启动日志清理线程
        JobLogFileCleanThread.getInstance().start(logRetentionDays);

        // 启动回调线程 即任务执行完 回调xxlJob
        TriggerCallbackThread.getInstance().start();

        // 利用netty初始化一个内嵌的网络服务 用于后续调度执行
        initEmbedServer(address, ip, port, appname, accessToken);
    }

    // ---------------------- admin-client (rpc invoker) ----------------------
    private static List<AdminBiz> adminBizList;

    private void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        if (adminAddresses != null && adminAddresses.trim().length() > 0) {
            for (String address : adminAddresses.trim().split(",")) {
                if (address != null && address.trim().length() > 0) {
                    // 对于客户端来说 这里为AdminBizClient 其是通过rpc的方式来向xxlJob注册执行器
                    AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);
                    if (adminBizList == null) {
                        adminBizList = new ArrayList<>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }

    // ---------------------- executor-server (rpc provider) ----------------------
    private EmbedServer embedServer = null;

    private void initEmbedServer(String address, String ip, int port, String appname, String accessToken) throws Exception {

        // 下面都是推断ip、port来启动基于netty的rpc服务
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();

        if (address == null || address.trim().length() == 0) {
            String ip_port_address = IpUtil.getIpPort(ip, port);   // registry-address：default use address to registry , otherwise use ip:port if address is null
            address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }

        if (accessToken == null || accessToken.trim().length() == 0) {
            logger.warn(">>>>>>>>>>> xxl-job accessToken is empty. To ensure system security, please set the accessToken.");
        }

        // start
        embedServer = new EmbedServer();
        embedServer.start(address, port, appname, accessToken);
    }

    // ---------------------- job handler repository ----------------------
    // 置于客户端内存的JobHandlerMap key为handler的name 即@XxlJob注解的value
    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info(">>>>>>>>>>> xxl-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    /**
     * 子类会调用该方法 从而注册JobHandler
     *
     * @param xxlJob
     * @param bean
     * @param executeMethod
     */
    protected void registJobHandler(XxlJob xxlJob, Object bean, Method executeMethod) {
        if (xxlJob == null) {
            return;
        }
        String name = xxlJob.value();
        Class<?> clazz = bean.getClass();
        String methodName = executeMethod.getName();
        // 一些基本判断
        if (name.trim().length() == 0) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + clazz + "#" + methodName + "] .");
        }
        if (loadJobHandler(name) != null) {
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }
        executeMethod.setAccessible(true);
        // init and destroy
        Method initMethod = null;
        Method destroyMethod = null;
        // 下面是init和destory方法  可通过@XxlJob注解配置
        if (xxlJob.init().trim().length() > 0) {
            try {
                initMethod = clazz.getDeclaredMethod(xxlJob.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        if (xxlJob.destroy().trim().length() > 0) {
            try {
                destroyMethod = clazz.getDeclaredMethod(xxlJob.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + clazz + "#" + methodName + "] .");
            }
        }
        // 将执行方法包装为MethodJobHandler 注册到JobHandlerMap中 等待后续JobThread调用执行
        registJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));
    }


    // ---------------------- job thread repository ----------------------
    // 存储真正执行job的threadMap  key为job的数据库id value为jobThread 其内部持有具体的JobHandler 委托于其执行任务
    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<>();

    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        // 使用JobThread包装jobHandler
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info(">>>>>>>>>>> xxl-job regist JobThread success, jobId:{}, handler:{}", jobId, handler);

        JobThread oldJobThread = jobThreadRepository.put(jobId, newJobThread);    // putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            // 停止oldThread 内置通过设置共享变量toStop=true的方式
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }

    public static JobThread removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();

            return oldJobThread;
        }
        return null;
    }

    public static JobThread loadJobThread(int jobId) {
        return jobThreadRepository.get(jobId);
    }
}
